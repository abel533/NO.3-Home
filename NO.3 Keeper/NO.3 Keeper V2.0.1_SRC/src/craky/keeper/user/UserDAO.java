package craky.keeper.user;

import java.util.ArrayList;
import java.util.List;

import craky.keeper.sql.Criterion;
import craky.keeper.sql.Restrictions;
import craky.keeper.util.DBUtil;

public class UserDAO
{
    public static User login(String name, String password)
    {
        List<User> users = getUsers(Restrictions.eq("name", name));
        User user = null;
        
        if(users.size() > 0 && !(user = users.get(0)).getPassword().equals(password))
        {
            user.setPassword(null);
        }
        
        return user;
    }
    
    public static boolean isFirst()
    {
        return getUsers(Restrictions.eq("purview", User.ADMIN)).isEmpty();
    }
    
    public static List<User> getUsers(Criterion...criterions)
    {
        List<User> users = new ArrayList<User>();
        String sql = "select * from user" + DBUtil.createConditionSQL(criterions);
        List<Object[]> datas = DBUtil.executeQuery(sql, DBUtil.createConditionParameters(criterions), 4);
        User user;
        
        for(Object[] data: datas)
        {
            user = new User();
            user.setId((Integer)data[0]);
            user.setName((String)data[1]);
            user.setPassword((String)data[2]);
            user.setPurview((Integer)data[3]);
            users.add(user);
        }
        
        return users;
    }
    
    public static void addUser(User user)
    {
        String sql = "insert into user (name, password, purview) values (?, ?, ?)";
        Object[] parameters = {user.getName(), user.getPassword(), user.getPurview()};
        int id = DBUtil.executeUpdate(sql, parameters, true);

        if(id >= 0)
        {
            user.setId(id);
        }
    }
    
    public static void deleteUser(User user)
    {
        DBUtil.executeUpdate("delete from user where id=" + user.getId(), false);
    }
    
    public static void updateUser(User user)
    {
        String sql = "update user set password=? where id=?";
        Object[] parameters = {user.getPassword(), user.getId()};
        DBUtil.executeUpdate(sql, parameters, false);
    }
}