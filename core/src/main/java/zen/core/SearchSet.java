package zen.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SearchSet<V>
{
    private Set<V> include;
    private Set<V> exclude;
    private Set<V> all;

    public SearchSet() {
        all = new HashSet<>();
        include = new HashSet<>();
        exclude = new HashSet<>();
    }
    public String toString() {
        return String.format("{include: %s, exclude: %s}", include, exclude);
    }

    public Set<V> getIncluded() {return include;}
    public SearchSet<V> include(V... objs) {
        add(include, objs);
        return this;
    }

    public Set<V> getExcluded() {return exclude;}
    public SearchSet<V> exclude(V... objs) {
        add(exclude, objs);
        return this;
    }

    public SearchSet<V> clear() {
        all.clear();
        include.clear();
        exclude.clear();
        return this;
    }

    // Support functions
    protected boolean allowAdd(V obj)
    {
        if (all.contains(obj))
            return false;
        if (obj instanceof String)
            return !"".equals(((String)obj).trim());
        return true;
    }

    private void add(Set<V> set, V... objs)
    {
        if (objs != null && objs.length > 0)
        {
            for (V obj : objs) {
                if (allowAdd(obj)) {
                    set.add(obj);
                    all.add(obj);
                }
            }
        }
    }
}
