package zen.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCmdLineArgs
{
    @Test public void init() {
        CmdLineArgs cla = new CmdLineArgs();
        assertNotNull(cla);
        assertTrue(cla.isEmpty());
        assertEquals("CmdLineArgs{}", cla.toString());
    }

    @Test public void testBuild01() {
        CmdLineArgs cla = CmdLineArgs.build(new String[] {});

        assertNotNull(cla);
        assertTrue(cla.isEmpty());
        assertEquals("CmdLineArgs{}", cla.toString());
    }

    @Test public void testBuild02() {
        CmdLineArgs cla = CmdLineArgs.build(new String[] {""});

        assertNotNull(cla);
        assertTrue(cla.isEmpty());
        assertEquals("CmdLineArgs{}", cla.toString());

        assertNull(cla.get("-f"));
        assertEquals("test", cla.get("-f", "test"));
        assertFalse(cla.containsKeys(new String[] {"-f", "-debug"}, 0));
    }

    @Test public void testBuild03() {
        CmdLineArgs cla = CmdLineArgs.build(new String[] {"-debug", "-f", "Hello, World", "donkey", "-f"});

        assertNotNull(cla);
        assertFalse(cla.isEmpty());
        assertEquals("CmdLineArgs{-debug=[ON], -f=[Hello, World, ON], 0=[donkey]}", cla.toString());

        assertTrue(cla.containsKey("-debug"));
        assertFalse(cla.containsKey("-test"));
        assertEquals("donkey", cla.get(0));
        assertEquals(2, cla.getAll("-f").size());
        assertEquals("Hello, World", cla.get("-f"));
        assertEquals("ON", cla.getAll("-f").get(1));

        assertTrue(cla.containsKeys(new String[] {"-f", "-debug"}, 0));
    }

    @Test public void testBuild04() {
        CmdLineArgs cla = CmdLineArgs.build(new String[] {"-f", "Hello", "-f", "World", "John"});

        assertNotNull(cla);
        assertFalse(cla.isEmpty());
        assertEquals("CmdLineArgs{-f=[Hello, World], 0=[John]}", cla.toString());

        assertFalse(cla.containsKey("-debug"));
        assertEquals("John", cla.get(0));
        assertEquals(2, cla.getAll("-f").size());
        assertEquals("Hello", cla.get("-f"));
        assertEquals("World", cla.getAll("-f").get(1));
        assertTrue(cla.containsKeys(new String[]{"-f", "-debug"}, 0));
        assertFalse(cla.containsKeys(new String[] {"-f", "-debug"}, 1));
        assertFalse(cla.containsKeys("-f", "-debug", 0));
    }

    @Test public void testBuild05() {
        CmdLineArgs cla = CmdLineArgs.build(new String[] {"-d", "-e", "Hello John"}, "-e");

        assertNotNull(cla);
        assertFalse(cla.isEmpty());
        assertEquals("CmdLineArgs{-d=[ON], -e=[ON], 0=[Hello John]}", cla.toString());

        assertFalse(cla.containsKey("-debug"));
        assertEquals("Hello John", cla.get(0));
        assertTrue(cla.containsKeys(0, "-e", "-d"));
    }

    @Test public void testClear() {
        CmdLineArgs cla = CmdLineArgs.build(new String[] {"-f", "Hello", "-f", "World", "John"});

        assertNotNull(cla);
        assertFalse(cla.isEmpty());
        assertEquals("CmdLineArgs{-f=[Hello, World], 0=[John]}", cla.toString());

        assertFalse(cla.containsKey("-debug"));
        assertEquals("John", cla.get(0));
        assertEquals(2, cla.getAll("-f").size());
        assertEquals("Hello", cla.get("-f"));

        assertEquals(cla, cla.clear());
        assertTrue(cla.isEmpty());
        assertEquals("CmdLineArgs{}", cla.toString());
    }
}
