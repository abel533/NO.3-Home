package craky.keeper.income;

import craky.keeper.KeeperApp;
import craky.keeper.KeeperMgr;
import craky.keeper.StatVisibleKeeperPane;

public class IncomePane extends StatVisibleKeeperPane
{
    private static final long serialVersionUID = -8391014681519430181L;
    
    private static final String[] COLUMNS_NAME = {"\u6536\u5165\u65E5\u671F", "\u91D1\u989D(\u5143)", "\u6458\u8981", "\u7C7B\u522B",
                                                  "\u660E\u7EC6", "\u5907\u6CE8", "\u5165\u8D26\u65F6\u95F4", "\u5165\u8D26\u7528\u6237"};
    
    public static final String TYPE = "\u6536\u5165";
    
    public IncomePane(KeeperApp keeper, KeeperMgr mgr)
    {
        super(keeper, mgr);
    }

    public String getType()
    {
        return TYPE;
    }

    protected String getPieChartTitle()
    {
        return "\u6536\u5165\u7C7B\u522B\u7EDF\u8BA1";
    }

    protected String getBarChartTitle()
    {
        return "\u6536\u5165\u65E5\u671F\u7EDF\u8BA1";
    }
    
    protected String[] getColumnsName()
    {
        return COLUMNS_NAME;
    }
    
    protected String getDataName()
    {
        return "\u6536\u5165";
    }
    
    public boolean isPay()
    {
        return false;
    }
}