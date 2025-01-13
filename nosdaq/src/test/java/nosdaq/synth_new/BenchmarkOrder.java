package nosdaq.synth_new;

import org.junit.jupiter.api.MethodDescriptor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;

public class BenchmarkOrder implements MethodOrderer {
    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort(
                (MethodDescriptor m1, MethodDescriptor m2) -> {
                    Integer m1No = Integer.parseInt(m1.getMethod().getName().replaceAll("\\D+",""));

                    Integer m2No = Integer.parseInt(m2.getMethod().getName().replaceAll("\\D+",""));
                    return m1No.compareTo(m2No);
                }

        );
    }
}
