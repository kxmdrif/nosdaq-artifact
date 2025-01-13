
import datetime
import math
import operator
import re
import warnings
from copy import deepcopy

from exprs import exprtypes
from frozendict import frozendict
from parsers.sexp import DATE_REGEX, NUM_REGEX, NULL
from semantics import semantics_lia
from semantics import semantics_types
from semantics.semantics_types import InterpretedFunctionBase

NumericType = int | float
DateType = datetime.datetime
ListType = list | tuple

logic_ops = {
    "<": operator.lt, "<=": operator.le, "=": operator.eq,
    "!=": operator.ne, ">": operator.gt, ">=": operator.ge,
}
arith_ops = {
    "+": operator.add, "-": operator.sub, "*": operator.mul,
    "/": operator.truediv, "%": operator.mod,
}

INF = 9999999999
GROUP_PREFIX = "_id"
PREDICATE_DEPTH = EXPRESSION_DEPTH = 3
NUM_FIELDS = 2
NUM_GROUP_KEYS = 2
NUM_GROUP_AGGS = 3


def frozenDoc(doc):
    if isinstance(doc, dict):
        for k, v in doc.items():
            doc[k] = frozenDoc(v)
        return frozendict(doc)
    elif isinstance(doc, list):
        doc = [frozenDoc(d) for d in doc]
        return tuple(doc)
    else:
        return doc


class FunctionWrapper:
    __slots__ = ['func', 'name']

    def __init__(self, func, name):
        self.func = func
        self.name = name

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return self.name

    def __call__(self, *args, **kwargs):
        return self.func(*args, **kwargs)


class PredicateWrapper(FunctionWrapper):
    __slots__ = ['func', 'name', 'depth']

    def __init__(self, func, name, depth=1):
        super().__init__(func, name)
        if depth > PREDICATE_DEPTH:
            # warnings.warn(f"Reach the max depth {PREDICATE_DEPTH} of predicates.")
            self.func = lambda *args, **kwargs: None
        self.depth = depth


class ExpressionWrapper(FunctionWrapper):
    __slots__ = ['func', 'name', 'depth']

    def __init__(self, func, name, depth=1):
        super().__init__(func, name)
        if depth > EXPRESSION_DEPTH:
            # warnings.warn(f"Reach the max depth {PREDICATE_DEPTH} of expressions.")
            self.func = lambda *args, **kwargs: None
        self.depth = depth


def _sum(xs):
    return sum(x if isinstance(x, NumericType) else 0 for x in xs)


def _count(xs):
    return len(xs)


def _avg(xs):
    sum_v, count_v = _sum(xs), _count(xs)
    if sum_v == NULL and count_v == NULL:
        return NULL
    else:
        return sum_v / count_v


def _min(xs):
    # only takes as input a list of NULLs or numeric values
    if all(x == NULL for x in xs):
        return NULL
    else:
        return min([x for x in xs if isinstance(x, NumericType)])


def _max(xs):
    # only takes as input a list of NULLs or numeric values
    if all(x == NULL for x in xs):
        return NULL
    else:
        return max([x for x in xs if isinstance(x, NumericType)])


aggregations = {"Sum": _sum, "Avg": _count, "Count": _count, "Min": _min, "Max": _max, }


def visit(doc, path, split_flag=False):
    if not split_flag:
        path = path.split('.')

    def _get(doc, key):
        if isinstance(doc, dict):
            if key in doc:
                doc = doc[key]
            else:
                return None
        elif isinstance(doc, list):
            doc = [_get(d, key) for d in doc]
            doc = [d for d in doc if d is not None]
            if len(doc) == 0:
                return None
        else:
            doc = NULL
        return doc

    for key in path:
        doc = _get(doc, key)
        if doc is None:
            return doc
    return doc


