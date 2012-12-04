package craky.keeper.category;

import java.io.Serializable;

public class Category implements Serializable
{
    private static final long serialVersionUID = 2085530002657221873L;
    
    public static final int TYPE_PAY_SYSTEM = 0;
    
    public static final int TYPE_PAY_CUSTOM = 1;
    
    public static final int TYPE_INCOME_SYSTEM = 100;
    
    public static final int TYPE_INCOME_CUSTOM = 101;
    
    public static final String SYSTEM_DES = "\u7CFB\u7EDF\u7C7B\u522B";
    
    public static final String CUSTOM_DES = "\u7528\u6237\u7C7B\u522B";

    private int id;
    
    private String name;
    
    private int type;
    
    private long count;
    
    public Category() {}
    
    public Category(String name, boolean isPay)
    {
        this.name = name;
        this.type = isPay? TYPE_PAY_CUSTOM: TYPE_INCOME_CUSTOM;
        this.count = 0;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }
    
    public String getTypeName()
    {
        String tn = null;
        
        switch(type)
        {
            case TYPE_PAY_SYSTEM:
            case TYPE_INCOME_SYSTEM:
            {
                tn = SYSTEM_DES;
                break;
            }
            case TYPE_PAY_CUSTOM:
            case TYPE_INCOME_CUSTOM:
            {
                tn = CUSTOM_DES;
                break;
            }
        }
        
        return tn == null? "": tn;
    }
    
    public void setTypeName(String typeName)
    {}
    
    public String toString()
    {
        return name;
    }
}