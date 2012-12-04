package craky.keeper;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import craky.componentc.JCCheckBoxMenuItem;
import craky.componentc.JCMenu;
import craky.componentc.JCMenuItem;
import craky.componentc.JCRadioButtonMenuItem;
import craky.keeper.category.CategoryListDialog;
import craky.keeper.user.User;
import craky.keeper.user.UserListDialog;
import craky.keeper.util.KeeperUtil;
import craky.util.Config;
import craky.util.UIUtil;

public class KeeperMenu extends JCMenu implements ActionListener, PopupMenuListener
{
    private static final long serialVersionUID = -7383303149120649519L;

    private static final Icon ICON = KeeperUtil.getIcon("menu_button_icon.png");
    
    private static final Image ROLLOVER_IMAGE = KeeperUtil.getImage("common_button_rollover_bg.png", true);
    
    private static final Image PRESSED_IMAGE = KeeperUtil.getImage("common_button_pressed_bg.png", true);
    
    private KeeperApp keeper;
    
    private KeeperMgr mgr;
    
    private Config config;
    
    private JMenuItem miExport, miExit, miUser, miPayCategory, miIncomeCategory, miTitleOpaque, miShowDetail, miShowFind, miSetting, miAbout;
    
    private enum MenuItemType
    {
        MenuItem, RadioMenuItem, CheckBoxMenuItem
    }
    
    public KeeperMenu(KeeperApp keeper, KeeperMgr mgr)
    {
        super();
        this.keeper = keeper;
        this.mgr = mgr;
        this.config = keeper.getConfig();
        setToolTipText("\u4E3B\u83DC\u5355");
        setPreferredSize(new Dimension(20, 20));
        setShowWhenRollover(false);
        init();
        getPopupMenu().addPopupMenuListener(this);
        addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent e)
            {
                getModel().setRollover(true);
            }
            
            public void mouseExited(MouseEvent e)
            {
                getModel().setRollover(false);
            }
        });
    }
    
    private void init()
    {
        JCMenu meFile, meView, meAdvanced;
        add(meFile = new JCMenu("\u6587\u4EF6"));
        add(meView = new JCMenu("\u67E5\u770B"));
        add(meAdvanced = new JCMenu("\u9AD8\u7EA7"));
        add(miSetting = createMenuItem("\u8BBE\u7F6E(S)", 0, 0, 'S', MenuItemType.MenuItem));
        add(miAbout = createMenuItem("\u5173\u4E8E(A)", 0, 0, 'A', MenuItemType.MenuItem));
        meFile.add(miExport = createMenuItem("\u5BFC\u51FA\u4E3ACSV(E)...", 0, 0, 'E', MenuItemType.MenuItem));
        meFile.addSeparator();
        meFile.add(miExit = createMenuItem("\u9000\u51FA(X)", 0, 0, 'X', MenuItemType.MenuItem));
        meAdvanced.add(miUser = createMenuItem("\u7528\u6237\u7BA1\u7406(U)", 0, 0, 'U', MenuItemType.MenuItem));
        meAdvanced.add(miPayCategory = createMenuItem("\u6D88\u8D39\u7C7B\u522B(P)", 0, 0, 'P', MenuItemType.MenuItem));
        meAdvanced.add(miIncomeCategory = createMenuItem("\u6536\u5165\u7C7B\u522B(I)", 0, 0, 'I', MenuItemType.MenuItem));
        meView.add(miTitleOpaque = createMenuItem("\u6807\u9898\u680F\u900F\u660E(T)", 0, 0, 'T', MenuItemType.CheckBoxMenuItem));
        meView.add(miShowDetail = createMenuItem("\u663E\u793A\u660E\u7EC6\u680F(D)", 0, 0, 'D', MenuItemType.CheckBoxMenuItem));
        meView.add(miShowFind = createMenuItem("\u663E\u793A\u67E5\u627E\u680F(F)", 'F', KeyEvent.CTRL_MASK, 'F',
                        MenuItemType.CheckBoxMenuItem));
        miExport.setIcon(KeeperUtil.getIcon("export.png"));
        miExit.setIcon(KeeperUtil.getIcon("menu_exit.png"));
        miUser.setIcon(KeeperUtil.getIcon("menu_user.png"));
        miPayCategory.setIcon(KeeperUtil.getIcon("menu_pay_category.png"));
        miIncomeCategory.setIcon(KeeperUtil.getIcon("menu_income_category.png"));
        miSetting.setIcon(KeeperUtil.getIcon("menu_setting.png"));
        miAbout.setIcon(KeeperUtil.getIcon("logo_16.png"));
        meAdvanced.setVisible(keeper.getCurrentUser().getPurview() == User.ADMIN);
    }
    
    private JMenuItem createMenuItem(String text, int key, int modifiers, char mnemonic, MenuItemType type)
    {
        JMenuItem item;
        
        if(type == MenuItemType.RadioMenuItem)
        {
            item = new JCRadioButtonMenuItem(text);
        }
        else if(type == MenuItemType.CheckBoxMenuItem)
        {
            item = new JCCheckBoxMenuItem(text);
        }
        else
        {
            item = new JCMenuItem(text);
        }
        
        if(key != 0)
        {
            item.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
        }
        
        if(mnemonic != 0)
        {
            item.setMnemonic(mnemonic);
        }
        
        item.addActionListener(this);
        return item;
    }
    
    protected void paintComponent(Graphics g)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        int iconWidth = ICON.getIconWidth();
        int iconHeight = ICON.getIconHeight();
        
        if(this.getModel().isSelected())
        {
            UIUtil.paintImage(g, PRESSED_IMAGE, new Insets(2, 2, 2, 2), new Rectangle(0, 0, width, height), this);
        }
        else if(this.getModel().isRollover())
        {
            UIUtil.paintImage(g, ROLLOVER_IMAGE, new Insets(2, 2, 2, 2), new Rectangle(0, 0, width, height), this);
        }
        
        ICON.paintIcon(this, g, (width - iconWidth) / 2, (height - iconHeight) / 2);
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        
        if(source == miExport)
        {
            mgr.export();
        }
        else if(source == miExit)
        {
            keeper.exit();
        }
        else if(source == miTitleOpaque)
        {
            mgr.setTitleOpaque(!Boolean.valueOf(config.getProperty(KeeperConst.TITLE_OPAQUE, "false")));
        }
        else if(source == miShowDetail)
        {
            mgr.showDetail(!Boolean.valueOf(config.getProperty(KeeperConst.SHOW_DETAIL, "true")));
        }
        else if(source == miShowFind)
        {
            mgr.showFind(!Boolean.valueOf(config.getProperty(KeeperConst.SHOW_FIND, "true")));
        }
        else if(source == miUser)
        {
            new UserListDialog(keeper);
        }
        else if(source == miPayCategory)
        {
            new CategoryListDialog(keeper, true);
        }
        else if(source == miIncomeCategory)
        {
            new CategoryListDialog(keeper, false);
        }
        else if(source == miSetting)
        {
            mgr.setting();
        }
        else if(source == miAbout)
        {
            new AboutDialog(keeper);
        }
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e)
    {
        miTitleOpaque.setSelected(!Boolean.valueOf(config.getProperty(KeeperConst.TITLE_OPAQUE, "false")));
        miShowDetail.setSelected(Boolean.valueOf(config.getProperty(KeeperConst.SHOW_DETAIL, "true")));
        miShowFind.setSelected(Boolean.valueOf(config.getProperty(KeeperConst.SHOW_FIND, "true")));
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

    public void popupMenuCanceled(PopupMenuEvent e) {}
}