package zen.util;

public class Fn {
    Fn() {throw new IllegalStateException("Fn cannot be initialized");}

    static final Object NIL = new Object();

    /**
     * Defines a method that will safely compare two values including
     * the possibility of {@code null}
     *
     * @param a - field to equate
     * @param b - field to equate
     * @return
     *      {@code true} - if {@code a.equals(b)} <br/>
     *      {@code false} - otherwise
     */
    public static boolean equal(Object a, Object b) {
        if (a == b) return true;
        if (a == null) a = NIL;
        if (b == null) b = NIL;
        return a.equals(b);
    }

    /**
     * isSet - determines if an object is not null or an empty string
     *
     * @param obj - the value to be tested
     * @return {@code false} - if {@code obj == null} or a string containing only spaces<br/>
     *         {@code true} - otherwise
     */
    public static boolean isSet(Object obj) {
        if (obj instanceof String)
            return !"".equals(((String)obj).trim());
        return obj != null;
    }

    /**
     * areSet - determines if all arguments passed are set according to {@link #isSet}
     * @param objects - a list of values to check
     * @return {@code false} - if any argument is {@code null} or an "empty" string<br/>
     *         {@code true} - otherwise
     */
    public static boolean areSet(Object... objects) {
        for (Object obj : objects) {
            if (!isSet(obj))
                return false;
        }
        return true;
    }
}
