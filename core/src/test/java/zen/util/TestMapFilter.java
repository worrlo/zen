package zen.util;

import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestMapFilter {
    @Test public void testInit() {
        Exception e = assertThrows(IllegalStateException.class, () -> new MapFilter(null));
        assertEquals("Map cannot be null", e.getMessage());

        assertEquals(
                "HashMap(filter: NA, redact: false) {}",
                new MapFilter(new HashMap<String, String>()).toString()
        );
    }

    @Test public void testFilter01() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("my.database","myDB");
        map.put("my.user", null);
        map.put("donkey","true");
        MapFilter mf = new MapFilter(map).filter("my\\..*");

        assertEquals(
                "LinkedHashMap(filter: my\\..*, redact: false) {my.database:myDB, my.user:null}",
                mf.toString()
        );
    }

    @Test public void testFilter02() {
        MapFilter mf = new MapFilter(
                Json.createObjectBuilder()
                    .add("my.database", "myDB")
                    .add("my.user", JsonValue.NULL)
                    .add("donkey", true)
                .build()
        ).filter("my\\..*");

        assertEquals(
                "JsonObjectImpl(filter: my\\..*, redact: false) {my.database:\"myDB\", my.user:null}",
                mf.toString()
        );
    }
    
    @Test public void testFilter03() {
        Properties props = new Properties();
        props.setProperty("my.database", "myDB");
        props.setProperty("my.user", "");
        props.setProperty("donkey", "true");

        MapFilter mf = new MapFilter(props).filter("my\\..*");

        assertEquals(
                "Properties(filter: my\\..*, redact: false) {my.database:myDB, my.user:}",
                mf.toString()
        );
    }

    @Test public void testRedact01() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("my.database","myDB");
        map.put("my.server", null);
        map.put("donkey","true");
        MapFilter mf = new MapFilter(map).redact("my\\..*");

        assertEquals(
                "LinkedHashMap(filter: NA, redact: true) {my.database:SET, my.server:NOT SET, donkey:true}",
                mf.toString()
        );
    }

    @Test public void testRedact02() {
        MapFilter mf = new MapFilter(
                Json.createObjectBuilder()
                        .add("my.database", "myDB")
                        .add("my.server", JsonValue.NULL)
                        .add("donkey", true)
                        .build()
        ).redact("my\\..*");
        assertEquals(
                "JsonObjectImpl(filter: NA, redact: true) {my.database:SET, my.server:NOT SET, donkey:true}",
                mf.toString()
        );
    }

    @Test public void testRedact03() {
        Properties props = new Properties();
        props.setProperty("my.database", "myDB");
        props.setProperty("my.user", "");
        props.setProperty("donkey", "true");

        MapFilter mf = new MapFilter(props).redact("my\\..*");

        assertEquals(
                "Properties(filter: NA, redact: true) {my.database:SET, donkey:true, my.user:NOT SET}",
                mf.toString()
        );
    }
}
