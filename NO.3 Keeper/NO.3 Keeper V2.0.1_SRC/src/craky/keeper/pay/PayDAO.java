package craky.keeper.pay;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import craky.keeper.category.Category;
import craky.keeper.category.CategoryDAO;
import craky.keeper.sql.Criterion;
import craky.keeper.util.DBUtil;

public class PayDAO
{
    public static List<Pay> getPays(Criterion...criterions)
    {
        List<Pay> pays = new ArrayList<Pay>();
        String sql = "select * from pay" + DBUtil.createConditionSQL(criterions);
        List<Object[]> datas = DBUtil.executeQuery(sql, DBUtil.createConditionParameters(criterions), 9);
        Pay pay;
        
        for(Object[] data: datas)
        {
            pay = new Pay();
            pay.setId((Integer)data[0]);
            pay.setDate((Date)data[1]);
            pay.setAmount((Float)data[2]);
            pay.setSummary((String)data[3]);
            pay.setType((String)data[4]);
            pay.setDetail((String)data[5]);
            pay.setRemark((String)data[6]);
            pay.setRecordTime((Timestamp)data[7]);
            pay.setRecorder((String)data[8]);
            pays.add(pay);
        }
        
        return pays;
    }

    public static void addPay(Pay pay, Category category)
    {
        String sql = "insert into pay (date, amount, summary, type, detail, remark, recordTime, recorder) values (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] parameters = {pay.getDate(), pay.getAmount(), pay.getSummary(), pay.getType(), pay.getDetail(), pay.getRemark(),
                               pay.getRecordTime(), pay.getRecorder()};
        int id = DBUtil.executeUpdate(sql, parameters, true);

        if(id >= 0)
        {
            pay.setId(id);
            CategoryDAO.afterInsertItem(category);
        }
    }

    public static void deletePay(Pay pay, Category category)
    {
        int ret = DBUtil.executeUpdate("delete from pay where id=" + pay.getId(), false);

        if(ret >= 0)
        {
            CategoryDAO.afterRemoveItem(category);
        }
    }

    public static void updatePay(Pay pay)
    {
        String sql = "update pay set date=?, amount=?, summary=?, type=?, detail=?, remark=?, recordTime=?, recorder=? where id=?";
        Object[] parameters = {pay.getDate(), pay.getAmount(), pay.getSummary(), pay.getType(), pay.getDetail(), pay.getRemark(),
                               pay.getRecordTime(), pay.getRecorder(), pay.getId()};
        DBUtil.executeUpdate(sql, parameters, false);
    }
}