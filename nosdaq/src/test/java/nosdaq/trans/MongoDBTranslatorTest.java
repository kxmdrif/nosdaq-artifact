package nosdaq.trans;

import nosdaq.ast.Program;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.query.Find;
import nosdaq.ast.query.Query;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nosdaq.trans.MongoDBTranslator.translate;

class MongoDBTranslatorTest {
    // Benchmark #1
    private Program buildProg1() {
        Predicate predicate = new LogicOperator(new Size(new AccessPath(null, new Attribute("type"))), LogicOpcode.EQ, new ValueExpr(new IntLiteral(2)));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // Benchmark #2
    private Program buildProg2() {
        Predicate predicate1 = new LogicOperator(new AccessPath(new AccessPath(null, new Attribute("cities")), new Attribute("name")),
                                                 LogicOpcode.EQ,
                                                 new ValueExpr(new NullLiteral()));
        Predicate predicate2 = new Not(new Exists(new AccessPath(new AccessPath(null, new Attribute("cities")), new Attribute("name"))));
        Predicate predicate = new Or(predicate1, predicate2);
        List<CommonExpr> list = new ArrayList<>();
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // Benchmark #3
    private Program buildProg3() {
        Predicate predicate1 = new LogicOperator(new AccessPath(null, new Attribute("created_at")),
                                                 LogicOpcode.GTE,
                                                 new ValueExpr(new ISODate("2010-04-29T00:00:00.000Z")));
        Predicate predicate2 = new LogicOperator(new AccessPath(null, new Attribute("created_at")),
                                                 LogicOpcode.LT,
                                                 new ValueExpr(new ISODate("2010-05-01T00:00:00.000Z")));
        Predicate predicate = new And(predicate1, predicate2);
        List<CommonExpr> list = new ArrayList<>();
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // Benchmark #9
    private Program buildProg4() {
        Predicate predicate1 = new Exists(new AccessPath(null, new Attribute("pictures")));
        Predicate predicate2 = new LogicOperator(new AccessPath(null, new Attribute("pictures")),
                LogicOpcode.NE,
                new ValueExpr(new Array(new ArrayList<>())));
        Predicate predicate = new And(predicate1, predicate2);
        List<CommonExpr> list = new ArrayList<>();
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // Benchmark #5
    private Program buildProg5() {
        List<Stage> stageList = new ArrayList<>();
        Stage match = new Match(new LogicOperator(new AccessPath(null, new Attribute("orderDate")),
                LogicOpcode.LT,
                new ValueExpr(new ISODate("2021-02-25T10:03:46.000Z"))));
        stageList.add(match);
        List<AccessPath> groupKeys = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("userid"))));
        List<Expression> expressions = new ArrayList<>(Arrays.asList(new UnaryOperator(UnaryOpcode.SUM, new ValueExpr(new IntLiteral(1)))));
        List<AccessPath> newFileds = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("clicks"))));
        Stage group = new Group(groupKeys, expressions, newFileds);
        stageList.add(group);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // Benchmark #11
    private Program buildProg6() {
        AccessPath ap = new AccessPath(null, new Attribute("awards"));
        Predicate predicate1 = new LogicOperator(new AccessPath(null, new Attribute("award")),
                LogicOpcode.EQ, new ValueExpr(new StringLiteral("National Medal")));
        Predicate predicate2 = new LogicOperator(new AccessPath(null, new Attribute("year")),
                LogicOpcode.EQ, new ValueExpr(new IntLiteral(1975)));
        Predicate elemMatchPredicate = new And(predicate1, predicate2);
        Predicate predicate = new ElemMatch(ap, elemMatchPredicate);
        List<CommonExpr> list = new ArrayList<>();
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // Benchmark #32 (modified the projection)
    private Program buildProg7() {
        Predicate predicate = new LogicOperator(new AccessPath(new AccessPath(null, new Attribute("shapes")), new Attribute("color")),
                LogicOpcode.EQ,
                new ValueExpr(new StringLiteral("red")));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("shapes"));
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // benchmark #6
    private Program buildProg8() {
        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("_id")),
                new AccessPath(null, new Attribute("name")),
                new AccessPath(null, new Attribute("zipcode"))));
        List<Expression> expressions = new ArrayList<>(Arrays.asList(new Size(new AccessPath(null, new Attribute("name")))));
        List<AccessPath> newFields = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("size_of_name"))));
        Stage project = new Project(accessPaths, expressions, newFields);
        stageList.add(project);
        Stage match = new Match(new LogicOperator(new AccessPath(null, new Attribute("size_of_name")),
                LogicOpcode.GT,
                new ValueExpr(new IntLiteral(1))));
        stageList.add(match);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // benchmark #8
    private Program buildProg9() {
        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("item")));
        Query query = new Find(new True(), list);
        return new Program(query);
    }

    // benchmark #19
    private Program buildProg10() {
        AccessPath ap = new AccessPath(null, new Attribute("unusual"));
        Predicate predicate1 = new LogicOperator(new AccessPath(null, new Attribute("defindex")),
                LogicOpcode.EQ, new ValueExpr(new IntLiteral(363)));

        Predicate predicate2 = new In(new AccessPath(null, new Attribute("_particleEffect")),
                new ValueExpr(new Array(Arrays.asList(new IntLiteral(6), new IntLiteral(20)))));
        Predicate elemMatchPredicate = new And(predicate1, predicate2);
        Predicate predicate = new ElemMatch(ap, elemMatchPredicate);
        Query query = new Find(predicate, new ArrayList<>());
        return new Program(query);
    }

    private Program buildProg11() {
        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("_id")),
                new AccessPath(null, new Attribute("item"))));
        List<Expression> expressions = new ArrayList<>(Arrays.asList(new Substr(new AccessPath(null, new Attribute("quarter")),
                new ValueExpr(new IntLiteral(0)),
                new ValueExpr(new IntLiteral(2))), new Substr(new AccessPath(null, new Attribute("quarter")),
                new ValueExpr(new IntLiteral(2)),
                new ValueExpr(new IntLiteral(-1)))));
        List<AccessPath> newFields = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("yearSubstring"))
                , new AccessPath(null, new Attribute("quarterSubstring"))));
        Stage project = new Project(accessPaths, expressions, newFields);
        stageList.add(project);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // Benchmark #43
    private Program buildProgBm43() {
        Predicate predicate = new Not(new LogicOperator(new Type(new AccessPath(null, new Attribute("a"))),
                LogicOpcode.EQ,
                new ValueExpr(new StringLiteral("array"))));
        List<CommonExpr> list = new ArrayList<>();
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // Benchmark #43' (modified for testing purpose)
    private Program buildProgBm43Prime() {
        Predicate predicate = new LogicOperator(new Type(new AccessPath(null, new Attribute("a"))),
                LogicOpcode.EQ,
                new ValueExpr(new StringLiteral("array")));
        List<CommonExpr> list = new ArrayList<>();
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    // Benchmark #12
    private Program buildProgramBm12() {
        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();
        Expression subExpression = new BinaryOperator(BinaryOpcode.SUB,
                new AccessPath(null, new Attribute("a")),
                new AccessPath(null, new Attribute("b")));
        expressions.add(subExpression);
        List<AccessPath> newFields = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("difference"))));
        Stage projectStage = new Project(accessPaths, expressions, newFields);
        List<Expression> fields = new ArrayList<>();
        fields.add(new Attribute("difference"));
        List<Expression> sortOrder = new ArrayList<>();
        sortOrder.add(new ValueExpr(new IntLiteral(-1)));
        Stage sortStage = new Sort(fields, sortOrder);
        stageList.add(projectStage);
        stageList.add(sortStage);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // Benchmark #17
    private Program buildProgramBm17() {
        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("author")),
                new AccessPath(null, new Attribute("title")),
                new AccessPath(null, new Attribute("tags"))));
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();
        Stage projectStage = new Project(accessPaths, expressions, newFields);
        Stage unwindStage = new Unwind(new AccessPath(null, new Attribute("tags")));
        stageList.add(projectStage);
        stageList.add(unwindStage);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // Benchmark #32 (Testing using aggregate)
    private Program buildProgramBm32() {
        List<Stage> stageList = new ArrayList<>();
        Predicate matchPredicate = new LogicOperator(new AccessPath(new AccessPath(null, new Attribute("shapes")), new Attribute("color")),
                LogicOpcode.EQ,
                new ValueExpr(new StringLiteral("red")));
        Stage match = new Match(matchPredicate);
        Stage unwind = new Unwind(new AccessPath(null, new Attribute("shapes")));
        stageList.add(match);
        stageList.add(unwind);
        stageList.add(match);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // Benchmark #21
    private Program buildProgramBm21() {
        List<Stage> stageList = new ArrayList<>();
        Stage skip = new Skip(new ValueExpr(new IntLiteral(4)));
        Stage limit = new Limit(new ValueExpr(new IntLiteral(3)));
        stageList.add(skip);
        stageList.add(limit);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // Filter example from MongoDB document
    private Program buildProgramFilter() {
        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>();
        CommonExpr input = new Attribute("items");
        Predicate condition = new LogicOperator(new AccessPath(new AccessPath(null, new Attribute("item")), new Attribute("price")),
                LogicOpcode.GTE,
                new ValueExpr(new IntLiteral(100)));
        ValueExpr as = new ValueExpr(new StringLiteral("item"));
        Expression filter = new Filter(input, condition, as, null);
        List<Expression> expressions = new ArrayList<>();
        expressions.add(filter);
        List<AccessPath> newFields = new ArrayList<>();
        newFields.add(new AccessPath(null, new Attribute("items")));
        Stage projectStage = new Project(accessPaths, expressions, newFields);
        stageList.add(projectStage);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    // Filter example from MongoDB document
    private Program buildProgramFilter2() {
        List<Stage> stageList = new ArrayList<>();
        List<Value> elements = new ArrayList<>(Arrays.asList(new IntLiteral(1), new StringLiteral("a"),
                new IntLiteral(2), new NullLiteral(), new FloatLiteral(3.1), new StringLiteral("5")));
        CommonExpr input = new ValueExpr(new Array(elements));
        Predicate predicate1 = new LogicOperator(new AccessPath(null, new Attribute("num")),
                LogicOpcode.GTE,
                new ValueExpr(new FloatLiteral(-10.1)));
        Predicate predicate2 = new LogicOperator(new AccessPath(null, new Attribute("num")),
                LogicOpcode.LTE,
                new ValueExpr(new FloatLiteral(10.1)));
        Predicate condition = new And(predicate1, predicate2);
        ValueExpr as = new ValueExpr(new StringLiteral("num"));
        ValueExpr limit = new ValueExpr(new IntLiteral(1));
        Expression filter = new Filter(input, condition, as, limit);

        List<AccessPath> accessPaths = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();
        expressions.add(filter);
        List<AccessPath> newFields = new ArrayList<>();
        newFields.add(new AccessPath(null, new Attribute("output")));
        Stage projectStage = new Project(accessPaths, expressions, newFields);
        stageList.add(projectStage);
        Query query = new Aggregate(stageList);
        return new Program(query);
    }

    @Test
    void testVisitFind1() {
        Assertions.assertEquals("db.collection.find({ type: {$size: 2} }, { name: 1, _id: 0 })".replaceAll("\\s", ""),
                translate(buildProg1()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitFind2() {
        Assertions.assertEquals(("db.collection.find({$or: [" +
                "{\"cities.name\": null}, " +
                "{\"cities.name\": {$not: {$exists: true}}}" +
                "]})").replaceAll("\\s", ""), translate(buildProg2()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitFind3() {
        Assertions.assertEquals(("db.collection.find({\n" +
                "    $and : [ {" +
                "         \"created_at\": {" +
                "        $gte: ISODate(\"2010-04-29T00:00:00.000Z\")" +
                "    }}," +
                "{\"created_at\": {" +
                "         $lt: ISODate(\"2010-05-01T00:00:00.000Z\")" +
                "    }" +
                "    }]" +
                "})").replaceAll("\\s", ""), translate(buildProg3()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitFind4() {
        Assertions.assertEquals((" db.collection.find({" +
                "    $and : [ { \"pictures\": {" +
                "        $exists: true" +
                "    }}, { \"pictures\": {" +
                "         $ne: []" +
                "    }}]" +
                "})").replaceAll("\\s", ""), translate(buildProg4()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitFind6() {
        Assertions.assertEquals((" db.collection.find({ " +
                        "   \"awards\": {$elemMatch: {$and: [{\"award\":\"National Medal\"},{\"year\":1975}]}}})")
                        .replaceAll("\\s", ""),
                translate(buildProg6()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitFind7() {
        Assertions.assertEquals((" db.collection.find( {\"shapes.color\":\"red\"}, {shapes: 1, _id:0} )").
                        replaceAll("\\s", ""),
                translate(buildProg7()).replaceAll("\\s",""));
    }

    @Test
    void testVisitFind8() {
        Assertions.assertEquals((" db.collection.find({a: {$not: {$type: \"array\"}}})").replaceAll("\\s", ""),
                translate(buildProgBm43()).replaceAll("\\s", ""));

    }

    @Test
    void testVisitFind9() {
        Assertions.assertEquals((" db.collection.find({a: {$type:\"array\"}})").replaceAll("\\s", ""),
                translate(buildProgBm43Prime()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitFind10() {
        Assertions.assertEquals(("db.collection.find({}, { \"item\": 1, _id: 0 })").replaceAll("\\s", ""),
                translate(buildProg9()).replaceAll("\\s", ""));

    }

    @Test
    void testVisitFind11() {
        Assertions.assertEquals(("db.collection.find({\"unusual\": {$elemMatch: {$and: [{\"defindex\": 363}, {\"_particleEffect\": {$in: [6,20]}}]}}})").replaceAll("\\s", ""),
                translate(buildProg10()).replaceAll("\\s", ""));

    }

    @Test
    void testVisitAggregate1() {
        Assertions.assertEquals(("db.collection.aggregate(" +
                "[{" +
                "  $match: {" +
                "    \"orderDate\": { $lt: ISODate(\"2021-02-25T10:03:46.000Z\") }" +
                "  }" +
                "}," +
                "{" +
                "  $group: {" +
                "    \"_id\": \"$userid\"," +
                "    \"clicks\": { $sum: 1 }" +
                "  }" +
                "}])").replaceAll("\\s", ""), translate(buildProg5()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregate2() {
        Assertions.assertEquals(("db.collection.aggregate([" +
                "  {$project: {\"_id\":1, \"name\":1, \"zipcode\":1, \"size_of_name\": {$size: \"$name\"},}}," +
                "  {$match: {\"size_of_name\": {$gt: 1}}}" +
                "])").replaceAll("\\s", ""), translate(buildProg8()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregate3() {
        System.out.println(translate(buildProgramBm12()));
        Assertions.assertEquals(("db.collection.aggregate([" +
                        "{$project: {\"difference\": {$subtract:[\"$a\", \"$b\"]},}}, " +
                        "{$sort:{difference:-1}}])").replaceAll("\\s", ""),
                translate(buildProgramBm12()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregate4() {
        Assertions.assertEquals(("db.collection.aggregate([{ $project : {\"author\" : 1 ,\"title\" : 1 ,\"tags\" : 1}},{ $unwind : \"$tags\" }])")
                        .replaceAll("\\s", ""),
                translate(buildProgramBm17()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregate5() {
        Assertions.assertEquals(("db.collection.aggregate(" +
                        "   [" +
                        "     {" +
                        "       $project:" +
                        "          {" +
                        "            \"_id\": 1," +
                        "            \"item\": 1," +
                        "            \"yearSubstring\": { $substr: [ \"$quarter\", 0, 2 ] }," +
                        "            \"quarterSubstring\": { $substr: [ \"$quarter\", 2, -1 ] }," +
                        "          }" +
                        "      }" +
                        "   ]" +
                        ")")
                        .replaceAll("\\s", ""),
                translate(buildProg11()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregateBm32() {
        Assertions.assertEquals(("db.collection.aggregate([" +
                        "{ $match : {\"shapes.color\": \"red\"}}, " +
                        "{ $unwind : \"$shapes\" }, " +
                        "{ $match : {\"shapes.color\": \"red\" }}])").replaceAll("\\s", ""),
                translate(buildProgramBm32()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregateBm21() {
        Assertions.assertEquals((" db.collection.aggregate([{$skip:4}, {$limit:3}])")
                        .replaceAll("\\s", ""),
                translate(buildProgramBm21()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregateFilter() {
        Assertions.assertEquals((" db.collection.aggregate( [{$project: " +
                        "{\"items\": {$filter: {input: \"$items\",as: \"item\",cond: { $gte: [ \"$$item.price\", 100 ] }}}, }}] )")
                .replaceAll("\\s", ""),
                translate(buildProgramFilter()).replaceAll("\\s", ""));
    }

    @Test
    void testVisitAggregateFilter2() {
        Assertions.assertEquals(("db.collection.aggregate( [ {$project: " +
                        "{ \"output\": " +
                        "  {$filter: {" +
                        "     input: [ 1, \"a\", 2, null, 3.1, \"5\" ], " +
                        "     as: \"num\"," +
                        "     cond: { $and:[" +
                        "        { $gte: [ \"$$num\", -10.1 ] }," +
                        "        { $lte: [ \"$$num\", 10.1 ] }" +
                        "     ] }, " +
                        "     limit: 1" +
                        "  }}," +
                        "}" +
                        "}])").replaceAll("\\s", ""),
                translate(buildProgramFilter2()).replaceAll("\\s", "")
        );
    }
}