class CheckEquiv(InterpretedFunctionBase):
    # check Program(input) = output
    def __init__(self):
        super().__init__('CheckEquiv', 1,
                         (exprtypes.DocType(),),
                         exprtypes.BoolType())

        def _f(doc):
            def _equal(input, output):
                if len(input) != len(output):
                    return False
                lhs_ids, rhs_ids = list(range(len(input))), list(range(len(output)))
                while len(lhs_ids) > 0:
                    i = lhs_ids[0]
                    for j in range(len(rhs_ids)):
                        if input[i] == output[rhs_ids[j]]:
                            break
                    else:
                        return False
                    lhs_ids.pop(0)
                    rhs_ids.pop(j)
                return len(rhs_ids) == 0

            # print(doc['stmts'])
            # if doc['stmts'] == [['Init'], ['AddFields', [('telephoneCount', 'Size(telephone)')]],
            #                     ['Project', ['_id', 'name', 'telephoneCount']]]:
            #     print(doc['stmts'])
            if len(doc['stmts'][-1]) == 1 or doc['input'] is None:
                return False
            if len(doc['input']) != len(doc['output']):
                return False
            # if doc['stmts'][-1][0] == "Group":
            #     return _equal(doc['input'], doc['output'])
            # else:
            #     return doc['input'] == doc['output']
            return _equal(doc['input'], doc['output'])

        self.eval_children = _f


def heat(doc):
    if isinstance(doc, frozendict):
        obj = {}
        for k, v in doc.items():
            obj[k] = heat(v)
        return obj
    elif isinstance(doc, tuple):
        obj = [heat(d) for d in doc]
        return obj
    else:
        return doc


class Init(InterpretedFunctionBase):
    # convert frozen obj to mutable obj
    def __init__(self):
        super().__init__('Init', 1,
                         (exprtypes.DocType(),),
                         exprtypes.DocType())

        def __f(doc):
            doc = heat(doc)
            doc["stmts"] = [['Init']]
            return doc

        self.eval_children = __f


################################################################
# Project(Stage, Paths) ::= Pi_{Paths}(Stage)
################################################################

class Project(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Project', 2,
                         (exprtypes.DocType(), exprtypes.DocType()),
                         exprtypes.DocType())

        def _f(doc, outpaths):
            # print(doc['stmts'][-1])
            # if doc['stmts'][-1] == ['AddFields', [('telephoneCount', 'Size(telephone)')]] and \
            #         outpaths == ['_id', 'name', 'telephoneCount']:
            #     print(doc['stmts'])
            if doc['input'] is None or doc['stmts'][-1][0] == 'Project':
                doc['input'] = None
                doc['stmts'].append(['Project'])
                return doc

            paths = [path.split('.') for path in outpaths]
            output = []
            # print(outpaths)
            for record in doc['input']:
                new_record = {}
                for keys in paths:
                    for key in keys[:-1]:
                        new_record[key] = {}
                    new_record[keys[-1]] = record.get(keys[-1], NULL)
                output.append(new_record)
            doc['input'] = output
            doc['stmts'].append(["Project", outpaths])
            return doc

        self.eval_children = _f


################################################################
# InPaths ::= {InPath} | InPaths ∪ InPaths
################################################################

