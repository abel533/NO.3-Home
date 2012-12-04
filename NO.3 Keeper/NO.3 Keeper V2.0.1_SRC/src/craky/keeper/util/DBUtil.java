package craky.keeper.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import craky.keeper.KeeperConst;
import craky.keeper.sql.Criterion;

public class DBUtil
{
    private static Connection connection;
    
    public static void createConnection(String rootPath)
    {
        try
        {
            if(rootPath == null)
            {
                rootPath = KeeperConst.USER_DIR;
            }
            
            Class.forName("smallsql.database.SSDriver");
            connection = DriverManager.getConnection("jdbc:smallsql:" + rootPath + KeeperConst.FILE_SEP + "data");
        }
        catch(Throwable e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int executeUpdate(String sql, boolean needGeneratedKeys)
    {
        Statement statement = null;
        ResultSet resultSet = null;
        int ret = -1;

        try
        {
            statement = connection.createStatement();
            
            if(needGeneratedKeys)
            {
                ret = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            }
            else
            {
                ret = statement.executeUpdate(sql);
            }

            if(needGeneratedKeys && ret >= 0)
            {
                resultSet = statement.getGeneratedKeys();

                if(resultSet.next())
                {
                    ret = resultSet.getInt(1);
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(resultSet, statement);
            resultSet = null;
            statement = null;
        }

        return ret;
    }

    public static int executeUpdate(String sql, Object[] parameters, boolean needGeneratedKeys)
    {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int ret = -1;

        try
        {
            if(needGeneratedKeys)
            {
                statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            }
            else
            {
                statement = connection.prepareStatement(sql);
            }
            
            setParameters(statement, parameters);
            ret = statement.executeUpdate();

            if(needGeneratedKeys && ret >= 0)
            {
                resultSet = statement.getGeneratedKeys();

                if(resultSet.next())
                {
                    ret = resultSet.getInt(1);
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(resultSet, statement);
            resultSet = null;
            statement = null;
        }

        return ret;
    }
    
    public static List<Object[]> executeQuery(String sql, int columnCount)
    {
        List<Object[]> datas = new ArrayList<Object[]>();
        Statement statement = null;
        ResultSet resultSet = null;

        try
        {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            buildResult(datas, resultSet, columnCount);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(resultSet, statement);
            resultSet = null;
            statement = null;
        }

        return datas;
    }
    
    public static List<Object[]> executeQuery(String sql, Object[] parameters, int columnCount)
    {
        List<Object[]> datas = new ArrayList<Object[]>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try
        {
            statement = connection.prepareStatement(sql);
            setParameters(statement, parameters);
            resultSet = statement.executeQuery();
            buildResult(datas, resultSet, columnCount);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(resultSet, statement);
            resultSet = null;
            statement = null;
        }

        return datas;
    }
    
    public static String createConditionSQL(Criterion...criterions)
    {
        StringBuilder sql = new StringBuilder();
        int index = 0;
        
        for(Criterion criterion: criterions)
        {
            if(index == 0)
            {
                sql.append(" where ");
            }
            else
            {
                sql.append(" and ");
            }
            
            sql.append(criterion.toSqlString());
            index++;
        }
        
        return sql.toString();
    }
    
    public static Object[] createConditionParameters(Criterion...criterions)
    {
        List<Object> list = new ArrayList<Object>();
        
        for(Criterion criterion: criterions)
        {
            if(criterion.getValue() != null)
            {
                list.add(criterion.getValue());
            }
            else if(criterion.getValues() != null)
            {
                list.addAll(criterion.getValues());
            }
        }
        
        return list.toArray();
    }
    
    private static void buildResult(List<Object[]> datas, ResultSet resultSet, int columnCount) throws SQLException
    {
        Object[] row;
        
        while(resultSet.next())
        {
            row = new Object[columnCount];
            
            for(int i = 0; i < columnCount; i++)
            {
                row[i] = resultSet.getObject(i + 1);
            }
            
            datas.add(row);
        }
    }

    private static void setParameters(PreparedStatement statement, Object[] parameters) throws SQLException
    {
        int index = 1;

        for(Object parameter: parameters)
        {
            statement.setObject(index++, parameter);
        }
    }
    
    private static void close(ResultSet resultSet, Statement statement)
    {
        if(resultSet != null)
        {
            try
            {
                resultSet.close();
                resultSet = null;
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }

        if(statement != null)
        {
            try
            {
                statement.close();
                statement = null;
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void closeConnection()
    {
        try
        {
            if(connection != null && !connection.isClosed())
            {
                connection.close();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}