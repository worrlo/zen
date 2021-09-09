package zen.core;

import zen.util.DiagnosticData;
import zen.util.Fn;

import java.util.Arrays;

public class SearchValue<V>
{
    private V[] values;
    private SearchOperand op;

    public SearchValue(SearchOperand op, V... vals)
    {
        this.values = vals;
        this.op = op;

        validate();
    }

    public String toString() {
        return Arrays.toString(values);
    }

    private void validate() {
        if (op == null)
            throw new IllegalStateException("SearchOperand missing");

        if (op.isValueRequired()) {
            if (values == null || values.length != op.getExpectedValues())
                throw DiagnosticData.on(
                        new IllegalStateException("Value list does not match requirements for operation"),
                        op, this
                );
            for (V val : values) {
                if (val == null)
                    throw DiagnosticData.on(
                            new IllegalArgumentException("NULL value detected"),
                            op, this
                    );
            }
        }
        else if (values.length > 0)
            throw DiagnosticData.on(
                    new IllegalStateException("Unused data values detected"),
                    op, this
            );
    }

    public V getValue(int index) {
        if (index >= values.length || index < 0)
            return null;
        return values[index];
    }

    public V[] getValues() { return values; }
    public SearchOperand getOp() { return op; }
    public String buildSQL(String fn1, String fn2)
    {
        if (!Fn.areSet(fn1, fn2))
            throw DiagnosticData.on(
                    new IllegalArgumentException("Invalid parameters for buildSQL"),
                    fn1, fn2
            );
        if (op == SearchOperand.BTWN)
            return String.format(op.getSql(), fn1, fn2);
        return String.format(op.getSql(), fn1);
    }
    public String buildSQL(String fn) { return buildSQL(fn, fn); }

}
