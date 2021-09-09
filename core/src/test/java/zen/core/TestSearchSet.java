package zen.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSearchSet {
    SearchSet<String> obj;

    @BeforeEach public void setup() {
        obj = new SearchSet<>();
    }

    @Test public void testInit() {
        assertEquals(0, obj.getExcluded().size());
        assertEquals(0, obj.getIncluded().size());
        assertEquals("{include: [], exclude: []}", obj.toString());
    }

    @Test public void testSetters() {
        obj.include("TEST", "INCLUDE", "");
        obj.exclude("TEST", "EXCLUDE", null);
        obj.include((String[]) null);
        obj.exclude();

        assertEquals(2, obj.getExcluded().size());
        assertEquals( 2, obj.getIncluded().size());
        assertEquals("{include: [TEST, INCLUDE], exclude: [null, EXCLUDE]}", obj.toString());

        obj.clear();
        testInit();
    }

}
