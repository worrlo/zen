package zen.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

public class TestHandler {
    Handler<String, Callable<Object>> handler;
    @BeforeEach public void setup() {
        handler = new Handler<>();
    }

    @Test public void init() {
        assertNotNull(handler);
        assertEquals(0 , handler.stream("test").count());
    }

    @Test public void testSet() throws Exception {
        handler.set("test", "test", () -> "Hello world");
        assertEquals(1, handler.stream("test").count());
        assertEquals("Hello world",
                handler.stream("test")
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(() -> null)
                    .call()
        );

        assertNull(
                handler.stream("mock")
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(() -> null)
                        .call()
        );
    }
}
