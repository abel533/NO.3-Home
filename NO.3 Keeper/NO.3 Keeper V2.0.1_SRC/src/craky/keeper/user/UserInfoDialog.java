package craky.keeper.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.componentc.JCComboBox;
import craky.componentc.JCDialog;
import craky.componentc.JCLabel;
import craky.componentc.JCLimitedPassword;
import craky.componentc.JCMessageBox;
import craky.componentc.JCTextField;
import craky.keeper.EmptyComponent;
import craky.keeper.KeeperApp;
import craky.keeper.KeeperConst;
import craky.keeper.LoginFrame;
import craky.keeper.sql.Restrictions;
import craky.keeper.util.KeeperUtil;
import craky.layout.LineLayout;
import craky.util.UIUtil;

public class UserInfoDialog extends JCDialog implements ActionListener
{
    private static final long serialVersionUID = 4787072148793183017L;

    private JComponent content;
    
    private JCButton btnOk, btnCancel;
    
    private JCTextField txtUser;
    
    private JCComboBox cbPurview;
    
    private JCLimitedPassword txtOld, txtNew, txtCheck;
    
    private String userName;
    
    private User user;
    
    private boolean needOldPassword;
    
    private boolean isAdd;
    
    public UserInfoDialog(KeeperApp keeper, Window owner, String userName, boolean needOldPassword)
    {
        super(owner, userName == null? "\u6DFB\u52A0\u7528\u6237": "\u4FEE\u6539\u5BC6\u7801", ModalityType.DOCUMENT_MODAL);
        this.userName = userName;
        this.needOldPassword = needOldPassword;
        this.isAdd = userName == null;
        this.initUI();
        String bannerName = null;
        
        if(owner == null)
        {
            setIconImage(KeeperUtil.getImage("logo_16.png"));
        }
        
        if((owner == null || owner instanceof LoginFrame) && (bannerName = keeper.getBannerName()) != null)
        {
            this.setBackgroundImage(new ImageIcon(KeeperConst.BANNER_BG_DIR + KeeperConst.FILE_SEP + bannerName).getImage());
        }
        
        this.setVisible(true);
    }
    
