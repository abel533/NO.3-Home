package craky.keeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import craky.component.JImagePane;
import craky.componentc.GridBorder;
import craky.componentc.JCButton;
import craky.componentc.JCCheckBox;
import craky.componentc.JCComboBox;
import craky.componentc.JCLabel;
import craky.componentc.JCLimitedPassword;
import craky.keeper.sql.Restrictions;
import craky.keeper.user.User;
import craky.keeper.user.UserDAO;
import craky.keeper.user.UserInfoDialog;
import craky.keeper.util.KeeperUtil;
import craky.layout.LineLayout;
import craky.util.Config;
import craky.util.UIUtil;

public class LoginFrame extends JFrame
{
    private static final long serialVersionUID = -3144576417706510085L;
    
    private KeeperApp keeper;
    
    private Config loginConfig;
    
    private JComponent content;
    
    private JImagePane bannerPane;
    
    private JCButton btnOk, btnCancel;
    
    private JCComboBox comboUser;
    
    private JCLimitedPassword txtPsd;
    
    private JCCheckBox cbSavePsd, cbAuto;
    
    private JCLabel lbDelUserInfo, lbModifyPsd;
    
    private boolean godReleased;
    
    public LoginFrame(KeeperApp keeper)
    {
        super();
        this.keeper = keeper;
        this.loginConfig = keeper.getLoginConfig();
        initUI();
        initBanner();
        initHistory();
        setVisible(true);
    }
    
