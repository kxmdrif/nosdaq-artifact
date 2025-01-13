package nosdaq.ast.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringArrayTest {
    @Test
    void testGetArray() {
        List<Value> array = List.of(new StringLiteral("test"), new StringLiteral("get array"));
        Array stringArray = new Array(array);
        Assertions.assertEquals(array, stringArray.getElements());
    }

    @Test
    void testToString() {
        List<Value> array = List.of(new StringLiteral("test"), new StringLiteral("get array"));
        Array stringArray = new Array(array);
        Assertions.assertEquals("[\"test\", \"get array\"]", stringArray.toString());
    }
}