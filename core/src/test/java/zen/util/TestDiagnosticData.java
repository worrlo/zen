package zen.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestDiagnosticData {
    @Test public void testInit() {
        DiagnosticData dd = new DiagnosticData();
        assertNotNull(dd);
        assertNull(dd.getMessage());
        assertNull(dd.getCause());
        assertEquals(dd, dd.fillInStackTrace());
        assertEquals(0, dd.getStackTrace().length);
        assertEquals(String.format("DiagnosticData(count: -1)%n"), dd.toString());
    }

    @Test public void testOn() {
        RuntimeException e = new RuntimeException();
        assertNotNull(e);
        assertEquals(0, e.getSuppressed().length);
        assertEquals(e, DiagnosticData.on(e, "Test"));
        assertEquals(1, e.getSuppressed().length);
        assertEquals(DiagnosticData.class, e.getSuppressed()[0].getClass());
        assertEquals(
                String.format("DiagnosticData(count: 1)%n\t[0] : Test%n"),
                e.getSuppressed()[0].toString()
        );
    }
}