    private void initUI()
    {
        content = (JComponent)getContentPane();
        setTitle(KeeperConst.APP_TITLE);
        setIconImage(KeeperUtil.getImage("logo_16.png"));
        content.setPreferredSize(new Dimension(334, 218));
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(0, 0, 0, 0));
        content.setBackground(Color.WHITE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initContent();
        getRootPane().setDefaultButton(btnOk);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initContent()
    {
        ActionHandler actionListener = new ActionHandler();
        bannerPane = new JImagePane();
        JPanel centerPane = new JPanel();
        JImagePane bottomPane = new JImagePane();
        btnOk = new JCButton("\u786E\u5B9A");
        btnCancel = new JCButton("\u53D6\u6D88");
        bannerPane.setPreferredSize(new Dimension(334, 64));
        bannerPane.setMode(JImagePane.TILED);
        bottomPane.setPreferredSize(new Dimension(334, 30));
        bottomPane.setImage(KeeperUtil.getImage("login_bottom.png"));
        bottomPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        bottomPane.setLayout(new LineLayout(5, 0, 7, 5, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        centerPane.setBackground(new Color(228, 244, 255));
        centerPane.setBorder(new GridBorder(Color.WHITE, 0, 1, 0, 1));
        centerPane.setLayout(new LineLayout(0, 0, 0, 0, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.VERTICAL));
        btnOk.setPreferredSize(new Dimension(69, 21));
        btnCancel.setPreferredSize(btnOk.getPreferredSize());
        initCenterPane(centerPane);
        bottomPane.add(btnOk, LineLayout.END);
        bottomPane.add(btnCancel, LineLayout.END);
        content.add(bannerPane, BorderLayout.NORTH);
        content.add(bottomPane, BorderLayout.SOUTH);
        content.add(centerPane, BorderLayout.CENTER);
        btnOk.addActionListener(actionListener);
        btnCancel.addActionListener(actionListener);
    }
    
    private void initCenterPane(JPanel centerPane)
    {
        MouseHandler mouseListener = new MouseHandler();
        ItemHandler itemListener = new ItemHandler();
        EditHandler editListener = new EditHandler();
        JCLabel lbUser = new JCLabel("\u7528\u6237\u540D\uFF1A");
        JCLabel lbPsd = new JCLabel("\u5BC6\u3000\u7801\uFF1A");
        lbDelUserInfo = new JCLabel("\u5220\u9664\u8BB0\u5F55");
        lbModifyPsd = new JCLabel("\u4FEE\u6539\u5BC6\u7801");
        comboUser = new JCComboBox();
        txtPsd = new JCLimitedPassword(User.MAX_PASSWORD_LENGTH);
        cbSavePsd = new JCCheckBox("\u8BB0\u4F4F\u5BC6\u7801");
        cbAuto = new JCCheckBox("\u81EA\u52A8\u767B\u5F55");
        EmptyComponent ecUser = new EmptyComponent();
        EmptyComponent ecPsd = new EmptyComponent();
        EmptyComponent ecStatus = new EmptyComponent();
        
        ecUser.setLayout(new LineLayout(0, 0, 0, 14, 0, LineLayout.CENTER, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecUser.setPreferredSize(new Dimension(-1, 50));
        lbDelUserInfo.setBorder(new EmptyBorder(1, 3, 0, 0));
        lbDelUserInfo.setPreferredSize(new Dimension(75, 24));
        lbDelUserInfo.setForeground(new Color(22, 112, 235));
        lbDelUserInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbUser.setBorder(new EmptyBorder(1, 0, 0, 0));
        lbUser.setPreferredSize(new Dimension(74, 24));
        lbUser.setHorizontalAlignment(JCLabel.RIGHT);
        comboUser.setPreferredSize(new Dimension(-1, 26));
        comboUser.setEditable(true);
        lbModifyPsd.setBorder(new EmptyBorder(1, 3, 0, 0));
        lbModifyPsd.setPreferredSize(lbDelUserInfo.getPreferredSize());
        lbModifyPsd.setForeground(lbDelUserInfo.getForeground());
        lbModifyPsd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ecPsd.setLayout(new LineLayout(0, 0, 0, 0, 0, LineLayout.CENTER, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecPsd.setPreferredSize(new Dimension(-1, 30));
        txtPsd.setPreferredSize(comboUser.getPreferredSize());
        lbPsd.setBorder(new EmptyBorder(1, 0, 0, 0));
        lbPsd.setPreferredSize(new Dimension(74, 24));
        lbPsd.setHorizontalAlignment(JCLabel.RIGHT);
        ecStatus.setLayout(new LineLayout(12, 105, 0, 0, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecStatus.setPreferredSize(new Dimension(-1, 27));
        
        ecUser.add(lbUser, LineLayout.START);
        ecUser.add(comboUser, LineLayout.MIDDLE);
        ecUser.add(lbDelUserInfo, LineLayout.END);
        ecPsd.add(lbPsd, LineLayout.START);
        ecPsd.add(txtPsd, LineLayout.MIDDLE);
        ecPsd.add(lbModifyPsd, LineLayout.END);
        ecStatus.add(cbSavePsd, LineLayout.START);
        ecStatus.add(cbAuto, LineLayout.START);
        centerPane.add(ecUser, LineLayout.START_FILL);
        centerPane.add(ecPsd, LineLayout.START_FILL);
        centerPane.add(ecStatus, LineLayout.END_FILL);
        bannerPane.addMouseListener(mouseListener);
        cbSavePsd.addItemListener(itemListener);
        cbAuto.addItemListener(itemListener);
        ((JTextComponent)comboUser.getEditor().getEditorComponent()).getDocument().addDocumentListener(editListener);
        
        UIUtil.actionLabel(lbDelUserInfo, new AbstractAction()
        {
            private static final long serialVersionUID = 1314650981076868723L;

            public void actionPerformed(ActionEvent e)
            {
                deleteUserInfo();
            }
        });
        
        UIUtil.actionLabel(lbModifyPsd, new AbstractAction()
        {
            private static final long serialVersionUID = -8098585019722294097L;

            public void actionPerformed(ActionEvent e)
            {
                modifyPassword();
            }
        });
    }
    
    private void initBanner()
    {
        String bannerName = keeper.getBannerName();
        
        if(bannerName != null)
        {
            Image image = new ImageIcon(KeeperConst.BANNER_DIR + KeeperConst.FILE_SEP + bannerName).getImage();
            bannerPane.setImage(image);
        }
    }
    
    private void initHistory()
    {
        List<String> history = keeper.getLoginHistory();
        
        if(!history.isEmpty())
        {
            for(String name: keeper.getLoginHistory())
            {
                comboUser.addItem(name);
            }
            
            comboUser.setSelectedIndex(0);
            requestPassword(false);
        }
        else
        {
            requestUser();
        }
    }
    
    public void requestUser()
    {
        comboUser.requestFocus();
    }
    
    public void requestPassword(boolean clear)
    {
        if(clear)
        {
            clearPassword();
        }
        
        txtPsd.requestFocus();
    }
    
    public void clearPassword()
    {
        txtPsd.setText("");
    }
    
    public boolean isSavePassword()
    {
        return cbSavePsd.isSelected();
    }
    
    private void setSavePassword(boolean save)
    {
        cbSavePsd.setSelected(save);
    }
    
    public boolean isAutoLogin()
    {
        return cbAuto.isSelected();
    }
    
    private void setAutoLogin(boolean auto)
    {
        cbAuto.setSelected(auto);
    }
    
    /**
     * 获取当前输入的用户名
     * 由于在某些事件中comboUser.getSelectedItem()返回null或历史值，故此处统一取其Editor中的值
     * @return 用户名
     */
    private String getSelectedUserName()
    {
        return ((JTextComponent)comboUser.getEditor().getEditorComponent()).getText();
    }
    
    private void deleteUserInfo()
    {
        String name = getSelectedUserName();
        keeper.removeHistoryUser(name);
        comboUser.removeItem(name);
        
        if(comboUser.getItemCount() > 0)
        {
            comboUser.setSelectedIndex(0);
        }
        else
        {
            ((JTextComponent)comboUser.getEditor().getEditorComponent()).setText(null);
        }
    }
    
    private void modifyPassword()
    {
        String name = getSelectedUserName();
        String failInfo = null;
        
        if(!User.isAllowedName(name))
        {
            failInfo = User.NAME_UNALLOWED;
        }
        else if(UserDAO.getUsers(Restrictions.eq("name", name)).isEmpty())
        {
            failInfo = User.USER_NOT_EXIST;
        }
        
        if(failInfo != null)
        {
            keeper.showMessageBeforeLogin("\u4FEE\u6539\u5BC6\u7801", failInfo, true);
            requestUser();
        }
        else
        {
            new UserInfoDialog(keeper, this, name, true);
        }
    }
    
    public boolean isGodReleased()
    {
        return godReleased;
    }
    
    private class MouseHandler extends MouseAdapter
    {
        public void mouseReleased(MouseEvent e)
        {
            godReleased = e.getSource() == bannerPane && e.isAltDown() && e.getClickCount() == 3 && SwingUtilities.isRightMouseButton(e);
        }
    }
    
    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            
            if(source == btnOk)
            {
                keeper.login(getSelectedUserName(), new String(txtPsd.getPassword()));
            }
            else if(source == btnCancel)
            {
                System.exit(0);
            }
        }
    }
    
    private class ItemHandler implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            Object source = e.getSource();
            int state = e.getStateChange();
            String savedPassword = null;
            
            if(source == cbSavePsd)
            {
                String name = getSelectedUserName();
                
                if(state == ItemEvent.DESELECTED)
                {
                    if(cbAuto.isSelected())
                    {
                        cbAuto.setSelected(false);
                    }
                }
                else if((savedPassword = loginConfig.getProperty(name + KeeperConst.PASSWORD_KEY)) != null)
                {
                    txtPsd.setText(keeper.getPasswordTextWhenSaved(savedPassword));
                }
            }
            else if(source == cbAuto && state == ItemEvent.SELECTED && !cbSavePsd.isSelected())
            {
                cbSavePsd.setSelected(true);
            }
        }
    }
    
    private class EditHandler implements DocumentListener
    {
        public void insertUpdate(DocumentEvent e)
        {
            checkState(e);
        }

        public void removeUpdate(DocumentEvent e)
        {
            checkState(e);
        }

        public void changedUpdate(DocumentEvent e)
        {
            checkState(e);
        }
        
        private void checkState(DocumentEvent e)
        {
            String name = getSelectedUserName();
            boolean savePassword = loginConfig.getProperty(name + KeeperConst.PASSWORD_KEY) != null;
            
            if(keeper.getLoginHistory().contains(name))
            {
                setAutoLogin(Boolean.parseBoolean(loginConfig.getProperty(name + KeeperConst.AUTO_LOGIN_KEY, "false")));
                setSavePassword(savePassword);
            }
            else
            {
                setSavePassword(false);
                setAutoLogin(false);
            }
            
            if(!isSavePassword())
            {
                clearPassword();
            }
        }
    }
}