class SingletonInPath(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('SingletonInPath', 1,
                         (exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path):
            return [path]

        self.eval_children = _f


class UnionInPaths(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('UnionInPaths', 2,
                         (exprtypes.DocType(), exprtypes.DocType()),
                         exprtypes.DocType())

        def _f(paths1, paths2):
            paths = paths1 + paths2
            return paths

        self.eval_children = _f


################################################################
# OutPaths ::= {OutPath} | OutPaths ∪ OutPaths
################################################################

class SingletonOutPath(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('SingletonOutPath', 1,
                         (exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path):
            return [path]

        self.eval_children = _f


class UnionOutPaths(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('UnionOutPaths', 2,
                         (exprtypes.DocType(), exprtypes.DocType()),
                         exprtypes.DocType())

        def _f(paths1, paths2):
            paths = paths1 + paths2
            return paths

        self.eval_children = _f


################################################################
# Match(Stage Pred) ::= Filter(Stage, Pred)
################################################################

class Match(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Match', 2,
                         (exprtypes.DocType(), exprtypes.DocType()),
                         exprtypes.DocType())

        def _f(doc, pred):
            # print(doc['stmts'])
            if doc['stmts'][-1][0] == 'Match' or pred is None or doc['input'] is None:
                # the following cases are not allowed
                # (1) sequential Match
                # (2) Predicate is None
                # (3) duplicate pred
                doc['input'] = None
                doc['stmts'].append(['Match'])
                return doc
            # print(pred)
            # if str(pred) == "(((_id <= null) ∧ (_id < null)) ∧ (_id < null))":
            #     print(pred)
            output = []  # ((start_date.$date < 2023-08-25T00:00:00.000Z) ∧ (end_date.$date > 2023-08-25T00:00:00.000Z))
            for record in doc['input']:
                pred_value = pred(record)
                if pred_value is None:
                    # print("---------------MATCH ERROR---------------")
                    doc['input'] = None
                    doc['stmts'].append(['Match'])
                    return doc
                if pred_value == True:
                    output.append(record)
            # print(str(pred), len(output))
            doc['input'] = output
            doc['stmts'].append(['Match', str(pred)])
            return doc

        self.eval_children = _f


################################################################
# Predicate P ::= top | bot | P /\ P | P \/ P | not P | SizeEq(path, c) | Exists(path)
#               | P < P | P <= P | P = P | P != P | P > P | P >= P |
################################################################

class And(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('And', 2,
                         (exprtypes.DocType(), exprtypes.DocType()),
                         exprtypes.DocType())

        def _f(pred1, pred2):
            def __f(x):
                v1, v2 = pred1(x), pred2(x)
                if v1 is None or v2 is None:
                    return None
                else:
                    return v1 and v2

            return PredicateWrapper(func=__f, name=f"({pred1} ∧ {pred2})", depth=max(pred1.depth, pred2.depth) + 1)

        self.eval_children = _f
        self.commutative = True
        self.associative = True


class Or(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Or', 2,
                         (exprtypes.DocType(), exprtypes.DocType()),
                         exprtypes.DocType())

        def _f(pred1, pred2):
            def __f(x):
                v1 = pred1(x)
                v2 = pred2(x)
                if v1 is None or v2 is None:
                    return None
                else:
                    return v1 or v2

            return PredicateWrapper(func=__f, name=f"({pred1} ∨ {pred2})", depth=max(pred1.depth, pred2.depth) + 1)

        self.eval_children = _f
        self.commutative = True
        self.associative = True


class Not(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Not', 1,
                         (exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(pred):
            def __f(x):
                v = pred(x)
                if v is None:
                    return None
                else:
                    return not v

            return PredicateWrapper(func=__f, name=f"(¬ {pred})", depth=pred.depth + 1)

        self.eval_children = _f


class LogicCmp(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('LogicCmp', 3,
                         (exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path, op, constant):
            def __f(x):
                lhs = visit(x, path)
                if not (
                        isinstance(lhs, NumericType | DateType | ListType) or lhs == NULL or lhs is None or \
                        isinstance(lhs, str) and isinstance(constant, str) and op in {'=', '!='}
                ):
                    return None
                rhs = constant
                if op in {'=', '!='}:
                    if isinstance(lhs, ListType):
                        return any(l == rhs for l in lhs)
                    else:
                        return logic_ops[op](lhs, rhs)
                elif op in {'<', '>'} and (
                        (isinstance(lhs, NumericType) and isinstance(rhs, NumericType)) or \
                        (isinstance(lhs, DateType) and isinstance(rhs, DateType))
                ):
                    return False if lhs == NULL else logic_ops[op](lhs, rhs)
                elif op in {'<=', '>='} and (
                        (
                                (isinstance(lhs, NumericType) or lhs == NULL) and
                                (isinstance(rhs, NumericType) or rhs == NULL)
                        ) or (isinstance(lhs, DateType) and isinstance(rhs, DateType))
                ):
                    if lhs == NULL and rhs == NULL:
                        return True
                    elif (lhs == NULL) != (rhs == NULL):
                        return False
                    else:
                        return logic_ops[op](lhs, rhs)
                else:
                    return None

            if str.isdigit(constant):
                constant = int(constant)
                return PredicateWrapper(func=__f, name=f"({path} {op} {constant})")
            elif constant == NULL:
                return PredicateWrapper(func=__f, name=f"({path} {op} {constant})")
            elif re.match(DATE_REGEX, constant):
                name = f"({path} {op} {constant})"
                date_info = list(map(int, re.findall(NUM_REGEX, constant)))
                constant = datetime.datetime(*date_info)
                return PredicateWrapper(func=__f, name=name)
            elif op in {'=', '!='}:
                # A.name = "A"
                return PredicateWrapper(func=__f, name=f"({path} {op} {constant})")
            else:
                return PredicateWrapper(func=lambda *args, **kwargs: None, name=f"({path} {op} {constant})")

        self.eval_children = _f


class SizeEq(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('SizeEq', 2,
                         (exprtypes.StringType(), exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path, constant):

            def __f(x):
                arr = visit(x, path)
                if arr == NULL:
                    return False
                elif isinstance(arr, ListType):
                    return len(arr) == constant
                else:
                    return None

            if str.isdigit(constant):
                constant = int(constant)
                return PredicateWrapper(func=__f, name=f"(Size({path}) == {constant})")
            else:
                return PredicateWrapper(func=lambda *args, **kwargs: None, name=f"(Size({path}) == {constant})")

        self.eval_children = _f


class Exists(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Exists', 1,
                         (exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path):
            def __f(x):
                def _get(objs, x):
                    new_objs = []
                    for obj in objs:
                        if isinstance(obj, dict):
                            if x in obj:
                                new_objs.append(obj[x])
                        elif isinstance(obj, list):
                            new_objs += _get(obj, x)
                    return new_objs

                objs = [x]
                for key in path.split('.'):
                    objs = _get(objs, key)
                return len(objs) > 0

            return PredicateWrapper(func=__f, name=f"Exists({path})")

        self.eval_children = _f


class Top(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Top', 0,
                         (),
                         exprtypes.DocType())

        def _f():
            PredicateWrapper(func=lambda *args, **kwargs: True, name='⊤', depth=1)

        self.eval_children = _f


class Bot(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Bot', 0,
                         (),
                         exprtypes.DocType())

        def _f():
            return PredicateWrapper(func=lambda *args, **kwargs: False, name='⊥', depth=1)

        self.eval_children = _f


################################################################
# AddFields(Stage, PathExprs) Stage[Path |-> Expr] for (Path, Expr) in PathExprs
################################################################


class AddFields(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('AddFields', 2,
                         (exprtypes.DocType(), exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(doc, outpath_exprs):
            # print(outpath_exprs)
            # if str(outpath_exprs) == "[('difference', (b - a))]":
            #     print(outpath_exprs)
            if doc['input'] is None or doc['stmts'][-1][0] == 'AddFields' or len(outpath_exprs) > 2 or \
                    any(outpath == "_id" for outpath, _ in outpath_exprs):
                doc['input'] = None
                doc['stmts'].append(['AddFields'])
                return doc
            keys_exprs = [(outpath.split('.'), expr) for (outpath, expr) in outpath_exprs]
            for idx, record in enumerate(doc['input']):
                for (keys, expr) in keys_exprs:
                    obj = record
                    for key in keys[:-1]:
                        if key in obj:
                            obj = obj[key]
                        else:
                            obj[key] = {}
                            obj = obj[key]
                    value = expr(record)
                    if expr.depth == 1 and value is None:
                        break  # if is a singleton expr, add no field
                    elif value is None:
                        doc['input'] = None
                        doc['stmts'].append(['AddFields'])
                        return doc
                    obj[keys[-1]] = value
                doc['input'][idx] = record
            doc['stmts'].append(['AddFields', [(outpath, str(expr)) for outpath, expr in outpath_exprs]])
            # doc['stmts'][-1] == ['AddFields', [('sub_abs', '(specs.doors - specs.wheels)')]]
            # print(doc['stmts'][-1])
            return doc

        self.eval_children = _f


################################################################
# PathExprs PE ::= {(Path, Expr)} | PE ∪ PE
################################################################

class PairOutPathExpr(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('PairOutPathExpr', 2,
                         (exprtypes.StringType(), exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(path, expr):
            return [(path, expr)]

        self.eval_children = _f


class UnionOutPathExprs(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('UnionOutPathExprs', 2,
                         (exprtypes.DocType(), exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(pathexprs1, pathexprs2):
            pathexprs = pathexprs1 + pathexprs2
            assert len(pathexprs) <= NUM_FIELDS
            return pathexprs

        self.eval_children = _f


################################################################
# Expr E ::= Path | Path + Path | Path - Path | Path * Path | Path / Path | Path % Path
#          | Sum(Path) | Min(Path) | Max(Path) | Avg(Path)
#          | Abs(Path) | Ceil(Path) | Size(Path)
################################################################

class SingletonExpr(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('SingletonExpr', 1,
                         (exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path):
            def __f(x):
                return visit(x, path)

            return ExpressionWrapper(func=__f, name=path, depth=1)

        self.eval_children = _f


class ArithCmp(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('ArithCmp', 3,
                         (exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path1, op, path2):

            def __f(doc):
                lhs = visit(doc, path1)
                rhs = visit(doc, path2)
                if lhs == NULL or rhs == NULL:
                    return NULL
                elif isinstance(lhs, NumericType) and isinstance(rhs, NumericType):
                    return arith_ops[op](lhs, rhs)
                elif lhs is None and rhs is None:
                    return NULL
                else:
                    return None

            return ExpressionWrapper(func=__f, name=f"({path1} {op} {path2})", depth=2)

        self.eval_children = _f


class AggExpr(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('AggExpr', 2,
                         (exprtypes.StringType(), exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(agg, path):
            # (Agg0 String ("Sum" "Avg" "Min" "Max")) ; Aggregation wo Count
            def __f(doc):
                values = visit(doc, path)
                if isinstance(values, NumericType):
                    return values
                elif isinstance(values, str):
                    if agg == "Sum":
                        return 0
                    elif agg == "Max" or agg == "Min":
                        return values
                    else:
                        return NULL
                elif values is None:
                    if agg == "Sum":
                        return 0
                    else:
                        return NULL
                elif isinstance(values, ListType):
                    return aggregations[agg](values)
                else:
                    return None

            return ExpressionWrapper(func=__f, name=f"{agg}({path})", depth=2)

        self.eval_children = _f


class Abs(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Abs', 1,
                         (exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(expr):
            def __f(x):
                value = expr(x)
                if isinstance(value, NumericType):
                    return -value if value < 0 else value
                elif value == NULL:
                    return NULL
                else:
                    return None

            return ExpressionWrapper(func=__f, name=f"Abs({expr})", depth=expr.depth + 1)

        self.eval_children = _f


class Ceil(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Ceil', 1,
                         (exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(expr):
            def __f(x):
                value = expr(x)
                if isinstance(value, NumericType):
                    return math.ceil(value)
                elif value == NULL:
                    return NULL
                else:
                    return None

            return ExpressionWrapper(func=__f, name=f"Ceil({expr})", depth=expr.depth + 1)

        self.eval_children = _f


class Size(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Size', 1,
                         (exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(path):
            def __f(x):
                arr = visit(x, path)
                if isinstance(arr, ListType):
                    return len(arr)
                else:
                    return None

            return ExpressionWrapper(func=__f, name=f"Size({path})", depth=2)

        self.eval_children = _f


################################################################
# Unwind(Stage, Path)
################################################################

class Unwind(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Unwind', 2,
                         (exprtypes.DocType(), exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(doc, inpath):
            if doc['input'] is None:
                doc['input'] = None
                doc['stmts'].append(['Unwind'])
                return doc
            num_sequential_unwind = 0
            for stmt in doc['stmts'][::-1]:
                if stmt[0] == 'Unwind':
                    num_sequential_unwind += 1
                else:
                    break
            if num_sequential_unwind > 2:
                doc['input'] = None
                doc['stmts'].append(['Unwind'])
                return doc

            # print(doc['stmts'])
            # print(inpath)
            # must exist and its a list
            output = []
            splited_path = inpath.split('.')
            for record in doc['input']:
                values = visit(record, inpath)
                if not isinstance(values, ListType):
                    doc['input'] = None
                    doc['stmts'] = ['Unwind']
                    return doc

                for v in values:
                    new_record = deepcopy(record)
                    new_obj = visit(new_record, splited_path[:-1], split_flag=True)
                    if isinstance(new_obj, dict):
                        new_obj[splited_path[-1]] = v
                    else:
                        # {A: [{B: 1}, {B: 2}]} =visit(A.B)=> [1, 2]
                        doc['input'] = None
                        doc['stmts'] = ['Unwind']
                        return doc
                    output.append(new_record)
            doc['input'] = output
            doc['stmts'].append(['Unwind', inpath])
            return doc

        self.eval_children = _f


################################################################
# AttrAggs AA ::= {(Attr, Agg(Path))} | AA ∪ AA
################################################################

class PairOutAttrAgg(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('PairOutAttrAgg', 3,
                         (exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(outattr, agg, inpath):

            def __f(doc):
                values = [visit(record, inpath) for record in doc]
                if all(isinstance(v, NumericType) or v == NULL for v in values):
                    return aggregations[agg](values)
                else:
                    return None

            if outattr == "_id":
                agg_func = ExpressionWrapper(func=lambda *args, **kwargs: None, name=f"{agg}({inpath})", depth=2)
            else:
                agg_func = ExpressionWrapper(func=__f, name=f"{agg}({inpath})", depth=2)
            return [(outattr, agg_func)]

        self.eval_children = _f


class PairOutAttrCount(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('PairOutAttrCount', 1,
                         (exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(OutAttr):

            def __f(doc):
                return len(doc)

            if OutAttr == "_id":
                aggExpr = ExpressionWrapper(func=lambda *args, **kwargs: None, name="Count()", depth=1)
            else:
                aggExpr = ExpressionWrapper(func=__f, name="Count()", depth=1)
            return [(OutAttr, aggExpr)]

        self.eval_children = _f


class UnionOutAttrAggs(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('UnionOutAttrAggs', 2,
                         (exprtypes.DocType(), exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(attraggs1, attraggs2):
            attraggs = attraggs1 + attraggs2
            return attraggs

        self.eval_children = _f


################################################################
# Group(Stage, Path, AttrAggs)
################################################################

class Group(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Group', 3,
                         (exprtypes.DocType(), exprtypes.DocType(), exprtypes.DocType(),),
                         exprtypes.DocType())

        def _f(doc, inpaths, outattr_aggs):
            """
            Group(Stage, Paths, AttrAggs) = > {
                 "_id": {path: Stage[path] for path in Paths},
                 **{attr: agg for (attr, agg) in AttrAggs}
            }
            Paths: group keys
            """
            # inpaths == ["name"] and str(outattr_aggs) == "[('count', Count())]"
            # print(inpaths, str(outattr_aggs))
            if ('_id' in inpaths) or any(key == "_id" for key, _ in outattr_aggs) or \
                    len(set(key for key, _ in outattr_aggs)) != len(outattr_aggs) or \
                    (len(set(inpaths)) != len(inpaths) and len(inpaths) > NUM_GROUP_KEYS) or doc['input'] is None:
                # (1) has duplicate attr-agg
                # (2) has duplicate group keys
                doc['input'] = None
                doc['stmts'].append(['Group'])
                return doc
            # (3) check paths of Max/Min/Sum/Avg store numeric values
            for _, agg in outattr_aggs:
                if str(agg).split('(')[0] == "Count":
                    continue
                else:
                    value = agg(doc['input'])
                    if not (isinstance(value, NumericType) or value == NULL):
                        doc['input'] = None
                        doc['stmts'].append(['Group'])
                        return doc
            # print(doc['stmts'][-1], inpaths, outattr_aggs)
            group_values = [[visit(record, path) for path in inpaths] for record in doc['input']]
            unique_group_values = []  # inpaths==["author_id", "referenced_tweets.id"]
            group_indices = []
            for idx, group_value in enumerate(group_values):
                if any(value is None for value in group_value):
                    # grouping an invalid path which returns a None value
                    doc['input'] = None
                    doc['stmts'].append(['Group'])
                    return doc
                group_idx = -1
                for i, unique_value in enumerate(unique_group_values):
                    if unique_value == group_value:
                        group_idx = i
                        break
                if group_idx == -1:
                    group_idx = len(unique_group_values)
                    unique_group_values.append(group_value)
                    group_indices.append([])
                group_indices[group_idx].append(idx)
            # project

            output = []
            for unique_values in unique_group_values:
                if isinstance(unique_values, list):
                    if len(unique_values) == 1:
                        output.append({'_id': unique_values[0]})
                    else:
                        grouping_keys = {}
                        for inpath, value in zip(inpaths, unique_values):
                            keys = inpath.split('.')
                            obj = grouping_keys
                            for key in keys[:-1]:
                                obj[key] = {}
                                obj = obj[key]
                            obj[keys[-1]] = value
                        output.append({'_id': grouping_keys})

                else:
                    raise NotImplementedError

            for i, group_ids in enumerate(group_indices):
                for name, expr in outattr_aggs:
                    output[i][name] = expr([doc['input'][idx] for idx in group_ids])
            doc['input'] = output
            doc['stmts'].append(["Group", inpaths, [(attr, str(agg)) for attr, agg in outattr_aggs]])
            return doc

        self.eval_children = _f


################################################################
# Lookup(Stage, Path, Name, Path, Attr)
################################################################


class LookUp(InterpretedFunctionBase):
    def __init__(self):
        super().__init__('Lookup', 5,
                         (exprtypes.DocType(), exprtypes.StringType(),
                          exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
                         exprtypes.DocType())

        def _f(doc, inpath, dbname, dbpath, outattr):
            if doc['input'] is None:
                doc['input'] = None
                doc['stmts'].append(['LookUp'])
                return doc

            dbpath = dbpath[len(dbname) + 1:]
            collection_values = [visit(record, dbpath) for record in doc['databases'][dbname]]
            collection_values = [NULL if value is None else value for value in collection_values]
            output = []
            for record in doc['input']:
                value = visit(record, inpath)
                if value is None:
                    value = NULL
                if value in collection_values:
                    output.append(deepcopy(record))
                    output[-1][outattr] = []
                    for idx, collect_value in enumerate(collection_values):
                        if value == collect_value:
                            output[-1][outattr].append(deepcopy(doc['databases'][dbname][idx]))
            doc['input'] = output
            doc['stmts'].append(['LookUp', inpath, dbname, dbpath, outattr])
            return doc

        self.eval_children = _f


class DOCInstantiator(semantics_types.InstantiatorBase):
    def __init__(self):
        super().__init__("Doc")
        self.lia_instantiator = semantics_lia.LIAInstantiator()
        self.function_types = {
            'CheckEquiv': (exprtypes.DocType(),),
            'Init': (exprtypes.DocType(),),
            'Project': (exprtypes.DocType(), exprtypes.DocType(),),
            'Match': (exprtypes.DocType(), exprtypes.DocType(),),
            'SingletonInPath': (exprtypes.StringType(),),
            'UnionInPaths': (exprtypes.DocType(), exprtypes.DocType(),),
            'SingletonOutPath': (exprtypes.StringType(),),
            'UnionOutPaths': (exprtypes.DocType(), exprtypes.DocType(),),
            'And': (exprtypes.DocType(), exprtypes.DocType(),),
            'Or': (exprtypes.DocType(), exprtypes.DocType(),),
            'Not': (exprtypes.DocType(),),
            'LogicCmp': (exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
            'SizeEq': (exprtypes.StringType(), exprtypes.StringType(),),
            'Exists': (exprtypes.StringType(),),
            'Top': (),
            'Bot': (),
            'AddFields': (exprtypes.DocType(), exprtypes.DocType(),),
            'PairOutPathExpr': (exprtypes.StringType(), exprtypes.DocType(),),
            'UnionOutPathExprs': (exprtypes.DocType(), exprtypes.DocType(),),
            'SingletonExpr': (exprtypes.StringType(),),
            'ArithCmp': (exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
            'AggExpr': (exprtypes.StringType(), exprtypes.StringType(),),
            'Abs': (exprtypes.DocType(),),
            'Ceil': (exprtypes.DocType(),),
            'Size': (exprtypes.StringType(),),
            'Unwind': (exprtypes.DocType(), exprtypes.StringType(),),
            'PairOutAttrAgg': (exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
            'PairOutAttrCount': (exprtypes.StringType(),),
            'UnionOutAttrAggs': (exprtypes.DocType(), exprtypes.DocType(),),
            'Group': (exprtypes.DocType(), exprtypes.DocType(), exprtypes.DocType(),),
            'LookUp': (exprtypes.DocType(), exprtypes.StringType(),
                       exprtypes.StringType(), exprtypes.StringType(), exprtypes.StringType(),),
        }
        self.function_instances = {
            'CheckEquiv': CheckEquiv(),
            'Init': Init(),
            'Project': Project(),
            'Match': Match(),
            'SingletonInPath': SingletonInPath(),
            'UnionInPaths': UnionInPaths(),
            'SingletonOutPath': SingletonOutPath(),
            'UnionOutPaths': UnionOutPaths(),
            'And': And(),
            'Or': Or(),
            'Not': Not(),
            'LogicCmp': LogicCmp(),
            'SizeEq': SizeEq(),
            'Exists': Exists(),
            'Top': Top(),
            'Bot': Bot(),
            'AddFields': AddFields(),
            'PairOutPathExpr': PairOutPathExpr(),
            'UnionOutPathExprs': UnionOutPathExprs(),
            'SingletonExpr': SingletonExpr(),
            'ArithCmp': ArithCmp(),
            'AggExpr': AggExpr(),
            'Abs': Abs(),
            'Ceil': Ceil(),
            'Size': Size(),
            'Unwind': Unwind(),
            'PairOutAttrAgg': PairOutAttrAgg(),
            'PairOutAttrCount': PairOutAttrCount(),
            'UnionOutAttrAggs': UnionOutAttrAggs(),
            'Group': Group(),
            'LookUp': LookUp(),
        }

    def _get_canonical_function_name(self, function_name):
        return function_name

    def _do_instantiation(self, function_name, mangled_name, arg_types):
        if function_name not in self.function_types:
            return None

        assert arg_types == self.function_types[function_name]
        return self.function_instances[function_name]
