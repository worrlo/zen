package zen.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zen.util.Fn;

import static org.junit.jupiter.api.Assertions.*;

public class TestAction {
    Action<String> action;
    @BeforeEach public void setup() {
        action = new Action<>() {
            @Override
            public Result<?> validate(String params) {
                return new Result<>(params,
                        Fn.isSet(params) ? null : new IllegalStateException("input cannot be null")
                );
            }

            @Override
            public Result<?> execute(String params) {
                return new Result<>(String.format("Hello, %s", params), null);
            }
        };
    }

    @Test public void testInit() {
        assertNotNull(action);
    }

    @Test public void testProcess01() throws Exception {
        Result<?> res = Action.process(null, action);
        assertFalse(res.hasValue());
        assertTrue(res.hasError());
        assertEquals("input cannot be null", res.error().getMessage());
    }

    @Test public void testProcess02() throws Exception {
        Result<?> res = Action.process("", action);
        assertTrue(res.hasValue());
        assertTrue(res.hasError());
        assertEquals("input cannot be null", res.error().getMessage());
    }

    @Test public void testProcess03() throws Exception {
        Result<?> res = Action.process(" ", action);
        assertTrue(res.hasValue());
        assertTrue(res.hasError());
        assertEquals("input cannot be null", res.error().getMessage());
    }

    @Test public void testProcess04() throws Exception {
        Result<?> res = Action.process("John", action);
        assertTrue(res.hasValue());
        assertFalse(res.hasError());
        assertEquals("Hello, John", res.value());
    }
}
