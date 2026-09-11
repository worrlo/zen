package zen.util;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class TestLog {
    @RegisterExtension 
    LogExtension logExt = new LogExtension();

    @Test public void testEntry() {
        long start = System.currentTimeMillis();

        String[] levels = {"Error", "Warn", "Audit", "Record", "Info", "Debug", "Trace", "Dog"};

        for (String level : levels) {
            if ("debug".equalsIgnoreCase(level)) 
                Log.entry(level.toUpperCase(), 1000, null, String.format("%s message", level));
            else
                Log.entry(level.toUpperCase(), null, String.format("%s message", level));
        }

        List<Log.Entry> log = logExt.getCapturedLogs();
        assertEquals(levels.length, log.size());
        Log.Entry entry = log.get(0);

        assertEquals("zen.util.TestLog", entry.getLogger());
        assertEquals("testEntry", entry.getMethod());
        assertEquals(23, entry.getLine());
        assertEquals(levels[0].toUpperCase(), entry.getLevel());
        assertEquals(String.format("%s message", levels[0]), entry.getMessage());
        assertEquals("main", entry.getThread());
        assertTrue(entry.getEpoch() >= start);
    }

    @Test public void testUtil() {
        TestObject obj = new TestObject();

        List<Log.Entry> log = logExt.getCapturedLogs();
        assertEquals(0, log.size());
        obj.test1();
        obj.test2();
        obj.test3();

        log = logExt.getCapturedLogs();
        Log.Entry e = log.get(0);
        assertEquals("INFO", e.getLevel());
        e = log.get(1);
        assertEquals("WARN", e.getLevel());
        e = log.get(2);
        assertEquals("ERROR", e.getLevel());
    }

    class TestObject implements Log.Utils {
        public void test1() { log("INFO", "test"); }
        public void test2() { log("WARN", new DiagnosticData(), "test"); }
        public void test3() { log("ERROR", new RuntimeException(), "test", "help"); }
    }
}
