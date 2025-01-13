package verify;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.*;
import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.Schema;
import nosdaq.trans.MongoJavaTranslator;
import org.bson.Document;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;

import static nosdaq.synth.EnumSynthesizer2.printTree;
import static nosdaq.trans.DSLTranslator.translate;

public class MongoVerifier implements Verifier {
    final static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    final static MongoDatabase database = mongoClient.getDatabase("verifier");
    static int count = 1;

    private static boolean compareDocumentListsIgnoreOrder(List<Document> list1, List<Document> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        List<Document> unmatchedDocuments = new ArrayList<>(list2);
        for (Document document : list1) {
            boolean matched = false;
            for (int i = 0; i < unmatchedDocuments.size(); i++) {
                if (document.equals(unmatchedDocuments.get(i))) {
                    unmatchedDocuments.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        return unmatchedDocuments.isEmpty();
    }

    @Override
    public boolean verify(Program program, Schema schema, List<Example> examples) {
        MongoCollection<Document> collection = database.getCollection(schema.getSchemaName());
        MongoJavaTranslator mongoJavaTranslator = new MongoJavaTranslator(collection);

        for (Example example : examples) {
            if (example.getForeign() != null) {
                for (String fName : example.getForeign().keySet()) {
                    MongoCollection<Document> fCollection = database.getCollection(fName);
                    fCollection.deleteMany(new Document());
                    fCollection.insertMany(example.getForeign().get(fName));
                }
            }
            // Delete all documents in the collection
            collection.deleteMany(new Document());

            // Insert the documents into the collection
            // cannot write an empty list to db
            if (!example.getInput().isEmpty()) {
                collection.insertMany(example.getInput());
            }

            try {
                Iterator<Document> iterator = mongoJavaTranslator.translate(program).iterator();
                List<Document> documents = new ArrayList<>();
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }
                /*
                if (count == 293) {
                    System.out.println(translate(program));
                    for (Document document : documents) {
                        System.out.println(document);
                    }
                }*/
                if (!compareDocumentListsIgnoreOrder(example.getOutput(), documents)) {
                    ++count;
                    return false;
                }
            } catch (MongoQueryException e) {
                System.out.println("MongoQueryException: " + e.getErrorMessage());
                ++count;
                return false;
            } catch (MongoCommandException e) {
                System.out.println("MongoCommandException: " + e.getErrorMessage());
                ++count;
                return false;
            }  catch (InputMismatchException e) {
                System.out.println("InputMismatchException: " + e.getMessage());
                ++count;
                return false;
            } catch (ClassCastException e) {
                System.out.println("ClassCastException: " + e.getMessage());
                ++count;
                return false;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                ++count;
                return false;
            }
        }

        return true;
    }
}
