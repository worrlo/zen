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

    @Test public void testAreSet() {
        assertTrue(Fn.areSet());
        assertFalse(Fn.areSet((Object) null));
        assertFalse(Fn.areSet("hello", null));
        assertTrue(Fn.areSet("Hello", new Object()));
    }

    @Test public void testEqual() {
        String a = "Hello", b = "World", c = "Hello";
        assertFalse(Fn.equal(null, b));
        assertFalse(Fn.equal(a, null));
        assertFalse(Fn.equal(a, b));
        assertTrue(Fn.equal(null, null));
        assertTrue(Fn.equal(a, a));
        assertTrue(Fn.equal(a, c));
    }
}
