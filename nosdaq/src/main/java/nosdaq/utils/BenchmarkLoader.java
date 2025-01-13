package nosdaq.utils;


import nosdaq.ast.Example;
import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.schema.Type;
import nosdaq.synth_new.SchemaExtractor;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public final class BenchmarkLoader {
    private static final JsonWriterSettings settings
            = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();

    private final Document benchmark;
    private final List<Example> examples;

    public BenchmarkLoader(int benchmarkNo) {
        final String fileName = "benchmark" + benchmarkNo + ".json";
        ClassLoader classLoader = BenchmarkLoader.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                String json = scanner.useDelimiter("\\A").next();
                this.benchmark = Document.parse(json);
                this.examples = this.benchmark.getList("examples", Document.class)
                        .stream().map(i -> {
                            List<Document> input = i.getList("input", Document.class);
                            List<Document> output = i.getList("output", Document.class);
                            if (i.get("foreign") == null) {
                                return new Example(input, output);
                            }
                            Document fd = i.get("foreign", Document.class);
                            Map<String, List<Document>> foreign = new HashMap<>();
                            for (String key : fd.keySet()) {
                                foreign.put(key, fd.getList(key, Document.class));
                            }
                            return new Example(input, output, foreign);
                        }).toList();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read the file " + fileName, e);
        }
    }

    public static void printExamples(List<Example> examples) {
        System.out.println("=================================");
        List<String> examplesStrList = new ArrayList<>();
        for (Example example : examples) {
            String str = new Document("input", example.getInput())
                    .append("output", example.getOutput()).toJson(settings);
            examplesStrList.add(str);
        }
        System.out.println("[");
        System.out.println(String.join(", ", examplesStrList));
        System.out.println("]");
        System.out.println("=================================");
    }


    public List<Example> loadExamples() {
        return this.examples;
    }

    public Schema loadInputSchema() {
        Document inputSchema = this.benchmark.getEmbedded(List.of("schema", "input"), Document.class);
        if (inputSchema == null) {
            List<List<Document>> inputs = this.examples.stream().map(Example::getInput).toList();
            Set<AttrSchema> attrSchemas = SchemaExtractor.extractAttrSchemas(inputs);
            return new Schema(this.benchmark.getString("name"), attrSchemas.stream().toList());
        }
        return extractSchema(inputSchema);

    }

    public Schema loadOutputSchema() {
        Document outputSchema = this.benchmark.getEmbedded(List.of("schema", "output"), Document.class);
        if (outputSchema == null) {
            List<List<Document>> outputs = this.examples.stream().map(Example::getOutput).toList();
            Set<AttrSchema> attrSchemas = SchemaExtractor.extractAttrSchemas(outputs);
            return new Schema(this.benchmark.getString("name"), attrSchemas.stream().toList());
        }
        return extractSchema(outputSchema);
    }

    public List<Schema> loadForeignSchema() {
        Document foreignSchema = this.benchmark.getEmbedded(List.of("schema", "foreign"), Document.class);
        if (foreignSchema == null) {
            return null;
        }

        Set<String> foreigns = foreignSchema.keySet();
        return foreigns.stream().map(foreign -> {
            Document schema = foreignSchema.get(foreign, Document.class);
            Set<String> keys = schema.keySet();
            List<AttrSchema> attrSchemas = keys.stream().map(i -> {
                AccessPath ap = Helper.constructAp(i);
                Type type = Helper.getSchemaType(schema.getString(i));
                return new AttrSchema(ap, type);
            }).toList();
            return new Schema(foreign, attrSchemas);
        }).toList();
    }

    public List<String> loadConstants() {
        return this.benchmark.getList("constants", String.class);
    }


    private Schema extractSchema(Document schema) {
        Set<String> keys = schema.keySet();
        List<AttrSchema> attrSchemas = keys.stream().map(i -> {
            AccessPath ap = Helper.constructAp(i);
            Type type = Helper.getSchemaType(schema.getString(i));
            return new AttrSchema(ap, type);
        }).toList();
        return new Schema(this.benchmark.getString("name"), attrSchemas);
    }
}
