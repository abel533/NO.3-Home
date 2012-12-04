package craky.keeper.sql;

import java.util.Collection;

public class SimpleExpression implements Criterion
{
    private String columnName;

    private Object value;

    private String op;

    public SimpleExpression(String columnName, Object value, String op)
    {
        this.columnName = columnName;
        this.value = value;
        this.op = op;
    }

    public String toSqlString()
    {
        return columnName + op + '?';
    }

    public Object getValue()
    {
        return value;
    }

    public Collection<?> getValues()
    {
        return null;
    }
}