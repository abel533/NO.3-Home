package craky.keeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.componentc.JCDialog;
import craky.componentc.JCLabel;
import craky.componentc.JCMessageBox;
import craky.componentc.JCTextArea;
import craky.layout.LineLayout;
import craky.util.UIUtil;

public class AboutDialog extends JCDialog implements ActionListener
{
    private static final long serialVersionUID = 3367067393044735726L;
    
    private static final int LABEL_HEIGHT = 21;
    
    private KeeperApp keeper;
    
    private JComponent content;
    
    private JCButton btnOk;

    public AboutDialog(KeeperApp keeper)
    {
        super(keeper.getMainFrame(), "\u5173\u4E8E" + KeeperConst.APP_TITLE, ModalityType.DOCUMENT_MODAL);
        this.keeper = keeper;
        getContentPane().setPreferredSize(new Dimension(300, 180));
        initUI();
        setLocationRelativeTo(this.getOwner());
        setVisible(true);
    }
    
    private void initUI()
    {
        String bannerName = keeper.getBannerName();
        content = (JComponent)getContentPane();
        JImagePane bannerPane = new JImagePane();
        content.setPreferredSize(new Dimension(338, 225));
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(0, 2, 2, 2));
        bannerPane.setPreferredSize(new Dimension(-1, 64));
        bannerPane.setMode(JImagePane.TILED);

        if(bannerName != null)
        {
            Image image = new ImageIcon(KeeperConst.BANNER_DIR + KeeperConst.FILE_SEP + bannerName).getImage();
            bannerPane.setImage(image);
        }

        content.add(bannerPane, BorderLayout.NORTH);
        content.add(createMainPane(), BorderLayout.CENTER);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner());
        UIUtil.escAndEnterAction(this, btnOk, new AbstractAction()
        {
            private static final long serialVersionUID = -8727069883429572261L;

            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
    }
    
    private JImagePane createMainPane()
    {
        JImagePane mainPane = new JImagePane();
        JCLabel lbVersion = new JCLabel(KeeperConst.APP_NAME + ' ' + KeeperConst.APP_VERSION);
        JCLabel lbCopyright = new JCLabel("Copyright\u00A9 2010-2012 003. All Rights Reserved");
        JCTextArea txtInfo = new JCTextArea();
        mainPane.setBackground(new Color(255, 255, 255, 150));
        mainPane.setLayout(new LineLayout(0, 6, 0, 4, 6, LineLayout.TRAILING, LineLayout.TRAILING, LineLayout.VERTICAL));
        lbVersion.setHorizontalAlignment(JCLabel.LEFT);
        lbVersion.setVerticalAlignment(JCLabel.TOP);
        lbVersion.setPreferredSize(new Dimension(-1, LABEL_HEIGHT));
        lbVersion.setBorder(null);
        lbCopyright.setHorizontalAlignment(JCLabel.LEFT);
        lbCopyright.setVerticalAlignment(JCLabel.TOP);
        lbCopyright.setPreferredSize(lbVersion.getPreferredSize());
        lbCopyright.setBorder(null);
        lbCopyright.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtInfo.setBorder(null);
        txtInfo.clearBorderListener();
        txtInfo.setImageOnly(true);
        txtInfo.setEditable(false);
        txtInfo.setPopupMenuEnabled(false);
        txtInfo.setFocusable(false);
        txtInfo.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        txtInfo.setText("\u672C\u8F6F\u4EF6\u5B8C\u5168\u514D\u8D39\uFF0C\u7981\u6B62\u7528\u4E8E\u4EFB\u4F55\u5546\u4E1A" +
        		"\u7528\u9014\uFF0C\u4E2A\u4EBA\u7528\u6237\u57FA\u4E8E\u5B66\u4E60\n\u548C\u7814\u7A76\u76EE\u7684\u7684" +
        		"\u4F20\u64AD\u3001\u4FEE\u6539\u5C06\u4E0D\u53D7\u9650\u5236\u3002");
        mainPane.add(lbVersion, LineLayout.START_FILL);
        mainPane.add(createAuthorComponent(), LineLayout.START_FILL);
        mainPane.add(lbCopyright, LineLayout.START_FILL);
        mainPane.add(txtInfo, LineLayout.MIDDLE_FILL);
        mainPane.add(createButton(), LineLayout.END);
        return mainPane;
    }
    
    private JComponent createAuthorComponent()
    {
        EmptyComponent ecAuthor = new EmptyComponent();
        JCLabel lbAuthor = new JCLabel("\u4F5C\u8005\uFF1A");
        JCLabel lbMail = new JCLabel("003");
        ecAuthor.setPreferredSize(new Dimension(-1, LABEL_HEIGHT));
        ecAuthor.setLayout(new LineLayout(0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        lbAuthor.setHorizontalAlignment(JCLabel.LEFT);
        lbAuthor.setVerticalAlignment(JCLabel.TOP);
        lbAuthor.setBorder(null);
        lbMail.setHorizontalAlignment(JCLabel.LEFT);
        lbMail.setVerticalAlignment(JCLabel.TOP);
        lbMail.setBorder(null);
        lbMail.setForeground(new Color(22, 112, 235));
        lbMail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbMail.setToolTipText("xyzt_003@163.com");
        ecAuthor.add(lbAuthor, LineLayout.START_FILL);
        ecAuthor.add(lbMail, LineLayout.START_FILL);
        UIUtil.actionLabel(lbMail, new AbstractAction()
        {
            private static final long serialVersionUID = -8727069883429572261L;

            public void actionPerformed(ActionEvent e)
            {
                mailToAuthor(e);
            }
        });
        
        return ecAuthor;
    }
    
    private JComponent createButton()
    {
        EmptyComponent ecButton = new EmptyComponent();
        btnOk = new JCButton("\u786E\u5B9A(O)");
        ecButton.setPreferredSize(new Dimension(79, 21));
        ecButton.setLayout(new BorderLayout());
        btnOk.setPreferredSize(new Dimension(73, 21));
        btnOk.setMnemonic('O');
        btnOk.addActionListener(this);
        ecButton.add(btnOk, BorderLayout.WEST);
        return ecButton;
    }
    
    private void mailToAuthor(ActionEvent e)
    {
        String mailTo = ((JCLabel)e.getSource()).getToolTipText();
        
        try
        {
            Desktop.getDesktop().mail(new URI("mailto:" + mailTo));
        }
        catch(Exception ex)
        {
            String message = "\u6253\u5F00\u90AE\u4EF6\u5BA2\u6237\u7AEF\u9519\u8BEF\uFF0C\u8BF7\u767B\u5F55web" +
                             "\u90AE\u7BB1\u8054\u7CFB\u4F5C\u8005\uFF1A" + mailTo + "\uFF01";
            JCMessageBox.createErrorMessageBox(AboutDialog.this, AboutDialog.this.getTitle(), message).open();
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == btnOk)
        {
            this.dispose();
        }
    }
}