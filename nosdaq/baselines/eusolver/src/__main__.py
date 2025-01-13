# -*- coding:utf-8 -*-
import sys

sys.path.append("../thirdparty/libeusolver/build")

import os
import json
import time
import math
from parsers.parser import stripComments, sexpParser
from semantics.semantic_doc import PREDICATE_DEPTH
from benchmarks_doc import synthesize
import itertools
from multiprocessing import (
    Process,
    Queue,
    Manager,
)

AtomicPreds = ["(LogicCmp InPath LogicOp Constant)", "(SizeEq InPath Constant)", "(Exists InPath)"]
TIMEOUT = 300
MAIN_DIR = os.path.dirname(os.path.dirname(__file__))
BENCHMARK_DIR = os.path.join(MAIN_DIR, "benchmarks", "mongodb")
BENCHMARK_COUNT = 110

def eusolver_synthesize(script):
    # print(script)
    bm = stripComments(script)
    bmExpr = sexpParser.parseString(bm, parseAll=True).asList()[0]
    out = synthesize(bmExpr)
    if out is None:
        return "No Program"
    else:
        return out[0]


def frozenize(obj):
    return json.dumps(obj).replace(' ', '')
    # return "<DOC>" + json.dumps(obj).replace(' ', '') + "</DOC>"


def divide(lst, partitions):
    chunk_size = math.ceil(len(lst) / partitions)
    for i in range(0, len(lst), chunk_size):
        yield lst[i:i + chunk_size]


def get_paths(doc, prefix=""):
    if isinstance(doc, dict):
        paths = set()
        for k, v in doc.items():
            paths |= get_paths(v, prefix=f"{prefix}.{k}" if len(prefix) != 0 else k)
        return paths
    elif isinstance(doc, list):
        paths = set()
        if len(prefix) > 0:
            paths.add(prefix)
        for d in doc:
            paths |= get_paths(d, prefix)
        return paths
    else:
        return {prefix}


def get_attrs(doc):
    if isinstance(doc, dict):
        attrs = set()
        for k, v in doc.items():
            if isinstance(v, dict):
                attrs.add(k)
            else:
                attrs |= get_attrs(v)
        return attrs
    elif isinstance(doc, list):
        attrs = set()
        for d in doc:
            attrs |= get_attrs(d)
        return attrs
    else:
        return set()


def get_constants(constants):
    for idx, const in enumerate(constants):
        if str.isdigit(const):
            constants[idx] = int(const)
    return constants


def enumerate_predicates(max_depth=PREDICATE_DEPTH):
    preds = [("Pred0", 1)]
    for depth in range(2, 1 + max_depth):
        new_preds = []
        # and or
        for (lhs, depth1), (rhs, depth2) in itertools.product(preds, preds):
            dst_depth = max(depth1, depth2) + 1
            if dst_depth <= max_depth:
                new_preds += [(f"(And {lhs} {rhs})", dst_depth), (f"(Or {lhs} {rhs})", dst_depth)]
        # not
        for pred, d in preds:
            dst_depth = d + 1
            if dst_depth <= max_depth:
                new_preds.append((f"(Not {pred})", dst_depth))
        preds += new_preds
    return preds


def write(inpaths, outpaths, outattrs, dbnames, dbpaths, constants, lines):
    def list2string(arr):
        return " ".join(map(lambda x: f'"{x}"', sorted(arr, key=lambda x: len(x))))

    lines = [f"(constraint (= (Execute {frozenize(line)}) true))" for line in lines]
    lines = "\n".join(lines)
    # preds = enumerate_predicates()
    # preds = ("\n" + "\t" * 3).join(pred for pred, _ in preds)
    template = f"""
(set-logic Doc)

(synth-fun Execute ((data Doc)) Bool
    (
        (Start Bool (
            (CheckEquiv Stage)
        ))
        (Stage Doc (
            (Init data)
            (Project Stage InPaths)
            (Match Stage Pred)
            (Unwind Stage InPath)
            (Group Stage InPaths OutAttrAggs)
            (AddFields Stage OutPathExprs)
            (LookUp Stage InPath DBName DBPath OutAttr)
        ))

        ; InPaths
        (InPaths Doc (
            (SingletonInPath InPath)
            (UnionInPaths InPaths InPaths)
        ))
        (InPath String ({list2string(inpaths)}))
        
        ; OutPaths
        (OutPaths Doc (
            (SingletonOutPath OutPath)
            (UnionOutPaths OutPaths OutPaths)
        ))
        (OutPath String ({list2string(outpaths)}))

        ; Pred
        (Pred Doc (
            (LogicCmp InPath LogicOp Constant)
            (SizeEq InPath Constant)
            (Exists InPath)
			(And Pred Pred)
			(Or Pred Pred)
			(Not Pred)
        ))
        (LogicOp String ("<" "<=" "=" "!=" ">" ">="))
        (Constant String ({list2string(constants)}))

        ; OutPath-Expr
        (OutPathExprs Doc (
            (PairOutPathExpr OutPath Expr)
            (UnionOutPathExprs OutPathExprs OutPathExprs)
        ))
        ; OutAttr-Agg
        (OutAttrAggs Doc (
            (PairOutAttrAgg OutAttr Agg InPath)
            (PairOutAttrCount OutAttr)
            (UnionOutAttrAggs OutAttrAggs OutAttrAggs)
        ))
        ; expr
        (Expr Doc (
            (SingletonExpr InPath)
            (ArithCmp InPath ArithOp InPath)
            (AggExpr Agg InPath)
            (Size InPath)
            (Ceil Expr)
            (Abs Expr)
        ))
        (Agg String ("Sum" "Avg" "Min" "Max"))
        (ArithOp String ("+" "-" "*" "/" "%"))
        
        ; group
        (DBName String ({list2string(dbnames)}))
        (DBPath String ({list2string(dbpaths)}))
        (OutAttr String ({list2string(outattrs)}))
    )
)

; input/output
{lines}

(check-synth)
""".strip()
    return template


