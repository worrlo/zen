package zen.core;

public enum SearchOperand
{
    GT   ( "Greater Than"            , " %s > ?",                   1 ),
    GTE  ( "Greater Than or Equal To", " %s >= ?",                  1 ),
    EQ   ( "Equal To",                 " %s = ?",                   1 ),
    NEQ  ( "Not Equal To",             " %s != ?",                  1 ),
    LTE  ( "Less Than or Equal To",    " %s <= ?",                  1 ),
    LT   ( "Less Than",                " %s < ?",                   1 ),
    NSET ( "Is NOT Set",               " %s IS NULL",               0 ),
    SET  ( "Is Set",                   " %s IS NOT NULL",           0 ),
    BTWN ( "Between",                  " ( %s >= ? AND %s <= ? )",  2 )
    ;

    private String display;
    private String sql;
    private int valueCount;

    SearchOperand(String display, String op, int valueCount) {
        this.display = display;
        this.sql = op;
        this.valueCount = valueCount;
    }

    public String getDisplay() { return display; }
    public String getSql() { return sql; }

    public boolean isValueRequired() { return valueCount > 0; }
    public int getExpectedValues() { return valueCount; }
    public String toString()
    {
        return String.format(
                "%s : {display: %s, valueCount: %s, sql: '%s'}",
                    super.toString(), display, valueCount, sql
                );
    }
}
