package craky.keeper.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LogicalExpression implements Criterion
{
    private Criterion lhs;

    private Criterion rhs;

    private String op;

    public LogicalExpression(Criterion lhs, Criterion rhs, String op)
    {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    public String toSqlString()
    {
        return '(' + lhs.toSqlString() + ' ' + op + ' ' + rhs.toSqlString() + ')';
    }

    public Object getValue()
    {
        return null;
    }

    public Collection<?> getValues()
    {
        List<Object> values = new ArrayList<Object>();
        
        if(lhs.getValue() != null)
        {
            values.add(lhs.getValue());
        }
        else if(lhs.getValues() != null)
        {
            values.addAll(lhs.getValues());
        }
        
        if(rhs.getValue() != null)
        {
            values.add(rhs.getValue());
        }
        else if(rhs.getValues() != null)
        {
            values.addAll(rhs.getValues());
        }
        
        return values.isEmpty()? null: values;
    }
}