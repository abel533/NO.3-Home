package craky.keeper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import craky.componentc.JCToggleButton;
import craky.keeper.pay.PayPane;
import craky.keeper.user.User;
import craky.keeper.util.KeeperUtil;
import craky.util.Config;

public class KeeperMgr implements ActionListener, ItemListener
{
    private KeeperApp keeper;
    
    private MainFrame mainFrame;
    
    private KeeperPane currentPane;
    
    private Config config;
    
    public KeeperMgr(KeeperApp keeper, MainFrame mainFrame)
    {
        this.keeper = keeper;
        this.mainFrame = mainFrame;
        this.config = keeper.getConfig();
    }
    
    public void showDetail(boolean show)
    {
        config.savePropertie(KeeperConst.SHOW_DETAIL, String.valueOf(show));
        
        if(currentPane != null)
        {
            currentPane.setShowDetail(show);
        }
    }
    
    public void showFind(boolean show)
    {
        config.savePropertie(KeeperConst.SHOW_FIND, String.valueOf(show));
        
        if(currentPane != null)
        {
            currentPane.setShowFind(show);
        }
    }
    
    public void setTitleOpaque(boolean opaque)
    {
        mainFrame.setTitleOpaque(opaque);
        config.savePropertie(KeeperConst.TITLE_OPAQUE, String.valueOf(opaque));
    }
    
    public void export()
    {
        if(currentPane != null)
        {
            KeeperUtil.switchToSystemLAFTemp(new AbstractAction()
            {
                private static final long serialVersionUID = -9177012597857998858L;

                public void actionPerformed(ActionEvent e)
                {
                    currentPane.exportToCSV();
                }
            });
        }
    }
    
    public void reload()
    {
        if(currentPane != null)
        {
            currentPane.reload();
        }
    }
    
    public void submit()
    {
        if(currentPane != null)
        {
            currentPane.submit();
        }
    }
    
    public void setting()
    {
        new SettingDialog(keeper);
    }
    
    public void needReload(String type)
    {
        if(currentPane instanceof PayPane)
        {
            KeeperPane pane = mainFrame.getPaneByType(type);
            
            if(pane != null)
            {
                pane.needReload();
            }
        }
        else if(currentPane.isPay())
        {
            mainFrame.getPaneByType(PayPane.TYPE).needReload();
        }
    }
    
    public void resetTotal()
    {
        int total = 0;
        float amount = 0;
        List<KeeperObject> datas;
        
        if(currentPane != null && (datas = currentPane.getAllData()) != null)
        {
            total = datas.size();
            
            for(KeeperObject keeperObject: datas)
            {
                amount += keeperObject.getAmount();
            }
        }
        
        mainFrame.resetTotal(total, amount);
    }
    
    public KeeperPane getCurrentPane()
    {
        return currentPane;
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        String cmd = e.getActionCommand();
        User user = keeper.getCurrentUser();
        
        if(cmd.equals(KeeperConst.CMD_CANCEL_AUTO_LOGIN))
        {
            Config loginConfig = keeper.getLoginConfig();
            loginConfig.removeAndSave(user.getName() + KeeperConst.AUTO_LOGIN_KEY);
            ((JComponent)source).setVisible(false);
        }
        else if(cmd.equals(KeeperConst.CMD_CANCEL_SAVED_PASSWORD))
        {
            Config loginConfig = keeper.getLoginConfig();
            Object linkedComponent = ((JComponent)source).getClientProperty(KeeperConst.LINKED_COMPONENT);
            loginConfig.removeAndSave(user.getName() + KeeperConst.PASSWORD_KEY);
            ((JComponent)source).setVisible(false);
            
            if(linkedComponent != null)
            {
                ((JComponent)linkedComponent).setVisible(false);
            }
        }
        else if(cmd.equals(KeeperConst.CMD_RELOAD))
        {
            reload();
        }
        else if(cmd.equals(KeeperConst.CMD_SETTING))
        {
            setting();
        }
    }

    public void itemStateChanged(ItemEvent e)
    {
        Object source = e.getSource();
        
        if(source instanceof JCToggleButton && e.getStateChange() == ItemEvent.SELECTED)
        {
            String cmd = ((JCToggleButton)source).getActionCommand();
            
            if(cmd.equals(KeeperConst.CMD_SHOW_KEEPER_PANE))
            {
                currentPane = (KeeperPane)((JCToggleButton)source).getClientProperty(KeeperConst.LINKED_COMPONENT);
                currentPane.setShowDetail(Boolean.valueOf(config.getProperty(KeeperConst.SHOW_DETAIL, "true")));
                currentPane.setShowFind(Boolean.valueOf(config.getProperty(KeeperConst.SHOW_FIND, "true")));
                mainFrame.showPane(currentPane.getName());
                
                if(!currentPane.isInited())
                {
                    currentPane.load();
                }
            }
        }
    }
}