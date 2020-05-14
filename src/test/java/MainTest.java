import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
    @Test
    @DisplayName("This test should run f everything  is well configured")
    public void testMain() {
        assertEquals(2, 1 + 1);
    }
}
