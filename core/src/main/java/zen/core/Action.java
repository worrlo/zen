package zen.core;

public interface Action<P> {
    Result<?> validate(P params) throws Exception;
    Result<?> execute(P params) throws Exception;

    static <I> Result<?> process(I params, Action<I> action)
            throws Exception
    {
        Result<?> res = action.validate(params);
        if (!res.hasError())
            res = action.execute(params);
        return res;
    }
}
