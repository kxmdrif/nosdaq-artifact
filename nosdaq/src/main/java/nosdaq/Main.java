package nosdaq;
import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.synth_new.AbstSynthesizer;
import nosdaq.synth_new.ISynthesizer;
import nosdaq.trans.DSLTranslator;
import nosdaq.trans.MongoDBTranslator;
import nosdaq.utils.BenchmarkLoader;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    // these global variables need to be set manually in AbstSynthesizerTest
    public static String EXPERIMENT;
    public static String RESULT_FOLDER;
    public static void main(String[] args) throws IOException {
        String option = args[0];
        String benchmarkId = args[1];
        EXPERIMENT = args[2];
        RESULT_FOLDER = args[3];

        switch (option) {
            case "FULL", "NO_LENGTH", "NO_TYPE", "NO_BOTH":
                runSingle(getRunOption(option), Integer.parseInt(benchmarkId));
                break;
            default:
                System.out.println("\u001B[31m" + "Invalid option!" + "\u001B[0m");

        }
    }


    private static void runSingle(RunOption runOption, int benchmarkNo) throws IOException {
        ISynthesizer synthesizer = new AbstSynthesizer();
        BenchmarkLoader benchmarkLoader = new BenchmarkLoader(benchmarkNo);
        List<Example> examples = benchmarkLoader.loadExamples();
        Schema inputSchema = benchmarkLoader.loadInputSchema();
        Schema outputSchema = benchmarkLoader.loadOutputSchema();
        List<Schema> foreignSchema = benchmarkLoader.loadForeignSchema();
        List<String> constants = benchmarkLoader.loadConstants();
        Program program = synthesizer.synthesize(runOption, inputSchema, outputSchema, examples, constants, foreignSchema);
        System.out.println(program);
        System.out.println(MongoDBTranslator.translate(program));
        System.out.println(DSLTranslator.translate(program));
    }

    private static RunOption getRunOption(String option) {
        return switch (option) {
            case "FULL" -> RunOption.FULL;
            case "NO_LENGTH" -> RunOption.NO_LENGTH;
            case "NO_TYPE" -> RunOption.NO_TYPE;
            case "NO_BOTH" -> RunOption.NO_BOTH;
            default -> null;
        };
    }

    public static String getOutputFolder(RunOption runOption) {
        if (RESULT_FOLDER == null) {
            throw new RuntimeException("Result folder not set!");
        }
        return switch (runOption) {
            case FULL -> RESULT_FOLDER + "/Nosdaq";
            case NO_LENGTH -> RESULT_FOLDER + "/Nosdaq_No_Length";
            case NO_TYPE -> RESULT_FOLDER + "/Nosdaq_No_Type";
            case NO_BOTH -> RESULT_FOLDER + "/Nosdaq_No_Both";
        };
    }
}
