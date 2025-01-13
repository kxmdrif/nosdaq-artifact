package nosdaq.trans;

import nosdaq.ast.Program;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.query.*;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static nosdaq.ast.pred.LogicOperator.opToMongo;
import static nosdaq.ast.expr.UnaryOperator.opToMongo;
import static nosdaq.ast.expr.BinaryOperator.opToMongo;

public final class MongoDBTranslator implements
        QueryVisitor<String>, PredicateVisitor<String>, ExpressionVisitor<String>, ValueVisitor<String>, StageVisitor<String> {

    private static boolean isFilter = false;
    public static boolean isAggregateOperator = false;

    public static String translate(Program program) {
        MongoDBTranslator translator = new MongoDBTranslator();
        try {
            return program.query().accept(translator);
        } catch (Exception e) {
            System.out.println("Exception caught: program is null");
            return "";
        }
    }

    @Override
    public String visit(Find findQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("db.collection.find(")
                .append(findQuery.getPredicate().accept(this));

        if (findQuery.getExpressions().size() == 0) {
            builder.append(")");
            return builder.toString();
        }

        builder.append(",{ ");
        boolean _id_check = false;
        for (Expression e : findQuery.getExpressions()) {
            if (e.equals(new AccessPath(null, new Attribute("_id")))) {
                _id_check = true;
            }
            builder.append(e.accept(this) + ": 1, ");
        }

        if (!_id_check) {
            builder.append("_id: 0");
        }
        builder.append("})");

        return builder.toString();
    }

    @Override
    public String visit(Aggregate aggregateQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("db.collection.aggregate([");
        builder.append(aggregateQuery.getStages().stream()
                .map(stage -> stage.accept(this))
                .collect(Collectors.joining(", ")));
        builder.append("])");
        return builder.toString();
    }

    @Override
    public String visit(LogicOperator operator) {
        StringBuilder builder = new StringBuilder();
        if (!isFilter) {
            if (operator.getLhs() instanceof Size || operator.getLhs() instanceof Type) {
                builder.append("{");
                builder.append(operator.getLhs().accept(new AccessPathExtractor()) + ": {");
                builder.append(operator.getLhs().accept(this));
                switch (operator.getOp()) {
                    case EQ:
                        builder.append(":");
                        break;
                    default:
                        builder.append(opToMongo(operator.getOp()));
                }
                builder.append(operator.getRhs().accept(this) + "}");
                builder.append("}");
            } else {
                builder.append("{" + operator.getLhs().accept(this) + ":");
                switch (operator.getOp()) {
                    case EQ:
                        builder.append(operator.getRhs().accept(this) + "}");
                        break;
                    default:
                        builder.append("{" + opToMongo(operator.getOp()) + ":");
                        builder.append(operator.getRhs().accept(this) + "}");
                        builder.append("}");
                }
            }
        } else {
            builder.append("{");
            builder.append(opToMongo(operator.getOp()) + ":");
            builder.append("[ " + operator.getLhs().accept(this) + ", ");
            builder.append(operator.getRhs().accept(this) + " ]");
            builder.append("}");
        }

        return builder.toString();
    }

    @Override
    public String visit(True t) {
        return "{}";
    }

    @Override
    public String visit(Not not) {
        StringBuilder builder = new StringBuilder();
        if (not.getPredicate() instanceof Exists) {
            builder.append("{\"" + not.getPredicate().accept((new AccessPathExtractor())) + "\": ");
            builder.append("{$not: ");
            builder.append("{$exists: true}");
            builder.append("}}");
        } else if ((not.getPredicate() instanceof LogicOperator)
                && (((LogicOperator) not.getPredicate()).getLhs() instanceof Type)) {
            builder.append("{" + ((LogicOperator) not.getPredicate()).getLhs().accept(new AccessPathExtractor()) + ":");
            builder.append("{$not: ");
            builder.append("{$type: " + ((LogicOperator) not.getPredicate()).getRhs().accept(this) + "}");
            builder.append("}}");
        }

        return builder.toString();
    }

    @Override
    public String visit(Or or) {
        StringBuilder builder = new StringBuilder();
        builder.append("{$or: [");
        builder.append(or.getLeft().accept(this) + ", ");
        builder.append(or.getRight().accept(this));
        builder.append("]}");
        return builder.toString();
    }

    @Override
    public String visit(And and) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ $and: [");
        builder.append(and.getLeft().accept(this) + ", ");
        builder.append(and.getRight().accept(this));
        builder.append("]}");
        return builder.toString();
    }

    @Override
    public String visit(In in) {
        StringBuilder builder = new StringBuilder();
        builder.append("{" + in.getField().accept(this) + ":");
        builder.append("{$in: " + in.getArray().accept(this) + "}");
        builder.append("}");
        return builder.toString();
    }

    // todo ?
    @Override
    public String visit(AllMatch allMatch) {
        return null;
    }

    @Override
    public String visit(ElemMatch elemMatch) {
        StringBuilder builder = new StringBuilder();
        builder.append("{" + elemMatch.getAccessPath().accept(this) + ":");
        builder.append("{ $elemMatch: ");
        builder.append(elemMatch.getPredicate().accept(this));
        builder.append("}}");
        return builder.toString();
    }

    @Override
    public String visit(Exists exists) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"" + exists.getAccessPath() + "\"");
        builder.append(": { $exists: true }");
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(SizeIs sizeIs) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"" + sizeIs.getAccessPath() + "\"");
        builder.append(": { $size: ").append(sizeIs.getSize().accept(this));
        builder.append("}}");
        return builder.toString();
    }

    @Override
    public String visit(Attribute attribute) {
        if (isFilter) {
            return "\"$" + attribute.getAttrName() + "\"";
        }

        return attribute.getAttrName();
    }

    // MongoJavaTranslator Use this method in MongoDBTranslator
    @Override
    public String visit(AccessPath accessPath) {
        if (isFilter) {
            return "\"$$ " + accessPath.toString() + "\"";
        }

        return "\"" + accessPath.toString() + "\"";
    }

    @Override
    public String visit(UnaryOperator operator) {
        StringBuilder builder = new StringBuilder();
        builder.append(opToMongo(operator.getOp()) + ":");
        // MongoDBTranslator cannot correctly add `$` to ap. So we handle this manually
        // cannot modify visit(AccessPath accessPath) because it is used by MongoJavaTranslator
        if (operator.getOperand() instanceof AccessPath) {
            builder.append("\"$").append(operator.getOperand().accept(this)
                    .replace("\"", "")).append("\"");
        } else if (operator.getOperand() instanceof AggregateExpr) {
            // AggregateExpr has form 'xx : xx', need add `{` `}`
            builder.append("{").append(operator.getOperand().accept(this)).append("}");
        } else {
            builder.append(operator.getOperand().accept(this));
        }
        return builder.toString();
    }

    @Override
    public String visit(BinaryOperator operator) {
        isAggregateOperator = true;
        StringBuilder builder = new StringBuilder();
        builder.append(opToMongo(operator.getOp()) + ": [");
        if (operator.getLhs() instanceof AccessPath) {
            builder.append("\"$").append(operator.getLhs().accept(this)
                    .replace("\"", "")).append("\"").append(", ");
        } if (operator.getLhs() instanceof AggregateExpr) {
            builder.append("{").append(operator.getLhs().accept(this)).append("},");
        } else {
            builder.append(operator.getLhs().accept(this) + ", ");
        }

        if (operator.getRhs() instanceof AccessPath) {
            builder.append("\"$").append(operator.getRhs().accept(this)
                    .replace("\"", "")).append("\"").append("]");
        } else if (operator.getRhs() instanceof AggregateExpr) {
            builder.append("{").append(operator.getRhs().accept(this)).append("}]");
        } else {
            builder.append(operator.getRhs().accept(this) + "]");
        }
        isAggregateOperator = false;
        return builder.toString();
    }

    @Override
    public String visit(Size size) {
        return "$size";
    }

    @Override
    public String visit(Type type) {
        return "$type";
    }

    @Override
    public String visit(Substr substr) {
        StringBuilder builder = new StringBuilder();
        builder.append("$substr" + ": [");
        builder.append("\"$" + substr.getAccessPath().accept(this).replace("\"", "") + "\", ");
        builder.append(substr.getStart().accept(this) + ", ");
        builder.append(substr.getLength().accept(this) + "]");
        return builder.toString();
    }

    @Override
    public String visit(Filter filter) {
        isFilter = true;
        StringBuilder builder = new StringBuilder();
        builder.append("$filter: {");
        builder.append("input: " + filter.getInput().accept(this) + ", ");
        if (filter.getAs() != null) {
            builder.append("as: " + filter.getAs().accept(this) + ", ");
        }

        builder.append("cond: " + filter.getCondition().accept(this));
        if (filter.getLimit() != null) {
            builder.append(", limit: " + filter.getLimit().accept(this));
        }
        builder.append("}");
        isFilter = false;
        return builder.toString();
    }

    @Override
    public String visit(ValueExpr valueExpr) {
        return valueExpr.getValue().accept(this);
    }

    @Override
    public String visit(IntLiteral intLiteral) {
        return intLiteral.toString();
    }

    @Override
    public String visit(FloatLiteral floatLiteral) {
        return floatLiteral.toString();
    }

    @Override
    public String visit(StringLiteral strValue) {
        return strValue.toString();
    }

    @Override
    public String visit(BoolLiteral boolValue) {
        return boolValue.toString();
    }

    @Override
    public String visit(NullLiteral n) {
        return n.toString();
    }

    @Override
    public String visit(ISODate isoDate) {
        return isoDate.toString();
    }

    @Override
    public String visit(Array array) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(array.getElements().stream()
                .map(value -> value.accept(this))
                .collect(Collectors.joining(",")));
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String visit(Count count) {
        StringBuilder builder = new StringBuilder();
        builder.append("{$count:");
        builder.append(count.getValueExpr().accept(this));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(Match match) {
        StringBuilder builder = new StringBuilder();
        builder.append("{$match:");
        builder.append(match.getPredicate().accept(this));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(Project project) {
        if (project.getExpressions().size() != project.getNewFields().size()) return "";

        boolean id_check = false;
        for (Expression e: project.getAccessPaths()) {
            if (e.equals(new AccessPath(null, new Attribute("_id")))) {
                id_check = true;
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("{$project: {");
        if (!id_check) {
            builder.append("\"_id\": 0,");
        }
        builder.append(project.getAccessPaths().stream()
                .map(ap -> ap.accept(this) + ":1")
                .collect(Collectors.joining(",")));

        if (!project.getAccessPaths().isEmpty() && !project.getNewFields().isEmpty()) {
            builder.append(", ");
        }

        for (int i = 0; i < project.getExpressions().size(); ++i) {
            builder.append(project.getNewFields().get(i).accept(this) + ":");
            if (project.getExpressions().get(i) instanceof Size) {
                builder.append("{" + project.getExpressions().get(i).accept(this) +
                        ":\"$" + project.getExpressions().get(i).accept(new AccessPathExtractor()) + "\"}");
            } else if (project.getExpressions().get(i) instanceof AccessPath) {
                builder.append("\"$").append(project.getExpressions().get(i)).append("\"");
            } else {
                builder.append("{" + project.getExpressions().get(i).accept(this) + "}");
            }
            builder.append(",");
        }
        builder.append("}}");
        return builder.toString();
    }

    @Override
    public String visit(Group group) {
        isAggregateOperator = true;
        if (group.getExpressions().size() != group.getNewFields().size()) return "";

        StringBuilder builder = new StringBuilder();
        builder.append("{$group: ");

        if (group.getGroupKey().isEmpty()) {
            builder.append("{\"_id\": null,");
        } else if (group.getGroupKey().size() == 1) {
            builder.append("{\"_id\": ").append("\"$")
                    .append(group.getGroupKey().get(0).accept(this).replace("\"", ""))
                    .append("\",");
        } else {
            /*
            builder.append("{\"_id\": {");
            for (AccessPath ap : group.getGroupKey()) {
                builder.append("\"").append(ap.accept(this).replace("\"", "")).append("\": ")
                        .append("\"$").append(ap.accept(this).replace("\"", "")).append("\",");
            }
            builder.deleteCharAt(builder.length() - 1).append("},");
            */
            // {_id: {"a.b": "$a.b"}} -> {_id: {"a": {"b": "$a.b"}}}
            // because group key cannot contain dot
            Map<String, Object> multiIdMap = new HashMap<>();
            for (AccessPath groupKey : group.getGroupKey()) {
                Map<String, Object> ptr = multiIdMap;
                for (int i = 0; i < groupKey.getFullPath().size() - 1; ++i) {
                    if (!ptr.containsKey(groupKey.getFullPath().get(i))) {
                        ptr.put(groupKey.getFullPath().get(i), new HashMap<>());
                    }
                    assert ptr.get(groupKey.getFullPath().get(i)) instanceof Map<?, ?>;
                    ptr = (Map<String, Object>) ptr.get(groupKey.getFullPath().get(i));
                }
                String lastAttrName = groupKey.getFullPath().get(groupKey.getFullPath().size() - 1);
                ptr.put(lastAttrName, groupKey.accept(new ValueExtractor()));
            }
            Document document = new Document(multiIdMap);
            builder.append("{\"_id\": ").append(document.toJson()).append(",");
        }


        for (int i = 0; i < group.getExpressions().size(); ++i) {
            isAggregateOperator = true; // keep same logic as MongoJavaTranslator
            builder.append(group.getNewFields().get(i).accept(this)).append(":");
            builder.append("{").append(group.getExpressions().get(i).accept(this)).append("},");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}}");
        isAggregateOperator = false;
        return builder.toString();
    }

    @Override
    public String visit(Unwind unwind) {
        StringBuilder builder = new StringBuilder();
        builder.append("{$unwind: ");
        builder.append(unwind.getAccessPath().accept(this).charAt(0) + "$"
                + unwind.getAccessPath().accept(this).substring(1));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(Lookup lookup) {
        StringBuilder builder = new StringBuilder();
        builder.append("{$lookup: {");
        builder.append("from: " + lookup.getForeignCollection().accept(this)).append(", ");
        builder.append("localField: " + lookup.getLocalField().accept(this)).append(", ");
        builder.append("foreignField: " + lookup.getForeignField().accept(this)).append(", ");
        builder.append("as: " + lookup.getAs()).append("}}");
        return builder.toString();
    }

    @Override
    public String visit(Sort sort) {
        if (sort.getFields().isEmpty() || sort.getSortOrder().isEmpty()) return "";
        if (sort.getFields().size() != sort.getSortOrder().size()) return "";

        StringBuilder builder = new StringBuilder();
        builder.append("{$sort: {");

        int size = sort.getFields().size();
        for (int i = 0; i < size - 1; ++i) {
            builder.append(sort.getFields().get(i).accept(this) + ":");
            builder.append(sort.getSortOrder().get(i).accept(this) + ", ");
        }
        builder.append(sort.getFields().get(size - 1).accept(this) + ":");
        builder.append(sort.getSortOrder().get(size - 1).accept(this));
        builder.append("}}");

        return builder.toString();
    }

    @Override
    public String visit(Skip skip) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ $skip: ");
        builder.append(skip.getValueExpr().accept(this) + "}");
        return builder.toString();
    }

    @Override
    public String visit(Limit limit) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ $limit: ");
        builder.append(limit.getValueExpr().accept(this) + "}");
        return builder.toString();
    }
}
