package craky.keeper;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sun.awt.AWTUtilities;

import craky.component.JImagePane;
import craky.componentc.CRootPaneUI.ImageDisplayMode;
import craky.componentc.JCButton;
import craky.componentc.JCFrame;
import craky.componentc.JCLabel;
import craky.componentc.JCToggleButton;
import craky.keeper.electricity.ElePane;
import craky.keeper.extra.ExtraPane;
import craky.keeper.gas.GasPane;
import craky.keeper.house.HousePane;
import craky.keeper.income.IncomePane;
import craky.keeper.pay.PayPane;
import craky.keeper.skin.SkinMenu;
import craky.keeper.user.User;
import craky.keeper.util.KeeperUtil;
import craky.keeper.water.WaterPane;
import craky.layout.LineLayout;
import craky.util.Config;
import craky.util.UIUtil;

public class MainFrame extends JCFrame
{
    private static final long serialVersionUID = -1008270690673070592L;
    
    private KeeperApp keeper;
    
    private KeeperMgr mgr;
    
    private Config config;
    
    private Map<String, KeeperPane> paneMap;
    
    private JImagePane titleContent, mainPane;
    
    private JCToggleButton btnPay, btnWater, btnEle, btnGas, btnHouse, btnExtra, btnIncome;
    
    private JCButton btnReload, btnSetting;
    
    private JImagePane statusBar;
    
    private JCLabel lbStatusInfo, lbTotal, lbAmount;
    
    private SkinMenu skinMenu;
    
    private CardLayout cardLayout;
    
    private AbstractButton[] buttons;
    
    private KeeperPane[] keeperPanes;
    
