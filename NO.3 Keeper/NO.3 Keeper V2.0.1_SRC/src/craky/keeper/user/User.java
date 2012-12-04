package craky.keeper.user;

import java.io.Serializable;

import craky.security.Digest;

public class User implements Serializable
{
    private static final long serialVersionUID = -1818298155807080501L;
    
    public static final int ADMIN = 0;
    
    public static final int USER = 10;
    
    public static final int VISITOR = 20;
    
    public static final int MIN_PASSWORD_LENGTH = 4;
    
    public static final int MAX_PASSWORD_LENGTH = 100;
    
    public static final String ADMIN_DES = "\u8D85\u7EA7\u7BA1\u7406\u5458";
    
    public static final String USER_DES = "\u4E00\u822C\u7528\u6237";
    
    public static final String VISITOR_DES = "\u53D7\u9650\u53C2\u89C2\u8005";
    
    public static final String USER_NOT_EXIST = "\u8BE5\u7528\u6237\u4E0D\u5B58\u5728\uFF01";
    
    public static final String USER_ALREADY_EXIST = "\u8BE5\u7528\u6237\u5DF2\u5B58\u5728\uFF01";
    
    public static final String ILLEGAL_PASSWORD = "\u5BC6\u7801\u9519\u8BEF\uFF01";
    
    public static final String NAME_UNALLOWED = "\u975E\u6CD5\u7528\u6237\u540D\uFF01";
    
    public static final String PASSWORD_LENGTH_ERROR = "\u5BC6\u7801\u957F\u5EA6\u4E0D\u80FD\u5C0F\u4E8E" +
                    MIN_PASSWORD_LENGTH + "\u4F4D\uFF01";
    
    public static final String NAME_REGEX_INFO = "\u7528\u6237\u540D\u4E0D\u80FD\u4E3A\u7A7A\u4E14\u4E0D\u80FD" +
    		        "\u51FA\u73B0\u534A\u89D2\u9017\u53F7(,)\uFF01";
    
    private int id;

    private String name;
    
    private String password;
    
    private int purview;
    
    private boolean autoLogin;
    
    private boolean savePassword;
    
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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getPurview()
    {
        return purview;
    }

    public void setPurview(int purview)
    {
        this.purview = purview;
    }

    public boolean isAutoLogin()
    {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin)
    {
        this.autoLogin = autoLogin;
    }

    public boolean isSavePassword()
    {
        return savePassword;
    }

    public void setSavePassword(boolean savePassword)
    {
        this.savePassword = savePassword;
    }
    
    public String getPurviewName()
    {
        String pn = null;
        
        switch(purview)
        {
            case ADMIN:
            {
                pn = ADMIN_DES;
                break;
            }
            case USER:
            {
                pn = USER_DES;
                break;
            }
            case VISITOR:
            {
                pn = VISITOR_DES;
                break;
            }
        }
        
        return pn == null? "": pn;
    }
    
    public void setPurviewName(String purviewName)
    {
        setPurview(getPurviewByName(purviewName));
    }

    public String toString()
    {
        return name + ", purview=" + purview;
    }
    
    public static int getPurviewByName(String name)
    {
        if(name.equals(ADMIN_DES))
        {
            return ADMIN;
        }
        else if(name.equals(USER_DES))
        {
            return USER;
        }
        else
        {
            return VISITOR;
        }
    }
    
    public static String createCiphertext(String name, String password)
    {
        return Digest.computeMD5(Digest.computeMD5(name) + Digest.computeMD5(password));
    }
    
    public static boolean isAllowedName(String name)
    {
        return name != null && !(name = name.trim()).isEmpty() && name.indexOf(',') < 0;
    }
    
    public static boolean isAllowedPassword(String password)
    {
        return password.length() >= MIN_PASSWORD_LENGTH;
    }
}