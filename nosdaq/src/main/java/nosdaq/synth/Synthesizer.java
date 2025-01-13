package nosdaq.synth;

import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.Schema;
import nosdaq.grammar.Grammar;

import java.io.IOException;
import java.util.List;

public interface Synthesizer {
    Program synthesize(Grammar grammar, Schema schema, List<Example> examples, int maxProgramSize) throws IOException;
}
