package zen.core;

public class Result<V> {
    private final int status;
    private final V value;
    private final Throwable error;

    public Result(V value, Throwable error) {
        this(0, value, error);
    }
    public Result(int status, V value, Throwable error) {
        this.status = status;
        this.value = value;
        this.error = error;
    }

    public int status() { return status; }

    public V value() { return value; }
    public boolean hasValue() {return value != null; }

    public Throwable error() { return error; }
    public boolean hasError() { return error != null;}
}
