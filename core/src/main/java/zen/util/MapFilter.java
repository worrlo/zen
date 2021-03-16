package zen.util;

import javax.json.JsonValue;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Stream;

public class MapFilter
{
    final String FIELD_FORMAT = ", %s:%s";
    private String filter;
    private String redact;
    final Map<?,?> map;

    public MapFilter(Map<?,?> data) {
        this.map = data;
        if(map == null)
            throw new IllegalStateException("Map cannot be null");
    }

    public MapFilter filter(String regex) {
        filter = regex;
        return this;
    }

    public MapFilter redact(String regex) {
        redact = regex;
        return this;
    }

    public String toString() {
        StringWriter buf = new StringWriter();
        if (!map.isEmpty()) {
            PrintWriter out = new PrintWriter(buf);
            Stream<?> keys = map.keySet().stream();
            if (filter != null) {
                keys = keys.filter(k -> String.valueOf(k).matches(filter));
            }

            if (redact != null) {
                keys.forEach(k -> {
                    String key = String.valueOf(k);
                    Object val = map.get(k);
                    String value;
                    if(key.matches(redact)) {
                        value = !Fn.isSet(val) || val == JsonValue.NULL
                                    ? "NOT SET"
                                    : "SET";
                    }
                    else
                        value = String.valueOf(val);
                    out.printf(FIELD_FORMAT, key, value);
                });
            }
            else {
                keys.forEach(k ->
                    out.printf(FIELD_FORMAT, k, map.get(k))
                );
            }
            out.close();
        }

        return String.format("%s(filter: %s, redact: %s) {%s}",
                map.getClass().getSimpleName(),
                filter != null ? filter : "NA",
                redact != null,
                buf.getBuffer().length() > 0 ? buf.toString().substring(2) : ""
        );
    }

}
