package verify;

import com.mongodb.client.*;
import nosdaq.ast.Program;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.pred.Predicate;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.query.Find;
import nosdaq.ast.query.Query;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import nosdaq.trans.MongoJavaTranslator;

//import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class VerifyTest {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("nosdaq");

    private static String toJson(MongoCursor<Document> cursor) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            builder.append(doc.toJson() + ",");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("]");
        return builder.toString();
    }

    @Test
    void test1() { // benchmark #1, add inputs before running test
        MongoCollection<Document> collection = database.getCollection("test1");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate predicate = new LogicOperator(new Size(new AccessPath(null, new Attribute("type"))), LogicOpcode.EQ, new ValueExpr(new IntLiteral(2)));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("name")));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals("[ { \"name\": \"apple\" }, { \"name\": \"grape\" } ]".replaceAll(" ", ""),
                        result.replaceAll(" ", ""));
        mongoClient.close();
    }

    @Test
    void test2() { // benchmark #2, add inputs before running test
        MongoCollection<Document> collection = database.getCollection("test2");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);
        Predicate predicate1 = new LogicOperator(new AccessPath(new AccessPath(null, new Attribute("cities")), new Attribute("name")),
                LogicOpcode.EQ,
                new ValueExpr(new NullLiteral()));
        Predicate predicate2 = new Not(new Exists(new AccessPath(new AccessPath(null, new Attribute("cities")), new Attribute("name"))));
        Predicate predicate = new Or(predicate1, predicate2);
        List<CommonExpr> list = new ArrayList<>();
        CommonExpr idField = new AccessPath(null, new Attribute("_id"));
        CommonExpr nameField = new AccessPath(null, new Attribute("name"));
        CommonExpr citiesField = new AccessPath(null, new Attribute("cities"));
        list.addAll(Arrays.asList(idField, nameField, citiesField));

        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(
                ("[{\n" +
                        "  \"_id\": {\n" +
                        "    \"$oid\": \"645d1e3be7bc6975168e2f85\"\n" +
                        "  },\n" +
                        "  \"name\": \"Spain\",\n" +
                        "  \"cities\": [\n" +
                        "    {\n" +
                        "      \"name\": \"Madrid\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"name\": null\n" +
                        "    }\n" +
                        "  ]\n" +
                        "},\n" +
                        "{\n" +
                        "  \"_id\": {\n" +
                        "    \"$oid\": \"645d1e3be7bc6975168e2f86\"\n" +
                        "  },\n" +
                        "  \"name\": \"France\"\n" +
                        "}]").replaceAll("\\s", ""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test3() { // benchmark #3, add inputs before running test
        MongoCollection<Document> collection = database.getCollection("test3");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate predicate1 = new LogicOperator(new AccessPath(null, new Attribute("created_at")),
                LogicOpcode.GTE,
                new ValueExpr(new ISODate("2010-04-29T00:00:00.000Z")));
        Predicate predicate2 = new LogicOperator(new AccessPath(null, new Attribute("created_at")),
                LogicOpcode.LT,
                new ValueExpr(new ISODate("2010-05-01T00:00:00.000Z")));
        Predicate predicate = new And(predicate1, predicate2);
        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("created_at")));
        list.add(new AccessPath(null, new Attribute("name")));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals("[{\"_id\": {\"$oid\": \"645d25128b4da145b7728846\"}, \"name\": \"example1\", \"created_at\": {\"$date\": 1272585600000}}]".replaceAll(" ", ""), result.replaceAll(" ", ""));
        mongoClient.close();
    }

    @Test
    void test5() { // benchmark #5, add inputs before running test
        MongoCollection<Document> collection = database.getCollection("test5");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

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

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertTrue("[ { \"_id\" : 0, \"clicks\" : 2 }, { \"_id\" : 2, \"clicks\" : 1 } ]".replaceAll(" ", "").
                                equals(result.replaceAll(" ", ""))
                                || "[ { \"_id\" : 2, \"clicks\" : 1 }, { \"_id\" : 0, \"clicks\" : 2 } ]".replaceAll(" ", "").
                                equals(result.replaceAll(" ", "")));

        mongoClient.close();
    }

    @Test
    void test6() { // benchmark #6, add inputs before running test
        MongoCollection<Document> collection = database.getCollection("test6");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

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

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals("[{\"_id\":{\"$oid\":\"645d2ecde7bc6975168e2faf\"},\"name\":[\"Another\",\"Name\"],\"zipcode\":[\"2224\"],\"size_of_name\":2}]".replaceAll(" ", ""),
                result.replaceAll(" ", ""));

        mongoClient.close();
    }

    @Test
    void test8() { // benchmark #8, add inputs before running test
        MongoCollection<Document> collection = database.getCollection("test8");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("item")));
        Query query = new Find(new True(), list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals("[ {\"item\": \"large box\"},{\"item\": \"small box\"},{\"item\": \"medium box\"} ]".replaceAll(" ", ""),
                result.replaceAll(" ", ""));

        mongoClient.close();
    }

    @Test
    void test11() { // benchmark #11  (will research why the query returns dates in NumberLong format even inserted ISODate type)
        MongoCollection<Document> collection = database.getCollection("test11");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        AccessPath ap = new AccessPath(null, new Attribute("awards"));
        Predicate predicate1 = new LogicOperator(new AccessPath(null, new Attribute("award")),
                LogicOpcode.EQ, new ValueExpr(new StringLiteral("National Medal")));
        Predicate predicate2 = new LogicOperator(new AccessPath(null, new Attribute("year")),
                LogicOpcode.EQ, new ValueExpr(new IntLiteral(1975)));
        Predicate elemMatchPredicate = new And(predicate1, predicate2);
        Predicate predicate = new ElemMatch(ap, elemMatchPredicate);

        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("name")));
        list.add(new AccessPath(null, new Attribute("birth")));
        list.add(new AccessPath(null, new Attribute("death")));
        list.add(new AccessPath(null, new Attribute("contribs")));
        list.add(new AccessPath(null, new Attribute("awards")));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[{ " +
                "  \"_id\": 1,\n" +
                "  \"name\": {\n" +
                "    \"first\": \"John\",\n" +
                "    \"last\": \"Backus\"\n" +
                "  },\n" +
                "  \"birth\": {\n" +
                "    \"$date\": -1422547200000 }," +
                "  \"death\": {\n" +
                "    \"$date\": 1174114800000" +
                "  },\n" +
                "  \"contribs\": [\n" +
                "    \"Fortran\",\n" +
                "    \"ALGOL\",\n" +
                "    \"Backus-Naur Form\",\n" +
                "    \"FP\"\n" +
                "  ],\n" +
                "  \"awards\": [\n" +
                "    {\n" +
                "      \"award\": \"National Medal\",\n" +
                "      \"year\": 1975,\n" +
                "      \"by\": \"NSF\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"award\": \"Turing Award\",\n" +
                "      \"year\": 1977,\n" +
                "      \"by\": \"ACM\"\n" +
                "    }\n" +
                "  ]\n" +
                "}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test12 () { // benchmark #12
        MongoCollection<Document> collection = database.getCollection("test12");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(List.of(new AccessPath(null, new Attribute("_id"))));
        List<Expression> expressions = new ArrayList<>();
        Expression subExpression = new BinaryOperator(BinaryOpcode.SUB,
                new AccessPath(null, new Attribute("a")),
                new AccessPath(null, new Attribute("b")));
        expressions.add(subExpression);
        List<AccessPath> newFields = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("difference"))));
        Stage projectStage = new Project(accessPaths, expressions, newFields);

        List<Expression> fields = new ArrayList<>();
        fields.add(new AccessPath(null, new Attribute("difference")));
        List<Expression> sortOrder = new ArrayList<>();
        sortOrder.add(new ValueExpr(new IntLiteral(-1)));
        Stage sortStage = new Sort(fields, sortOrder);
        stageList.add(projectStage);
        stageList.add(sortStage);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[" +
                "  { \"_id\": {\"$oid\":\"645d5f227deb90b336ad91b3\"}, \"difference\": 9 },\n" +
                "  { \"_id\": {\"$oid\":\"645d5f227deb90b336ad91b4\"}, \"difference\": 0 },\n" +
                "  { \"_id\": {\"$oid\":\"645d5f227deb90b336ad91b5\"}, \"difference\": -8 }\n" +
                "]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test16() { // benchmark #16
        MongoCollection<Document> collection = database.getCollection("test16");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> groupKey = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();

        groupKey.add(new AccessPath(null, new Attribute("province")));
        expressions.add(new UnaryOperator(UnaryOpcode.SUM, new ValueExpr(new IntLiteral(1))));
        newFields.add(new AccessPath(null, new Attribute("count")));
        Stage groupStage = new Group(groupKey, expressions, newFields);
        stageList.add(groupStage);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertTrue(("[  { \"_id\": \"BC\", \"count\": 2 }, { \"_id\": \"ON\", \"count\": 3 }]").replaceAll("\\s", "").equals(result.replaceAll("\\s", ""))
                        || ("[{ \"_id\": \"ON\", \"count\": 3 }, { \"_id\": \"BC\", \"count\": 2 }]").replaceAll("\\s", "").equals(result.replaceAll("\\s", "")));
        mongoClient.close();
    }

    @Test
    void test17() { // benchmark #17
        MongoCollection<Document> collection = database.getCollection("test17");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("author")),
                new AccessPath(null, new Attribute("title")),
                new AccessPath(null, new Attribute("tags")),
                new AccessPath(null, new Attribute("_id"))));
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();
        Stage projectStage = new Project(accessPaths, expressions, newFields);
        Stage unwindStage = new Unwind(new AccessPath(null, new Attribute("tags")));
        stageList.add(projectStage);
        stageList.add(unwindStage);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[\n" +
                        "  {\n" +
                        "    \"_id\": {\"$oid\":\"645d646a49aa0491bf346706\"},\n" +
                        "    \"title\": \"this is my title\",\n" +
                        "    \"author\": \"bob\",\n" +
                        "    \"tags\": \"fun\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "     \"_id\": {\"$oid\":\"645d646a49aa0491bf346706\"},\n" +
                        "    \"title\": \"this is my title\",\n" +
                        "    \"author\": \"bob\",\n" +
                        "    \"tags\": \"good\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "     \"_id\": {\"$oid\":\"645d646a49aa0491bf346706\"},\n" +
                        "    \"title\": \"this is my title\",\n" +
                        "    \"author\": \"bob\",\n" +
                        "    \"tags\": \"fun\"\n" +
                        "  }\n" +
                        "]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test21() { // benchmark #21
        MongoCollection<Document> collection = database.getCollection("test21");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        Stage skip = new Skip(new ValueExpr(new IntLiteral(4)));
        Stage limit = new Limit(new ValueExpr(new IntLiteral(3)));
        stageList.add(skip);
        stageList.add(limit);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[\n" +
                "  { \"_id\": {\"$oid\": \"645afc88452474023d99b1c9\"}, \"a\": 5 },\n" +
                "  { \"_id\": {\"$oid\":\"645afc88452474023d99b1ca\"}, \"a\": 6 },\n" +
                "  { \"_id\": {\"$oid\":\"645afc88452474023d99b1cb\"}, \"a\": 7 }\n" +
                "]").replaceAll("\\s", ""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test43() {
        MongoCollection<Document> collection = database.getCollection("test43");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate predicate = new Not(new LogicOperator(new Type(new AccessPath(null, new Attribute("a"))),
                LogicOpcode.EQ,
                new ValueExpr(new StringLiteral("array"))));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("a")));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[\n" +
                "  {\n" +
                "    \"_id\": 2,\n" +
                "    \"a\": {\n" +
                "      \"b\": \"\",\n" +
                "      \"c\": \"\"\n" +
                "    }\n" +
                "  }\n" +
                "]").replaceAll("\\s", ""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test51() { // benchmark #51
        MongoCollection<Document> collection = database.getCollection("test51");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("_id")),
                new AccessPath(null, new Attribute("item"))));
        List<Expression> expressions = new ArrayList<>(Arrays.asList(
                new Substr(new AccessPath(null, new Attribute("quarter")),new ValueExpr(new IntLiteral(0)),new ValueExpr(new IntLiteral(2))),
                new Substr(new AccessPath(null, new Attribute("quarter")), new ValueExpr(new IntLiteral(2)), new ValueExpr(new IntLiteral(-1)))));
        List<AccessPath> newFields = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("yearSubstring"))
                , new AccessPath(null, new Attribute("quarterSubstring"))));
        Stage project = new Project(accessPaths, expressions, newFields);
        stageList.add(project);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[\n" +
                "  { \"_id\": 1, \"item\": \"ABC1\", \"yearSubstring\": \"13\", \"quarterSubstring\": \"Q1\" },\n" +
                "  { \"_id\": 2, \"item\": \"ABC2\", \"yearSubstring\": \"13\", \"quarterSubstring\": \"Q4\" },\n" +
                "  { \"_id\": 3, \"item\": \"XYZ1\", \"yearSubstring\": \"14\", \"quarterSubstring\": \"Q2\" }\n" +
                "]").replaceAll("\\s", ""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void testForDebug() {
        MongoCollection<Document> collection = database.getCollection("test2");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);
        Predicate predicate = new LogicOperator(new AccessPath(new AccessPath(null, new Attribute("cities")), new Attribute("name")),
                LogicOpcode.EQ,
                new AccessPath(null, new Attribute("_id")));
        List<CommonExpr> list = new ArrayList<>();
        CommonExpr idField = new AccessPath(null, new Attribute("_id"));
        CommonExpr nameField = new AccessPath(null, new Attribute("name"));
        CommonExpr citiesField = new AccessPath(null, new Attribute("cities"));
        list.addAll(Arrays.asList(idField, nameField, citiesField));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);
        System.out.println(result);
    }

    @Test
    void test13() { // benchmark #13
        /**
         * Current test: aggregate([ { $project: { Story: 1, points: 1, importance: 1, avg: { $add: ['$points', '$importance']}}},{ $sort: { avg: 1}}]);
         * Original query: aggregate([ { $project: { Story: 1, points: 1, importance: 1, avg: { $divide: [{ $add: ['$points', '$importance']}, 2]}}},
         *                             { $sort: { avg: 1}}]);
         */
        MongoCollection<Document> collection = database.getCollection("test13");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("Story")),
                new AccessPath(null, new Attribute("points")),
                new AccessPath(null, new Attribute("importance")),
                new AccessPath(null, new Attribute("_id"))));
        List<Expression> expressions = new ArrayList<>();
        Expression addExpr = new BinaryOperator(BinaryOpcode.ADD,
                new AccessPath(null, new Attribute("points")),
                new AccessPath(null, new Attribute("importance")));
        Expression divExpr = new BinaryOperator(BinaryOpcode.DIV, addExpr, new ValueExpr(new IntLiteral(2)));
        expressions.add(addExpr);
        List<AccessPath> newFields = new ArrayList<>();
        newFields.add(new AccessPath(null, new Attribute("avg")));
        Stage project = new Project(accessPaths, expressions, newFields);

        List<Expression> fields = new ArrayList<>();
        fields.add(new AccessPath(null, new Attribute("avg")));
        List<Expression> sortOrder = new ArrayList<>();
        sortOrder.add(new ValueExpr(new IntLiteral(1)));
        Stage sort = new Sort(fields, sortOrder);
        stageList.add(project);
        stageList.add(sort);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[\n" +
                "  {\n" +
                "    \"_id\":{\"$oid\":\"645acf2b9eaa04866a2ea50e\"}, \"Story\": \"first expected\", \"points\": 1, \"importance\": 1, \"avg\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"_id\":{\"$oid\":\"645acf2b9eaa04866a2ea50f\"}, \"Story\": \"second expected\", \"points\": 1, \"importance\": 3, \"avg\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"_id\": {\"$oid\": \"645acf2b9eaa04866a2ea510\"}, \"Story\": \"third expected\", \"points\": 5, \"importance\": 1, \"avg\": 6\n" +
                "  }\n" +
                "]").replaceAll("\\s", ""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test15() { // benchmark #15
        /**
         * db.collection.aggregate( [{ $project: { "name": "$name", "telephoneCount": { $size: '$telephone' } } }] );
         */
        MongoCollection<Document> collection = database.getCollection("test15");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(List.of(new AccessPath(null, new Attribute("_id"))));
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();
        newFields.add(new AccessPath(null, new Attribute("name")));
        newFields.add(new AccessPath(null, new Attribute("telephoneCount")));
        expressions.add(new AccessPath(null, new Attribute("name")));
        expressions.add(new Size(new AccessPath(null, new Attribute("telephone"))));
        Stage project = new Project(accessPaths, expressions, newFields);
        stageList.add(project);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[" +
                "{ \"_id\": 1, \"name\": \"Sam\", \"telephoneCount\": 3 }," +
                "{ \"_id\": 2, \"name\": \"Joe\", \"telephoneCount\": 2 } ]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test18() { // benchmark #18
        /**
         * db.collection.aggregate([{
         *      $group: { _id: null, minPrice: { $min: "$price" } }
         * }])
         */
        MongoCollection<Document> collection = database.getCollection("test18");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> groupKeys = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>(List.of(new UnaryOperator(UnaryOpcode.MIN, new AccessPath(null, new Attribute("price")))));
        List<AccessPath> newFileds = new ArrayList<>(List.of(new AccessPath(null, new Attribute("minPrice"))));
        Stage group = new Group(groupKeys, expressions, newFileds);
        stageList.add(group);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[ { \"_id\": null, \"minPrice\": 5 } ]").replaceAll("\\s",""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test19() { // benchmark #19
        /**
         * db.collection.find( { "unusual": {"$elemMatch": {"defindex":363, "_particleEffect":{"$in":[6,20]}} } })
         */
        MongoCollection<Document> collection = database.getCollection("test19");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        AccessPath ap = new AccessPath(null, new Attribute("unusual"));
        Predicate elemMatchPredicate = new And(
                new LogicOperator(new AccessPath(null, new Attribute("defindex")),LogicOpcode.EQ, new ValueExpr(new IntLiteral(363))),
                new In(new AccessPath(null, new Attribute("_particleEffect")), new ValueExpr(new Array(List.of(new IntLiteral(6), new IntLiteral(20))))));
        Predicate elemMatch = new ElemMatch(ap, elemMatchPredicate);
        List<CommonExpr> list = new ArrayList();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("timecreated")));
        list.add(new AccessPath(null, new Attribute("unusual")));
        Query query = new Find(elemMatch, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[\n" +
                "  {\n" +
                "    \"_id\": \"76561198045636214\",\n" +
                "    \"timecreated\": 1311148549,\n" +
                "    \"unusual\": [\n" +
                "      { \"id\": 1960169991, \"original_id\": 698672623, \"defindex\": 313, \"_particleEffect\": 19 },\n" +
                "      {\n" +
                "        \"id\": 965349033, \"original_id\": 931933064, \"defindex\": 363, \"_particleEffect\": 6 }\n" +
                "    ]\n" +
                "  }\n" +
                "]").replaceAll("\\s", ""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test20() { // benchmark #20
        /**
         * db.collection.find( { $and: [ { $or: [ { "first_name": "john"}, {"last_name": "john"} ] }, { "phone": "12345678" } ] });
         */
        MongoCollection<Document> collection = database.getCollection("test20");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate or = new Or(new LogicOperator(new AccessPath(null, new Attribute("first_name")), LogicOpcode.EQ, new ValueExpr(new StringLiteral("john"))),
                new LogicOperator(new AccessPath(null, new Attribute("last_name")), LogicOpcode.EQ, new ValueExpr(new StringLiteral("john"))));
        Predicate and = new And(or, new LogicOperator(new AccessPath(null, new Attribute("phone")), LogicOpcode.EQ, new ValueExpr(new StringLiteral("12345678"))));
        List<CommonExpr> list = new ArrayList();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("first_name")));
        list.add(new AccessPath(null, new Attribute("last_name")));
        list.add(new AccessPath(null, new Attribute("phone")));
        Query query = new Find(and, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[{\"_id\": {\"$oid\": \"645af9e9452474023d99b1bd\"}, \"first_name\": \"john\", \"last_name\": \"hersh\", \"phone\": \"12345678\"}," +
                "{\"_id\": {\"$oid\": \"645af9e9452474023d99b1be\"}, \"first_name\": \"elton\", \"last_name\": \"john\", \"phone\": \"12345678\"}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test24() { // benchmark #24
        /**
         *  db.countries.aggregate([ { $group: { _id: "$country" } }, { $count: "countOfDistinchCountries" } ])
         */
        MongoCollection<Document> collection = database.getCollection("test24");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> groupKeys = new ArrayList<>(List.of(new AccessPath(null, new Attribute("country"))));
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();
        Stage group = new Group(groupKeys, expressions, newFields);
        stageList.add(group);
        Stage count = new Count(new ValueExpr(new StringLiteral("countOfDistinctCountries")));
        stageList.add(count);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[ { \"countOfDistinctCountries\": 3 } ]").replaceAll("\\s", ""), result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test23() { // benchmark #23
        /**
         * db.collection.aggregate( [
         *    { $group: { _id: { userId: "$userId", name: "$name" }, count: { $sum: 1 } } },
         *    { $match: { count: { $gt: 1 } } },
         *    { $project: { _id: 0, count: 1, userId: "$_id.userId", name: "$_id.name"}}
         * ] )
         */
        MongoCollection<Document> collection = database.getCollection("test23");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> groupKeys = new ArrayList<>(List.of(new AccessPath(null, new Attribute("userId")),
                new AccessPath(null, new Attribute("name"))));
        List<Expression> groupExpressions = new ArrayList<>(List.of(new UnaryOperator(UnaryOpcode.SUM, new ValueExpr(new IntLiteral(1)))));
        List<AccessPath> newFileds = new ArrayList<>(List.of(new AccessPath(null, new Attribute("count"))));
        Stage group = new Group(groupKeys, groupExpressions, newFileds);
        stageList.add(group);

        Stage match = new Match(new LogicOperator(new AccessPath(null, new Attribute("count")),
            LogicOpcode.GT, new ValueExpr(new IntLiteral(1))));
        stageList.add(match);

        List<AccessPath> accessPaths = new ArrayList<>(List.of(new AccessPath(null, new Attribute("count"))));
        List<Expression> projectExpressions = new ArrayList<>(Arrays.asList(
                new AccessPath(new AccessPath(null, new Attribute("_id")), new Attribute("userId")),
                new AccessPath(new AccessPath(null, new Attribute("_id")), new Attribute("name"))));
        List<AccessPath> newFields = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("userId")),
                new AccessPath(null, new Attribute("name"))));
        Stage project = new Project(accessPaths, projectExpressions, newFields);
        stageList.add(project);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertTrue(("[\n" +
                "  { \"count\": 2, \"userId\": 1, \"name\": \"Brian\" },\n" +
                "  { \"count\": 2, \"userId\": 2, \"name\": \"Ben\" }\n" +
                "]").replaceAll("\\s", "").equals(result.replaceAll("\\s", "")) ||
                        ("[ { \"count\": 2, \"userId\": 2, \"name\": \"Ben\" },\n" +
                        "  { \"count\": 2, \"userId\": 1, \"name\": \"Brian\" }\n" +
                        "]").replaceAll("\\s", "").equals(result.replaceAll("\\s", "")));
        mongoClient.close();
    }

    @Test
    void test9() { // benchmark #9
        //  db.collection.find( { pictures: { $ne: [] } } )
        MongoCollection<Document> collection = database.getCollection("test9");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate ne = new LogicOperator(new AccessPath(null, new Attribute("pictures")),
                LogicOpcode.NE, new ValueExpr(new Array(new ArrayList<>())));
        List<CommonExpr> list = new ArrayList();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("item")));
        list.add(new AccessPath(null, new Attribute("qty")));
        list.add(new AccessPath(null, new Attribute("pictures")));
        Query query = new Find(ne, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[{\"_id\": {\"$oid\": \"645d3839e7bc6975168e2fbb\"}, " +
                        "\"item\": \"large box\", \"qty\": 20, " +
                        "\"pictures\": [\"1\", \"2\", \"3\"]}," +
                "{\"_id\": {\"$oid\": \"645d3839e7bc6975168e2fbc\"}, " +
                        "\"item\": \"small box\", \"qty\": 55, " +
                        "\"pictures\": [\"1\", \"2\"]}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test14() { // benchmark #14
        /**
         * db.collection.aggregate([
         *     {"$group" : { "_id": "$name", "count": { "$sum": 1 } } },
         *     {"$match": {"_id" :{ "$ne" : null } , "count" : {"$gt": 1} } },
         *     {"$project": {"name" : "$_id", "_id" : 0} }
         * ]);
         */
        MongoCollection<Document> collection = database.getCollection("test14");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> groupKey = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();

        groupKey.add(new AccessPath(null, new Attribute("name")));
        expressions.add(new UnaryOperator(UnaryOpcode.SUM, new ValueExpr(new IntLiteral(1))));
        newFields.add(new AccessPath(null, new Attribute("count")));
        Stage groupStage = new Group(groupKey, expressions, newFields);
        stageList.add(groupStage);

        Predicate and = new And(
                new LogicOperator(new AccessPath(null, new Attribute("_id")),
                        LogicOpcode.NE, new ValueExpr(new NullLiteral())),
                new LogicOperator(new AccessPath(null, new Attribute("count")),
                        LogicOpcode.GT, new ValueExpr(new IntLiteral(1))));
        Stage match = new Match(and);
        stageList.add(match);

        List<AccessPath> accessPaths = new ArrayList<>();
        List<Expression> projectExpressions = new ArrayList<>(List.of(new AccessPath(null,new Attribute("_id"))));
        List<AccessPath> projectNewFields = new ArrayList<>(List.of(new AccessPath(null, new Attribute("name"))));
        Stage project = new Project(accessPaths, projectExpressions, projectNewFields);
        stageList.add(project);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);
        System.out.println(result);

        Assertions.assertEquals(("[{\"name\": \"Brian\"}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test27() { // benchmark #27
        // db.test27.find({$and:[{"start_date":{$lte:new Date()}}, {"end_date":{$gte:new Date()}}]});
        MongoCollection<Document> collection = database.getCollection("test27");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate predicate1 = new LogicOperator(new AccessPath(null, new Attribute("start_date")),
                LogicOpcode.LTE,
                new ValueExpr(new ISODate("")));
        Predicate predicate2 = new LogicOperator(new AccessPath(null, new Attribute("end_date")),
                LogicOpcode.GTE,
                new ValueExpr(new ISODate("")));
        Predicate predicate = new And(predicate1, predicate2);
        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("name")));
        list.add(new AccessPath(null, new Attribute("start_date")));
        list.add(new AccessPath(null, new Attribute("end_date")));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals((
                "[{\"_id\": {\"$oid\": \"64c9d7c4c9fd42453121621e\"}, \"name\": \"a\", " +
                        "\"start_date\": {\"$date\": 1674283414151}, \"end_date\": {\"$date\": 1695537814000}}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test28() { // benchmark # 28
        // db.collection.find( { ln : { $exists : true } } )
        MongoCollection<Document> collection = database.getCollection("test28");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate predicate = new Exists(new AccessPath(null, new Attribute("ln")));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("fn")));
        list.add(new AccessPath(null, new Attribute("ln")));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals((
                "[{\"_id\": \"1\", \"fn\": \"sagar\", \"ln\": \"Varpe\"}," +
                "{\"_id\": \"3\", \"ln\": \"ln1\"}," +
                "{\"_id\": \"4\", \"ln\": \"ln2\"}]\n").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test32_1() { // benchmark #32
        /**
         * db.test.aggregate(
         *   { $unwind : "$shapes" },
         *   { $match : { "shapes.color": "red" }},
         *   { $project: {shapes": 1, "_id": 0 } }
         * )
         */
        MongoCollection<Document> collection = database.getCollection("test32");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        Stage unwindStage = new Unwind(new AccessPath(null, new Attribute("shapes")));
        stageList.add(unwindStage);
        Predicate matchPredicate = new LogicOperator(new AccessPath(new AccessPath(null, new Attribute("shapes")), new Attribute("color")),
                        LogicOpcode.EQ, new ValueExpr(new StringLiteral("red")));
        Stage match = new Match(matchPredicate);
        stageList.add(match);

        List<AccessPath> accessPaths = new ArrayList<>(List.of(new AccessPath(null, new Attribute("shapes"))));
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();
        Stage project = new Project(accessPaths, expressions, newFields);
        stageList.add(project);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[{\"shapes\": {\"shape\": \"circle\", \"color\": \"red\"}}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test41() { // benchmark #41
        // db.collection.find({ "post": { $ne: "" })
        MongoCollection<Document> collection = database.getCollection("test41");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        Predicate predicate = new LogicOperator(new AccessPath(null, new Attribute("post")),
                LogicOpcode.NE,
                new ValueExpr(new StringLiteral("")));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new AccessPath(null, new Attribute("_id")));
        list.add(new AccessPath(null, new Attribute("author")));
        list.add(new AccessPath(null, new Attribute("post")));
        Query query = new Find(predicate, list);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals((
                "[{\"_id\": 2, " +
                        "\"author\": \"you\", \"post\": \"how to query\"}]\n").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test50() { // benchmark #50
        /**
         * db.collection.aggregate(
         *      { $group: { _id: { a: "$a", b: "$b" }, count: { $sum: 1 } } },
         *      { $match: { count: { $gt: 1 } } }
         * )
         */
        MongoCollection<Document> collection = database.getCollection("test50");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> groupKeys = new ArrayList<>(List.of(new AccessPath(null, new Attribute("a")),
                new AccessPath(null, new Attribute("b"))));
        List<Expression> groupExpressions = new ArrayList<>(List.of(new UnaryOperator(UnaryOpcode.SUM, new ValueExpr(new IntLiteral(1)))));
        List<AccessPath> newFileds = new ArrayList<>(List.of(new AccessPath(null, new Attribute("count"))));
        Stage group = new Group(groupKeys, groupExpressions, newFileds);
        stageList.add(group);

        Predicate matchPredicate = new LogicOperator(new AccessPath(null, new Attribute("count")),
                LogicOpcode.GT, new ValueExpr(new IntLiteral(1)));
        Stage match = new Match(matchPredicate);
        stageList.add(match);
        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals(("[{\"_id\": {\"a\": 1, \"b\": 2}, \"count\": 2}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
        mongoClient.close();
    }

    @Test
    void test7() { // benchmark #7
        /**
         * 1: db.collection.aggregate([
         *    { "$project": {"Line": 1, "LineStart": 1, "SSCEXPEND": 1, "SSCEXPEND_count": { "$size": "$SSCEXPEND" } } },
         *    { "$match": { "SSCEXPEND_count": { "$gte": 2 } } }
         * ])
         */
        MongoCollection<Document> collection = database.getCollection("test7");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>(Arrays.asList(
                new AccessPath(null, new Attribute("_id")),
                new AccessPath(null, new Attribute("Line")),
                new AccessPath(null, new Attribute("LineStart")),
                new AccessPath(null, new Attribute("SSCEXPEND"))));
        List<Expression> expressions = new ArrayList<>(Arrays.asList(new Size(new AccessPath(null, new Attribute("SSCEXPEND")))));
        List<AccessPath> newFields = new ArrayList<>(Arrays.asList(new AccessPath(null, new Attribute("SSCEXPEND_count"))));
        Stage project = new Project(accessPaths, expressions, newFields);
        stageList.add(project);

        Stage match = new Match(new LogicOperator(new AccessPath(null, new Attribute("SSCEXPEND_count")),
                LogicOpcode.GTE, new ValueExpr(new IntLiteral(2))));
        stageList.add(match);

        Query query = new Aggregate(stageList);

        MongoCursor<Document> cursor = mongoJavaTranslator.translate(new Program(query)).iterator();
        String result = toJson(cursor);

        Assertions.assertEquals((
                "[{\"_id\": {\"$oid\": \"645d317ce7bc6975168e2fb2\"}, " +
                        "\"Line\": \"1\", \"LineStart\": {\"$date\": 1426056575000}, " +
                        "\"SSCEXPEND\": [{\"Secuence\": 10, \"Title\": 1}, {\"Secuence\": 183, \"Title\": 613}], " +
                        "\"SSCEXPEND_count\": 2}]").replaceAll("\\s", ""),
                result.replaceAll("\\s", ""));
    }
}
