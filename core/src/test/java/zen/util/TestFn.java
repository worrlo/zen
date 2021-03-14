package zen.util;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestFn {
    @Test public void testInit() {
        Exception e = assertThrows(IllegalStateException.class, Fn::new);
        assertEquals("Fn cannot be initialized", e.getMessage());
    }

    @Test public void testIsSet() {
        assertTrue(Fn.isSet("Hello"));
        assertTrue(Fn.isSet("    test"));
        assertTrue(Fn.isSet(new Object()));
        assertFalse(Fn.isSet(""));
        assertFalse(Fn.isSet("     "));
        assertFalse(Fn.isSet(null));

        assertEquals( 3,
                Stream.of("test", "    ", "   hello", null,new Object())
                .filter(Fn::isSet).count()
        );

    }
}
