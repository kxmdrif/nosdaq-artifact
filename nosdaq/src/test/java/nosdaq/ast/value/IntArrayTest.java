package nosdaq.ast.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class IntArrayTest {
    @Test
    void testGetIntArray() {
        List<Value> arr = new ArrayList<>();
        arr.add(new IntLiteral(1));
        arr.add(new IntLiteral(2));
        Array intArray = new Array(arr);
        Assertions.assertEquals(arr, intArray.getElements());
    }

    @Test
    void testToString() {
        List<Value> intArray = new ArrayList<>();
        intArray.add(new IntLiteral(1));
        intArray.add(new IntLiteral(2));
        Assertions.assertEquals("[1, 2]", intArray.toString());
    }
}