package craky.keeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.componentc.JCDialog;
import craky.componentc.JCLabel;
import craky.componentc.JCRadioButton;
import craky.keeper.util.KeeperUtil;
import craky.layout.LineLayout;
import craky.util.Config;
import craky.util.UIUtil;

public class SettingDialog extends JCDialog implements ActionListener, PopupMenuListener, ItemListener
{
    private static final long serialVersionUID = 3367067393044735726L;
    
    private static final int SKIN_POPUP_LEFT_X = -216;
    
    private static final int SKIN_POPUP_RIGHT_X = -130;
    
    private MainFrame mainFrame;
    
    private Config config;
    
    private JComponent content;
    
    private JCButton btnOk, btnCancel, btnApply;
    
    private JCLabel lbTabsFG, lbStatusFG;
    
    private JCRadioButton rbSkinMenuLeft, rbSkinMenuRight;
    
    private ColorChooserPopup colorPopup;
    
    private JCLabel currentColorLabel;
    
    private Color tabsFG, statusFG;
    
    private boolean changed;

    public SettingDialog(KeeperApp keeper)
    {
        super(keeper.getMainFrame(), "\u8BBE\u7F6E", ModalityType.DOCUMENT_MODAL);
        this.config = keeper.getConfig();
        this.mainFrame = keeper.getMainFrame();
        this.initUI();
        this.load();
        this.setVisible(true);
    }
    
