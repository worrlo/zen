package zen.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class CmdLineArgs
{
    public final static String PROP_ON = "ON";
    private final LinkedHashMap<Object, ArrayList<String>> data;

    public CmdLineArgs() {
        data = new LinkedHashMap<>();
    }

    public CmdLineArgs clear() {
        data.clear();
        return this;
    }

    public CmdLineArgs add(Object key, String value) {
        data.computeIfAbsent(key, (v) -> new ArrayList<String>())
                .add(value);
        return this;
    }
    public String toString() {
        return String.format("CmdLineArgs%s", data.toString());
    }

    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean containsKeys(Object...keys)
    {
        for (Object key : keys) {
            if (key.getClass().isArray()) {
                boolean found = false;
                for (Object k : (Object[]) key) {
                    if (data.containsKey(k))
                        found = true;
                }
                if (!found)
                    return false;
            }
            else {
                if (!data.containsKey(key))
                    return false;
            }
        }
        return true;
    }

    public String get( Object key ) { return get(key, null); }
    public String get( Object key, String dflt )
    {
        ArrayList<String> list = data.get(key);
        return list != null ? list.get(0) : dflt;
    }
    public ArrayList<String> getAll( Object key )
    {
        return data.get(key);
    }

    public static CmdLineArgs build(String[] args, Object... pFlags) {
        CmdLineArgs cla = new CmdLineArgs();

        HashSet<Object> propFlags = new HashSet<>();
        for (Object obj: pFlags) {
            propFlags.add(obj);
        }

        int cmdIndex = 0;
        for (int i = 0; i < args.length; i += 1) {
            String key = args[i].trim();
            if (Fn.isSet(key)) {
                if(key.startsWith("-")) {
                    if (propFlags.contains(key) || i == args.length - 1 || args[i + 1].startsWith("-")) {
                        cla.add(key, PROP_ON);
                    }
                    else {
                        cla.add(key, args[i + 1]);
                        i += 1;
                    }
                }
                else {
                    cla.add(cmdIndex, key);
                    cmdIndex += 1;
                }
            }
        }

        return cla;
    }
}
