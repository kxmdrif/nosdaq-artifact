package nosdaq.synth;

import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.Attribute;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.grammar.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static nosdaq.ast.schema.Type.*;
import static nosdaq.ast.schema.Type.INT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnumSynthesizerTest {
    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test1EnumSynthesizer() throws IOException { // Success
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema name = new AttrSchema(new AccessPath(null, new Attribute("name")), STRING);
        AttrSchema type = new AttrSchema(new AccessPath(null, new Attribute("type")), ARRAY);
        attrSchemas.add(id);
        attrSchemas.add(name);
        attrSchemas.add(type);
        Schema schema = new Schema("test1", attrSchemas);

        // Add the documents to the input list
        List<Document> inputs = new ArrayList<>();
        Document input1_1 = new Document("_id", "645d1c47e7bc6975168e2f7f")
                .append("name", "apple")
                .append("type", List.of("Granny smith", "Fuji"));
        inputs.add(input1_1);

        Document input1_2 = new Document("_id", "645d1c47e7bc6975168e2f80")
                .append("name", "grape")
                .append("type", List.of("green", "black"));
        inputs.add(input1_2);

        Document input1_3 = new Document("_id", "645d1c47e7bc6975168e2f81")
                .append("name", "orange")
                .append("type", List.of("navel"));
        inputs.add(input1_3);

        // Add the documents to the output list
        List<Document> outputs = new ArrayList<>();
        Document output1_1 = new Document("name", "apple");
        outputs.add(output1_1);

        Document output1_2 = new Document("name", "grape");
        outputs.add(output1_2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        List<Document> inputs2 = new ArrayList<>();
        Document input2_1 = new Document("_id", "645d1c47e7bc6975168e2f82")
                .append("name", "apple")
                .append("type", List.of("Granny smith", "Fuji", "Gala"));
        inputs2.add(input2_1);

        Document input2_2 = new Document("_id", "645d1c47e7bc6975168e2f83")
                .append("name", "melon")
                .append("type", List.of("Cantaloupe", "Honeydew"));
        inputs2.add(input2_2);

        List<Document> outputs2 = new ArrayList<>();
        Document output2_1 = new Document("name", "melon");
        outputs2.add(output2_1);
        examples.add(new Example(inputs2, outputs2));

        // Add example 3 (empty output)
        List<Document> inputs3 = new ArrayList<>();
        Document input3_1 = new Document("_id", "645d1c47e7bc6975168e2f84")
                .append("name", "strawberry")
                .append("type", List.of("Alpine"));
        inputs3.add(input3_1);

        List<Document> outputs3 = new ArrayList<>();
        examples.add(new Example(inputs3, outputs3));

        Program program = synthesizer.synthesize(grammar, schema, examples, 12);
        assertNotNull(program);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test2EnumSynthesizer() throws IOException { // Success if we reduce grammar size
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema name = new AttrSchema(new AccessPath(null, new Attribute("name")), STRING);
        AttrSchema cities = new AttrSchema(new AccessPath(null, new Attribute("cities")), ARRAY);
        AttrSchema cities_name = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("cities")), new Attribute("name")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(name);
        attrSchemas.add(cities);
        attrSchemas.add(cities_name);
        Schema schema = new Schema("test2", attrSchemas);

        // Add the documents to the input list
        Document spain = new Document("_id", "645d1e3be7bc6975168e2f85")
                .append("name", "Spain")
                .append("cities", Arrays.asList(
                        new Document("name", "Madrid"),
                        new Document("name", null)
                ));
        Document france = new Document("_id", "645d1e3be7bc6975168e2f86")
                .append("name", "France");
        Document usa = new Document("_id", "645d1e3be7bc6975168e2f87")
                .append("name", "USA")
                .append("cities", Arrays.asList(
                        new Document("name", "Boston")
                ));
        List<Document> inputs = Arrays.asList(spain, france, usa);

        // Add the documents to the output list
        Document output1_1 = new Document("_id", "645d1e3be7bc6975168e2f85")
                .append("name", "Spain")
                .append("cities", Arrays.asList(
                        new Document("name", "Madrid"),
                        new Document("name", null)
                ));
        Document output1_2 = new Document("_id", "645d1e3be7bc6975168e2f86")
                .append("name", "France");
        List<Document> outputs = Arrays.asList(output1_1, output1_2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input2_1 = new Document("_id", "645d1e3be7bc6975168e2f87")
                .append("name", "Mexico").append("cities", Arrays.asList(new Document("name", "Mexico City")));
        Document input2_2 = new Document("_id", "645d1e3be7bc6975168e2f88")
                .append("name", "Canada")
                .append("cities", Arrays.asList(
                        new Document("name", "Vancouver"),
                        new Document("name", null)
                ));
        List<Document> inputs2 = Arrays.asList(input2_1, input2_2);
        List<Document> outputs2 = Arrays.asList(input2_2);
        examples.add(new Example(inputs2, outputs2));

        // Add example 3 ({"cities.name":null} also matches documents that have no "cities" array)
        Document input3_1 = input2_1;
        Document input3_2 = new Document("_id", "645d1e3be7bc6975168e2f89")
                .append("name", "Germany");
        List<Document> inputs3 = Arrays.asList(input3_1, input3_2);
        List<Document> outputs3 = Arrays.asList(input3_2);
        examples.add(new Example(inputs3, outputs3));

        Program program = synthesizer.synthesize(grammar, schema, examples, 18);
        if (program == null) {
            System.out.println("Can't find the program");
        }
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test3EnumSynthesizer() throws IOException, ParseException { // Success if we reduce grammar size
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema name = new AttrSchema(new AccessPath(null, new Attribute("name")), STRING);
        AttrSchema created_at = new AttrSchema(new AccessPath(null, new Attribute("created_at")), DATE);
        attrSchemas.add(id);
        attrSchemas.add(name);
        attrSchemas.add(created_at);
        Schema schema = new Schema("test3", attrSchemas);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Document input1 = new Document("_id", "645d25128b4da145b7728846")
                .append("name", "example1")
                .append("created_at", format.parse("2010-04-30T00:00:00.000Z"));
        Document input2 = new Document("_id", "645d25128b4da145b7728847")
                .append("name", "example2")
                .append("created_at", format.parse("2011-04-30T00:00:00.000Z"));
        List<Document> inputs = Arrays.asList(input1, input2);

        // Add the documents to the output list
        Document output1 = new Document("_id", "645d25128b4da145b7728846")
                .append("name", "example1")
                .append("created_at", format.parse("2010-04-30T00:00:00.000Z"));
        List<Document> outputs = Arrays.asList(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2 (expect empty result)
        Document input3 = new Document("_id", "645d25128b4da145b7728847")
                .append("name", "input3")
                .append("created_at", format.parse("2010-05-10T00:00:00.000Z"));
        Document input4 = new Document("_id", "645d25128b4da145b7728848")
                .append("name", "input4")
                .append("created_at", format.parse("2011-04-30T00:00:00.000Z"));
        List<Document> inputs2 = Arrays.asList(input3, input4);
        Document output2 = new Document();
        List<Document> outputs2 = Arrays.asList();
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input5 = new Document("_id", "645d25128b4da145b7728849")
                .append("name", "input5")
                .append("created_at", format.parse("2010-05-01T00:00:00.000Z"));
        Document input6 = new Document("_id", "645d25128b4da145b7728850")
                .append("name", "input6")
                .append("created_at", format.parse("2011-04-30T00:00:00.000Z"));
        List<Document> inputs3 = Arrays.asList(input5, input6);
        Document output3 = input5;
        List<Document> outputs3 = Arrays.asList();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        Program program = synthesizer.synthesize(grammar, schema, examples, 24);
        if (program == null) {
            System.out.println("Can't find the program");
        }
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test5EnumSynthesizer() throws IOException, ParseException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema userID = new AttrSchema(new AccessPath(null, new Attribute("userid")), INT);
        AttrSchema type = new AttrSchema(new AccessPath(null, new Attribute("type")), STRING);
        AttrSchema orderDate = new AttrSchema(new AccessPath(null, new Attribute("orderDate")), DATE);
        attrSchemas.add(id);
        attrSchemas.add(userID);
        attrSchemas.add(type);
        attrSchemas.add(orderDate);
        Schema schema = new Schema("test5", attrSchemas);

        // Add the documents to the input list
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Document input1 = new Document("_id", "645d2c73e7bc6975168e2fa8")
                .append("userid", 0)
                .append("type", "chocolate")
                .append("orderDate", format.parse(("2020-05-18T14:10:30.000Z")));
        Document input2 = new Document("_id", "645d2c73e7bc6975168e2fa9")
                .append("userid", 0)
                .append("type", "mango")
                .append("orderDate", format.parse("2021-01-15T14:10:30.000Z"));
        Document input3 = new Document("_id", "645d2c73e7bc6975168e2faa")
                .append("userid", 1)
                .append("type", "strawberry")
                .append("orderDate", format.parse("2021-03-20T11:30:05.000Z"));
        Document input4 = new Document("_id", "645d2c73e7bc6975168e2fab")
                .append("userid", 2)
                .append("type", "vanilla")
                .append("orderDate", format.parse("2021-01-15T06:31:15.000Z"));
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4);

        // We don't know "clicks" for now. We have to look at Example.output
        Document output1 = new Document("_id", 0).append("clicks", 2);
        Document output2 = new Document("_id", 2).append("clicks", 1);
        List<Document> outputs = Arrays.asList(output1, output2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input5 = new Document("_id", "645d2c73e7bc6975168e2fac")
                .append("userid", 0)
                .append("type", "vanilla")
                .append("orderDate", format.parse("2021-01-15T06:31:15.000Z"));
        List<Document> inputs2 = Arrays.asList(input1, input2, input4, input5);
        Document output3 = new Document("_id", 0).append("clicks", 3);
        Document output3_ = new Document("_id", 2).append("clicks", 1);
        List<Document> outputs2 = List.of(output3, output3_);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input6 = new Document("_id", "645d2c73e7bc6975168e2fad")
                .append("userid", 0)
                .append("type", "chocolate")
                .append("orderDate", format.parse(("2022-05-18T14:10:30.000Z")));
        Document input7 = new Document("_id", "645d2c73e7bc6975168e2fae")
                .append("userid", 2)
                .append("type", "vanilla")
                .append("orderDate", format.parse("2021-01-15T06:31:15.000Z"));
        List<Document> inputs3 = Arrays.asList(input6, input7);
        Document output4 = new Document("_id", 2).append("clicks", 1);
        List<Document> outputs3 = List.of(output4);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 24);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test6EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema name = new AttrSchema(new AccessPath(null, new Attribute("name")), ARRAY);
        AttrSchema zipcode = new AttrSchema(new AccessPath(null, new Attribute("zipcode")), ARRAY);
        attrSchemas.add(id);
        attrSchemas.add(name);
        attrSchemas.add(zipcode);
        Schema schema = new Schema("test6", attrSchemas);

        // Add the documents to the input list
        // Insert the first document
        Document input1 = new Document("_id", "645d2ecde7bc6975168e2fae")
                .append("name", Arrays.asList("Name"))
                .append("zipcode", Arrays.asList("2223"));
        Document input2 = new Document("_id", "645d2ecde7bc6975168e2faf")
                .append("name", Arrays.asList("Another", "Name"))
                .append("zipcode", Arrays.asList("2224"));
        List<Document> inputs = Arrays.asList(input1, input2);

        // We don't know "size_of_name" for now. We have to look at Example.output
        Document output1 = new Document("_id", "645d2ecde7bc6975168e2faf")
                .append("name", Arrays.asList("Another", "Name"))
                .append("zipcode", Arrays.asList("2224"))
                .append("size_of_name", 2);
        List<Document> outputs = Arrays.asList(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input3 = new Document("_id", "645d2ecde7bc6975168e2fag")
                .append("name", Arrays.asList("Name1", "Name2", "Name3"))
                .append("zipcode", Arrays.asList("2225"));
        Document input4 = new Document("_id", "645d2ecde7bc6975168e2fah")
                .append("name", Arrays.asList("Name1", "Name2", "Name3", "Name4"))
                .append("zipcode", Arrays.asList("2226"));
        List<Document> inputs2 = Arrays.asList(input3, input4);
        Document output2 = new Document("_id", "645d2ecde7bc6975168e2fag")
                .append("name", Arrays.asList("Name1", "Name2", "Name3"))
                .append("zipcode", Arrays.asList("2225"))
                .append("size_of_name", 3);
        Document output3 = new Document("_id", "645d2ecde7bc6975168e2fah")
                .append("name", Arrays.asList("Name1", "Name2", "Name3", "Name4"))
                .append("zipcode", Arrays.asList("2226"))
                .append("size_of_name", 4);;
        List<Document> outputs2 = Arrays.asList(output2, output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3 (expect empty result)
        Document input5 = new Document("_id", "645d2ecde7bc6975168e2fai")
                .append("name", List.of("Name0"))
                .append("zipcode", Arrays.asList("2227"));
        Document input6 = new Document("_id", "645d2ecde7bc6975168e2faj")
                .append("name", List.of("Name0"))
                .append("zipcode", Arrays.asList("2228"));
        List<Document> inputs3 = Arrays.asList(input5, input6);
        List<Document> outputs3 = new ArrayList<>();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 29);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test7EnumSynthesizer() throws IOException, ParseException { // benchmark #7 OOM
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema line = new AttrSchema(new AccessPath(null, new Attribute("Line")), STRING);
        AttrSchema lineStart = new AttrSchema(new AccessPath(null, new Attribute("LineStart")), DATE);
        AttrSchema SSCEXPEND = new AttrSchema(new AccessPath(null, new Attribute("SSCEXPEND")), ARRAY);
        AttrSchema Secuence = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("SSCEXPEND")), new Attribute("Secuence")), INT);
        AttrSchema title = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("SSCEXPEND")), new Attribute("Title")), INT);
        attrSchemas.add(id);
        attrSchemas.add(line);
        attrSchemas.add(lineStart);
        attrSchemas.add(SSCEXPEND);
        attrSchemas.add(Secuence);
        attrSchemas.add(title);
        Schema schema = new Schema("test7", attrSchemas);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Document input1 = new Document("_id", "645d317ce7bc6975168e2fb2")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", Arrays.asList(new Document("Secuence", 10).append("title", 1),
                        new Document("Secuence", 183).append("title", 613)));
        Document input2 = new Document("_id", "645d317ce7bc6975168e2fb3")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", Arrays.asList(new Document("Secuence", 10).append("title", 1)));
        List<Document> inputs = Arrays.asList(input1, input2);

        Document output1 = new Document("_id", "645d317ce7bc6975168e2fb2")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", Arrays.asList(new Document("Secuence", 10).append("title", 1),
                        new Document("Secuence", 183).append("title", 613)))
                .append("SSCEXPEND_count",2);
        List<Document> outputs = List.of(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2 (expect empty result)
        Document input3 = new Document("_id", "645d317ce7bc6975168e2fb4")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", new ArrayList<>());
        Document input4 = new Document("_id", "645d317ce7bc6975168e2fb5")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"));
        List<Document> inputs2 = Arrays.asList(input3, input4);
        List<Document> outputs2 = new ArrayList<>();
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input5 = new Document("_id", "645d317ce7bc6975168e2fb6")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", Arrays.asList(new Document("Secuence", 10).append("title", 1), new Document("Secuence", 183).append("title", 613)));
        Document input6 = new Document("_id", "645d317ce7bc6975168e2fb7")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", Arrays.asList(new Document("Secuence", 10).append("title", 1), new Document("Secuence", 184).append("title", 614)));
        List<Document> inputs3 = Arrays.asList(input5, input6);
        Document output2 = new Document("_id", "645d317ce7bc6975168e2fb6")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", Arrays.asList(new Document("Secuence", 10).append("title", 1), new Document("Secuence", 183).append("title", 613)))
                .append("SSCEXPEND_count",2);
        Document output3 = new Document("_id", "645d317ce7bc6975168e2fb7")
                .append("line", 1).append("lineStart", format.parse("2015-03-11T06:49:35.000Z"))
                .append("SSCEXPEND", Arrays.asList(new Document("Secuence", 10).append("title", 1), new Document("Secuence", 184).append("title", 614)))
                .append("SSCEXPEND_count",2);
        List<Document> outputs3 = Arrays.asList(output2, output3);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 32);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test8EnumSynthesizer() throws IOException { // Success
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema item = new AttrSchema(new AccessPath(null, new Attribute("item")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(item);
        Schema schema = new Schema("test8", attrSchemas);

        // Add the documents to the input list
        // Insert the first document
        Document input1 = new Document("_id", "645d366ee7bc6975168e2fb6")
                .append("item", "large box")
                .append("qty", 20);
        Document input2 = new Document("_id", "645d366ee7bc6975168e2fb7")
                .append("item", "small box")
                .append("qty", 55);
        Document input3 = new Document("_id", "645d366ee7bc6975168e2fb8")
                .append("item", "medium box")
                .append("qty", 30);
        List<Document> inputs = Arrays.asList(input1, input2, input3);

        Document output1 = new Document("item", "large box");
        Document output2 = new Document("item", "small box");
        Document output3 = new Document("item", "medium box");
        List<Document> outputs = Arrays.asList(output1, output2, output3);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        List<Document> inputs2 = Arrays.asList(input1, input2);
        List<Document> outputs2 = Arrays.asList(output1, output2);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input4 = new Document("_id", "645d366ee7bc6975168e2fb9")
                .append("item", "extra medium box")
                .append("qty", 10);
        List<Document> inputs3 = List.of(input4);
        Document output4 = new Document("item", "extra medium box");
        List<Document> outputs3 = List.of(output4);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 6);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test9EnumSynthesizer() throws IOException { // Found db.test9.aggregate([{$match: {"pictures" : {$ne: []}}}]);
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema item = new AttrSchema(new AccessPath(null, new Attribute("item")), STRING);
        AttrSchema qty = new AttrSchema(new AccessPath(null, new Attribute("qty")), INT);
        AttrSchema pictures = new AttrSchema(new AccessPath(null, new Attribute("pictures")), ARRAY);
        attrSchemas.add(id);
        attrSchemas.add(item);
        attrSchemas.add(qty);
        attrSchemas.add(pictures);
        Schema schema = new Schema("test9", attrSchemas);

        Document input1 = new Document("_id", new ObjectId("645d3839e7bc6975168e2fbb"))
                .append("item", "large box")
                .append("qty", 20)
                .append("pictures", Arrays.asList("1", "2", "3"));
        Document input2 = new Document("_id", new ObjectId("645d3839e7bc6975168e2fbc"))
                .append("item", "small box")
                .append("qty", 55)
                .append("pictures", Arrays.asList("1", "2"));
        Document input3 = new Document("_id", new ObjectId("645d3839e7bc6975168e2fbd"))
                .append("item", "medium box")
                .append("qty", 30)
                .append("pictures", Arrays.asList());
        List<Document> inputs = Arrays.asList(input1, input2, input3);
        List<Document> outputs = Arrays.asList(input1, input2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2 (expect empty result)
        Document input4 = new Document("_id", new ObjectId("645d3839e7bc6975168e2fbe"))
                .append("item", "medium box")
                .append("qty", 30)
                .append("pictures", Arrays.asList());
        List<Document> inputs2 = List.of(input4);
        List<Document> outputs2 = new ArrayList<>();
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input5 = new Document("_id", new ObjectId("645d3839e7bc6975168e2fbf"))
                .append("item", "large box")
                .append("qty", 10)
                .append("pictures", Arrays.asList("1"));
        List<Document> inputs3 = List.of(input5);
        List<Document> outputs3 = List.of(input5);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 9);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test11EnumSynthesizer() throws IOException, ParseException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema name = new AttrSchema(new AccessPath(null, new Attribute("name")), OBJECT);
        AttrSchema first_name = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("name")), new Attribute("first")), STRING);
        AttrSchema last_name = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("name")), new Attribute("last")), STRING);
        AttrSchema birth = new AttrSchema(new AccessPath(null, new Attribute("birth")), DATE);
        AttrSchema death = new AttrSchema(new AccessPath(null, new Attribute("death")), DATE);
        AttrSchema contribs = new AttrSchema(new AccessPath(null, new Attribute("contribs")), DATE);
        AttrSchema awards = new AttrSchema(new AccessPath(null, new Attribute("awards")), ARRAY);
        AttrSchema awards_award = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("awards")), new Attribute("award")), STRING);
        AttrSchema awards_year = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("awards")), new Attribute("year")), INT);
        AttrSchema awards_by = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("awards")), new Attribute("by")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(name);
        attrSchemas.add(first_name);
        attrSchemas.add(last_name);
        attrSchemas.add(birth);
        attrSchemas.add(death);
        attrSchemas.add(contribs);
        attrSchemas.add(awards);
        attrSchemas.add(awards_award);
        attrSchemas.add(awards_year);
        attrSchemas.add(awards_by);
        Schema schema = new Schema("test11", attrSchemas);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Document input1 = new Document("_id", 1)
                .append("name", new Document("first", "John").append("last", "Backus"))
                .append("birth", format.parse("1924-12-03T08:00:00.000Z"))
                .append("death", format.parse("2007-03-17T07:00:00.000Z"))
                .append("contribs", Arrays.asList("Fortran", "ALGOL", "Backus-Naur Form", "FP"))
                .append("awards", Arrays.asList(
                        new Document("award", "National Medal").append("year", 1975).append("by", "NSF"),
                        new Document("award", "Turing Award").append("year", 1977).append("by", "ACM")
                ));
        Document input2 = new Document("_id", 2)
                .append("name", new Document("first", "Brian").append("last", "Backus"))
                .append("birth", format.parse("1924-12-03T08:00:00.000Z"))
                .append("death", format.parse("2007-03-17T07:00:00.000Z"))
                .append("contribs", Arrays.asList("Fortran", "ALGOL", "Backus-Naur Form", "FP"))
                .append("awards", Arrays.asList(
                        new Document("award", "National Medal").append("year", 1979).append("by", "NSF"),
                        new Document("award", "Turing Award").append("year", 1977).append("by", "ACM")
                ));
        List<Document> inputs = Arrays.asList(input1, input2);

        Document output1 = new Document("_id", 1)
                .append("name", new Document("first", "John").append("last", "Backus"))
                .append("birth", format.parse("1924-12-03T08:00:00.000Z"))
                .append("death", format.parse("2007-03-17T07:00:00.000Z"))
                .append("contribs", Arrays.asList("Fortran", "ALGOL", "Backus-Naur Form", "FP"))
                .append("awards", Arrays.asList(
                        new Document("award", "National Medal").append("year", 1975).append("by", "NSF"),
                        new Document("award", "Turing Award").append("year", 1977).append("by", "ACM")
                ));
        List<Document> outputs = Arrays.asList(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input3 = new Document("_id", 3)
                .append("name", new Document("first", "John").append("last", "Backus"))
                .append("birth", format.parse("1924-12-03T08:00:00.000Z"))
                .append("death", format.parse("2007-03-17T07:00:00.000Z"))
                .append("contribs", Arrays.asList("Fortran", "ALGOL", "Backus-Naur Form", "FP"))
                .append("awards", Arrays.asList(
                        new Document("award", "National Medal").append("year", 1975).append("by", "NSF")
                ));
        Document input4 = new Document("_id", 4)
                .append("name", new Document("first", "Brian").append("last", "Backus"))
                .append("birth", format.parse("1924-12-03T08:00:00.000Z"))
                .append("death", format.parse("2007-03-17T07:00:00.000Z"))
                .append("contribs", Arrays.asList("Fortran", "ALGOL", "Backus-Naur Form", "FP"))
                .append("awards", Arrays.asList(
                        new Document("award", "National Medal").append("year", 1975).append("by", "NSF"),
                        new Document("award", "Turing Award").append("year", 1977).append("by", "ACM")
                ));
        List<Document> inputs2 = Arrays.asList(input3, input4);
        List<Document> outputs2 = Arrays.asList(input3, input4);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input5 = new Document("_id", 5)
                .append("name", new Document("first", "John").append("last", "Backus"))
                .append("birth", format.parse("1924-12-03T08:00:00.000Z"))
                .append("death", format.parse("2007-03-17T07:00:00.000Z"))
                .append("contribs", Arrays.asList("Fortran", "ALGOL", "Backus-Naur Form", "FP"))
                .append("awards", Arrays.asList(
                        new Document("award", "International Medal").append("year", 1975).append("by", "NSF")
                ));
        List<Document> inputs3 = List.of(input5);
        List<Document> outputs3 = new ArrayList<>();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 36);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test12EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema a = new AttrSchema(new AccessPath(null, new Attribute("a")), INT);
        AttrSchema b = new AttrSchema(new AccessPath(null, new Attribute("b")), INT);
        attrSchemas.add(id);
        attrSchemas.add(a);
        attrSchemas.add(b);
        Schema schema = new Schema("test12", attrSchemas);

        Document input1 = new Document("_id", "645d5f227deb90b336ad91b3")
                .append("a", 10)
                .append("b", 1);
        Document input2 = new Document("_id", "645d5f227deb90b336ad91b4")
                .append("a", 5)
                .append("b", 5);
        Document input3 = new Document("_id", "645d5f227deb90b336ad91b5")
                .append("a", 1)
                .append("b", 9);
        List<Document> inputs = Arrays.asList(input1, input2, input3);

        // We have to look output for "difference"
        Document output1 = new Document("_id", "645d5f227deb90b336ad91b3")
                .append("difference", 9);
        Document output2 = new Document("_id", "645d5f227deb90b336ad91b4")
                .append("difference", 0);
        Document output3 = new Document("_id", "645d5f227deb90b336ad91b5")
                .append("difference", -8);
        List<Document> outputs = Arrays.asList(output1, output2, output3);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input4 = new Document("_id", "645d5f227deb90b336ad91b6")
                .append("a", 1)
                .append("b", 10);
        Document input5 = new Document("_id", "645d5f227deb90b336ad91b7")
                .append("a", 5)
                .append("b", 5);
        List<Document> inputs2 = Arrays.asList(input4, input5);
        Document output4 = new Document("_id", "645d5f227deb90b336ad91b7")
                .append("difference", 0);
        Document output5 = new Document("_id", "645d5f227deb90b336ad91b6")
                .append("difference", -9);
        List<Document> outputs2 = Arrays.asList(output4, output5);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input6 = new Document("_id", "645d5f227deb90b336ad91b8")
                .append("a", 20)
                .append("b", 10);
        Document input7 = new Document("_id", "645d5f227deb90b336ad91b9")
                .append("a", 1)
                .append("b", 1);
        List<Document> inputs3 = Arrays.asList(input6, input7);
        Document output6 = new Document("_id", "645d5f227deb90b336ad91b8")
                .append("difference", 10);
        Document output7 = new Document("_id", "645d5f227deb90b336ad91b9")
                .append("difference", 0);
        List<Document> outputs3 = Arrays.asList(output6, output7);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 24);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test13EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema story = new AttrSchema(new AccessPath(null, new Attribute("Story")), STRING);
        AttrSchema points = new AttrSchema(new AccessPath(null, new Attribute("points")), INT);
        AttrSchema importance = new AttrSchema(new AccessPath(null, new Attribute("importance")), INT);
        attrSchemas.add(id);
        attrSchemas.add(story);
        attrSchemas.add(points);
        attrSchemas.add(importance);
        Schema schema = new Schema("test13", attrSchemas);

        Document input1 = new Document("_id", "645acf2b9eaa04866a2ea50e")
                .append("Story", "first expected")
                .append("points", 1)
                .append("importance", 1);
        Document input2 = new Document("_id", "645acf2b9eaa04866a2ea50f")
                .append("Story", "second expected")
                .append("points", 1)
                .append("importance", 3);
        Document input3 = new Document("_id", "645acf2b9eaa04866a2ea510")
                .append("Story", "third expected")
                .append("points", 5)
                .append("importance", 1);
        List<Document> inputs = Arrays.asList(input1, input2, input3);

        // We have to look output for "avg"
        Document output1 = new Document("_id", "645acf2b9eaa04866a2ea50e")
                .append("Story", "first expected")
                .append("points", 1)
                .append("importance", 1)
                .append("avg", 1);
        Document output2 = new Document("_id", "645acf2b9eaa04866a2ea50f")
                .append("Story", "second expected")
                .append("points", 1)
                .append("importance", 3)
                .append("avg", 2);
        Document output3 = new Document("_id", "645acf2b9eaa04866a2ea510")
                .append("Story", "third expected")
                .append("points", 5)
                .append("importance", 1)
                .append("avg", 3);
        List<Document> outputs = Arrays.asList(output1, output2, output3);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input4 = new Document("_id", "645acf2b9eaa04866a2ea511")
                .append("Story", "second expected")
                .append("points", 7)
                .append("importance", 1);
        Document input5 = new Document("_id", "645acf2b9eaa04866a2ea512")
                .append("Story", "first expected")
                .append("points", 5)
                .append("importance", 1);
        List<Document> inputs2 = Arrays.asList(input4, input5);
        Document output4 = new Document("_id", "645acf2b9eaa04866a2ea511")
                .append("Story", "second expected")
                .append("points", 7)
                .append("importance", 1)
                .append("avg", 4);
        Document output5 = new Document("_id", "645acf2b9eaa04866a2ea512")
                .append("Story", "first expected")
                .append("points", 5)
                .append("importance", 1)
                .append("avg", 3);
        List<Document> outputs2 = Arrays.asList(output5, output4);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input6 = new Document("_id", "645acf2b9eaa04866a2ea513")
                .append("Story", "third expected")
                .append("points", 5)
                .append("importance", 5);
        List<Document> inputs3 = Arrays.asList(input6, input4, input5);
        Document output6 = new Document("_id", "645acf2b9eaa04866a2ea513")
                .append("Story", "third expected")
                .append("points", 5)
                .append("importance", 5)
                .append("avg", 5);
        List<Document> outputs3 = Arrays.asList(output5, output4, output6);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 18);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test14EnumSynthesizer() throws IOException { // Timeout
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema item = new AttrSchema(new AccessPath(null, new Attribute("name")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(item);
        Schema schema = new Schema("test14", attrSchemas);

        Document input1 = new Document("_id", new ObjectId("645ad191452474023d99b1a3"))
                .append("name", "Brian");
        Document input2 = new Document("_id", new ObjectId("645ad191452474023d99b1a4"))
                .append("name", "Brian");
        Document input3 = new Document("_id", new ObjectId("645ad191452474023d99b1a5"))
                .append("name", "Ben");
        Document input4 = new Document("_id", new ObjectId("645ad191452474023d99b1a6"))
                .append("name", "Liam");
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4);

        Document output1 = new Document("name", "Brian");
        List<Document> outputs = List.of(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add exmaple 2 (expect 2 outputs)
        Document input5 = new Document("_id", new ObjectId("645ad191452474023d99b1a7"))
                .append("name", "Brian");
        Document input6 = new Document("_id", new ObjectId("645ad191452474023d99b1a8"))
                .append("name", "Brian");
        Document input7 = new Document("_id", new ObjectId("645ad191452474023d99b1a9"))
                .append("name", "Ben");
        Document input8 = new Document("_id", new ObjectId("645ad191452474023d99b1aa"))
                .append("name", "Liam");
        Document input9 = new Document("_id", new ObjectId("645ad191452474023d99b1ab"))
                .append("name", "Liam");
        List<Document> inputs2 = Arrays.asList(input5, input6, input7, input8, input9);
        Document output2 = new Document("name", "Brian");
        Document output3 = new Document("name", "Liam");
        List<Document> outputs2 = List.of(output2, output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3 (expect 0 output)
        Document input10 = new Document("_id", new ObjectId("645ad191452474023d99b1ac"))
                .append("name", "Brian");
        Document input11 = new Document("_id", new ObjectId("645ad191452474023d99b1ad"))
                .append("name", "Ben");
        List<Document> inputs3 = Arrays.asList(input10, input11);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 41);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test15EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema name = new AttrSchema(new AccessPath(null, new Attribute("name")), STRING);
        AttrSchema telephone = new AttrSchema(new AccessPath(null, new Attribute("telephone")), ARRAY);
        AttrSchema age = new AttrSchema(new AccessPath(null, new Attribute("age")), INT);
        attrSchemas.add(id);
        attrSchemas.add(name);
        attrSchemas.add(telephone);
        attrSchemas.add(age);
        Schema schema = new Schema("test15", attrSchemas);

        Document input1 = new Document("_id", 1)
                .append("name", "Sam")
                .append("telephone", Arrays.asList(1234, 4567, 8678))
                .append("age", 34);
        Document input2 = new Document("_id", 2)
                .append("name", "Joe")
                .append("telephone", Arrays.asList(4456, 4434))
                .append("age", 42);
        List<Document> inputs = Arrays.asList(input1, input2);

        // We have to look output for "telephoneCount"
        Document output1 = new Document("_id", 1)
                .append("name", "Sam")
                .append("telephoneCount", 3);
        Document output2 = new Document("_id", 2)
                .append("name", "Joe")
                .append("telephoneCount", 2);
        List<Document> outputs = Arrays.asList(output1, output2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input3 = new Document("_id", 3)
                .append("name", "Joe")
                .append("telephone", Arrays.asList(4456, 4434, 8678, 8679))
                .append("age", 42);
        List<Document> inputs2 = List.of(input3);
        Document output3 = new Document("_id", 3)
                .append("name", "Joe")
                .append("telephoneCount", 4);
        List<Document> outputs2 = List.of(output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input4 = new Document("_id", 4)
                .append("name", "Joe")
                .append("telephone", List.of())
                .append("age", 42);
        List<Document> inputs3 = List.of(input4);
        Document output4 = new Document("_id", 4)
                .append("name", "Joe")
                .append("telephoneCount", 0);
        List<Document> outputs3 = List.of(output4);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 23);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test16EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema province = new AttrSchema(new AccessPath(null, new Attribute("province")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(province);
        Schema schema = new Schema("test16", attrSchemas);

        Document input1 = new Document("_id", "645adfcf452474023d99b1b3")
                .append("province", "BC");
        Document input2 = new Document("_id", "645adfcf452474023d99b1b4")
                .append("province", "BC");
        Document input3 = new Document("_id", "645adfcf452474023d99b1b5")
                .append("province", "ON");
        Document input4 = new Document("_id", "645adfcf452474023d99b1b6")
                .append("province", "ON");
        Document input5 = new Document("_id", "645adfcf452474023d99b1b7")
                .append("province", "ON");
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4, input5);

        // We have to look output for "count"
        Document output1 = new Document("_id", "ON")
                .append("count", 3);
        Document output2 = new Document("_id", "BC")
                .append("count", 2);
        List<Document> outputs = Arrays.asList(output1, output2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input6 = new Document("_id", "645adfcf452474023d99b1b8")
                .append("province", "BC");
        Document input7 = new Document("_id", "645adfcf452474023d99b1b9")
                .append("province", "BC");
        Document input8 = new Document("_id", "645adfcf452474023d99b1ba")
                .append("province", "BC");
        Document input9 = new Document("_id", "645adfcf452474023d99b1bb")
                .append("province", "BC");
        List<Document> inputs2 = Arrays.asList(input6, input7, input8, input9);
        Document output3 = new Document("_id", "BC")
                .append("count", 4);
        List<Document> outputs2 = List.of(output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input10 = new Document("_id", "645adfcf452474023d99b1bc");
        List<Document> inputs3 = List.of(input10);
        Document output4 = new Document("_id", null).append("count", 1);
        List<Document> outputs3 = List.of(output4);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 17);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test17EnumSynthesizer() throws IOException, ParseException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema title = new AttrSchema(new AccessPath(null, new Attribute("title")), STRING);
        AttrSchema author = new AttrSchema(new AccessPath(null, new Attribute("author")), STRING);
        AttrSchema posted = new AttrSchema(new AccessPath(null, new Attribute("posted")), OBJECT);
        AttrSchema pageViews = new AttrSchema(new AccessPath(null, new Attribute("pageViews")), INT);
        AttrSchema tags = new AttrSchema(new AccessPath(null, new Attribute("pageViews")), ARRAY);
        attrSchemas.add(id);
        attrSchemas.add(title);
        attrSchemas.add(author);
        attrSchemas.add(posted);
        attrSchemas.add(pageViews);
        attrSchemas.add(tags);
        Schema schema = new Schema("test17", attrSchemas);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Document input1 = new Document("_id", "645d646a49aa0491bf346706")
                .append("title", "this is my title")
                .append("author", "bob")
                .append("posted", format.parse("2023-05-11T21:55:54.131Z"))
                .append("pageViews", 5)
                .append("tags", Arrays.asList("fun", "good", "fun"))
                .append("comments", Arrays.asList(
                        new Document("author", "joe").append("text", "this is cool"),
                        new Document("author", "sam").append("text", "this is bad")
                ))
                .append("other", new Document("foo", 5));
        List<Document> inputs = Arrays.asList(input1);

        Document output1 = new Document("_id", "645d646a49aa0491bf346706")
                .append("title", "this is my title")
                .append("author", "bob")
                .append("tags", "fun");
        Document output2 = new Document("_id", "645d646a49aa0491bf346706")
                .append("title", "this is my title")
                .append("author", "bob")
                .append("tags", "good");
        Document output3 = new Document("_id", "645d646a49aa0491bf346706")
                .append("title", "this is my title")
                .append("author", "bob")
                .append("tags", "fun");
        List<Document> outputs = Arrays.asList(output1, output2, output3);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input2 = new Document("_id", "645d646a49aa0491bf346707")
                .append("title", "this is my title")
                .append("author", "bob")
                .append("posted", format.parse("2023-05-11T21:55:54.131Z"))
                .append("pageViews", 5)
                .append("tags", List.of("fun"))
                .append("comments", Arrays.asList(
                        new Document("author", "joe").append("text", "this is cool"),
                        new Document("author", "sam").append("text", "this is bad")
                ))
                .append("other", new Document("foo", 5));
        Document input3 = new Document("_id", "645d646a49aa0491bf346708")
                .append("title", "Life of Pi")
                .append("author", "Yann Martel")
                .append("posted", format.parse("2001-09-11T21:55:54.131Z"))
                .append("pageViews", 5)
                .append("tags", Arrays.asList("fiction", "adventure", "classics"))
                .append("comments", List.of(new Document("author", "joe").append("text", "this is cool")))
                .append("other", new Document("foo", 5));
        List<Document> inputs2 = Arrays.asList(input2, input3);
        Document output4 = new Document("_id", "645d646a49aa0491bf346707")
                .append("title", "this is my title")
                .append("author", "bob")
                .append("tags", "fun");
        Document output5 = new Document("_id", "645d646a49aa0491bf346708")
                .append("title", "Life of Pi")
                .append("author", "Yann Martel")
                .append("tags", "fiction");
        Document output6 = new Document("_id", "645d646a49aa0491bf346708")
                .append("title", "Life of Pi")
                .append("author", "Yann Martel")
                .append("tags", "adventure");
        Document output7 = new Document("_id", "645d646a49aa0491bf346708")
                .append("title", "Life of Pi")
                .append("author", "Yann Martel")
                .append("tags", "classics");
        List<Document> outputs2 = Arrays.asList(output4, output5, output6);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3 (expect empty result)
        Document input4 = new Document("_id", "645d646a49aa0491bf346709")
                .append("title", "Life of Pi")
                .append("author", "Yann Martel")
                .append("posted", format.parse("2001-09-11T21:55:54.131Z"))
                .append("pageViews", 5)
                .append("tags", List.of())
                .append("comments", List.of())
                .append("other", new Document("foo", 5));
        List<Document> inputs3 = List.of();
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 23);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test18EnumSynthesizer() throws IOException, ParseException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema item = new AttrSchema(new AccessPath(null, new Attribute("item")), STRING);
        AttrSchema price = new AttrSchema(new AccessPath(null, new Attribute("price")), INT);
        AttrSchema quantity = new AttrSchema(new AccessPath(null, new Attribute("quantity")), INT);
        AttrSchema date = new AttrSchema(new AccessPath(null, new Attribute("pageViews")), DATE);
        attrSchemas.add(id);
        attrSchemas.add(item);
        attrSchemas.add(price);
        attrSchemas.add(quantity);
        attrSchemas.add(date);
        Schema schema = new Schema("test18", attrSchemas);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Document input1 = new Document("_id", 1)
                .append("item", "abc")
                .append("price", 10)
                .append("quantity", 2)
                .append("date", format.parse("2014-01-01T08:00:00.000Z"));
        Document input2 = new Document("_id", 2)
                .append("item", "jkl")
                .append("price", 20)
                .append("quantity", 1)
                .append("date", format.parse("2014-02-03T09:00:00.000Z"));
        Document input3 = new Document("_id", 3)
                .append("item", "xyz")
                .append("price", 5)
                .append("quantity", 5)
                .append("date", format.parse("2014-02-03T09:05:00.000Z"));
        Document input4 = new Document("_id", 4)
                .append("item", "abc")
                .append("price", 10)
                .append("quantity", 10)
                .append("date", format.parse("2014-02-15T08:00:00.000Z"));
        Document input5 = new Document("_id", 5)
                .append("item", "xyz")
                .append("price", 5)
                .append("quantity", 10)
                .append("date", format.parse("2014-02-15T09:05:00.000Z"));
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4, input5);

        Document output1 = new Document("_id", new Document())
                .append("minPrice", 5);
        List<Document> outputs = Arrays.asList(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input6 = new Document("_id", 6)
                .append("item", "abc")
                .append("price", 10)
                .append("quantity", 2)
                .append("date", format.parse("2014-01-01T08:00:00.000Z"));
        List<Document> inputs2 = List.of(input6);
        Document output2 = new Document("_id", new Document())
                .append("minPrice", 10);
        List<Document> outputs2 = List.of(output2);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input7 = new Document("_id", 7)
                .append("item", "abc")
                .append("price", 11)
                .append("quantity", 10)
                .append("date", format.parse("2014-02-15T08:00:00.000Z"));
        Document input8 = new Document("_id", 8)
                .append("item", "xyz")
                .append("price", 4)
                .append("quantity", 10)
                .append("date", format.parse("2014-02-15T09:05:00.000Z"));
        List<Document> inputs3 = Arrays.asList(input7, input8);
        Document output3 = new Document("_id", new Document())
                .append("minPrice", 4);
        List<Document> outputs3 = List.of(output3);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 15);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test19EnumSynthesizer() throws IOException, ParseException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema timecreated = new AttrSchema(new AccessPath(null, new Attribute("timecreated")), INT);
        AttrSchema unusual = new AttrSchema(new AccessPath(null, new Attribute("unusual")), ARRAY);
        AttrSchema unusual_id = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("unusual")), new Attribute("id")), INT);
        AttrSchema unusual_original_id = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("unusual")), new Attribute("original_id")), INT);
        AttrSchema unusual_particleEffect = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("unusual")), new Attribute("_particleEffect")), INT);
        attrSchemas.add(id);
        attrSchemas.add(timecreated);
        attrSchemas.add(unusual);
        attrSchemas.add(unusual_id);
        attrSchemas.add(unusual_original_id);
        attrSchemas.add(unusual_particleEffect);
        Schema schema = new Schema("test18", attrSchemas);

        Document input1 = new Document("_id", "76561198045636214")
                .append("timecreated", 1311148549)
                .append("unusual", Arrays.asList(
                        new Document("id", 1960169991)
                                .append("original_id", 698672623)
                                .append("defindex", 313)
                                .append("particleEffect", 19),
                        new Document("id", 965349033)
                                .append("original_id", 931933064)
                                .append("defindex", 363)
                                .append("particleEffect", 6)
                ));
        List<Document> inputs = Arrays.asList(input1);

        Document output1 = new Document("_id", "76561198045636214")
                .append("timecreated", 1311148549)
                .append("unusual", Arrays.asList(
                        new Document("id", 1960169991)
                                .append("original_id", 698672623)
                                .append("defindex", 313)
                                .append("particleEffect", 19),
                        new Document("id", 965349033)
                                .append("original_id", 931933064)
                                .append("defindex", 363)
                                .append("particleEffect", 6)
                ));
        List<Document> outputs = Arrays.asList(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input2 = new Document("_id", "76561198045636215")
                .append("timecreated", 1311148549)
                .append("unusual", Arrays.asList(
                        new Document("id", 1960169992)
                                .append("original_id", 698672623)
                                .append("defindex", 313)
                                .append("particleEffect", 19),
                        new Document("id", 965349033)
                                .append("original_id", 931933064)
                                .append("defindex", 363)
                                .append("particleEffect", 20)
                ));
        Document input3 = new Document("_id", "76561198045636216")
                .append("timecreated", 1311148549)
                .append("unusual", List.of(
                        new Document("id", 965349035)
                                .append("original_id", 931933064)
                                .append("defindex", 363)
                                .append("particleEffect", 6)
                ));
        List<Document> inputs2 = Arrays.asList(input2, input3);
        List<Document> outputs2 = Arrays.asList(input2, input3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input4 = new Document("_id", "76561198045636217")
                .append("timecreated", 1311148549)
                .append("unusual", List.of(
                        new Document("id", 965349035)
                                .append("original_id", 931933064)
                                .append("defindex", 360)
                                .append("particleEffect", 6)
                ));
        List<Document> inputs3 = List.of(input4);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 29);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test20EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema first_name = new AttrSchema(new AccessPath(null, new Attribute("first_name")), STRING);
        AttrSchema last_name = new AttrSchema(new AccessPath(null, new Attribute("last_name")), ARRAY);
        AttrSchema phone = new AttrSchema(new AccessPath(null, new Attribute("phone")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(first_name);
        attrSchemas.add(last_name);
        attrSchemas.add(phone);
        Schema schema = new Schema("test20", attrSchemas);

        Document input1 = new Document("_id", "645af9e9452474023d99b1bc")
                .append("first_name", "john")
                .append("last_name", "hersh")
                .append("phone", "2222");
        Document input2 = new Document("_id", "645af9e9452474023d99b1bd")
                .append("first_name", "john")
                .append("last_name", "hersh")
                .append("phone", "12345678");
        Document input3 = new Document("_id", "645af9e9452474023d99b1be")
                .append("first_name", "elton")
                .append("last_name", "john")
                .append("phone", "12345678");
        Document input4 = new Document("_id", "645af9e9452474023d99b1bf")
                .append("first_name", "eltonush")
                .append("last_name", "john")
                .append("phone", "5555");
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4);

        Document output1 = new Document("_id", "645af9e9452474023d99b1bd")
                .append("first_name", "john")
                .append("last_name", "hersh")
                .append("phone", "12345678");
        Document output2 = new Document("_id", "645af9e9452474023d99b1be")
                .append("first_name", "elton")
                .append("last_name", "john")
                .append("phone", "12345678");
        List<Document> outputs = Arrays.asList(output1, output2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input5 = new Document("_id", "645af9e9452474023d99b1c0")
                .append("first_name", "johnathon")
                .append("last_name", "hersh")
                .append("phone", "12345678");
        Document input6 = new Document("_id", "645af9e9452474023d99b1c1")
                .append("first_name", "elton")
                .append("last_name", "john")
                .append("phone", "12345678");
        List<Document> inputs2 = Arrays.asList(input5, input6);
        Document output3 = input6;
        List<Document> outputs2 = List.of(output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input7 = new Document("_id", "645af9e9452474023d99b1c2")
                .append("first_name", "elton")
                .append("last_name", "johnathon")
                .append("phone", "123456789");
        List<Document> inputs3 = List.of(input7);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 34);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test21EnumSynthesizer() throws IOException { // Success
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema a = new AttrSchema(new AccessPath(null, new Attribute("a")), INT);
        attrSchemas.add(id);
        attrSchemas.add(a);
        Schema schema = new Schema("test21", attrSchemas);

        Document input1 = new Document("_id", "645afc88452474023d99b1c5")
                .append("a", 1);
        Document input2 = new Document("_id", "645afc88452474023d99b1c6")
                .append("a", 2);
        Document input3 = new Document("_id", "645afc88452474023d99b1c7")
                .append("a", 3);
        Document input4 = new Document("_id", "645afc88452474023d99b1c8")
                .append("a", 4);
        Document input5 = new Document("_id", "645afc88452474023d99b1c9")
                .append("a", 5);
        Document input6 = new Document("_id", "645afc88452474023d99b1ca")
                .append("a", 6);
        Document input7 = new Document("_id", "645afc88452474023d99b1cb")
                .append("a", 7);
        Document input8 = new Document("_id", "645afc88452474023d99b1cc")
                .append("a", 8);
        Document input9 = new Document("_id", "645afc88452474023d99b1cd")
                .append("a", 9);
        Document input10 = new Document("_id", "645afc88452474023d99b1ce")
                .append("a", 10);
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4, input5, input6, input7, input8, input9, input10);

        Document output1 = new Document("_id", "645afc88452474023d99b1c9")
                .append("a", 5);
        Document output2 = new Document("_id", "645afc88452474023d99b1ca")
                .append("a", 6);
        Document output3 = new Document("_id", "645afc88452474023d99b1cb")
                .append("a", 7);
        List<Document> outputs = Arrays.asList(output1, output2, output3);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input11 = new Document("_id", "645afc88452474023d99b1cf")
                .append("a", 11);
        List<Document> inputs2 = Arrays.asList(input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11);
        List<Document> outputs2 = Arrays.asList(output1, output2, output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        List<Document> inputs3 = Arrays.asList(input1, input2, input3, input4, input5);
        List<Document> outputs3 = List.of(output1);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 10);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test23EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema userId = new AttrSchema(new AccessPath(null, new Attribute("userId")), INT);
        AttrSchema name = new AttrSchema(new AccessPath(null, new Attribute("name")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(userId);
        attrSchemas.add(name);
        Schema schema = new Schema("test23", attrSchemas);

        Document input1 = new Document("_id", "645b050c452474023d99b1d1")
                .append("userId", 1)
                .append("name", "Brian");
        Document input2 = new Document("_id", "645b050c452474023d99b1d2")
                .append("userId", 1)
                .append("name", "Brian");
        Document input3 = new Document("_id", "645b050c452474023d99b1d3")
                .append("userId", 2)
                .append("name", "Ben");
        Document input4 = new Document("_id", "645b050c452474023d99b1d4")
                .append("userId", 2)
                .append("name", "Ben");
        Document input5 = new Document("_id", "645b050c452474023d99b1d5")
                .append("userId", 3)
                .append("name", "Ryan");
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4, input5);

        // We have to look output for "count"
        Document output1 = new Document("count", 2)
                .append("userId", 1)
                .append("name", "Brian");
        Document output2 = new Document("count", 2)
                .append("userId", 2)
                .append("name", "Ben");
        List<Document> outputs = Arrays.asList(output1, output2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input6 = new Document("_id", "645b050c452474023d99b1d6")
                .append("userId", 3)
                .append("name", "Ryan");
        List<Document> inputs2 = Arrays.asList(input1, input2, input5, input6);
        Document output3 = new Document("count", 2)
                .append("userId", 3)
                .append("name", "Ryan");
        List<Document> outputs2 = Arrays.asList(output1, output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input7 = new Document("_id", "645b050c452474023d99b1d7")
                .append("userId", 1)
                .append("name", "Brian");
        List<Document> inputs3 = Arrays.asList(input1, input2, input7, input5, input6);
        Document output4 = new Document("count", 3)
                .append("userId", 1)
                .append("name", "Brian");
        List<Document> outputs3 = Arrays.asList(output4, output3);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 18);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test24EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema country = new AttrSchema(new AccessPath(null, new Attribute("country")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(country);
        Schema schema = new Schema("test24", attrSchemas);

        Document input1 = new Document("_id", "645b0766452474023d99b1d8")
                .append("country", "Spain");
        Document input2 = new Document("_id", "645b0766452474023d99b1d9")
                .append("country", "Spain");
        Document input3 = new Document("_id", "645b0766452474023d99b1da")
                .append("country", "France");
        Document input4 = new Document("_id", "645b0766452474023d99b1db")
                .append("country", "France");
        Document input5 = new Document("_id", "645b0766452474023d99b1dc")
                .append("country", "Canada");
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4, input5);

        // We have to look output for renamed attributes
        Document output1 = new Document("countOfDistinctCountries", 3);
        List<Document> outputs = Arrays.asList(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input6 = new Document("_id", "645b0766452474023d99b1dd")
                .append("country", "Spain");
        Document input7 = new Document("_id", "645b0766452474023d99b1de")
                .append("country", "Iceland");
        List<Document> inputs2 = Arrays.asList(input6, input7);
        Document output2 = new Document("countOfDistinctCountries", 2);
        List<Document> outputs2 = List.of(output2);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input8 = new Document("_id", "645b0766452474023d99b1df")
                .append("country", "Spain");
        Document input9 = new Document("_id", "645b0766452474023d99b1f0")
                .append("country", "Spain");
        List<Document> inputs3 = Arrays.asList(input8, input9);
        Document output3 = new Document("countOfDistinctCountries", 1);
        List<Document> outputs3 = List.of(output3);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 14);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test43EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema a1 = new AttrSchema(new AccessPath(null, new Attribute("a")), OBJECT);
        AttrSchema a2 = new AttrSchema(new AccessPath(null, new Attribute("a")), ARRAY);
        AttrSchema ab = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("a")), new Attribute("b")), STRING);
        AttrSchema ac = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("a")), new Attribute("c")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(a1);
        attrSchemas.add(a2);
        attrSchemas.add(ab);
        attrSchemas.add(ac);
        Schema schema = new Schema("test43", attrSchemas);

        Document input1 = new Document("_id", 1)
                .append("a", List.of(new Document("b", "").append("c", "")));
        Document input2 = new Document("_id", 2)
                .append("a", new Document("b", "").append("c", ""));
        List<Document> inputs = Arrays.asList(input1, input2);

        Document output1 = new Document("_id", 2)
                .append("a", new Document("b", "").append("c", ""));
        List<Document> outputs = List.of(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input3 = new Document("_id", 3)
                .append("a", Arrays.asList(new Document("first", ""), new Document("second", "")));
        Document input4 = new Document("_id", 4)
                .append("a", new Document("b", ""));
        List<Document> inputs2 = Arrays.asList(input4, input3);
        List<Document> outputs2 = List.of(input4);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3 (expect no result)
        List<Document> inputs3 = Arrays.asList(input1, input3);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 15);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test51EnumSynthesizer() throws IOException { // Fail
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema item = new AttrSchema(new AccessPath(null, new Attribute("item")), STRING);
        AttrSchema quarter = new AttrSchema(new AccessPath(null, new Attribute("quarter")), STRING);
        AttrSchema description = new AttrSchema(new AccessPath(null, new Attribute("description")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(item);
        attrSchemas.add(quarter);
        attrSchemas.add(description);
        Schema schema = new Schema("test51", attrSchemas);

        Document input1 = new Document("_id", 1)
                .append("item", "ABC1")
                .append("quarter", "13Q1")
                .append("description", "product 1");
        Document input2 = new Document("_id", 2)
                .append("item", "ABC2")
                .append("quarter", "13Q4")
                .append("description", "product 2");
        Document input3 = new Document("_id", 3)
                .append("item", "XYZ1")
                .append("quarter", "14Q2")
                .append("description", null);
        List<Document> inputs = Arrays.asList(input1, input2, input3);

        Document output1 = new Document("_id", 1)
                .append("item", "ABC1")
                .append("yearSubstring", "13")
                .append("quarterSubstring", "Q1");
        Document output2 = new Document("_id", 2)
                .append("item", "ABC2")
                .append("yearSubstring", "13")
                .append("quarterSubstring", "Q4");
        Document output3 = new Document("_id", 3)
                .append("item", "XYZ1")
                .append("yearSubstring", "14")
                .append("quarterSubstring", "Q2");
        List<Document> outputs = Arrays.asList(output1, output2, output3);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input4 = new Document("_id", 4)
                .append("item", "XYZ1")
                .append("quarter", "23Q3")
                .append("description", null);
        Document input5 = new Document("_id", 5)
                .append("item", "XYZ1")
                .append("quarter", "23Q4")
                .append("description", "product 3");
        List<Document> inputs2 = Arrays.asList(input4, input5);
        Document output4 = new Document("_id", 4)
                .append("item", "XYZ1")
                .append("yearSubstring", "23")
                .append("quarterSubstring", "Q3");
        Document output5 = new Document("_id", 5)
                .append("item", "XYZ1")
                .append("yearSubstring", "23")
                .append("quarterSubstring", "Q4");
        List<Document> outputs2 = Arrays.asList(output4, output5);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input6 = new Document("_id", 6)
                .append("item", "QWE")
                .append("quarter", "23Q4");
        List<Document> inputs3 = List.of(input6);
        Document output6 = new Document("_id", 6)
                .append("item", "QWE")
                .append("yearSubstring", "23")
                .append("quarterSubstring", "Q4");
        List<Document> outputs3 = List.of(output6);
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 35);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test27EnumSynthesizer() throws IOException, ParseException{ // No new Date() in grammar
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema item = new AttrSchema(new AccessPath(null, new Attribute("name")), STRING);
        AttrSchema start_date = new AttrSchema(new AccessPath(null, new Attribute("start_date")), DATE);
        AttrSchema end_date = new AttrSchema(new AccessPath(null, new Attribute("end_date")), DATE);
        attrSchemas.add(id);
        attrSchemas.add(item);
        attrSchemas.add(start_date);
        attrSchemas.add(end_date);
        Schema schema = new Schema("test27", attrSchemas);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Document input1 = new Document("_id", 1)
                .append("name", "a")
                .append("start_date", format.parse("2023-01-21T12:13:34.151Z"))
                .append("end_date", format.parse("2023-09-24T12:13:34.000Z"));
        Document input2 = new Document("_id", 2)
                .append("name", "b")
                .append("start_date", format.parse("2021-09-21T12:13:34.151Z"))
                .append("end_date", format.parse("2021-09-22T12:13:34.000Z"));
        Document input3 = new Document("_id", 3)
                .append("name", "c")
                .append("start_date", format.parse("2021-09-21T12:13:34.151Z"))
                .append("end_date", format.parse("2021-09-21T12:13:34.000Z"));
        List<Document> inputs = Arrays.asList(input1, input2, input3);
        List<Document> outputs = List.of(input1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input4 = new Document("_id", 4)
                .append("name", "d")
                .append("start_date", format.parse("2023-01-21T12:13:34.151Z"))
                .append("end_date", format.parse("2024-09-24T12:13:34.000Z"));
        Document input5 = new Document("_id", 5)
                .append("name", "e")
                .append("start_date", format.parse("2023-01-21T12:13:34.151Z"))
                .append("end_date", format.parse("2025-09-24T12:13:34.000Z"));
        List<Document> inputs2 = Arrays.asList(input4, input5);
        List<Document> outputs2 = Arrays.asList(input4, input5);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input6 = new Document("_id", 6)
                .append("name", "f")
                .append("start_date", format.parse("2023-01-21T12:13:34.151Z"))
                .append("end_date", format.parse("2023-08-24T12:13:34.000Z"));
        List<Document> inputs3 = List.of(input6);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 25);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test28EnumSynthesizer() throws IOException { // Found db.collection.aggregate([{$match:{"ln": { $exists: true }}}])
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema fn = new AttrSchema(new AccessPath(null, new Attribute("fn")), STRING);
        AttrSchema ln = new AttrSchema(new AccessPath(null, new Attribute("ln")), STRING);
        AttrSchema score = new AttrSchema(new AccessPath(null, new Attribute("score")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(fn);
        attrSchemas.add(ln);
        attrSchemas.add(score);
        Schema schema = new Schema("test28", attrSchemas);

        Document input1 = new Document("_id", 1).append("fn", "sagar").append("ln", "Varpe");
        Document input2 = new Document("_id", 2).append("fn", "sag").append("score", "10");
        Document input3 = new Document("_id", 3).append("ln", "ln1").append("score", "10");
        Document input4 = new Document("_id", 4).append("ln", "ln2");
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4);
        List<Document> outputs = Arrays.asList(input1, input3, input4);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input5 = new Document("_id", 5).append("fn", "sag").append("ln", "ln3");
        Document input6 = new Document("_id", 6).append("ln", "ln1").append("score", "10");
        List<Document> inputs2 = Arrays.asList(input5, input6);
        List<Document> outputs2 = Arrays.asList(input5, input6);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input7 = new Document("_id", 7).append("fn", "sag").append("score", "1");
        Document input8 = new Document("_id", 8).append("fn", "ln1");
        List<Document> inputs3 = Arrays.asList(input7, input8);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 7);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test32EnumSynthesizer() throws IOException {
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema shapes = new AttrSchema(new AccessPath(null, new Attribute("shapes")), ARRAY);
        AttrSchema shapes_shape = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("shapes")), new Attribute("shape")), STRING);
        AttrSchema shapes_color = new AttrSchema(new AccessPath(new AccessPath(null, new Attribute("shapes")), new Attribute("color")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(shapes);
        attrSchemas.add(shapes_shape);
        attrSchemas.add(shapes_color);
        Schema schema = new Schema("test32", attrSchemas);

        Document input1 = new Document("_id", "562e7c594c12942f08fe4192")
                .append("shapes", Arrays.asList(new Document("shape", "square").append("color", "blue"),new Document("shape", "circle").append("color", "red")));
        Document input2 = new Document("_id", "562e7c594c12942f08fe4193")
                .append("shapes", Arrays.asList(new Document("shape", "square").append("color", "black"),new Document("shape", "circle").append("color", "green")));
        List<Document> inputs = Arrays.asList(input1, input2);

        Document output1 = new Document("shapes", List.of(new Document("shape", "circle").append("color", "red")));
        List<Document> outputs = List.of(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input3 = new Document("_id", "562e7c594c12942f08fe4194")
                .append("shapes", Arrays.asList(new Document("shape", "square").append("color", "blue"),new Document("shape", "circle").append("color", "red")));
        Document input4 = new Document("_id", "562e7c594c12942f08fe4195")
                .append("shapes", List.of(new Document("shape", "rectangle").append("color", "red")));
        List<Document> inputs2 = Arrays.asList(input3, input4);
        Document output2 = new Document("shapes", List.of(new Document("shape", "circle").append("color", "red")));
        Document output3 = new Document("shapes", List.of(new Document("shape", "rectangle").append("color", "red")));
        List<Document> outputs2 = Arrays.asList(output2, output3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input5 = new Document("_id", "562e7c594c12942f08fe4196")
                .append("shapes", List.of(new Document("shape", "rectangle").append("color", "blue")));
        Document input6 = new Document("_id", "562e7c594c12942f08fe4197")
                .append("shapes", List.of(new Document("shape", "circle").append("color", "green")));
        List<Document> inputs3 = Arrays.asList(input5, input6);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 25);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test41EnumSynthesizer() throws IOException { // Found db.collection.aggregate([{$match: {"post": {$ne: ""}}}]);
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema author = new AttrSchema(new AccessPath(null, new Attribute("author")), STRING);
        AttrSchema post = new AttrSchema(new AccessPath(null, new Attribute("post")), STRING);
        attrSchemas.add(id);
        attrSchemas.add(author);
        attrSchemas.add(post);
        Schema schema = new Schema("test41", attrSchemas);

        Document input1 = new Document("_id", 1).append("author", "me").append("post", "");
        Document input2 = new Document("_id", 2).append("author", "you").append("post", "how to query");
        List<Document> inputs = Arrays.asList(input1, input2);
        List<Document> outputs = List.of(input2);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input3 = new Document("_id", 3).append("author", "me").append("post", "m");
        List<Document> inputs2 = Arrays.asList(input2, input3);
        List<Document> outputs2 = Arrays.asList(input2, input3);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input4 = new Document("_id", 4).append("author", "me").append("post", "");
        Document input5 = new Document("_id", 5).append("author", "you").append("post", "");
        List<Document> inputs3 = Arrays.asList(input4, input5);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        synthesizer.synthesize(grammar, schema, examples, 10);
        File file = new File("./build/possible_program.txt");
        Assertions.assertNotEquals(0, file.length());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void test50EnumSynthesizer() throws IOException { // New attribute "count" in outputs
        Synthesizer synthesizer = new EnumSynthesizer();
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar2.txt");

        List<AttrSchema> attrSchemas = new ArrayList<>();
        AttrSchema id = new AttrSchema(new AccessPath(null, new Attribute("_id")), OBJECT_ID);
        AttrSchema a = new AttrSchema(new AccessPath(null, new Attribute("a")), INT);
        AttrSchema b = new AttrSchema(new AccessPath(null, new Attribute("b")), INT);
        AttrSchema c = new AttrSchema(new AccessPath(null, new Attribute("c")), INT);
        attrSchemas.add(id);
        attrSchemas.add(a);
        attrSchemas.add(b);
        attrSchemas.add(c);
        Schema schema = new Schema("test50", attrSchemas);

        Document input1 = new Document("_id", 1).append("a", 1).append("b", 2).append("c", 3);
        Document input2 = new Document("_id", 2).append("a", 1).append("b", 2).append("c", 4);
        Document input3 = new Document("_id", 3).append("a", 0).append("b", 2).append("c", 3);
        Document input4 = new Document("_id", 4).append("a", 3).append("b", 2).append("c", 4);
        List<Document> inputs = Arrays.asList(input1, input2, input3, input4);

        Document output1 = new Document("_id", new Document("a", 1).append("b", 2)).append("count", 2);
        List<Document> outputs = List.of(output1);

        Example example = new Example(inputs, outputs);
        List<Example> examples = new ArrayList<>();
        examples.add(example);

        // Add example 2
        Document input5 = new Document("_id", 5).append("a", 1).append("b", 2).append("c", 3);
        List<Document> inputs2 = Arrays.asList(input1, input2, input5, input4);
        Document output2 = new Document("_id", new Document("a", 1).append("b", 2)).append("count", 3);
        List<Document> outputs2 = List.of(output2);
        Example example2 = new Example(inputs2, outputs2);
        examples.add(example2);

        // Add example 3
        Document input6 = new Document("_id", 6).append("a", 0).append("b", 2).append("c", 3);
        List<Document> inputs3 = Arrays.asList(input6, input3);
        List<Document> outputs3 = List.of();
        Example example3 = new Example(inputs3, outputs3);
        examples.add(example3);

        Program program = synthesizer.synthesize(grammar, schema, examples, 29);
        assertNotNull(program);
    }
}