    private void initUI()
    {
        ShowColorPopupListener colorListener = new ShowColorPopupListener();
        content = (JComponent)this.getContentPane();
        JImagePane mainPane = new JImagePane();
        JImagePane buttonPane = new JImagePane();
        EmptyComponent ecColor = new EmptyComponent();
        JCLabel lbTabsFGTitle = new JCLabel("\u5DE5\u5177\u680F\u5B57\u4F53\u989C\u8272\uFF1A");
        JCLabel lbStatusFGTitle = new JCLabel("\u72B6\u6001\u680F\u5B57\u4F53\u989C\u8272\uFF1A");
        lbTabsFG = new JCLabel();
        lbStatusFG = new JCLabel();
        btnOk = new JCButton("\u786E\u5B9A");
        btnCancel = new JCButton("\u53D6\u6D88");
        btnApply = new JCButton("\u5E94\u7528");
        colorPopup = new ColorChooserPopup(null, new AbstractAction()
        {
            private static final long serialVersionUID = 8201411013412150507L;

            public void actionPerformed(ActionEvent e)
            {
                changePurview(false);
            }
        });
        
        content.setBorder(new EmptyBorder(0, 2, 2, 2));
        content.setPreferredSize(new Dimension(512, 333));
        content.setLayout(new BorderLayout());
        mainPane.setLayout(new LineLayout(10, 15, 15, 15, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.VERTICAL));
        mainPane.setBackground(new Color(255, 255, 255, 210));
        buttonPane.setLayout(new LineLayout(5, 7, 7, 7, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        buttonPane.setBackground(new Color(255, 255, 255, 150));
        buttonPane.setPreferredSize(new Dimension(-1, 32));
        ecColor.setLayout(new LineLayout(0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecColor.setPreferredSize(new Dimension(-1, 18));
        lbStatusFGTitle.setPreferredSize(new Dimension(135, -1));
        lbStatusFGTitle.setHorizontalAlignment(JCLabel.RIGHT);
        lbStatusFGTitle.setVerticalAlignment(JCLabel.BOTTOM);
        lbTabsFGTitle.setVerticalAlignment(JCLabel.BOTTOM);
        lbTabsFG.setBorder(new LineBorder(Color.BLACK));
        lbTabsFG.setBackgroundAlpha(1.0f);
        lbTabsFG.setPreferredSize(new Dimension(100, -1));
        lbTabsFG.addMouseListener(colorListener);
        lbStatusFG.setBorder(new LineBorder(Color.BLACK));
        lbStatusFG.setBackgroundAlpha(1.0f);
        lbStatusFG.setPreferredSize(lbTabsFG.getPreferredSize());
        lbStatusFG.addMouseListener(colorListener);
        btnOk.setPreferredSize(new Dimension(69, 21));
        btnOk.addActionListener(this);
        btnCancel.setPreferredSize(btnOk.getPreferredSize());
        btnCancel.addActionListener(this);
        btnApply.setPreferredSize(btnOk.getPreferredSize());
        btnApply.addActionListener(this);
        colorPopup.addPopupMenuListener(this);
        
        ecColor.add(lbTabsFGTitle, LineLayout.START_FILL);
        ecColor.add(lbTabsFG, LineLayout.START_FILL);
        ecColor.add(lbStatusFGTitle, LineLayout.START_FILL);
        ecColor.add(lbStatusFG, LineLayout.START_FILL);
        mainPane.add(ecColor, LineLayout.START_FILL);
        mainPane.add(createSkinMenuPurview(), LineLayout.START_FILL);
        buttonPane.add(btnOk, LineLayout.END);
        buttonPane.add(btnCancel, LineLayout.END);
        buttonPane.add(btnApply, LineLayout.END);
        content.add(buttonPane, BorderLayout.SOUTH);
        content.add(mainPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner());
        UIUtil.escAndEnterAction(this, btnOk, new AbstractAction()
        {
            private static final long serialVersionUID = -933736543655670099L;

            public void actionPerformed(ActionEvent e)
            {
                cancel();
            }
        });
    }
    
    private JComponent createSkinMenuPurview()
    {
        ButtonGroup group = new ButtonGroup();
        EmptyComponent ec = new EmptyComponent();
        EmptyComponent ecButton = new EmptyComponent();
        EmptyComponent ecImage = new EmptyComponent();
        JCLabel lbPos = new JCLabel("\u76AE\u80A4\u83DC\u5355\u663E\u793A\u4F4D\u7F6E\uFF1A");
        rbSkinMenuLeft = new JCRadioButton("\u6309\u94AE\u53F3\u5BF9\u9F50");
        rbSkinMenuRight = new JCRadioButton("\u7A97\u53E3\u53F3\u5BF9\u9F50");
        JImagePane leftPurview = new JImagePane(KeeperUtil.getImage("skin_popup_left.png"), JImagePane.TILED);
        JImagePane rightPurview = new JImagePane(KeeperUtil.getImage("skin_popup_right.png"), JImagePane.TILED);
        ec.setPreferredSize(new Dimension(-1, 250));
        ec.setLayout(new LineLayout(5, 0, 0, 0, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.VERTICAL));
        ecButton.setPreferredSize(new Dimension(480, 18));
        ecButton.setLayout(new GridLayout(1, 2, 8, 0));
        ecImage.setPreferredSize(new Dimension(480, 172));
        ecImage.setLayout(new GridLayout(1, 2, 8, 0));
        lbPos.setPreferredSize(new Dimension(-1, 18));
        lbPos.setVerticalAlignment(JCLabel.BOTTOM);
        rbSkinMenuLeft.addItemListener(this);
        rbSkinMenuRight.addItemListener(this);
        group.add(rbSkinMenuLeft);
        group.add(rbSkinMenuRight);
        ecImage.add(leftPurview);
        ecImage.add(rightPurview);
        ecButton.add(rbSkinMenuLeft);
        ecButton.add(rbSkinMenuRight);
        ec.add(lbPos, LineLayout.START_FILL);
        ec.add(ecButton, LineLayout.START);
        ec.add(ecImage, LineLayout.START);
        return ec;
    }
    
    private void load()
    {
        tabsFG = KeeperUtil.stringToColor(config.getProperty(KeeperConst.TABS_FOREGROUND, "225,242,250"));
        statusFG = KeeperUtil.stringToColor(config.getProperty(KeeperConst.STATUS_FOREGROUND, "225,242,250"));
        int skinPopupX = Integer.parseInt(config.getProperty(KeeperConst.SKIN_POPUP_X, "0"));
        
        if(skinPopupX == SKIN_POPUP_LEFT_X)
        {
            rbSkinMenuLeft.setSelected(true);
        }
        else if(skinPopupX == SKIN_POPUP_RIGHT_X)
        {
            rbSkinMenuRight.setSelected(true);
        }
        
        lbTabsFG.setBackground(tabsFG);
        lbStatusFG.setBackground(statusFG);
        btnApply.setEnabled(changed = false);
    }
    
    private void changePurview(boolean submit)
    {
        Color color = null;
        
        if(submit)
        {
            btnApply.setEnabled(changed = changed || !colorPopup.isCanceled());
            color = colorPopup.getColor();
        }
        else
        {
            color = colorPopup.getCurrentColor();
        }
        
        currentColorLabel.setBackground(color);
        
        if(currentColorLabel == lbTabsFG)
        {
            mainFrame.setTabForeground(color);
        }
        else if(currentColorLabel == lbStatusFG)
        {
            mainFrame.setStatusForeground(color);
        }
    }
    
    private void submit()
    {
        if(!changed)
        {
            return;
        }
        
        Integer skinPopupX = null;
        
        if(rbSkinMenuLeft.isSelected())
        {
            skinPopupX = SKIN_POPUP_LEFT_X;
        }
        else if(rbSkinMenuRight.isSelected())
        {
            skinPopupX = SKIN_POPUP_RIGHT_X;
        }
        
        if(skinPopupX != null)
        {
            config.setPropertie(KeeperConst.SKIN_POPUP_X, skinPopupX.toString());
            mainFrame.resetSkinPopupLocation();
        }
        
        config.setPropertie(KeeperConst.TABS_FOREGROUND, KeeperUtil.colorToString(tabsFG = lbTabsFG.getBackground()));
        config.setPropertie(KeeperConst.STATUS_FOREGROUND, KeeperUtil.colorToString(statusFG = lbStatusFG.getBackground()));
        config.saveConfig();
        btnApply.setEnabled(changed = false);
    }
    
    private void cancel()
    {
        if(changed)
        {
            mainFrame.setTabForeground(tabsFG);
            mainFrame.setStatusForeground(statusFG);
        }
        
        dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        
        if(source == btnOk)
        {
            submit();
            dispose();
        }
        else if(source == btnApply)
        {
            submit();
        }
        else if(source == btnCancel)
        {
            cancel();
        }
    }
    
    public void itemStateChanged(ItemEvent e)
    {
        btnApply.setEnabled(changed = changed || (e.getStateChange() == ItemEvent.SELECTED));
    }
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
    {
        changePurview(true);
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
    
    public void popupMenuCanceled(PopupMenuEvent e) {}
    
    private class ShowColorPopupListener extends MouseAdapter
    {
        public void mouseReleased(MouseEvent e)
        {
            if(SwingUtilities.isLeftMouseButton(e))
            {
                currentColorLabel = (JCLabel)e.getSource();
                colorPopup.setColor(currentColorLabel.getBackground(), true);

                if(currentColorLabel == lbTabsFG)
                {
                    colorPopup.show(currentColorLabel, -5, currentColorLabel.getHeight());
                }
                else if(currentColorLabel == lbStatusFG)
                {
                    colorPopup.show(currentColorLabel, currentColorLabel.getWidth() -
                                    colorPopup.getPreferredSize().width + 5, currentColorLabel.getHeight());
                }
            }
        }
    }
}