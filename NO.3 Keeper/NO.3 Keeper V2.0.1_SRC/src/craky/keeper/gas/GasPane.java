package craky.keeper.gas;

import javax.swing.JComponent;

import craky.keeper.KeeperApp;
import craky.keeper.KeeperMgr;
import craky.keeper.KeeperObject;
import craky.keeper.KeeperPane;

public class GasPane extends KeeperPane
{
    private static final long serialVersionUID = -5477856002595587473L;
    
    public static final String TYPE = "\u71C3\u6C14\u8D39";
    
    public GasPane(KeeperApp keeper, KeeperMgr mgr)
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