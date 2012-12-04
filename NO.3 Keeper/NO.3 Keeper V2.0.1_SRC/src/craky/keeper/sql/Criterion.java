package craky.keeper.sql;

import java.util.Collection;

public interface Criterion
{
    public Object getValue();

    public Collection<?> getValues();

    public String toSqlString();
}