def execute(func, args):
    manager = Manager()
    queue = manager.Queue()

    queue.empty()

    proc = Process(target=func, args=(*args, queue))
    proc.start()

    start = time.time()
    while time.time() - start <= TIMEOUT:
        if not proc.is_alive():
            break
        time.sleep(0.1)
    else:
        proc.terminate()
        proc.join()

    try:
        prog, timecost = [queue.get() for _ in range(queue.qsize())]
    except:
        prog, timecost = None, TIMEOUT
    return prog, timecost


def generate_script(file):
    with open(file, 'r') as reader:
        data = json.load(reader)
        inpaths, outpaths, dbpaths = set(), set(), set()
        outattrs = set()
        lines = []
        dbnames = None
        constants = data['constants'] + ["null"]
        constants = ["null" if str.lower(const) == "null" else const for const in constants]
        constants = list(set(constants))
        for ioexamples in data['examples']:
            line = {
                'input': ioexamples['input'],
                'output': ioexamples['output'],
                'constants': constants,
                'databases': ioexamples.get("foreign", {}),
            }
            inpaths |= get_paths(line['input'])
            outpaths |= get_paths(line['output'])
            for out in line['output']:
                outattrs |= set(out.keys())
            if len(line['databases']) > 0:
                # db names
                new_dbnames = set(line['databases'].keys())
                if dbnames is not None:
                    assert dbnames == new_dbnames
                dbnames = new_dbnames
                # db paths
                dbpaths |= get_paths(line['databases'])

            lines.append(line)
        if dbnames is None:
            dbnames = []

        script = write(inpaths | outpaths, outpaths, outattrs, dbnames, dbpaths, constants, lines)
    return script


def process(infile, outfile, queue: Queue = None):
    script = generate_script(infile)
    os.makedirs(os.path.dirname(outfile), exist_ok=True)
    with open(outfile, 'w') as writer:
        print(script, file=writer)

    start = time.time()
    with open(outfile, 'r') as reader:
        prog = eusolver_synthesize(reader)
    timecost = round(time.time() - start, 4)

    if queue is None:
        return prog, timecost
    else:
        queue.put(prog)
        queue.put(timecost)


def run(file, parameters):
    with open(file, 'w') as writer:
        for infile, idx, outfile in parameters:
            prog, timecost = execute(func=process, args=(infile, outfile))
            # prog, timecost = process(*params)
            print(json.dumps({"index": idx, "time": timecost, "program": prog, }, ensure_ascii=False), file=writer)



if __name__ == '__main__':
    DATA_DIR = "../../../src/main/resources"
    # DATA_FILES = [file for file in os.listdir(DATA_DIR) if file.startswith("benchmark") and file.endswith(".json")]
    DATA_FILES = [f"benchmark{i}.json" for i in range(1, 1 + BENCHMARK_COUNT)]
    DATA_FILES = sorted(DATA_FILES, key=lambda x: int(x.split(os.path.sep)[0][len("benchmark"):-len(".json")]))
    DST_FILES = [os.path.join(BENCHMARK_DIR, f'{file[len("benchmark"):-len(".json")]}.sl') for file in DATA_FILES]
    INDICES = [int(file[len("benchmark"):-len(".json")]) for file in DATA_FILES]
    DATA_FILES = [os.path.join(DATA_DIR, file) for file in DATA_FILES]
    parameters = list(zip(DATA_FILES, INDICES, DST_FILES))
    OUT_FILE = os.path.join(MAIN_DIR, "result.out")

    # NUM_COUNT = mp.cpu_count()
    NUM_COUNT = 12
    if NUM_COUNT == 1:
        run(OUT_FILE, parameters=parameters)
    else:
        parameters = list(divide(parameters, NUM_COUNT))
        procs = []
        for worker_idx in range(len(parameters)):
            proc = Process(
                target=run,
                args=(
                    OUT_FILE + str(worker_idx),
                    parameters[worker_idx],
                ),
            )
            proc.start()
            procs.append(proc)

        for proc in procs:
            proc.join()

        with open(OUT_FILE, 'w') as writer:
            results = []
            for worker_idx in range(len(parameters)):
                file = OUT_FILE + str(worker_idx)
                with open(file, 'r') as reader:
                    for line in reader:
                        line = json.loads(line)
                        results.append(line)
                os.remove(file)
            for line in results:
                print(json.dumps(line, ensure_ascii=False), file=writer)
