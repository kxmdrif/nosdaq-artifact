package nosdaq.synth_new;

import nosdaq.RunOption;
import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.Schema;
import nosdaq.grammar.Grammar;

import java.io.IOException;
import java.util.List;

public interface ISynthesizer {
    Program synthesize(
            RunOption runOption,
            Schema inputSchema,
            Schema outputSchema,
            List<Example> examples,
            List<String> constants,
            List<Schema> foreignSchema) throws IOException;
}
