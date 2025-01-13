package verify;

import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.Schema;

import java.util.List;

public interface Verifier {
    public boolean verify(Program program, Schema schema, List<Example> examples);
}
