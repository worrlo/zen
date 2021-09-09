package zen.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSearchValue
{
    private SearchValue<String> value() {
        return new SearchValue<>(SearchOperand.BTWN, "Hello", "World");
    }

    @Test public void testValidInit1() {
        SearchValue<String> sv = new SearchValue<>(SearchOperand.EQ, "Hello");
        assertEquals("Hello", sv.getValue(0));
        assertEquals(SearchOperand.EQ, sv.getOp());
        assertEquals("[Hello]", sv.toString());
        assertArrayEquals(new String[] {"Hello"}, sv.getValues());
    }
    @Test public void testValidInit2() {
        SearchValue<String> sv = new SearchValue<>(SearchOperand.NSET);
        assertNull(sv.getValue(0));
        assertEquals(SearchOperand.NSET, sv.getOp());
        assertEquals("[]", sv.toString());
        assertArrayEquals(new String[0], sv.getValues());
    }

    @Test public void testInvalidInit1() {
        Throwable e = assertThrows(IllegalStateException.class, () -> new SearchValue<>(null, (String[]) null));
        assertEquals("SearchOperand missing", e.getMessage());
        assertEquals( 0, e.getSuppressed().length );
    }

    @Test public void testInvalidInit2() {
        Throwable e = assertThrows(IllegalStateException.class, () -> new SearchValue<>(null, "hello"));
        assertEquals("SearchOperand missing", e.getMessage());
        assertEquals( 0, e.getSuppressed().length );
    }

    @Test public void testInvalidInit3() {
        Throwable e = assertThrows(IllegalStateException.class, () -> new SearchValue<>(SearchOperand.EQ, (String[]) null));
        assertEquals("Value list does not match requirements for operation", e.getMessage());
        assertEquals( 1, e.getSuppressed().length );
    }

    @Test public void testInvalidInit4() {
        Throwable e = assertThrows(IllegalStateException.class, () -> new SearchValue<>(SearchOperand.EQ, "hello", "world"));
        assertEquals("Value list does not match requirements for operation", e.getMessage());
        assertEquals( 1, e.getSuppressed().length );
    }

    @Test public void testInvalidInit5() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new SearchValue<>(SearchOperand.BTWN, "hello", null));
        assertEquals("NULL value detected", e.getMessage());
        assertEquals( 1, e.getSuppressed().length );
    }

    @Test public void testInvalidInit6() {
        Throwable e = assertThrows(IllegalStateException.class, () -> new SearchValue<>(SearchOperand.NSET, "hello"));
        assertEquals("Unused data values detected", e.getMessage());
        assertEquals( 1, e.getSuppressed().length );
    }

    @Test public void testBuildSQL1() {
        SearchValue<String> sv = new SearchValue<>(SearchOperand.NSET);
        assertEquals(" enabled IS NULL", sv.buildSQL("enabled"));
    }

    @Test public void testBuildSQL2() {
        SearchValue<String> sv = new SearchValue<>(SearchOperand.EQ, "Hello");
        assertEquals(" enabled = ?", sv.buildSQL("enabled"));
    }

    @Test public void testBuildSQL3() {
        SearchValue<String> sv = value();
        assertEquals(" ( enabled >= ? AND enabled <= ? )", sv.buildSQL("enabled"));
    }

    @Test public void testBuildSQL4() {
        SearchValue<String> sv = value();
        assertEquals(" ( enabled >= ? AND disabled <= ? )", sv.buildSQL("enabled", "disabled"));
    }

    @Test public void testBuildSQLFailure1() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> value().buildSQL(null));
        assertEquals("Invalid parameters for buildSQL", e.getMessage());
        assertEquals(1, e.getSuppressed().length);
    }

    @Test public void testBuildSQLFailure2() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> value().buildSQL("enabled", null));
        assertEquals("Invalid parameters for buildSQL", e.getMessage());
        assertEquals(1, e.getSuppressed().length);
    }

    @Test public void testBuildSQLFailure3() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> value().buildSQL(null, "enabled"));
        assertEquals("Invalid parameters for buildSQL", e.getMessage());
        assertEquals(1, e.getSuppressed().length);
    }

    @Test public void testGetValue() {
        SearchValue<String> sv = value();
        assertNull(sv.getValue(-1));
        assertEquals("Hello", sv.getValue(0));
        assertNull(sv.getValue(2));
    }
}
