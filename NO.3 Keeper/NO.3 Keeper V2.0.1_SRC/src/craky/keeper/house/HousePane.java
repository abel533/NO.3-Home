package craky.keeper.house;

import javax.swing.JComponent;

import craky.keeper.KeeperApp;
import craky.keeper.KeeperMgr;
import craky.keeper.KeeperObject;
import craky.keeper.KeeperPane;

public class HousePane extends KeeperPane
{
    private static final long serialVersionUID = -6547714216522529561L;
    
    public static final String TYPE = "\u4F4F\u623F";
    
    public HousePane(KeeperApp keeper, KeeperMgr mgr)
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