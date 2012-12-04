package craky.keeper.water;

import javax.swing.JComponent;

import craky.keeper.KeeperApp;
import craky.keeper.KeeperMgr;
import craky.keeper.KeeperObject;
import craky.keeper.KeeperPane;

public class WaterPane extends KeeperPane
{
    private static final long serialVersionUID = 1750643905524912755L;
    
    public static final String TYPE = "\u6C34\u8D39";
    
    public WaterPane(KeeperApp keeper, KeeperMgr mgr)
    {
        super(keeper, mgr);
    }
    
    protected JComponent createCenter()
    {
        super.createCenter();
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(3));
        return center;
    }
    
    protected JComponent createBottom()
    {
        super.createBottom();
        ecType.setVisible(false);
        return bottom;
    }
    
    protected JComponent createTop()
    {
        super.createTop();
        ecFType.setVisible(false);
        return top;
    }
    
    protected void resetRowValues(KeeperObject keeperObject)
    {
        super.resetRowValues(keeperObject);
        keeperObject.setType(TYPE);
    }
    
    public String getType()
    {
        return TYPE;
    }
}