    private void initUI()
    {
        content = (JComponent)this.getContentPane();
        JImagePane mainPane = new JImagePane();
        EmptyComponent ecUser = new EmptyComponent();
        EmptyComponent ecOldPsd = new EmptyComponent();
        EmptyComponent ecNewPsd = new EmptyComponent();
        EmptyComponent ecCheckPsd = new EmptyComponent();
        EmptyComponent ecPurview = new EmptyComponent();
        JImagePane buttonPane = new JImagePane();
        JCLabel lbAddAdmin = new JCLabel("\u9996\u6B21\u4F7F\u7528\uFF0C\u8BF7\u5148\u6CE8" +
                                         "\u518C\u8D85\u7EA7\u7BA1\u7406\u5458\u8D26\u53F7\u3002");
        JCLabel lbUser = new JCLabel("\u7528\u6237\u540D\uFF1A");
        JCLabel lbOld = new JCLabel("\u65E7\u5BC6\u7801\uFF1A");
        JCLabel lbNew = new JCLabel(isAdd? "\u5BC6\u7801\uFF1A": "\u65B0\u5BC6\u7801\uFF1A");
        JCLabel lbCheck = new JCLabel("\u5BC6\u7801\u786E\u8BA4\uFF1A");
        JCLabel lbPurview = new JCLabel("\u6743\u9650\uFF1A");
        txtUser = new JCTextField(userName);
        txtOld = new JCLimitedPassword(User.MAX_PASSWORD_LENGTH);
        txtNew = new JCLimitedPassword(User.MAX_PASSWORD_LENGTH);
        txtCheck = new JCLimitedPassword(User.MAX_PASSWORD_LENGTH);
        cbPurview = new JCComboBox(new String[]{User.USER_DES, User.VISITOR_DES});
        btnOk = new JCButton("\u786E\u5B9A");
        btnCancel = new JCButton("\u53D6\u6D88");
        
        content.setBorder(new EmptyBorder(0, 2, 2, 2));
        content.setPreferredSize(new Dimension(310, (!isAdd && !needOldPassword)? 175: 205));
        content.setLayout(new BorderLayout());
        mainPane.setLayout(new LineLayout(7, 15, 15, 15, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.VERTICAL));
        mainPane.setBackground(new Color(255, 255, 255, 210));
        ecUser.setLayout(new BorderLayout());
        ecUser.setPreferredSize(new Dimension(-1, 23));
        ecOldPsd.setLayout(new BorderLayout());
        ecOldPsd.setPreferredSize(ecUser.getPreferredSize());
        ecOldPsd.setVisible(!isAdd && needOldPassword);
        ecNewPsd.setLayout(new BorderLayout());
        ecNewPsd.setPreferredSize(ecUser.getPreferredSize());
        ecCheckPsd.setLayout(new BorderLayout());
        ecCheckPsd.setPreferredSize(ecUser.getPreferredSize());
        ecPurview.setLayout(new BorderLayout());
        ecPurview.setPreferredSize(ecUser.getPreferredSize());
        ecPurview.setVisible(isAdd && this.getOwner() != null);
        lbAddAdmin.setForeground(new Color(255, 40, 110));
        lbAddAdmin.setPreferredSize(ecUser.getPreferredSize());
        lbAddAdmin.setVisible(isAdd && this.getOwner() == null);
        buttonPane.setLayout(new LineLayout(5, 7, 7, 7, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        buttonPane.setBackground(new Color(255, 255, 255, 150));
        buttonPane.setPreferredSize(new Dimension(-1, 32));
        txtUser.setEditable(isAdd);
        lbUser.setPreferredSize(new Dimension(60, -1));
        lbOld.setPreferredSize(lbUser.getPreferredSize());
        lbNew.setPreferredSize(lbUser.getPreferredSize());
        lbCheck.setPreferredSize(lbUser.getPreferredSize());
        lbPurview.setPreferredSize(lbUser.getPreferredSize());
        btnOk.setPreferredSize(new Dimension(69, 21));
        btnOk.addActionListener(this);
        btnCancel.setPreferredSize(btnOk.getPreferredSize());
        btnCancel.addActionListener(this);
        
        ecUser.add(lbUser, BorderLayout.WEST);
        ecUser.add(txtUser, BorderLayout.CENTER);
        ecOldPsd.add(lbOld, BorderLayout.WEST);
        ecOldPsd.add(txtOld, BorderLayout.CENTER);
        ecNewPsd.add(lbNew, BorderLayout.WEST);
        ecNewPsd.add(txtNew, BorderLayout.CENTER);
        ecCheckPsd.add(lbCheck, BorderLayout.WEST);
        ecCheckPsd.add(txtCheck, BorderLayout.CENTER);
        ecPurview.add(lbPurview, BorderLayout.WEST);
        ecPurview.add(cbPurview, BorderLayout.CENTER);
        buttonPane.add(btnOk, LineLayout.END);
        buttonPane.add(btnCancel, LineLayout.END);
        mainPane.add(lbAddAdmin, LineLayout.START_FILL);
        mainPane.add(ecUser, LineLayout.START_FILL);
        mainPane.add(ecOldPsd, LineLayout.START_FILL);
        mainPane.add(ecNewPsd, LineLayout.START_FILL);
        mainPane.add(ecCheckPsd, LineLayout.START_FILL);
        mainPane.add(ecPurview, LineLayout.START_FILL);
        content.add(buttonPane, BorderLayout.SOUTH);
        content.add(mainPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner());
        UIUtil.escAndEnterAction(this, btnOk, new AbstractAction()
        {
            private static final long serialVersionUID = 7092223290373790975L;

            public void actionPerformed(ActionEvent e)
            {
                cancel();
            }
        });
        
        if(isAdd)
        {
            txtUser.requestFocus();
        }
        else if(needOldPassword)
        {
            txtOld.requestFocus();
        }
        else
        {
            txtNew.requestFocus();
        }
        
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                cancel();
            }
        });
    }
    
    private void submit()
    {
        userName = isAdd? txtUser.getText(): userName;
        String oldPsd = needOldPassword? new String(txtOld.getPassword()): null;
        String newPsd = new String(txtNew.getPassword());
        String checkPsd = new String(txtCheck.getPassword());
        String retInfo = null;
        String newCiphertext = null;
        
        if(isAdd && !User.isAllowedName(userName))
        {
            txtUser.requestFocus();
            retInfo = User.NAME_REGEX_INFO;
        }
        else if(!isAdd && needOldPassword && !User.isAllowedPassword(oldPsd))
        {
            txtOld.requestFocus();
            retInfo = User.PASSWORD_LENGTH_ERROR;
        }
        else if(!User.isAllowedPassword(newPsd))
        {
            txtNew.requestFocus();
            retInfo = User.PASSWORD_LENGTH_ERROR;
        }
        else if(!User.isAllowedPassword(checkPsd))
        {
            txtCheck.requestFocus();
            retInfo = User.PASSWORD_LENGTH_ERROR;
        }
        
        if(retInfo == null)
        {
            boolean checkOk = newPsd.equals(checkPsd);
            
            if(checkOk)
            {
                newCiphertext = User.createCiphertext(userName, newPsd);
                List<User> users = UserDAO.getUsers(Restrictions.eq("name", userName));
                
                if(users.isEmpty())
                {
                    retInfo = isAdd? null: User.USER_NOT_EXIST;
                }
                else
                {
                    if(isAdd)
                    {
                        txtUser.requestFocus();
                        retInfo = User.USER_ALREADY_EXIST;
                    }
                    else
                    {
                        user = users.get(0);
                        
                        if(needOldPassword && !User.createCiphertext(userName, oldPsd).equals(user.getPassword()))
                        {
                            txtOld.requestFocus();
                            txtOld.setText(null);
                            retInfo = "\u65E7\u5BC6\u7801\u9519\u8BEF\uFF01";
                        }
                    }
                }
            }
            else
            {
                txtCheck.requestFocus();
                retInfo = "\u4E24\u6B21\u5BC6\u7801\u8F93\u5165\u4E0D\u4E00\u81F4\uFF01";
            }
        }
        
        if(retInfo == null)
        {
            if(isAdd)
            {
                String purview = String.valueOf(cbPurview.getSelectedItem());
                user = new User();
                user.setName(userName);
                user.setPassword(newCiphertext);
                user.setPurview(this.getOwner() == null? User.ADMIN: User.getPurviewByName(purview));
                UserDAO.addUser(user);
                retInfo = "\u7528\u6237\u6DFB\u52A0\u6210\u529F\uFF01";
            }
            else
            {
                user.setPassword(newCiphertext);
                UserDAO.updateUser(user);
                retInfo = "\u5BC6\u7801\u4FEE\u6539\u6210\u529F\uFF0C\u8BF7\u7262\u8BB0\u65B0\u5BC6\u7801\uFF01";
            }
            
            JCMessageBox.createInformationMessageBox(this, this.getTitle(), retInfo).open();
            this.dispose();
        }
        else
        {
            JCMessageBox.createErrorMessageBox(this, this.getTitle(), retInfo).open();
        }
    }
    
    public User getUser()
    {
        return user;
    }
    
    private void cancel()
    {
        user = null;
        this.dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        
        if(source == btnOk)
        {
            submit();
        }
        else if(source == btnCancel)
        {
            cancel();
        }
    }
}