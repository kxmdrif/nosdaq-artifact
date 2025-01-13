package nosdaq.synth_new;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class UtilsTest {
    @Test
    public void isISODateTest() {
        assertTrue(Utils.isISODate("2010-04-29T00:00:00.000Z"));
        assertFalse(Utils.isISODate("2010-04-29"));

    }
}
