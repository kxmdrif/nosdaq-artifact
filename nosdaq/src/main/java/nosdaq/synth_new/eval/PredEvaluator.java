package nosdaq.synth_new.eval;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nosdaq.ast.Program;
import nosdaq.ast.pred.Predicate;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.stage.Match;
import nosdaq.trans.MongoJavaTranslator;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// Each partial program correspond to a PredEvaluator
public class PredEvaluator {
    final static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    final static MongoDatabase database = mongoClient.getDatabase("verifier");


    private List<MongoCollection<Document>> collections;
    private boolean partialInCanWrite;

    public PredEvaluator(Schema schema, List<List<Document>> partialIns) {
        partialInCanWrite = true;
        collections = new ArrayList<>();
        for (int i = 0; i < partialIns.size(); ++i) {
            MongoCollection<Document> collection =
                    database.getCollection(schema.getSchemaName() + "_pred_partial_eval_" + i);
            try {
                collection.deleteMany(new Document());
                if (!partialIns.get(i).isEmpty()) {
                    collection.insertMany(copyAndRemoveId(partialIns.get(i)));
                }
                collections.add(collection);
            } catch (Exception e) {
                partialInCanWrite = false;
                break;
            }

        }

    }

    private static List<Document> copyAndRemoveId(List<Document> documents) {
        List<Document> res = new ArrayList<>();
        for (Document document : documents) {
            int dSize = document.keySet().size();
            Document copy = new Document();
            copy.putAll(document);
            copy.remove("_id");
            assert dSize == document.keySet().size();
            res.add(copy);
        }
        return res;
    }

    public void destroy() {
        collections.forEach(MongoCollection::drop);
        Collections.fill(collections, null);
        collections = null;
    }
    /**
     * @return bit maps for each example
     * bitmaps: 0011|1100...
     * If partialIn has _id and contains array or has duplicate values, we cannot write
     * documents into db. To deal with this, we remove the _id field and then write into db.
     * This does not matter because we assume we will never use _id and its subfields to filter.
     */
    public String evalPredicate(Predicate predicate) {
        if (!partialInCanWrite) {
            return null;
        }
        Program singleStage = new Program(new Aggregate(List.of(new Match(predicate))));
        List<String> bitMaps = new ArrayList<>();

        for (MongoCollection<Document> collection : this.collections) {
            MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);
            try {
                // mongodb might add _id after insertion.
                // so query again to get the actual input data
                Iterator<Document> inputIterator = collection.find().iterator();
                List<Document> input = new ArrayList<>();
                while (inputIterator.hasNext()) {
                    input.add(inputIterator.next());
                }

                Iterator<Document> iterator = mongoJavaTranslator
                        .translate(singleStage)
                        .iterator();
                List<Document> output = new ArrayList<>();
                while (iterator.hasNext()) {
                    output.add(iterator.next());
                }

                bitMaps.add(genBitMapForMatch(input, output));

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return String.join("|", bitMaps);
    }

    private static String genBitMapForMatch(List<Document> input, List<Document> output) {
        boolean[] occupy = new boolean[input.size()];
        for (Document document : output) {
            for (int j = 0; j < input.size(); ++j) {
                if (document.equals(input.get(j)) && !occupy[j]) {
                    occupy[j] = true;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (boolean o : occupy) {
            if (o) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        return sb.toString();
    }
}
