package nosdaq.trans;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import nosdaq.ast.Program;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.query.Find;
import nosdaq.ast.query.QueryVisitor;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import static nosdaq.trans.MongoDBTranslator.isAggregateOperator;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.computed;
import static com.mongodb.client.model.Projections.include;

public final class MongoJavaTranslator implements
        QueryVisitor<MongoIterable<Document>>, PredicateVisitor<Bson>, ExpressionVisitor<Bson>, ValueVisitor<Object>, StageVisitor<Bson> {
    private final MongoCollection<Document> collection;

    public MongoJavaTranslator(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public MongoIterable<Document> translate(Program program) {
        return program.query().accept(this);
    }

    @Override
    public MongoIterable<Document> visit(Find findQuery) {
        Bson query = findQuery.getPredicate().accept(this);
        List<String> inclusions = new ArrayList<>();
        List<Bson> projections = new ArrayList<>();

        boolean id_check = false;
        for (Expression e: findQuery.getExpressions()) {
            if (e.equals(new AccessPath(null, new Attribute("_id")))) {
                id_check = true;
            }
            inclusions.add(e.accept(new MongoDBTranslator()).replaceAll("\"", ""));
        }
        projections.add(include(inclusions));

        if (!id_check) {
            projections.add(Projections.excludeId());
        }

        if (query == null) {
            return collection.find().projection(Projections.fields(projections));
        }

        FindIterable<Document> result = collection.find(query).projection(Projections.fields(projections));
        return result;
    }

    @Override
    public MongoIterable<Document> visit(Aggregate aggregateQuery) {
        List<Bson> stages = new ArrayList<>();
        for (Stage s : aggregateQuery.getStages()) {
            stages.add(s.accept(this));
            //System.out.println(s.accept(this));
        }

        return collection.aggregate(stages);
    }

    @Override
    public Bson visit(LogicOperator operator) {
        Bson query;
        if (operator.getLhs() instanceof Size || operator.getLhs() instanceof Type) {
            query = new Document(operator.getLhs().accept(new AccessPathExtractor()).accept(new MongoDBTranslator()).replaceAll("\"", ""),
                    new Document(operator.getLhs().accept(new MongoDBTranslator()), operator.getRhs().accept(new ValueExtractor())));
        } else {
            switch (operator.getOp()) {
                case EQ:
                    query = Filters.eq(operator.getLhs().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                            operator.getRhs().accept(new ValueExtractor()));

                    break;
                case NE:
                    query = Filters.ne(operator.getLhs().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                            operator.getRhs().accept(new ValueExtractor()));

                    break;
                case GT:
                    query = Filters.gt(operator.getLhs().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                            operator.getRhs().accept(new ValueExtractor()));

                    break;
                case GTE:
                    query = Filters.gte(operator.getLhs().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                            operator.getRhs().accept(new ValueExtractor()));

                    break;
                case LT:
                    query = Filters.lt(operator.getLhs().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                            operator.getRhs().accept(new ValueExtractor()));

                    break;
                case LTE:
                    query = Filters.lte(operator.getLhs().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                            operator.getRhs().accept(new ValueExtractor()));

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + operator.getOp());
            }
        }

        return query;
    }

    @Override
    public Bson visit(True t) {
        // Keep it for now, we check if it is null in Find()
        return Filters.exists("_id");
    }

    @Override
    public Bson visit(Not not) {
        if (not.getPredicate() instanceof And || not.getPredicate() instanceof Or) {
            return new Document("$not", not.getPredicate().accept(this));
        }
        else {
            return Filters.not(not.getPredicate().accept(this));
        }
    }

    @Override
    public Bson visit(Or or) {
        return Filters.or(List.of(or.getLeft().accept(this), or.getRight().accept(this)));
    }

    @Override
    public Bson visit(And and) {
        return Filters.and(List.of(and.getLeft().accept(this), and.getRight().accept(this)));
    }

    @Override
    public Bson visit(In in) {
        return Filters.in(in.getField().accept(new MongoDBTranslator()), in.getArray().accept(this));
    }

    @Override
    public Bson visit(AllMatch allMatch) {
        return Filters.all(allMatch.getAccessPath().accept(new MongoDBTranslator()).replaceAll("\"", ""), allMatch.getPredicate().accept(this));
    }

    @Override
    public Bson visit(ElemMatch elemMatch) {
        return Filters.elemMatch(elemMatch.getAccessPath().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                elemMatch.getPredicate().accept(this));
    }

    @Override
    public Bson visit(Exists exists) {
        return Filters.exists(exists.getAccessPath().accept(new MongoDBTranslator()).replaceAll("\"", ""));
    }

    @Override
    public Bson visit(SizeIs sizeIs) {
        return Filters.size(sizeIs.getAccessPath().accept(new MongoDBTranslator()).replaceAll("\"", ""),
                (int) sizeIs.getSize().accept(new ValueExtractor()));
    }

    @Override
    public Bson visit(Count count) {
        return count(count.getValueExpr().accept(new ValueExtractor()).toString());
    }

    @Override
    public Bson visit(Match match) {
        return match(match.getPredicate().accept(this));
    }

    @Override
    public Bson visit(Project project) {
        isAggregateOperator = true;
        if (project.getExpressions().size() != project.getNewFields().size()) {
            throw new InputMismatchException("In $Project stage, expressions size != newFields size.");
        }

        boolean id_check = false;
        for (Expression e: project.getAccessPaths()) {
            if (e.equals(new AccessPath(null, new Attribute("_id")))) {
                id_check = true;
            }
        }

        List<Bson> projections = new ArrayList<>();
        for (AccessPath ap : project.getAccessPaths()) {
            projections.add(include(ap.accept(new MongoDBTranslator()).replaceAll("\"", "")));
        }

        for (int i = 0; i < project.getExpressions().size(); ++i) {
            isAggregateOperator = true; // set it to true again because previous translation for Binary Exps will set it to false
            projections.add(computed(project.getNewFields().get(i).accept(new MongoDBTranslator()).replaceAll("\"", ""), project.getExpressions().get(i).accept(new ValueExtractor())));
        }
        isAggregateOperator = false;
        if (!id_check) {
            projections.add(Projections.excludeId());
        }
        return project(Projections.fields(projections));
    }

    @Override
    public Bson visit(Group group) {
        isAggregateOperator = true;
        if (group.getExpressions().size() != group.getNewFields().size()) {
            throw new InputMismatchException("In $Group stage, expressions size != newFields size");
        }

        Document document;
        // Empty groupKey causes MongoCommandException. Set _id as null (or any constant value) so
        // the $group stage returns a single document that aggregates values across all of the input documents.
        if (group.getGroupKey().isEmpty()) {
            document = new Document("_id", null);
        } else if (group.getGroupKey().size() == 1) {
            document = new Document("_id", "$" + group.getGroupKey().get(0).accept(new MongoDBTranslator()).replaceAll("\"", ""));
        } else {
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
            document = new Document("_id", multiIdMap);
        }

        for (int i = 0; i < group.getExpressions().size(); ++i) {
            isAggregateOperator = true; // set it to true again because previous translation for Binary Exps will set it to false
            document.append(group.getNewFields().get(i).accept(new MongoDBTranslator()).replaceAll("\"", ""),
                    group.getExpressions().get(i).accept(new ValueExtractor()));
        }
        isAggregateOperator = false;
        return new Document("$group", document);
    }

    @Override
    public Bson visit(Unwind unwind) {
        return unwind("$" + unwind.getAccessPath().accept(new MongoDBTranslator()).replaceAll("\"", ""));
    }

    @Override
    public Bson visit(Lookup lookup) {
        String from = (String) lookup.getForeignCollection().getValue().accept(this);
        String localField = lookup.getLocalField().accept(new MongoDBTranslator()).replaceAll("\"", "");
        String foreignField = lookup.getForeignField().accept(new MongoDBTranslator()).replaceAll("\"", "");
        String as = lookup.getAs().accept(new MongoDBTranslator()).replaceAll("\"", "");
        return lookup(from, localField, foreignField, as);
    }

    @Override
    public Bson visit(Sort sort) {
        if (sort.getFields().size() != sort.getSortOrder().size()) {
            throw new  InputMismatchException("In $Sort stage, fields size != sortOrder size.");
        }

        List<Bson> sorts = new ArrayList<>();
        for (int i = 0; i < sort.getFields().size(); ++i) {
            if (sort.getSortOrder().get(i).accept(new ValueExtractor()).equals(1)) {
                sorts.add(sort(Sorts.ascending(sort.getFields().get(i).accept(new MongoDBTranslator()).replaceAll("\"", ""))));
            } else if (sort.getSortOrder().get(i).accept(new ValueExtractor()).equals(-1)){
                sorts.add(sort(Sorts.descending(sort.getFields().get(i).accept(new MongoDBTranslator()).replaceAll("\"", ""))));
            } else {
                throw new RuntimeException();
            }
        }

        return Sorts.orderBy(sorts);
    }

    @Override
    public Bson visit(Skip skip) {
        try {
            return skip((int) skip.getValueExpr().getValue().accept(this));
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    @Override
    public Bson visit(Limit limit) {
        try {
            return limit((int) limit.getValueExpr().accept(new ValueExtractor()));
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    @Override
    public Bson visit(Attribute attribute) {
        return null;
    }

    @Override
    public Bson visit(AccessPath accessPath) {
        return null;
    }

    @Override
    public Bson visit(UnaryOperator operator) {
        switch (operator.getOp()) {
            case ABS:
                return new Document("$abs", operator.getOperand().accept(new ValueExtractor()));
            case CEIL:
                return new Document("$ceil", operator.getOperand().accept(new ValueExtractor()));
            case MIN:
                return new Document("$min", operator.getOperand().accept(new ValueExtractor()));
            case MAX:
                return new Document("$max", operator.getOperand().accept(new ValueExtractor()));
            case AVG:
                return new Document("$avg", operator.getOperand().accept(new ValueExtractor()));
            case SUM:
                return new Document("$sum", operator.getOperand().accept(new ValueExtractor()));
            case COUNT:
                return new Document("$count", operator.getOperand().accept(new ValueExtractor()));
            default:
                throw new RuntimeException("Unknown operator");
        }
    }

    @Override
    public Bson visit(BinaryOperator operator) {
        isAggregateOperator = true;
        switch (operator.getOp()) {
            case ADD:
                Document addDoc = new Document("$add", List.of(operator.getLhs().accept(new ValueExtractor()), operator.getRhs().accept(new ValueExtractor())));
                isAggregateOperator = false;
                return addDoc;
            case SUB:
                Document subDoc = new Document("$subtract", List.of(operator.getLhs().accept(new ValueExtractor()), operator.getRhs().accept(new ValueExtractor())));
                isAggregateOperator = false;
                return subDoc;
            case MUL:
                Document mulDoc = new Document("$multiply", List.of(operator.getLhs().accept(new ValueExtractor()), operator.getRhs().accept(new ValueExtractor())));
                isAggregateOperator = false;
                return mulDoc;
            case DIV:
                Document divDoc = new Document("$divide", List.of(operator.getLhs().accept(new ValueExtractor()), operator.getRhs().accept(new ValueExtractor())));
                isAggregateOperator = false;
                return divDoc;
            case MOD:
                Document modDoc = new Document("$mod", List.of(operator.getLhs().accept(new ValueExtractor()), operator.getRhs().accept(new ValueExtractor())));
                isAggregateOperator = false;
                return modDoc;
            default:
                throw new RuntimeException("Unknown operator");
        }
    }

    @Override
    public Bson visit(Size size) {
        return new Document("$size", size.getAccessPath().accept(new ValueExtractor()));
    }

    @Override
    public Bson visit(Type type) {
        return new Document("$type", type.getAccessPath().accept(new MongoDBTranslator()));
    }

    @Override
    public Bson visit(Substr substr) {
        Document doc = new Document("$substr", List.of(substr.getAccessPath().accept(new ValueExtractor()),
                substr.getStart().accept(new ValueExtractor()), substr.getLength().accept(new ValueExtractor())));
        return doc;
    }

    @Override
    public Bson visit(Filter filter) {
        return null;
    }

    @Override
    public Bson visit(ValueExpr valueExpr) {
        return null;
    }

    @Override
    public Object visit(IntLiteral intLiteral) {
        return intLiteral.getValue();
    }

    @Override
    public Object visit(FloatLiteral floatLiteral) {
        return floatLiteral.getValue();
    }

    @Override
    public Object visit(StringLiteral stringValue) {
        return stringValue.getValue();
    }

    @Override
    public Object visit(BoolLiteral boolValue) {
        return boolValue.getValue();
    }

    @Override
    public Object visit(NullLiteral n) {
        return null;
    }

    @Override
    public Object visit(ISODate isoDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        // Need to set the timezone as UTC,
        // otherwise it is set to the default value which is usually the timezone of the machine is in (e.g. PST)
        // but the data in mongodb is still UTC and this will cause mismatch
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (isoDate.getISODate().equals("")) {
            Date date = new Date();
            String dateString = format.format(date);
            date = format.parse(dateString);
            return date;
        }
        try {
            return format.parse(isoDate.getISODate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object visit(Array array) {
        return array.getElements();
    }
}
