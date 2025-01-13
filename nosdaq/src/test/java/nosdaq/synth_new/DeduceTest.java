package nosdaq.synth_new;

import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import org.junit.jupiter.api.Test;

public class DeduceTest {

    @Test
    public void Z3Test() {
        Context ctx = new Context();
        IntExpr intExpr = ctx.mkIntConst("aa.bb");
        IntExpr intExpr1 = ctx.mkIntConst("aa.bb");
        assert intExpr1.equals(intExpr);
    }
}
