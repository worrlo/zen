package zen.util;

public class Fn {
    Fn() {throw new IllegalStateException("Fn cannot be initialized");}

    /**
     * isSet - determines if an object is not null or an empty string
     *
     * @return {@code false} - if {@code obj == null} or a string containing only spaces<br/>
     *         {@code true} - otherwise
     */
    public static boolean isSet(Object obj) {
        if (obj instanceof String)
            return !"".equals(((String)obj).trim());
        return obj != null;
    }

}
