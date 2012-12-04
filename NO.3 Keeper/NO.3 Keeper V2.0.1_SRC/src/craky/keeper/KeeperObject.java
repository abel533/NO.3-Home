package craky.keeper;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;

import craky.util.Util;

public abstract class KeeperObject implements Serializable
{
    private static final long serialVersionUID = 195657733922652608L;

    private int id;
    
    private Date date;
    
    private float amount;
    
    private String summary;
    
    private String type;
    
    private String detail;
    
    private String remark;
    
    private Timestamp recordTime;
    
    private String recorder;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public float getAmount()
    {
        return amount;
    }

    public void setAmount(float amount)
    {
        this.amount = amount;
    }
    
    public String getAmountString()
    {
        return KeeperConst.AMOUNT_FORMAT.format(amount);
    }
    
    public void setAmountString(String amountString)
    {
        try
        {
            setAmount(KeeperConst.AMOUNT_FORMAT.parse(amountString).floatValue());
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Timestamp getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(Timestamp recordTime)
    {
        this.recordTime = recordTime;
    }

    public String getRecorder()
    {
        return recorder;
    }

    public void setRecorder(String recorder)
    {
        this.recorder = recorder;
    }
    
    public String toCSVString()
    {
        StringBuilder str = new StringBuilder();
        char comma = ',';
        str.append(String.valueOf(getDate()));
        str.append(comma);
        str.append(Util.toCSVString(getAmountString()));
        str.append(comma);
        str.append(Util.toCSVString(getSummary()));
        str.append(comma);
        str.append(Util.toCSVString(getType()));
        str.append(comma);
        str.append(Util.toCSVString(getDetail()));
        str.append(comma);
        str.append(Util.toCSVString(getRemark()));
        str.append(comma);
        str.append(String.valueOf(getRecordTime()));
        str.append(comma);
        str.append(Util.toCSVString(getRecorder()));
        return str.toString();
    }
    
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        char comma = ',';
        str.append(String.valueOf(getDate()));
        str.append(comma);
        str.append(getAmountString());
        str.append(comma);
        str.append(getSummary());
        str.append(comma);
        str.append(getType());
        str.append(comma);
        str.append(getDetail());
        str.append(comma);
        str.append(getRemark());
        str.append(comma);
        str.append(String.valueOf(getRecordTime()));
        str.append(comma);
        str.append(getRecorder());
        return str.toString();
    }
}