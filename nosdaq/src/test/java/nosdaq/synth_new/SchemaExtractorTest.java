package nosdaq.synth_new;

import nosdaq.ast.Example;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.utils.BenchmarkLoader;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class SchemaExtractorTest {

    @Test
    public void test() {
        BenchmarkLoader benchmarkLoader = new BenchmarkLoader(6);
        List<Example> examples = benchmarkLoader.loadExamples();
        List<List<Document>> ins = examples.stream().map(Example::getInput).toList();
        Set<AttrSchema> attrSchemaSet = SchemaExtractor.extractAttrSchemas(ins);
        System.out.println(attrSchemaSet);
    }

    @Test
    public void documentEqTest() {
        Document doc1 = Document.parse("{a: 1}");
        Document doc2 = Document.parse("{a: 1.0}");
        assert !doc1.equals(doc2);
    }
}
