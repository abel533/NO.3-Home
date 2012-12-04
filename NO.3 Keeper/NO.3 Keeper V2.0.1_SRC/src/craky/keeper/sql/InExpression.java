package craky.keeper.sql;

import java.util.Arrays;
import java.util.Collection;

public class InExpression implements Criterion
{
    private String columnName;

    private Object[] values;

    protected InExpression(String columnName, Object[] values)
    {
        this.columnName = columnName;
        this.values = values;
    }

    public String toSqlString()
    {
        StringBuilder params = new StringBuilder();
        
        for(int i = 0; i < values.length; i++)
        {
            if(i > 0)
            {
                params.append(',');
                params.append(' ');
            }
            
            params.append('?');
        }
        
        return columnName + " in (" + params + ')';
    }

    public Object getValue()
    {
        return null;
    }

    public Collection<?> getValues()
    {
        return values.length > 0? Arrays.asList(values): null;
    }
}