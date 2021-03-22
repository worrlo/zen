package zen.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestInputs {
    Inputs.BasicInputs params;

    @BeforeEach public void setup() {
        params = new Inputs.BasicInputs();
    }
    @Test public void testInit() {
        assertNotNull(params);
        assertNull(params.getParameter("id"));
        assertNull(params.getParameterValues("test"));
        assertEquals(
                "BasicInputs{data: LinkedHashMap(filter: NA, redact: true) {}}"
                , params.toString()
        );
    }

    @Test public void testAdd() {
        params.add("id", "1234")
            .add("id", "2345")
            .add("message", "hello")
            .add("test.id", "123456")
        ;
        assertEquals("1234", params.getParameter("id"));
        assertArrayEquals(new String[] {"1234", "2345"}, params.getParameterValues("id"));
        assertEquals(
                "BasicInputs{data: LinkedHashMap(filter: NA, redact: true) {id:SET, message:[hello], test.id:SET}}"
                ,params.toString()
        );
    }

    @Test public void testSet() {
        testAdd();

        params.set("id", "1234567");
        assertEquals("1234567", params.getParameter("id"));
        assertArrayEquals(new String[] {"1234567"}, params.getParameterValues("id"));
    }

    @Test public void testClear() {
        testAdd();

        params.clear();
        testInit();
    }

    @Test public void testSysPropRead() {
        Properties sysProps = System.getProperties();
        Properties props = new Properties();
        props.setProperty("zen.input.redact", "(?i)test\\.id");
        System.setProperties(props);

        params.add("id", "1234")
                .add("test.id", "23456");

        assertEquals(
                "BasicInputs{data: LinkedHashMap(filter: NA, redact: true) {id:[1234], test.id:SET}}"
                ,params.toString()
        );

        System.setProperties(sysProps);
    }
}
