package nosdaq.trans;

import nosdaq.ast.Program;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import nosdaq.ast.query.Find;
import nosdaq.ast.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nosdaq.trans.DSLTranslator.translate;

class DSLTranslatorTest {
    private Program buildProg1() {
        Predicate predicate = new LogicOperator(new Size(new AccessPath(null, new Attribute("type"))), LogicOpcode.EQ, new ValueExpr(new IntLiteral(2)));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        list.add(new Attribute("address"));
        Query query = new Find(predicate, list);
        return new Program(query);
    }

    /**
     * Testing Query Visitors
     */
    @Test
    void testVisitFind() {
        Assertions.assertEquals("find(size(type) == 2, [name, address])", translate(buildProg1()));
    }

    @Test
    void testVisitAggregate() {
        List<Stage> stageList = new ArrayList<>();
        Stage stage = new Limit(new ValueExpr(new IntLiteral(3)));
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([limit(3)])", translate(program));
    }


    /**
     * Testing Predicate Visitors
     */
    @Test
    void testVisitLogicOperator() {
        LogicOperator Op = new LogicOperator(new ValueExpr(new StringLiteral("name")), LogicOpcode.NE, new ValueExpr(new StringLiteral("Joe")));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        Query query = new Find(Op, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(name != Joe, [name])", translate(program));
    }

    @Test
    void testVisitNot() {
        Predicate predicate = new Not(new True());
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(not(true), [name])", translate(program));
    }

    @Test
    void testVisitAnd() {
        Predicate predicate = new And(new LogicOperator(new ValueExpr(new IntLiteral(3)), LogicOpcode.GT, new ValueExpr(new IntLiteral(2))), new True());
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find((3 > 2 and true), [project])", translate(program));
    }

    @Test
    void testVisitOr() {
        Predicate predicate = new Or(new LogicOperator(new ValueExpr(new IntLiteral(3)), LogicOpcode.GT, new ValueExpr(new IntLiteral(2))), new True());
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find((3 > 2 or true), [project])", translate(program));
    }

    @Test
    void testVisitIn() {
        AccessPath field = new AccessPath(null, new Attribute("test"));
        List<Value> intArray = new ArrayList<>();
        intArray.add(new IntLiteral(1));
        intArray.add(new IntLiteral(2));
        Predicate predicate = new In(field, new ValueExpr(new Array(intArray)));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(in(test, [1, 2]), [project])", translate(program));
    }

    @Test
    void testVisitAllMatch() {
        AccessPath accessPath = new AccessPath(null, new Attribute("ap"));
        Predicate predicate = new True();
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(new AllMatch(accessPath, predicate), list);
        Program program = new Program(query);
        Assertions.assertEquals("find(allMatch(ap, true), [project])", translate(program));
    }

    @Test
    void testVisitElemMatch() {
        AccessPath ap = new AccessPath(null, new Attribute("ap"));
        Predicate predicate = new True();
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(new ElemMatch(ap, predicate), list);
        Program program = new Program(query);
        Assertions.assertEquals("find(elemMatch(ap, true), [project])", translate(program));
    }

    @Test
    void testVisitExists() {
        AccessPath ap = new AccessPath(null, new Attribute("ap"));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(new Exists(ap), list);
        Program program = new Program(query);
        Assertions.assertEquals("find(exists(ap), [project])", translate(program));
    }

    /**
     * Testing Expression Visitors
     */
    @Test
    void testVisitBinaryOp() {
        List<Stage> stageList = new ArrayList<>();
        Stage stage = new Limit(new ValueExpr(new IntLiteral(4)));
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([limit(4)])", translate(program));
    }

    @Test
    void testVisitUnaryOp() {
        List<Stage> stageList = new ArrayList<>();
        Stage stage = new Limit(new ValueExpr(new IntLiteral(2)));
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([limit(2)])", translate(program));
    }

    @Test
    void testVisitType() {
        Predicate predicate = new LogicOperator(new Type(new AccessPath(null, new Attribute("name"))), LogicOpcode.EQ, new ValueExpr(new StringLiteral("String")));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(type(name) == String, [project])", translate(program));
    }

    @Test
    void testVisitSubstr() {
        Expression expr = new Substr(new AccessPath(null, new Attribute("test")), new ValueExpr(new IntLiteral(1)), new ValueExpr(new IntLiteral(2)));
        Predicate predicate = new LogicOperator(expr, LogicOpcode.EQ, new ValueExpr(new StringLiteral("es")));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(substr(test, 1, 2) == es, [project])", translate(program));
    }

    @Test
    void testVisitFilter() {
        List<Stage> stageList = new ArrayList<>();
        Expression expr = new Filter(new ValueExpr(new StringLiteral("items")), new True(), new ValueExpr(new StringLiteral("item")), new ValueExpr(new IntLiteral(3)));
        List<AccessPath> accessPaths = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();
        expressions.add(expr);
        List<AccessPath> newFields = new ArrayList<>();
        Stage stage = new Project(accessPaths, expressions, newFields);
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([project([], [filter(items, true, item, 3)], [])])", translate(program));
    }

    @Test
    void testVisitSize() {
        AccessPath ap = new AccessPath(null, new Attribute("ap"));
        Expression expr = new Size(ap);
        Predicate predicate = new LogicOperator(expr, LogicOpcode.GT, new ValueExpr(new IntLiteral(2)));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        Query query1 = new Find(predicate, list);
        Program program1 = new Program(query1);
        Assertions.assertEquals("find(size(ap) > 2, [name])", translate(program1));
    }

    /**
     * Testing Stage Visitors
     */
    @Test
    void testVisitProject() {
        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> accessPaths = new ArrayList<>();
        accessPaths.add(new AccessPath(null, new Attribute("name")));
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();
        Stage stage = new Project(accessPaths, expressions, newFields);
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([project([name], [], [])])", translate(program));
    }

    @Test
    void testVisitCount() {
        List<Stage> stageList = new ArrayList<>();
        Stage stage = new Count(new ValueExpr(new StringLiteral("name")));
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([count(name)])", translate(program));
    }

    @Test
    void testVisitMatch() {
        List<Stage> stageList = new ArrayList<>();
        Stage stage = new Match(new True());
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([match(true)])", translate(program));
    }

    @Test
    void testVisitGroup() {
        List<Stage> stageList = new ArrayList<>();
        List<AccessPath> groupKey = new ArrayList<>();
        groupKey.add(new AccessPath(null, new Attribute("key")));
        List<Expression> expressions = new ArrayList<>();
        List<AccessPath> newFields = new ArrayList<>();
        newFields.add(new AccessPath(null, new Attribute("field")));
        Stage stage = new Group(groupKey, expressions, newFields);
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([group([key], [], [field])])", translate(program));
    }

    @Test
    void testVisitUnwind() {
        List<Stage> stageList = new ArrayList<>();
        AccessPath ap = new AccessPath(null, new Attribute("books"));
        stageList.add(new Unwind(ap));
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([unwind(books)])", translate(program));
    }

    @Test
    void testVisitSort() {
        List<Stage> stageList = new ArrayList<>();
        List<Expression> fields = new ArrayList<>();
        fields.add(new ValueExpr(new StringLiteral("names")));
        List<Expression> sortOrder = new ArrayList<>();
        sortOrder.add(new ValueExpr(new IntLiteral(1)));
        Stage stage = new Sort(fields, sortOrder);
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([sort([names], [1])])", translate(program));
    }

    @Test
    void testVisitSkip() {
        List<Stage> stageList = new ArrayList<>();
        Stage stage = new Skip(new ValueExpr(new IntLiteral(3)));
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([skip(3)])",translate(program));
    }

    @Test
    void testVisitLimit() {
        List<Stage> stageList = new ArrayList<>();
        Stage stage = new Limit(new ValueExpr(new IntLiteral(10)));
        stageList.add(stage);
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([limit(10)])",translate(program));
    }

    /**
     * Testing Value Visitors
     */
    @Test
    void testVisitIntValue() {
        List<Stage> stageList = new ArrayList<>();
        stageList.add(new Limit(new ValueExpr(new IntLiteral(2))));
        Query query = new Aggregate(stageList);
        Program program = new Program(query);
        Assertions.assertEquals("aggregate([limit(2)])", translate(program));
    }

    @Test
    void testVisitIntArray() {
        List<Value> lArray = new ArrayList<>();
        lArray.add(new IntLiteral(1));
        lArray.add(new IntLiteral(2));
        List<Value> rArray = new ArrayList<>();
        rArray.add(new IntLiteral(3));
        rArray.add(new IntLiteral(4));
        Expression lExpr = new ValueExpr(new Array(lArray));
        Expression rExpr = new ValueExpr(new Array(rArray));
        Predicate predicate = new LogicOperator(lExpr, LogicOpcode.NE, rExpr);
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find([1, 2] != [3, 4], [project])", translate(program));
    }

    @Test
    void testVisitFloatValue() {
        Predicate predicate = new Not(new LogicOperator(new ValueExpr(new FloatLiteral(3.01)), LogicOpcode.GTE, new ValueExpr(new FloatLiteral(2.01))));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(not(3.01 >= 2.01), [name])", translate(program));
    }

    @Test
    void testVisitStringLiteral() {
        Expression expr = new UnaryOperator(UnaryOpcode.SUM, new ValueExpr(new StringLiteral("name")));
        Predicate predicate = new LogicOperator(expr, LogicOpcode.LT, new ValueExpr(new IntLiteral(10)));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(sum(name) < 10, [name])", translate(program));
    }

    @Test
    void testVisitBoolValue() {
        Predicate predicate = new Not(new True());
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(not(true), [project])", translate(program));
    }

    @Test
    void testVisitNullValue() {
        Predicate predicate = new LogicOperator(new ValueExpr(new StringLiteral("name")), LogicOpcode.NE, new ValueExpr(new NullLiteral()));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("name"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(name != null, [name])", translate(program));
    }

    @Test
    void testVisitISODate() {
        Predicate predicate = new LogicOperator(new ValueExpr(new StringLiteral("date")), LogicOpcode.EQ, new ValueExpr(new ISODate("2020-05-18T14:10:30Z")));
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("date"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find(date == ISODate(\"2020-05-18T14:10:30Z\"), [date])", translate(program));
    }

    @Test
    void testVisitStringArray() {
        List<Value> lArray = new ArrayList<>();
        lArray.add(new StringLiteral("John"));
        List<Value> rArray = new ArrayList<>();
        rArray.add(new StringLiteral("Doe"));
        Expression lExpr = new ValueExpr(new Array(lArray));
        Expression rExpr = new ValueExpr(new Array(rArray));
        Predicate predicate = new LogicOperator(lExpr, LogicOpcode.NE, rExpr);
        List<CommonExpr> list = new ArrayList<>();
        list.add(new Attribute("project"));
        Query query = new Find(predicate, list);
        Program program = new Program(query);
        Assertions.assertEquals("find([\"John\"] != [\"Doe\"], [project])", translate(program));
    }
}