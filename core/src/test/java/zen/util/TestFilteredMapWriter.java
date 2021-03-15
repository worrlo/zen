package zen.util;

import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestFilteredMapWriter {
    @Test public void testInit() {
        Exception e = assertThrows(IllegalStateException.class, () -> new FilteredMapWriter(null));
        assertEquals("Map cannot be null", e.getMessage());

        assertEquals(
                "HashMap(filter: NA, redact: false) {}",
                new FilteredMapWriter(new HashMap<String, String>()).toString()
        );
    }

    @Test public void testFilter01() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("my.database","myDB");
        map.put("my.user", null);
        map.put("donkey","true");
        FilteredMapWriter fmw = new FilteredMapWriter(map).filter("my\\..*");

        assertEquals(
                "LinkedHashMap(filter: my\\..*, redact: false) {my.database:myDB, my.user:null}",
                fmw.toString()
        );
    }

    @Test public void testFilter02() {
        FilteredMapWriter fmw = new FilteredMapWriter(
                Json.createObjectBuilder()
                    .add("my.database", "myDB")
                    .add("my.user", JsonValue.NULL)
                    .add("donkey", true)
                .build()
        ).filter("my\\..*");

        assertEquals(
                "JsonObjectImpl(filter: my\\..*, redact: false) {my.database:\"myDB\", my.user:null}",
                fmw.toString()
        );
    }

    @Test public void testRedact01() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("my.database","myDB");
        map.put("my.server", null);
        map.put("donkey","true");
        FilteredMapWriter fmw = new FilteredMapWriter(map).redact("my\\..*");

        assertEquals(
                "LinkedHashMap(filter: NA, redact: true) {my.database:SET, my.server:NOT SET, donkey:true}",
                fmw.toString()
        );
    }

    @Test public void testRedact02() {
        FilteredMapWriter fmw = new FilteredMapWriter(
                Json.createObjectBuilder()
                        .add("my.database", "myDB")
                        .add("my.server", JsonValue.NULL)
                        .add("donkey", true)
                        .build()
        ).redact("my\\..*");
        assertEquals(
                "JsonObjectImpl(filter: NA, redact: true) {my.database:SET, my.server:NOT SET, donkey:true}",
                fmw.toString()
        );
    }
}
