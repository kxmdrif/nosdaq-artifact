package nosdaq.synth_new;

import nosdaq.RunOption;
import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.Schema;
import nosdaq.trans.DSLTranslator;
import nosdaq.trans.MongoDBTranslator;
import nosdaq.utils.BenchmarkLoader;
import nosdaq.utils.EXP_MODE;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static nosdaq.Main.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@TestMethodOrder(BenchmarkOrder.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class AbstSynthesizerTest {
    private static final int TIMEOUT_MIN = 5;

    private void runBenchmark(int benchmarkNo) throws IOException {
        ISynthesizer synthesizer = new AbstSynthesizer();
        BenchmarkLoader benchmarkLoader = new BenchmarkLoader(benchmarkNo);
        List<Example> examples = benchmarkLoader.loadExamples();
        Schema inputSchema = benchmarkLoader.loadInputSchema();
        Schema outputSchema = benchmarkLoader.loadOutputSchema();
        List<Schema> foreignSchema = benchmarkLoader.loadForeignSchema();
        List<String> constants = benchmarkLoader.loadConstants();
        Program program = synthesizer.synthesize(RunOption.FULL, inputSchema, outputSchema, examples, constants, foreignSchema);
        assertNotNull(program);
        System.out.println(program);
        System.out.println(MongoDBTranslator.translate(program));
        System.out.println(DSLTranslator.translate(program));
    }

    @BeforeAll
    public void setUpBeforeClass() {
        RESULT_FOLDER = "result_unit_tests";
        EXPERIMENT = EXP_MODE.MAIN;
        try {
            Files.createDirectories(Paths.get(getOutputFolder(RunOption.FULL)));
            Files.createDirectories(Paths.get(getOutputFolder(RunOption.FULL) + "/benchstats"));
            Files.createDirectories(Paths.get(getOutputFolder(RunOption.FULL) + "/maineval"));
            Files.createDirectories(Paths.get(getOutputFolder(RunOption.FULL) + "/programs"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void test1AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(1));
    }

    @Test
    void test2AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(2));
    }

    @Test
    void test3AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(3));
    }

    @Test
    void test4AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(4));
    }

    @Test
    void test5AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(5));
    }

    @Test
    void test6AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(6));
    }

    @Test
    void test7AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(7));
    }

    @Test
    void test8AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(8));
    }

    @Test
    void test9AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(9));
    }

    @Test
    void test10AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(10));
    }

    @Test
    void test11AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(11));
    }

    @Test
    void test12AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(12));
    }

    @Test
    void test13AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(13));
    }

    @Test
    void test14AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(14));
    }

    @Test
    void test15AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(15));
    }

    @Test
    void test16AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(16));
    }

    @Test
    void test17AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(17));
    }

    @Test
    void test18AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(18));
    }

    @Test
    void test19AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(19));
    }

    @Test
    void test20AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(20));
    }

    @Test
    void test21AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(21));
    }

    @Test
    void test22AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(22));
    }

    @Test
    void test23AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(23));
    }

    @Test
    void test24AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(24));
    }

    @Test
    void test25AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(25));
    }

    @Test
    void test26AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(26));
    }

    @Test
    void test27AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(27));
    }

    @Test
    void test28AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(28));
    }

    @Test
    void test29AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(29));
    }

    @Test
    void test30AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(30));
    }

    @Test
    void test31AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(31));
    }

    @Test
    void test32AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(32));
    }

    @Test
    void test33AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(33));
    }

    @Test
    void test34AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(34));
    }

    @Test
    void test35AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(35));
    }

    @Test
    void test36AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(36));
    }

    @Test
    void test37AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(37));
    }

    @Test
    void test38AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(38));
    }

    @Test
    void test39AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(39));
    }

    @Test
    void test40AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(40));
    }

    @Test
    void test41AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(41));
    }

    @Test
    void test42AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(42));
    }

    @Test
    void test43AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(43));
    }

    @Test
    void test44AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(44));
    }

    @Test
    void test45AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(45));
    }

    @Test
    void test46AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(46));
    }

    @Test
    void test47AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(47));
    }

    @Test
    void test48AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(48));
    }

    @Test
    void test49AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(49));
    }

    @Test
    void test50AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(50));
    }

    @Test
    void test51AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(51));
    }

    @Test
    void test52AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(52));
    }

    @Test
    void test53AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(53));
    }

    @Test
    void test54AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(54));
    }

    @Test
    void test55AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(55));
    }

    @Test
    void test56AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(56));
    }

    @Test
    void test57AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(57));
    }

    @Test
    void test58AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(58));
    }

    @Test
    void test59AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(59));
    }

    @Test
    void test60AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(60));
    }

    @Test
    void test61AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(61));
    }

    @Test
    void test62AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(62));
    }

    @Test
    void test63AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(63));
    }

    @Test
    void test64AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(64));
    }

    @Test
    void test65AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(65));
    }

    @Test
    void test66AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(66));
    }

    @Test
    void test67AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(67));
    }

    @Test
    void test68AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(68));
    }

    @Test
    void test69AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(69));
    }

    @Test
    void test70AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(70));
    }

    @Test
    void test71AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(71));
    }

    @Test
    void test72AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(72));
    }

    @Test
    void test73AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(73));
    }

    @Test
    void test74AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(74));
    }

    @Test
    void test75AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(75));
    }

    @Test
    void test76AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(76));
    }

    @Test
    void test77AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(77));
    }

    @Test
    void test78AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(78));
    }

    @Test
    void test79AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(79));
    }

    @Test
    void test80AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(80));
    }

    @Test
    void test81AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(81));
    }

    @Test
    void test82AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(82));
    }

    @Test
    void test83AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(83));
    }

    @Test
    void test84AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(84));
    }

    @Test
    void test85AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(85));
    }

    @Test
    void test86AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(86));
    }

    @Test
    void test87AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(87));
    }

    @Test
    void test88AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(88));
    }

    @Test
    void test89AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(89));
    }

    @Test
    void test90AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(90));
    }

    @Test
    void test91AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(91));
    }

    @Test
    void test92AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(92));
    }

    @Test
    void test93AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(93));
    }

    @Test
    void test94AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(94));
    }

    @Test
    void test95AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(95));
    }

    @Test
    void test96AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(96));
    }

    @Test
    void test97AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(97));
    }

    @Test
    void test98AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(98));
    }

    @Test
    void test99AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(99));
    }

    @Test
    void test100AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(100));
    }

    @Test
    void test101AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(101));
    }

    @Test
    void test102AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(102));
    }

    @Test
    void test103AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(103));
    }

    @Test
    void test104AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(104));
    }

    @Test
    void test105AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(105));
    }

    @Test
    void test106AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(106));
    }

    @Test
    void test107AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(107));
    }

    @Test
    void test108AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(108));
    }

    @Test
    void test109AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(109));
    }

    @Test
    void test110AbstSynthesizer() {
        assertTimeoutPreemptively(Duration.ofMinutes(TIMEOUT_MIN), () -> runBenchmark(110));
    }



}
