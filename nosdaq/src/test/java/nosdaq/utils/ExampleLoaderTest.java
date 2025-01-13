package nosdaq.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class ExampleLoaderTest {
    @Test
    public void deserializeTest() {
        String ejsonStr = "{ \"aa\": {\"_id\": { \"$oid\": \"507f1f77bcf86cd799439011\"}}," +
                "\"myNumber\": {\"$numberLong\": \"4794261\" }}}";
        Document doc = Document.parse(ejsonStr);
        System.out.println(doc.toJson());
    }
}
