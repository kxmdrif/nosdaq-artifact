package nosdaq.synth_new.eval;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.pred.Predicate;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.stage.Match;
import nosdaq.ast.stage.Stage;
import nosdaq.synth_new.AbstractCol;
import nosdaq.synth_new.SchemaExtractor;
import nosdaq.synth_new.sketch.Sketch;
import nosdaq.trans.MongoDBTranslator;
import nosdaq.trans.MongoJavaTranslator;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class Evaluator {
    final static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    final static MongoDatabase database = mongoClient.getDatabase("verifier");

    public static List<List<Document>>
            evaluate(Schema schema, List<Stage> partial, List<Example> examples) {
        MongoCollection<Document> collection = database.getCollection(schema.getSchemaName() + "_partial_eval");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<List<Document>> res = new ArrayList<>();
        for (Example example : examples) {
            try {
                if (example.getForeign() != null) {
                    for (String fName : example.getForeign().keySet()) {
                        MongoCollection<Document> fCollection = database.getCollection(fName);
                        fCollection.deleteMany(new Document());
                        fCollection.insertMany(example.getForeign().get(fName));
                    }
                }

                collection.deleteMany(new Document());
                if (!example.getInput().isEmpty()) {
                    collection.insertMany(example.getInput());
                }
                Iterator<Document> iterator = mongoJavaTranslator
                        .translate(new Program(new Aggregate(partial)))
                        .iterator();
                List<Document> documents = new ArrayList<>();
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }

                res.add(documents);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                res.add(null);
            }
        }
        return res;

    }

    public static AbstractCol evaluateSketch(Schema schema, List<Example> examples, Sketch sketch) {
        MongoCollection<Document> collection = database.getCollection(schema.getSchemaName() + "_sketch_eval");
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        List<List<Document>> res = new ArrayList<>();
        for (Example example : examples) {
            try {
                if (example.getForeign() != null) {
                    for (String fName : example.getForeign().keySet()) {
                        MongoCollection<Document> fCollection = database.getCollection(fName);
                        fCollection.deleteMany(new Document());
                        fCollection.insertMany(example.getForeign().get(fName));
                    }
                }

                collection.deleteMany(new Document());
                if (!example.getInput().isEmpty()) {
                    collection.insertMany(example.getInput());
                }
                Iterator<Document> iterator = mongoJavaTranslator
                        .translate(sketch.getProgram())
                        .iterator();
                List<Document> documents = new ArrayList<>();
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }

                res.add(documents);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                res.add(null);
            }
        }
        res = res.stream().filter(Objects::nonNull).toList();
        if (res.isEmpty()) {
            return null;
        }
        List<AttrSchema> attrSchemas = new ArrayList<>(SchemaExtractor.extractAttrSchemas(res));
        return new AbstractCol(res, new Schema(schema.getSchemaName() + "_sketch_eval", attrSchemas));
    }





}

