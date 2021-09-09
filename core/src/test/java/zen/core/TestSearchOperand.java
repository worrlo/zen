package zen.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSearchOperand {
    @Test
    public void testInit() {
        assertNotNull(SearchOperand.EQ);
        assertNotNull(SearchOperand.NEQ);

        assertEquals(
                "GT : {display: Greater Than, valueCount: 1, sql: ' %s > ?'}",
                SearchOperand.GT.toString()
        );
    }

    @Test public void testFunctions() {
        assertEquals(2, SearchOperand.BTWN.getExpectedValues());
        assertEquals("Between", SearchOperand.BTWN.getDisplay());
        assertTrue(SearchOperand.BTWN.isValueRequired());
        assertEquals(
                " ( %s >= ? AND %s <= ? )",
                SearchOperand.BTWN.getSql()
        );
        assertFalse(SearchOperand.NSET.isValueRequired());
    }
}