    public MainFrame(KeeperApp keeper)
    {
        super();
        this.keeper = keeper;
        this.mgr = new KeeperMgr(keeper, this);
        this.config = keeper.getConfig();
        this.titleContent = getTitleContentPane();
        this.paneMap = new HashMap<String, KeeperPane>();
        initUI();
        setTabForegroundDes(config.getProperty(KeeperConst.TABS_FOREGROUND, "225,242,250"));
        setStatusForegroundDes(config.getProperty(KeeperConst.STATUS_FOREGROUND, "225,242,250"));
        
        if(UIUtil.isTranslucencySupported())
        {
            AWTUtilities.setWindowOpacity(this, Float.valueOf(config.getProperty(KeeperConst.WINDOW_ALPHA, "1.0")));
        }
        
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                MainFrame.this.keeper.exit();
            }
        });
        UIUtil.registerKeyEvent(this, new AbstractAction()
        {
            private static final long serialVersionUID = 2054393387947500297L;

            public void actionPerformed(ActionEvent e)
            {
                mgr.reload();
            }
        }, "F5", KeyEvent.VK_F5, 0);
        UIUtil.registerKeyEvent(this, new AbstractAction()
        {
            private static final long serialVersionUID = 7021461035699115902L;

            public void actionPerformed(ActionEvent e)
            {
                mgr.submit();
            }
        }, "Ctrl+Enter", KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
        setVisible(true);
    }
    
    private void initUI()
    {
        JPanel content = (JPanel)getContentPane();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle(KeeperConst.APP_TITLE + " - " + KeeperConst.APP_VERSION);
        setIconImage(KeeperUtil.getImage("logo_16.png"));
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(0, 2, 5, 2));
        setMinimumSize(new Dimension(800, 550));
        setSize(KeeperUtil.stringToSize(config.getProperty(KeeperConst.WINDOW_SIZE, "900,600")));
        setLocationRelativeTo(null);
        setTitleOpaque(Boolean.valueOf(config.getProperty(KeeperConst.TITLE_OPAQUE, "false")));
        setImageDisplayMode(ImageDisplayMode.valueOf(config.getProperty(KeeperConst.SKIN_MODE, "FILL")));
        setImageAlpha(Float.valueOf(config.getProperty(KeeperConst.SKIN_ALPHA, "0.50")));
        titleContent.setLayout(new LineLayout(0, 0, 0, 2, 0, LineLayout.TRAILING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        initMenu();
        initButtons();
        initPanes();
        btnPay.setSelected(true);
        
        if(Boolean.parseBoolean(config.getProperty(KeeperConst.WINDOW_MAXIMIZED, "false")))
        {
            setExtendedState(MAXIMIZED_BOTH);
        }
    }
    
    private void initMenu()
    {
        JMenuBar bar = new JMenuBar();
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 0, 0));
        bar.setBorderPainted(false);
        bar.setLayout(new GridLayout(1, 2));
        bar.setPreferredSize(new Dimension(40, 20));
        bar.setFocusable(false);
        bar.add(new KeeperMenu(keeper, mgr));
        bar.add(skinMenu = new SkinMenu(keeper));
        titleContent.add(bar, LineLayout.END);
        resetSkinPopupLocation();
    }
    
    private void initButtons()
    {
        boolean isVisitor = keeper.getCurrentUser().getPurview() >= User.VISITOR;
        Dimension buttonSize = new Dimension(84, 78);
        ButtonGroup group = new ButtonGroup();
        Image normalImage = KeeperUtil.getImage("tab_button_normal.png", true);
        Image selImage = KeeperUtil.getImage("tab_button_chosen.png", true);
        btnPay = new JCToggleButton("\u65E5\u5E38\u5F00\u652F");
        btnWater = new JCToggleButton("\u6C34\u8D39");
        btnEle = new JCToggleButton("\u7535\u8D39");
        btnGas = new JCToggleButton("\u71C3\u6C14\u8D39");
        btnHouse = new JCToggleButton("\u4F4F\u623F\u652F\u51FA");
        btnExtra = isVisitor? null: new JCToggleButton("\u989D\u5916\u652F\u51FA");
        btnIncome = isVisitor? null: new JCToggleButton("\u6536\u5165");
        btnReload = new JCButton("\u5237\u65B0");
        btnSetting = new JCButton("\u8BBE\u7F6E");
        buttons = new AbstractButton[]{btnPay, btnWater, btnEle, btnGas, btnHouse, btnExtra, btnIncome, btnReload, btnSetting};
        keeperPanes = new KeeperPane[]{new PayPane(keeper, mgr), new WaterPane(keeper, mgr), new ElePane(keeper, mgr),
                                       new GasPane(keeper, mgr), new HousePane(keeper, mgr), (isVisitor? null: new ExtraPane(keeper, mgr)),
                                       (isVisitor? null: new IncomePane(keeper, mgr)), null, null};
        String[] iconNames = {"all_pay.png", "water.png", "eletricity.png", "gas.png", "house.png", "extra.png", "income.png", "reload.png", "setting.png"};
        JCToggleButton ctb;
        JCButton cb;
        int index = 0;
        
        for(AbstractButton button: buttons)
        {
            if(button != null)
            {
                group.add(button);
                button.setIcon(KeeperUtil.getIcon(iconNames[index]));
                button.setPreferredSize(buttonSize);
                button.setFocusable(false);
                button.setVerticalTextPosition(SwingConstants.BOTTOM);
                button.setHorizontalTextPosition(SwingConstants.CENTER);
                button.setIconTextGap(0);
                button.putClientProperty(KeeperConst.LINKED_COMPONENT, keeperPanes[index]);

                if(button instanceof JCToggleButton)
                {
                    ctb = (JCToggleButton)button;
                    ctb.setPaintPressDown(false);
                    ctb.setImage(normalImage);
                    ctb.setRolloverImage(selImage);
                    ctb.setSelectedImage(selImage);
                    ctb.setActionCommand(KeeperConst.CMD_SHOW_KEEPER_PANE);
                    ctb.addItemListener(mgr);
                }
                else
                {
                    cb = (JCButton)button;
                    cb.setImage(normalImage);
                    cb.setRolloverImage(selImage);
                    cb.setPressedImage(selImage);
                    cb.addActionListener(mgr);
                }
            }
            
            index++;
        }
        
        btnReload.setActionCommand(KeeperConst.CMD_RELOAD);
        btnSetting.setActionCommand(KeeperConst.CMD_SETTING);
    }
    
    private void initPanes()
    {
        mainPane = new JImagePane();
        JPanel buttonPane = new JPanel();
        buttonPane.setPreferredSize(new Dimension(-1, 78));
        buttonPane.setBorder(new EmptyBorder(0, 5, 0, 0));
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new LineLayout(0, 0, 0, 0, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        mainPane.setImageOnly(false);
        mainPane.setBackground(new Color(255, 255, 255, 230));
        mainPane.setLayout(cardLayout = new CardLayout());
        Object linkedObject;
        KeeperPane keeperPane;
        
        for(AbstractButton button: buttons)
        {
            if(button != null)
            {
                linkedObject = button.getClientProperty(KeeperConst.LINKED_COMPONENT);
                buttonPane.add(button, LineLayout.START);

                if(linkedObject != null && linkedObject instanceof KeeperPane)
                {
                    keeperPane = (KeeperPane)linkedObject;
                    mainPane.add(keeperPane, keeperPane.getName());
                    paneMap.put(keeperPane.getType(), keeperPane);
                }
            }
        }
        
        getContentPane().add(buttonPane, BorderLayout.NORTH);
        getContentPane().add(initStatusBar(), BorderLayout.SOUTH);
        getContentPane().add(mainPane, BorderLayout.CENTER);
    }
    
    private JComponent initStatusBar()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        User user = keeper.getCurrentUser();
        statusBar = new JImagePane();
        JCLabel lbDate = new JCLabel(format.format(new Date()));
        lbStatusInfo = new JCLabel("\u5F53\u524D\u7528\u6237\uFF1A" + user.getName() + "(" + user.getPurviewName() + ")");
        lbTotal = new JCLabel("\u8BB0\u5F55\u6761\u6570\uFF1A0");
        lbAmount = new JCLabel("\u603B\u91D1\u989D(\u5143)\uFF1A0");
        JCButton btnCancelAutoLogin, btnCancelSavedPassword = null;
        statusBar.setImageOnly(true);
        statusBar.setPreferredSize(new Dimension(-1, 21));
        statusBar.setBorder(new EmptyBorder(4, 5, 3, 5));
        statusBar.setLayout(new LineLayout(8, 0, 0, 0, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        lbAmount.setBorder(new EmptyBorder(0, 5, 0, 5));
        statusBar.add(lbTotal, LineLayout.END_FILL);
        statusBar.add(lbAmount, LineLayout.END_FILL);
        
        if(user.isSavePassword())
        {
            btnCancelSavedPassword = new JCButton("\u53D6\u6D88\u8BB0\u4F4F\u5BC6\u7801");
            btnCancelSavedPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCancelSavedPassword.setFocusable(false);
            btnCancelSavedPassword.setImage(null);
            btnCancelSavedPassword.setActionCommand(KeeperConst.CMD_CANCEL_SAVED_PASSWORD);
            btnCancelSavedPassword.addActionListener(mgr);
            statusBar.add(btnCancelSavedPassword, LineLayout.END_FILL);
        }
        
        if(user.isAutoLogin())
        {
            btnCancelAutoLogin = new JCButton("\u53D6\u6D88\u81EA\u52A8\u767B\u5F55");
            btnCancelAutoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCancelAutoLogin.setFocusable(false);
            btnCancelAutoLogin.setImage(null);
            btnCancelAutoLogin.setActionCommand(KeeperConst.CMD_CANCEL_AUTO_LOGIN);
            btnCancelAutoLogin.addActionListener(mgr);
            btnCancelSavedPassword.putClientProperty(KeeperConst.LINKED_COMPONENT, btnCancelAutoLogin);
            statusBar.add(btnCancelAutoLogin, LineLayout.END_FILL);
        }
        
        statusBar.add(lbDate, LineLayout.END_FILL);
        statusBar.add(lbStatusInfo, LineLayout.MIDDLE_FILL);
        return statusBar;
    }
    
    public void resetSkinPopupLocation()
    {
        skinMenu.setMenuLocation(Integer.parseInt(config.getProperty(KeeperConst.SKIN_POPUP_X, "-216")), skinMenu.getPreferredSize().height);
    }
    
    public void setTabForegroundDes(String colorDes)
    {
        Color color = KeeperUtil.stringToColor(colorDes);
        
        if(color != null)
        {
            setTabForeground(color);
        }
    }
    
    public String getTabForegroundDes()
    {
        return KeeperUtil.colorToString(getTabForeground());
    }
    
    public void setTabForeground(Color fg)
    {
        for(AbstractButton button: buttons)
        {
            if(button != null)
            {
                button.setForeground(fg);
            }
        }
    }
    
    public Color getTabForeground()
    {
        return btnPay.getForeground();
    }
    
    public void setStatusForegroundDes(String colorDes)
    {
        Color color = KeeperUtil.stringToColor(colorDes);
        
        if(color != null)
        {
            setStatusForeground(color);
        }
    }
    
    public String getStatusForegroundDes()
    {
        return KeeperUtil.colorToString(getStatusForeground());
    }
    
    public void setStatusForeground(Color fg)
    {
        statusBar.setForeground(fg);
        
        for(Component c: statusBar.getComponents())
        {
            c.setForeground(fg);
        }
    }
    
    public Color getStatusForeground()
    {
        return statusBar.getForeground();
    }
    
    public void setStatus(String status)
    {
        lbStatusInfo.setText(status);
    }
    
    public void resetTotal(int total, float amount)
    {
        lbTotal.setText("\u8BB0\u5F55\u6761\u6570\uFF1A" + total);
        lbAmount.setText("\u603B\u91D1\u989D(\u5143)\uFF1A" + KeeperConst.AMOUNT_FORMAT.format(amount));
        statusBar.doLayout();
    }
    
    public void showPane(String name)
    {
        cardLayout.show(mainPane, name);
    }
    
    public void updateCategory(boolean isPay)
    {
        for(KeeperPane pane: keeperPanes)
        {
            if(pane != null && pane.isPay() == isPay)
            {
                pane.updateCategory();
            }
        }
    }
    
    public KeeperPane getPaneByType(String type)
    {
        return paneMap.get(type);
    }
}