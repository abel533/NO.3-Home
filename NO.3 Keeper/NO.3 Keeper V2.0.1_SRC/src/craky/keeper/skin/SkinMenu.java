package craky.keeper.skin;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

import craky.component.JImagePane;
import craky.componentc.CRootPaneUI.ImageDisplayMode;
import craky.componentc.JCSlider;
import craky.componentc.JCToggleButton;
import craky.keeper.KeeperApp;
import craky.keeper.KeeperConst;
import craky.keeper.util.KeeperUtil;
import craky.layout.LineLayout;
import craky.util.Config;
import craky.util.UIUtil;

public class SkinMenu extends JMenu
{
    private static final long serialVersionUID = -2020810008867284350L;

    private static final Icon ICON = KeeperUtil.getIcon("skin_button_icon.png");
    
    private static final Image ROLLOVER_IMAGE = KeeperUtil.getImage("common_button_rollover_bg.png", true);
    
    private static final Image PRESSED_IMAGE = KeeperUtil.getImage("skin_button_pressed_bg.png", true);
    
    private KeeperApp keeper;
    
    private Config config;
    
    private SkinMgr skinMgr;
    
    private JImagePane mainPane, skinDispalyPane, skinListPane, modeDisplayPane;
    
    private DeleteConfirmPane confirmPane;
    
    private SkinUnit[] units;
    
    private JPopupMenu popup;
    
    private CardLayout mainCardLayout, skinDispalyCardLayout;
    
    private Rectangle skinListBounds;
    
    public SkinMenu(KeeperApp keeper)
    {
        super();
        setUI(new SkinMenuUI());
        this.keeper = keeper;
        this.config = keeper.getConfig();
        this.skinMgr = new SkinMgr(keeper, this);
        this.units = new SkinUnit[KeeperConst.MAX_SKIN_COUNT];
        setPreferredSize(new Dimension(20, 20));
        initUI();
    }
    
