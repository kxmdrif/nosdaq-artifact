package nosdaq.synth;

import nosdaq.ast.Program;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.query.Find;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import nosdaq.grammar.TreeNode;

import java.util.*;

import static nosdaq.ast.expr.BinaryOpcode.*;
import static nosdaq.ast.expr.UnaryOpcode.*;
import static nosdaq.ast.pred.LogicOpcode.*;
import static nosdaq.utils.Helper.constructAp;

public final class Parser {
    public static Program parseProgram(TreeNode treeNode) {
        if (Objects.equals(treeNode.getSymbol().getName(), "find")) {
            Predicate predicate = parsePredicate(treeNode.getChildren().get(0));
            List<CommonExpr> list = parseCEL(treeNode.getChildren().get(1));
            return new Program(new Find(predicate, list));
        } else if (Objects.equals(treeNode.getSymbol().getName(), "aggregate")) {
            List<Stage> list = parseSL(treeNode.getChildren().get(0));
            return new Program(new Aggregate(list));
        } else {
            throw new InputMismatchException();
        }
    }

    private static List<Stage> parseSL(TreeNode treeNode) {
        if (Objects.equals(treeNode.getSymbol().getName(), "empty")) {
            return new ArrayList<>();
        }

        Stage stage = parseStage(treeNode.getChildren().get(0));
        List<Stage> SL = parseSL(treeNode.getChildren().get(1));
        SL.add(0, stage);
        return SL;
    }

