package zen.core;

import zen.util.MapFilter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface Inputs {
    String getParameter(String key);
    String[] getParameterValues(String key);

    class BasicInputs implements Inputs
    {
        private LinkedHashMap<String, ArrayList<String>> data;
        public BasicInputs() {
            data = new LinkedHashMap<>();
        }

        @Override public String getParameter(String key) {
            ArrayList<String> list = data.get(key);
            return list == null || list.isEmpty() ? null : list.get(0);
        }

        @Override public String[] getParameterValues(String key) {
            ArrayList<String> list = data.get(key);
            return list != null ? list.toArray(String[]::new) : null;
        }

        public BasicInputs add(String key, String value) {
            data.computeIfAbsent(key, v -> new ArrayList<>())
                    .add(value);
            return this;
        }

        public BasicInputs set(String key, String value) {
            ArrayList<String> list = data.computeIfAbsent(key, v -> new ArrayList<>());
            list.clear();
            list.add(value);
            return this;
        }

        public BasicInputs clear() {
            data.clear();
            return this;
        }

        public String toString() {
            return String.format(
                    "%s{data: %s}",
                    this.getClass().getSimpleName(),
                    new MapFilter(data)
                        .redact(System.getProperty("zen.input.redact",
                            "(?i).*(key|to|from|pass(word)?|id)"
                        ))
            );
        }
    }
}