    private void initUI()
    {
        popup = getPopupMenu();
        mainPane = new JImagePane(KeeperUtil.getImage("skin_popup_bg.png"), JImagePane.CENTER);
        confirmPane = new DeleteConfirmPane(skinMgr);
        skinDispalyPane = new JImagePane(KeeperUtil.getImage("skin_list_bg.png"), JImagePane.CENTER);
        skinListPane = new JImagePane(KeeperUtil.getImage("skin_unit_bg.png"), JImagePane.CENTER);
        modeDisplayPane = new JImagePane();
        setToolTipText("\u66F4\u6539\u5916\u89C2");
        setOpaque(false);
        setFocusable(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setMargin(new Insets(0, 0, 0, 0));
        setDelay(200);
        setIconTextGap(0);
        setBorderPainted(false);
        setRolloverEnabled(true);
        confirmPane.setName("Dialog");
        mainPane.setName("Main");
        mainPane.setImageOnly(true);
        mainPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        mainPane.setLayout(new LineLayout(0, 0, 0, 0, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.VERTICAL));
        skinDispalyPane.setImageOnly(true);
        skinDispalyPane.setPreferredSize(new Dimension(236, 97));
        skinDispalyPane.setBorder(new EmptyBorder(2, 2, 2, 2));
        skinDispalyPane.setLayout(skinDispalyCardLayout = new CardLayout());
        skinListPane.setImageOnly(true);
        skinListPane.setName("SkinList");
        skinListPane.setBorder(new EmptyBorder(5, 8, 5, 8));
        skinListPane.setLayout(new GridLayout(2, 7, 1, 1));
        skinListPane.setFilledBorderArea(true);
        modeDisplayPane.setImageOnly(true);
        modeDisplayPane.setName("ModeDispaly");
        modeDisplayPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        popup.setOpaque(false);
        popup.setBorder(new EmptyBorder(0, 0, 0, 0));
        popup.setBorderPainted(false);
        popup.setFocusable(false);
        popup.setLayout(mainCardLayout = new CardLayout());
        popup.setPreferredSize(new Dimension(236, 152));
        popup.addPopupMenuListener(skinMgr);
        skinDispalyPane.add(skinListPane, skinListPane.getName());
        skinDispalyPane.add(modeDisplayPane, modeDisplayPane.getName());
        mainPane.add(skinDispalyPane, LineLayout.START);
        mainPane.add(createModePane(), LineLayout.START);
        mainPane.add(createAlphaPane(), LineLayout.MIDDLE_FILL);
        popup.add(mainPane, mainPane.getName());
        popup.add(confirmPane, confirmPane.getName());
        skinDispalyCardLayout.show(skinDispalyPane, skinListPane.getName());
        initSkinUnits();
        cacheSkinListBounds();
    }
    
    private void cacheSkinListBounds()
    {
        Dimension parentSize = skinDispalyPane.getPreferredSize();
        Insets parentInsets = skinDispalyPane.getInsets();
        Insets insets = skinListPane.getInsets();
        int width = parentSize.width - parentInsets.left - parentInsets.right - insets.left - insets.right;
        int height = parentSize.height - parentInsets.top - parentInsets.bottom - insets.top - insets.bottom;
        skinListBounds = new Rectangle(insets.left, insets.top, width, height);
    }
    
    private void initSkinUnits()
    {
        int length = units.length;
        
        for(int i = 0; i < length; i++)
        {
            units[i] = new SkinUnit(keeper, skinMgr);
            skinListPane.add(units[i]);
        }
        
        units[length - 1].setLast(true);
        resetSkins();
    }
    
    public void resetSkins()
    {
        Iterator<Skin> skins = keeper.getAllSkins().values().iterator();
        Skin skin;
        int index = 1;
        
        while(index < units.length)
        {
            skin = skins.hasNext()? skins.next(): null;
            
            if(skin != null && skin.isDefault())
            {
                continue;
            }
            
            units[index++].setSkin(skin);
        }
        
        units[0].setSkin(keeper.getSkin(KeeperConst.DEFAULT_SKIN_NAME));
    }
    
    private JPanel createModePane()
    {
        JPanel modePane = new JPanel(new GridLayout(1, 3));
        ButtonGroup group = new ButtonGroup();
        JCToggleButton btnFill = new JCToggleButton();
        JCToggleButton btnTiled = new JCToggleButton();
        JCToggleButton btnScaled = new JCToggleButton();
        Map<String, JCToggleButton> buttonMap = new HashMap<String, JCToggleButton>();
        modePane.setBorder(new EmptyBorder(0, 4, 1, 4));
        modePane.setPreferredSize(new Dimension(236, 40));
        modePane.setOpaque(false);
        btnFill.setFocusable(false);
        btnFill.setToolTipText("\u586B\u5145");
        btnFill.setImage(KeeperUtil.getImage("skin_fill_small.png", true));
        btnFill.setRolloverImage(KeeperUtil.getImage("skin_fill_rollover.png", true));
        btnFill.setSelectedImage(KeeperUtil.getImage("skin_fill_sel.png", true));
        btnFill.putClientProperty(KeeperConst.BIG_IMAGE_KEY, KeeperUtil.getImage("skin_fill_big.png", true));
        btnFill.putClientProperty(KeeperConst.EVENT_KEY, KeeperConst.SKIN_MODE_PREVIEW);
        btnFill.putClientProperty(KeeperConst.SKIN_MODE_VALUE, ImageDisplayMode.FILL);
        btnFill.addMouseListener(skinMgr);
        btnTiled.setFocusable(false);
        btnTiled.setToolTipText("\u5E73\u94FA");
        btnTiled.setImage(KeeperUtil.getImage("skin_tiled_small.png", true));
        btnTiled.setRolloverImage(KeeperUtil.getImage("skin_tiled_rollover.png", true));
        btnTiled.setSelectedImage(KeeperUtil.getImage("skin_tiled_sel.png", true));
        btnTiled.putClientProperty(KeeperConst.BIG_IMAGE_KEY, KeeperUtil.getImage("skin_tiled_big.png", true));
        btnTiled.putClientProperty(KeeperConst.EVENT_KEY, KeeperConst.SKIN_MODE_PREVIEW);
        btnTiled.putClientProperty(KeeperConst.SKIN_MODE_VALUE, ImageDisplayMode.TILED);
        btnTiled.addMouseListener(skinMgr);
        btnScaled.setFocusable(false);
        btnScaled.setToolTipText("\u62C9\u4F38");
        btnScaled.setImage(KeeperUtil.getImage("skin_scaled_small.png", true));
        btnScaled.setRolloverImage(KeeperUtil.getImage("skin_scaled_rollover.png", true));
        btnScaled.setSelectedImage(KeeperUtil.getImage("skin_scaled_sel.png", true));
        btnScaled.putClientProperty(KeeperConst.BIG_IMAGE_KEY, KeeperUtil.getImage("skin_scaled_big.png", true));
        btnScaled.putClientProperty(KeeperConst.EVENT_KEY, KeeperConst.SKIN_MODE_PREVIEW);
        btnScaled.putClientProperty(KeeperConst.SKIN_MODE_VALUE, ImageDisplayMode.SCALED);
        btnScaled.addMouseListener(skinMgr);
        buttonMap.put(ImageDisplayMode.FILL.toString(), btnFill);
        buttonMap.put(ImageDisplayMode.TILED.toString(), btnTiled);
        buttonMap.put(ImageDisplayMode.SCALED.toString(), btnScaled);
        buttonMap.get(config.getProperty(KeeperConst.SKIN_MODE, "FILL")).setSelected(true);
        btnFill.addActionListener(skinMgr);
        btnTiled.addActionListener(skinMgr);
        btnScaled.addActionListener(skinMgr);
        group.add(btnFill);
        group.add(btnTiled);
        group.add(btnScaled);
        modePane.add(btnFill);
        modePane.add(btnTiled);
        modePane.add(btnScaled);
        return modePane;
    }
    
    private JPanel createAlphaPane()
    {
        int bgAlphaValue = (int)(Float.valueOf(config.getProperty(KeeperConst.SKIN_ALPHA, "0.50")) * 100);
        int winAlphaValue = (int)(Float.valueOf(config.getProperty(KeeperConst.WINDOW_ALPHA, "1.0")) * 100);
        JPanel alphaPane = new JPanel(new GridLayout(1, 2));
        JCSlider bgAlphaSlider = new JCSlider(JCSlider.HORIZONTAL, 0, 100, bgAlphaValue);
        JCSlider winAlphaSlider = new JCSlider(JCSlider.HORIZONTAL, 20, 100, winAlphaValue);
        alphaPane.setBorder(new EmptyBorder(0, 4, 2, 3));
        alphaPane.setOpaque(false);
        bgAlphaSlider.setToolTipText("\u76AE\u80A4\u900F\u660E\u5EA6");
        bgAlphaSlider.setMiniMode(true);
        bgAlphaSlider.putClientProperty(KeeperConst.EVENT_KEY, KeeperConst.SKIN_ALPHA_CHANGED);
        bgAlphaSlider.addChangeListener(skinMgr);
        bgAlphaSlider.addMouseListener(skinMgr);
        winAlphaSlider.setToolTipText("\u7A97\u4F53\u900F\u660E\u5EA6");
        winAlphaSlider.setMiniMode(true);
        winAlphaSlider.putClientProperty(KeeperConst.EVENT_KEY, KeeperConst.WINDOW_ALPHA_CHANGED);
        winAlphaSlider.addChangeListener(skinMgr);
        winAlphaSlider.addMouseListener(skinMgr);
        alphaPane.add(bgAlphaSlider);
        
        if(UIUtil.isTranslucencySupported())
        {
            alphaPane.add(winAlphaSlider);
        }
        
        return alphaPane;
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
    
    public void modePreview(Image image, boolean hidden)
    {
        if(hidden)
        {
            skinDispalyCardLayout.show(skinDispalyPane, skinListPane.getName());
        }
        else
        {
            modeDisplayPane.setImage(image);
            skinDispalyCardLayout.show(skinDispalyPane, modeDisplayPane.getName());
        }
    }

    public Rectangle getSkinListBounds()
    {
        return skinListBounds;
    }
    
    public void showPane(boolean isMain, Skin skin)
    {
        if(!isMain)
        {
            keeper.changeSkin(keeper.getCurrentSkin(), false);
            confirmPane.updateBackground(skin, mainPane);
        }
        
        mainCardLayout.show(popup, isMain? mainPane.getName(): confirmPane.getName());
    }
    
    @Deprecated
    public void updateUI() {}
    
    private static class SkinMenuUI extends BasicMenuUI
    {
        public static ComponentUI createUI(JComponent c)
        {
            return new SkinMenuUI();
        }
        
        protected MouseInputListener createMouseInputListener(JComponent c)
        {
            return new MouseInputHandler();
        }
        
        protected class MouseInputHandler extends BasicMenuUI.MouseInputHandler
        {
            public void mouseEntered(MouseEvent evt)
            {
                ((JMenu)evt.getSource()).getModel().setRollover(true);
            }

            public void mouseExited(MouseEvent evt)
            {
                ((JMenu)evt.getSource()).getModel().setRollover(false);
            }
        }
    }
}