    private static Stage parseStage(TreeNode treeNode) {
        if (Objects.equals(treeNode.getSymbol().getName(), "count")) {
            return new Count((ValueExpr) parseCE(treeNode.getChildren().get(0)));
        } else if (Objects.equals(treeNode.getSymbol().getName(), "limit")) {
            return new Limit((ValueExpr) parseCE(treeNode.getChildren().get(0)));
        } else if (Objects.equals(treeNode.getSymbol().getName(), "skip")) {
            return new Skip((ValueExpr) parseCE(treeNode.getChildren().get(0)));
        } else if (Objects.equals(treeNode.getSymbol().getName(), "match")) {
            return new Match(parsePredicate(treeNode.getChildren().get(0)));
        } else if (Objects.equals(treeNode.getSymbol().getName(), "unwind")) {
            return new Unwind((AccessPath) parseCE(treeNode.getChildren().get(0)));
        } else if (Objects.equals(treeNode.getSymbol().getName(), "group")) {
            List<AccessPath> accessPaths = parseAPL(treeNode.getChildren().get(0));
            List<Expression> expressions = parseEL(treeNode.getChildren().get(1));
            List<AccessPath> attributes = parseAPL(treeNode.getChildren().get(2));
            return new Group(accessPaths, expressions, attributes);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "project")) {
            List<AccessPath> accessPaths = parseAPL(treeNode.getChildren().get(0));
            List<Expression> expressions = parseEL(treeNode.getChildren().get(1));
            List<AccessPath> attributes = parseAPL(treeNode.getChildren().get(2));
            return new Project(accessPaths, expressions, attributes);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "sort")) {
            List<Expression> fields = parseEL(treeNode.getChildren().get(0));
            List<Expression> sortOrders = parseEL(treeNode.getChildren().get(1));
            return new Sort(fields, sortOrders);
        } else {
            throw new InputMismatchException();
        }
    }

    private static List<Expression> parseEL(TreeNode treeNode) {
        if (Objects.equals(treeNode.getSymbol().getName(), "empty")) {
            return new ArrayList<>();
        }

        Expression expression = parseExpression(treeNode.getChildren().get(0));
        List<Expression> EL = parseEL(treeNode.getChildren().get(1));
        EL.add(0, expression);
        return EL;
    }

    private static List<AccessPath> parseAPL(TreeNode treeNode) {
        if (Objects.equals(treeNode.getSymbol().getName(), "empty")) {
            return new ArrayList<>();
        }

        AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
        List<AccessPath> accessPaths = parseAPL(treeNode.getChildren().get(1));
        accessPaths.add(0, accessPath);
        return accessPaths;
    }

    private static Predicate parsePredicate(TreeNode treeNode) {
        if (Objects.equals(treeNode.getSymbol().getName(), "true")) {
            return new True();
        } else if (Objects.equals(treeNode.getSymbol().getName(), "in")) {
            AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
            ValueExpr valueExpr = (ValueExpr) parseCE(treeNode.getChildren().get(1));
            return new In(accessPath, valueExpr);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "exists")) {
            AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
            return new Exists(accessPath);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "allmatch")) {
            AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
            Predicate predicate = parsePredicate(treeNode.getChildren().get(1));
            return new AllMatch(accessPath, predicate);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "elemmatch")) {
            AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
            Predicate predicate = parsePredicate(treeNode.getChildren().get(1));
            return new ElemMatch(accessPath, predicate);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "not")) {
            Predicate predicate = parsePredicate(treeNode.getChildren().get(0));
            return new Not(predicate);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "and")) {
            Predicate lhs = parsePredicate(treeNode.getChildren().get(0));
            Predicate rhs = parsePredicate(treeNode.getChildren().get(1));
            return new And(lhs, rhs);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "or")) {
            Predicate lhs = parsePredicate(treeNode.getChildren().get(0));
            Predicate rhs = parsePredicate(treeNode.getChildren().get(1));
            return new Or(lhs, rhs);
        } else if (Objects.equals(treeNode.getSymbol().getName(), "lop")) {
            LogicOpcode op = parseLOC(treeNode.getChildren().get(0));
            Expression lhs = parseExpression(treeNode.getChildren().get(1));
            Expression rhs = parseExpression(treeNode.getChildren().get(2));
            return new LogicOperator(lhs, op, rhs);
        } else {
            throw new InputMismatchException();
        }
    }

    private static Expression parseExpression(TreeNode treeNode) {
        return isCommonExpr(treeNode.getSymbol().getName()) ?
                parseCE(treeNode) : parseAE(treeNode);
    }

    private static boolean isCommonExpr(String name) {
        return Objects.equals(name, "ap") ||
                Objects.equals(name, "size") || Objects.equals(name, "type") ||
                Objects.equals(name, "int") ||
                Objects.equals(name, "float") || Objects.equals(name, "str") ||
                Objects.equals(name, "isodate") || Objects.equals(name, "trueValue") ||
                Objects.equals(name, "falseValue") || Objects.equals(name, "null") ||
                Objects.equals(name, "emptyList") || Objects.equals(name, "list");
    }

    private static CommonExpr parseCE(TreeNode treeNode) {
        String symbolName = treeNode.getSymbol().getName();
        if (Objects.equals(symbolName, "ap")) {
            return constructAp(treeNode.getChildren().get(0).getSymbol().getName());
        } else if (Objects.equals(symbolName, "size")) {
            AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
            return new Size(accessPath);
        } else if (Objects.equals(symbolName, "type")) {
            AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
            return new Type(accessPath);
        } else if (Objects.equals(symbolName, "int")) {
            int num = Integer.parseInt(treeNode.getChildren().get(0).getSymbol().getName());
            return new ValueExpr(new IntLiteral(num));
        } else if (Objects.equals(symbolName, "float")) {
            double num = Float.parseFloat(treeNode.getChildren().get(0).getSymbol().getName());
            return new ValueExpr(new FloatLiteral(num));
        } else if (Objects.equals(symbolName, "str")) {
            if (treeNode.getChildren().size() == 0) {
                return new ValueExpr(new StringLiteral(""));
            } else {
                return new ValueExpr(new StringLiteral(treeNode.getChildren().get(0).getSymbol().getName()));
            }
        } else if (Objects.equals(symbolName, "isodate")) {
            return new ValueExpr(new ISODate(treeNode.getChildren().get(0).getSymbol().getName()));
        } else if (Objects.equals(symbolName, "trueValue")) {
            return new ValueExpr(new BoolLiteral(true));
        } else if (Objects.equals(symbolName, "falseValue")) {
            return new ValueExpr(new BoolLiteral(false));
        } else if (Objects.equals(symbolName, "null")) {
            return new ValueExpr(new NullLiteral());
        } else if (Objects.equals(symbolName, "emptyList")) {
            List<Value> list = new ArrayList<>();
            return new ValueExpr(new Array(list));
        } else if (Objects.equals(symbolName, "list")) {
            return parseValueList(treeNode);
        } else {
            throw new InputMismatchException();
        }
    }

    private static ValueExpr parseValueList(TreeNode treeNode) {
        List<Value> list = new ArrayList<>();

        for (TreeNode node : treeNode.getChildren()) {
            String symbolName = node.getSymbol().getName();
            if (Objects.equals(symbolName, "int")) {
                int num = Integer.parseInt(node.getChildren().get(0).getSymbol().getName());
                list.add(new IntLiteral(num));
            } else if (Objects.equals(symbolName, "float")) {
                double num = Float.parseFloat(node.getChildren().get(0).getSymbol().getName());
                list.add(new FloatLiteral(num));
            } else if (Objects.equals(symbolName, "str")) {
                if (node.getChildren().size() == 0) {
                    list.add(new StringLiteral(""));
                } else {
                    list.add(new StringLiteral(node.getChildren().get(0).getSymbol().getName()));
                }
            } else if (Objects.equals(symbolName, "isodate")) {
                list.add(new ISODate(node.getChildren().get(0).getSymbol().getName()));
            } else if (Objects.equals(symbolName, "trueValue")) {
                list.add(new BoolLiteral(true));
            } else if (Objects.equals(symbolName, "falseValue")) {
                list.add(new BoolLiteral(false));
            } else if (Objects.equals(symbolName, "null")) {
                list.add(new NullLiteral());
            }
        }

        return new ValueExpr(new Array(list));
    }

    private static List<CommonExpr> parseCEL(TreeNode treeNode) {
        if (Objects.equals(treeNode.getSymbol().getName(), "empty")) {
            return new ArrayList<>();
        }

        CommonExpr CE = parseCE(treeNode.getChildren().get(0));
        List<CommonExpr> CEL = parseCEL(treeNode.getChildren().get(1));
        CEL.add(0, CE);
        return CEL;
    }

    private static AggregateExpr parseAE(TreeNode treeNode) {
        String symbolName = treeNode.getSymbol().getName();
        if (Objects.equals(symbolName, "uop")){
            UnaryOpcode op = parseUOC(treeNode.getChildren().get(0));
            Expression expression = null;
            if (isCommonExpr(treeNode.getChildren().get(1).getSymbol().getName())) {
                expression = parseCE(treeNode.getChildren().get(1));
            } else {
                expression = parseAE(treeNode.getChildren().get(1));
            }

            return new UnaryOperator(op, expression);
        } else if (Objects.equals(symbolName, "bop")) {
            BinaryOpcode op = parseBOC(treeNode.getChildren().get(0));
            Expression lhs = null;
            if (isCommonExpr(treeNode.getChildren().get(1).getSymbol().getName())) {
                lhs = parseCE(treeNode.getChildren().get(1));
            } else {
                lhs = parseAE(treeNode.getChildren().get(1));
            }

            Expression rhs = null;
            if (isCommonExpr(treeNode.getChildren().get(1).getSymbol().getName())) {
                rhs = parseCE(treeNode.getChildren().get(1));
            } else {
                rhs = parseAE(treeNode.getChildren().get(1));
            }

            return new BinaryOperator(op, lhs, rhs);
        } else if (Objects.equals(symbolName, "filter")) {
            CommonExpr commonExpr = parseCE(treeNode.getChildren().get(0));
            Predicate predicate = parsePredicate(treeNode.getChildren().get(1));
            ValueExpr as = (ValueExpr) parseCE(treeNode.getChildren().get(2));
            ValueExpr limit = (ValueExpr) parseCE(treeNode.getChildren().get(3));

            return new Filter(commonExpr, predicate, as, limit);
        } else if (Objects.equals(symbolName, "substr")) {
            AccessPath accessPath = (AccessPath) parseCE(treeNode.getChildren().get(0));
            ValueExpr start = (ValueExpr) parseCE(treeNode.getChildren().get(1));
            ValueExpr length = (ValueExpr) parseCE(treeNode.getChildren().get(2));
            return new Substr(accessPath, start, length);
        }

        return null;
    }

    private static UnaryOpcode parseUOC(TreeNode treeNode) {
        String name = treeNode.getSymbol().getName();
        if (Objects.equals(name, "abs")) {
            return ABS;
        } else if (Objects.equals(name, "ciel")) {
            return CEIL;
        } else if (Objects.equals(name, "min")) {
            return MIN;
        } else if (Objects.equals(name, "max")) {
            return MAX;
        } else if (Objects.equals(name, "avg")) {
            return AVG;
        } else if (Objects.equals(name, "sum")) {
            return SUM;
        } else if (Objects.equals(name, "count")) {
            return COUNT;
        } else {
            throw new InputMismatchException();
        }
    }

    private static BinaryOpcode parseBOC(TreeNode treeNode) {
        String name = treeNode.getSymbol().getName();
        if (Objects.equals(name, "add")) {
            return ADD;
        } else if (Objects.equals(name, "sub")) {
            return SUB;
        } else if (Objects.equals(name, "mul")) {
            return MUL;
        } else if (Objects.equals(name, "div")) {
            return DIV;
        } else if (Objects.equals(name, "mod")) {
            return MOD;
        } else {
            throw new InputMismatchException();
        }
    }

    private static LogicOpcode parseLOC(TreeNode treeNode) {
        String name = treeNode.getSymbol().getName();
        if (Objects.equals(name, "eq")) {
            return EQ;
        } else if (Objects.equals(name, "ne")) {
            return NE;
        } else if (Objects.equals(name, "lt")) {
            return LT;
        } else if (Objects.equals(name, "lte")) {
            return LTE;
        } else if (Objects.equals(name, "gt")) {
            return GT;
        } else if (Objects.equals(name, "gte")) {
            return GTE;
        } else {
            throw new InputMismatchException();
        }
    }
}
