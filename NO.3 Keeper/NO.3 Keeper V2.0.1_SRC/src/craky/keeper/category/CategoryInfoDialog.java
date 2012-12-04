package craky.keeper.category;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.componentc.JCDialog;
import craky.componentc.JCLabel;
import craky.componentc.JCMessageBox;
import craky.componentc.JCStringField;
import craky.keeper.EmptyComponent;
import craky.keeper.KeeperApp;
import craky.keeper.sql.Restrictions;
import craky.layout.LineLayout;
import craky.util.UIUtil;

public class CategoryInfoDialog extends JCDialog implements ActionListener
{
    private static final long serialVersionUID = -538668122701322794L;
    
    private JComponent content;
    
    private JCButton btnOk, btnCancel;
    
    private JCStringField txtName;
    
    private Category category;
    
    private boolean isAdd;
    
    private boolean isPay;

    public CategoryInfoDialog(KeeperApp keeper, Window owner, Category category, boolean isPay)
    {
        super(owner, (category == null? "\u6DFB\u52A0": "\u4FEE\u6539") + (isPay? "\u6D88\u8D39\u7C7B\u522B": "\u6536\u5165\u7C7B\u522B"),
                        ModalityType.DOCUMENT_MODAL);
        this.category = category;
        this.isAdd = category == null;
        this.isPay = isPay;
        this.initUI();
        this.setVisible(true);
    }
    
    private void initUI()
    {
        content = (JComponent)this.getContentPane();
        JImagePane mainPane = new JImagePane();
        EmptyComponent ecName = new EmptyComponent();
        JImagePane buttonPane = new JImagePane();
        JCLabel lbName = new JCLabel("\u540D\u79F0\uFF1A");
        txtName = new JCStringField(25);
        btnOk = new JCButton("\u786E\u5B9A");
        btnCancel = new JCButton("\u53D6\u6D88");
        
        txtName.setText(isAdd? null: category.getName());
        content.setBorder(new EmptyBorder(0, 2, 2, 2));
        content.setPreferredSize(new Dimension(230, 120));
        content.setLayout(new BorderLayout());
        mainPane.setLayout(new LineLayout(7, 15, 15, 15, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.VERTICAL));
        mainPane.setBackground(new Color(255, 255, 255, 210));
        ecName.setLayout(new BorderLayout());
        ecName.setPreferredSize(new Dimension(-1, 23));
        buttonPane.setLayout(new LineLayout(5, 7, 7, 7, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        buttonPane.setBackground(new Color(255, 255, 255, 150));
        buttonPane.setPreferredSize(new Dimension(-1, 32));
        lbName.setPreferredSize(new Dimension(40, -1));
        btnOk.setPreferredSize(new Dimension(69, 21));
        btnOk.addActionListener(this);
        btnCancel.setPreferredSize(btnOk.getPreferredSize());
        btnCancel.addActionListener(this);
        ecName.add(lbName, BorderLayout.WEST);
        ecName.add(txtName, BorderLayout.CENTER);
        buttonPane.add(btnOk, LineLayout.END);
        buttonPane.add(btnCancel, LineLayout.END);
        mainPane.add(ecName, LineLayout.START_FILL);
        content.add(buttonPane, BorderLayout.SOUTH);
        content.add(mainPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
        
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                cancel();
            }
        });
    }
    
    private boolean exist(String name)
    {
        return !CategoryDAO.getCategorys(isPay, CategoryDAO.createCriterions(isPay, Restrictions.eq("name", name))).isEmpty();
    }
    
    private void submit()
    {
        String name = txtName.getText();
        String retInfo = null;
        boolean success = false;
        
        if(name == null || (name = name.trim()).isEmpty())
        {
            retInfo = "\u540D\u79F0\u4E0D\u80FD\u4E3A\u7A7A\uFF01";
        }
        else if(exist(name))
        {
            retInfo = "\u8BE5" + (isPay? "\u6D88\u8D39\u7C7B\u522B": "\u6536\u5165\u7C7B\u522B") + "\u5DF2\u5B58\u5728\uFF01";
        }
        else
        {
            CategoryDAO.addCategory(category = new Category(name, isPay));
            retInfo = (isPay? "\u6D88\u8D39\u7C7B\u522B": "\u6536\u5165\u7C7B\u522B") + "\u6DFB\u52A0\u6210\u529F\uFF01";
            success = true;
        }
        
        if(success)
        {
            JCMessageBox.createInformationMessageBox(this, this.getTitle(), retInfo).open();
            this.dispose();
        }
        else
        {
            JCMessageBox.createErrorMessageBox(this, this.getTitle(), retInfo).open();
            txtName.requestFocus();
        }
    }
    
    private void cancel()
    {
        category = null;
        this.dispose();
    }
    
    public Category getCategory()
    {
        return category;
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