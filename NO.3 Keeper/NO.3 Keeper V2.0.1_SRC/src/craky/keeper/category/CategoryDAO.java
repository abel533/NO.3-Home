package craky.keeper.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import craky.keeper.sql.Criterion;
import craky.keeper.sql.Restrictions;
import craky.keeper.util.DBUtil;

public class CategoryDAO
{
    public static List<Category> getCategorys(boolean isPay, Criterion...criterions)
    {
        criterions = createCriterions(isPay, criterions);
        List<Category> categorys = new ArrayList<Category>();
        String sql = "select * from category" + DBUtil.createConditionSQL(criterions);
        List<Object[]> datas = DBUtil.executeQuery(sql, DBUtil.createConditionParameters(criterions), 4);
        Category category;
        
        for(Object[] data: datas)
        {
            category = new Category();
            category.setId((Integer)data[0]);
            category.setName((String)data[1]);
            category.setType((Integer)data[2]);
            category.setCount((Long)data[3]);
            
            //用户类别
            if(category.getType() % 100 != 0 && categorys.size() >= 2)
            {
                categorys.add(categorys.size() - 2, category);
            }
            //系统类别
            else
            {
                categorys.add(category);
            }
        }
        
        return categorys;
    }
    
    public static Criterion[] createCriterions(boolean isPay, Criterion...criterions)
    {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        
        if(criterions != null && criterions.length > 0)
        {
            criterionList.addAll(Arrays.asList(criterions));
        }
        
        if(isPay)
        {
            criterionList.add(Restrictions.or(Restrictions.eq("type", Category.TYPE_PAY_SYSTEM), Restrictions.eq("type", Category.TYPE_PAY_CUSTOM)));
        }
        else
        {
            criterionList.add(Restrictions.or(Restrictions.eq("type", Category.TYPE_INCOME_SYSTEM), Restrictions.eq("type", Category.TYPE_INCOME_CUSTOM)));
        }
        
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }
    
    public static void addCategory(Category category)
    {
        String sql = "insert into category (name, type, count) values (?, ?, ?)";
        Object[] parameters = {category.getName(), category.getType(), category.getCount()};
        int id = DBUtil.executeUpdate(sql, parameters, true);

        if(id >= 0)
        {
            category.setId(id);
        }
    }
    
    public static void deleteCategory(Category category)
    {
        DBUtil.executeUpdate("delete from category where id=" + category.getId(), false);
    }
    
    public static void updateCategory(Category category)
    {
        String sql = "update category set count=? where id=?";
        Object[] parameters = {category.getCount(), category.getId()};
        DBUtil.executeUpdate(sql, parameters, false);
    }
    
    public static void afterInsertItem(Category category)
    {
        category.setCount(category.getCount() + 1);
        updateCategory(category);
    }
    
    public static void afterRemoveItem(Category category)
    {
        category.setCount(category.getCount() - 1);
        updateCategory(category);
    }
}