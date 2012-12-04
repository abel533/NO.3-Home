package craky.keeper.sql;

import java.util.Collection;

public class Restrictions
{
    public static SimpleExpression eq(String propertyName, Object value)
    {
        return new SimpleExpression(propertyName, value, "=");
    }

    public static SimpleExpression ne(String propertyName, Object value)
    {
        return new SimpleExpression(propertyName, value, "<>");
    }

    public static SimpleExpression like(String propertyName, Object value)
    {
        return new SimpleExpression(propertyName, value, " like ");
    }

    public static SimpleExpression gt(String propertyName, Object value)
    {
        return new SimpleExpression(propertyName, value, ">");
    }

    public static SimpleExpression lt(String propertyName, Object value)
    {
        return new SimpleExpression(propertyName, value, "<");
    }

    public static SimpleExpression le(String propertyName, Object value)
    {
        return new SimpleExpression(propertyName, value, "<=");
    }

    public static SimpleExpression ge(String propertyName, Object value)
    {
        return new SimpleExpression(propertyName, value, ">=");
    }

    public static Criterion in(String propertyName, Object[] values)
    {
        return new InExpression(propertyName, values);
    }

    public static Criterion in(String propertyName, Collection<?> values)
    {
        return new InExpression(propertyName, values.toArray());
    }

    public static LogicalExpression and(Criterion lhs, Criterion rhs)
    {
        return new LogicalExpression(lhs, rhs, "and");
    }

    public static LogicalExpression or(Criterion lhs, Criterion rhs)
    {
        return new LogicalExpression(lhs, rhs, "or");
    }
}