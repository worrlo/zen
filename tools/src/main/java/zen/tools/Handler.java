package zen.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Handler<K,V> {

    private HashMap<String,HashMap<K,V>> handles;

    public Handler() {
        handles = new HashMap<>();
    }

    public Handler<K,V> set(String root, K key, V value) {
        handles.computeIfAbsent(root, v -> new HashMap<>())
                .put(key, value);
        return this;
    }

    public Stream<Map.Entry<K,V>> stream(String root) {
        return handles.computeIfAbsent(root, v -> new HashMap<>())
                .entrySet().stream();
    }
}
