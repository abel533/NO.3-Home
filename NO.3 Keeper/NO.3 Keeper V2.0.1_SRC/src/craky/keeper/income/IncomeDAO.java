package craky.keeper.income;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import craky.keeper.category.Category;
import craky.keeper.category.CategoryDAO;
import craky.keeper.sql.Criterion;
import craky.keeper.util.DBUtil;

public class IncomeDAO
{
    public static List<Income> getIncomes(Criterion...criterions)
    {
        List<Income> incomes = new ArrayList<Income>();
        String sql = "select * from income" + DBUtil.createConditionSQL(criterions);
        List<Object[]> datas = DBUtil.executeQuery(sql, DBUtil.createConditionParameters(criterions), 9);
        Income income;
        
        for(Object[] data: datas)
        {
            income = new Income();
            income.setId((Integer)data[0]);
            income.setDate((Date)data[1]);
            income.setAmount((Float)data[2]);
            income.setSummary((String)data[3]);
            income.setType((String)data[4]);
            income.setDetail((String)data[5]);
            income.setRemark((String)data[6]);
            income.setRecordTime((Timestamp)data[7]);
            income.setRecorder((String)data[8]);
            incomes.add(income);
        }
        
        return incomes;
    }

    public static void addIncome(Income income, Category category)
    {
        String sql = "insert into income (date, amount, summary, type, detail, remark, recordTime, recorder) values (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] parameters = {income.getDate(), income.getAmount(), income.getSummary(), income.getType(), income.getDetail(), income.getRemark(),
                               income.getRecordTime(), income.getRecorder()};
        int id = DBUtil.executeUpdate(sql, parameters, true);

        if(id >= 0)
        {
            income.setId(id);
            CategoryDAO.afterInsertItem(category);
        }
    }

    public static void deleteIncome(Income income, Category category)
    {
        int ret = DBUtil.executeUpdate("delete from income where id=" + income.getId(), false);

        if(ret >= 0)
        {
            CategoryDAO.afterRemoveItem(category);
        }
    }

    public static void updateIncome(Income income)
    {
        String sql = "update income set date=?, amount=?, summary=?, type=?, detail=?, remark=?, recordTime=?, recorder=? where id=?";
        Object[] parameters = {income.getDate(), income.getAmount(), income.getSummary(), income.getType(), income.getDetail(), income.getRemark(),
                               income.getRecordTime(), income.getRecorder(), income.getId()};
        DBUtil.executeUpdate(sql, parameters, false);
    }
}