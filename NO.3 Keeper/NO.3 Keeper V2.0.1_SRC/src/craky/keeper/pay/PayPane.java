package craky.keeper.pay;

import craky.keeper.KeeperApp;
import craky.keeper.KeeperMgr;
import craky.keeper.StatVisibleKeeperPane;

public class PayPane extends StatVisibleKeeperPane
{
    private static final long serialVersionUID = -381623737074535966L;
    
    public static final String TYPE = "\u5168\u90E8";
    
    public PayPane(KeeperApp keeper, KeeperMgr mgr)
    {
        super(keeper, mgr);
    }

    public String getType()
    {
        return TYPE;
    }

    protected String getPieChartTitle()
    {
        return "\u6D88\u8D39\u7C7B\u522B\u7EDF\u8BA1";
    }

    protected String getBarChartTitle()
    {
        return "\u6D88\u8D39\u65E5\u671F\u7EDF\u8BA1";
    }
}