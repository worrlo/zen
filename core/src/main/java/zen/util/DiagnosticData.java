package zen.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DiagnosticData extends RuntimeException {
    ArrayList<String> data;

    public DiagnosticData(Object... objects) {
        super();
        data = new ArrayList<>();
        for(Object obj : objects) {
            if(obj.getClass().isArray())
                data.add(Arrays.toString((Object[])obj));
            else
                data.add(String.valueOf(obj));
        }
    }

    @Override public Throwable fillInStackTrace() {return this;}

    @Override public String toString() {
        StringBuilder diag = new StringBuilder();
        for (int i = 0; i < data.size(); i++)
            diag.append(String.format("\t[%d] : %s%n", i, data.get(i)));
        return String.format(
                "DiagnosticData(count: %d)%n%s",
                data.isEmpty() ? -1 : data.size(),
                diag.toString()
        );
    }

    public static <E extends Throwable> E on(E t, Object... objs) {
        t.addSuppressed(new DiagnosticData(objs));
        return t;
    }
}
