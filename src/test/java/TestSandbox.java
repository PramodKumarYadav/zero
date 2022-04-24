
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSandbox {
    /**
     * a very basic test
     */
    @Test
    void assertThatTrueIsTrue() {
        assertTrue(true, "true is true");
    }

    @Test
    void assertThatADayIsADay() {
        assertEquals("day", "night", "true is true");
    }

    @Test
    void createAFlakyTest() {
        long currentTimeStamp = System.currentTimeMillis();
        System.out.println(currentTimeStamp);
        if (currentTimeStamp % 2 == 0) {
            assertTrue(true, "time is even");
        } else {
            assertTrue(false, "true is odd");
        }
    }
}
