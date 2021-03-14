package zen.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestResult {

    @Test public void testInit() {
        Result<String> res = new Result<>(null,null);
        assertEquals(0, res.status());
        assertFalse(res.hasValue());
        assertNull(res.value());
        assertFalse(res.hasError());
        assertNull(res.error());

        res = new Result<>(400, "Missing parameters", new IllegalArgumentException("missing hello"));
        assertEquals(400, res.status());
        assertTrue(res.hasValue());
        assertEquals("Missing parameters", res.value());
        assertTrue(res.hasError());
        assertEquals(IllegalArgumentException.class, res.error().getClass());
        assertEquals("missing hello", res.error().getMessage());
    }

}
