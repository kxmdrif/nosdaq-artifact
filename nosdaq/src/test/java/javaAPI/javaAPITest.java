package javaAPI;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class javaAPITest {
    @Test
    void test1() {
        Document apple = new Document("name", "apple");
        Document grape = new Document("name", "grape");

        List<Document> fruits = new ArrayList<>();
        fruits.add(apple);
        fruits.add(grape);

        // Count the number of distinct keys in the "fruits" field
        Set<String> distinctKeys = new HashSet<>();
        for (Document fruit : fruits) {
            distinctKeys.addAll(fruit.keySet());
        }

        int distinctKeysCount = distinctKeys.size();
        System.out.println("Number of distinct keys in 'fruits': " + distinctKeysCount);
    }

    @Test
    void test2() {
        // Create the first city document
        Document city1 = new Document("name", "Madrid");

        // Create the second city document with a null name
        Document city2 = new Document("name", null);

        // Create the document for Spain with an array of cities
        Document spain = new Document("_id", "645d1f51e7bc6975168e2f8d")
                .append("name", "Spain")
                .append("cities", List.of(city1, city2));

        // Create the document for France
        Document france = new Document("_id", "645d1f51e7bc6975168e2f8e")
                .append("name", "France");

        List<Document> documents = new ArrayList<>();
        documents.add(spain);
        documents.add(france);

        Set<String> distinctKeys = new HashSet<>();
        for (Document fruit : documents) {
            distinctKeys.addAll(fruit.keySet());
        }

        int distinctKeysCount = distinctKeys.size();
        System.out.println("Number of distinct keys in 'fruits': " + distinctKeysCount);
    }
}
