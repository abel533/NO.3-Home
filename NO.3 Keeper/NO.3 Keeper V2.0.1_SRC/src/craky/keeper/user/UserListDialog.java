package craky.keeper.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import craky.component.CommonTableModel;
import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.componentc.JCDialog;
import craky.componentc.JCMenuItem;
import craky.componentc.JCMessageBox;
import craky.componentc.JCPopupMenu;
import craky.componentc.JCScrollTable;
import craky.componentc.JCTable;
import craky.componentc.JCTableHeader;
import craky.keeper.KeeperApp;
import craky.layout.LineLayout;
import craky.util.UIUtil;

public class UserListDialog extends JCDialog
{
    private static final long serialVersionUID = 3367067393044735726L;
    
    private KeeperApp keeper;
    
    private JComponent content;
    
    private Action addAction, modifyAction, deleteAction, cancelAction;
    
    private CommonTableModel<User> model;
    
    private JCTable table;
    
    private JCPopupMenu popup;
    
    private List<User> userList;
    
    private User currentUser;
    
    public UserListDialog(KeeperApp keeper)
    {
        super(keeper.getMainFrame(), "\u7528\u6237\u7BA1\u7406", ModalityType.DOCUMENT_MODAL);
        this.keeper = keeper;
        initActions();
        initUI();
        load();
        setVisible(true);
    }
    
    private void initActions()
    {
        addAction = new AbstractAction("\u6DFB\u52A0")
        {
            private static final long serialVersionUID = -3647919093948535545L;

            public void actionPerformed(ActionEvent e)
            {
                UserInfoDialog infoDialog = new UserInfoDialog(keeper, UserListDialog.this, null, false);
                User user = infoDialog.getUser();
                
                if(user != null)
                {
                    model.insertRowData(user);
                }
            }
        };
        
        modifyAction = new AbstractAction("\u4FEE\u6539\u5BC6\u7801")
        {
            private static final long serialVersionUID = -1204636388592876724L;

            public void actionPerformed(ActionEvent e)
            {
                new UserInfoDialog(keeper, UserListDialog.this, currentUser.getName(), false);
            }
        };
        
        deleteAction = new AbstractAction("\u5220\u9664")
        {
            private static final long serialVersionUID = -5377194934609055107L;

            public void actionPerformed(ActionEvent e)
            {
                UserListDialog dialog = UserListDialog.this;
                String title = "\u5220\u9664\u7528\u6237";
                String info = "\u786E\u5B9A\u8981\u5220\u9664\u8BE5\u7528\u6237\u5417\uFF1F";
                
                if(JCMessageBox.createQuestionMessageBox(dialog, title, info).open() == JCMessageBox.YES_OPTION)
                {
                    info = "\u7528\u6237\u5220\u9664\u6210\u529F\uFF01";
                    keeper.removeHistoryUser(currentUser.getName());
                    UserDAO.deleteUser(currentUser);
                    model.delRowData(currentUser);
                    JCMessageBox.createInformationMessageBox(dialog, title, info).open();
                }
            }
        };
        
        cancelAction = new AbstractAction("\u53D6\u6D88")
        {
            private static final long serialVersionUID = 7342539127081422526L;

            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        };
        
        modifyAction.setEnabled(false);
        deleteAction.setEnabled(false);
    }
    
    private void initUI()
    {
        final String[] columnsName = {"\u7528\u6237\u540D", "\u6743\u9650"};
        final Class<?>[] columnsClass = {String.class, String.class};
        final String[] getMethodsName = {"getName", "getPurviewName"};
        final String[] setMethodsName = {"setName", "setPurviewName"};
        JImagePane buttonPane = new JImagePane();
        content = (JComponent)this.getContentPane();
        JCButton btnAdd = new JCButton(addAction);
        JCButton btnModify = new JCButton(modifyAction);
        JCButton btnDelete = new JCButton(deleteAction);
        JCButton btnCancel = new JCButton(cancelAction);
        userList = new ArrayList<User>();
        JCScrollTable scTable = new JCScrollTable();
        table = scTable.getTable();
        JCTableHeader header = (JCTableHeader)table.getTableHeader();
        model = new CommonTableModel<User>(scTable.getTable(), columnsName, columnsClass, getMethodsName, setMethodsName, userList);
        popup = new JCPopupMenu();
        JCMenuItem miModify = new JCMenuItem(modifyAction);
        JCMenuItem miDelete = new JCMenuItem(deleteAction);
        
        content.setPreferredSize(new Dimension(400, 250));
        content.setBorder(new EmptyBorder(0, 2, 2, 2));
        content.setLayout(new BorderLayout());
        scTable.setColumnControlEnabled(false);
        header.setShowPopupMenu(false);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        table.setModel(model);
        table.setDragEnabled(false);
        table.setAutoResizeMode(JCTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        buttonPane.setLayout(new LineLayout(5, 7, 7, 7, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        buttonPane.setBackground(new Color(255, 255, 255, 150));
        buttonPane.setPreferredSize(new Dimension(-1, 32));
        btnAdd.setPreferredSize(new Dimension(69, 21));
        btnModify.setPreferredSize(btnAdd.getPreferredSize());
        btnDelete.setPreferredSize(btnAdd.getPreferredSize());
        btnCancel.setPreferredSize(btnAdd.getPreferredSize());
        miModify.setMnemonic('M');
        miModify.setText(miModify.getText() + "(M)");
        miDelete.setMnemonic('D');
        miDelete.setText(miDelete.getText() + "(D)");
        
        popup.add(miModify);
        popup.add(miDelete);
        buttonPane.add(btnModify, LineLayout.END);
        buttonPane.add(btnDelete, LineLayout.END);
        buttonPane.add(btnAdd, LineLayout.END);
        buttonPane.add(btnCancel, LineLayout.END);
        content.add(buttonPane, BorderLayout.SOUTH);
        content.add(scTable, BorderLayout.CENTER);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner());
        UIUtil.escAndEnterAction(this, btnModify, cancelAction);
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                whenSelectionChanged(e);
            }
        });
        
        table.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                select(e);
            }
            
            public void mouseReleased(MouseEvent e)
            {
                showPopupMenu(e);
            }
        });
    }
    
    private void load()
    {
        List<User> list = UserDAO.getUsers();
        model.delAllRow();
        userList.addAll(list);
        model.refreshUI();
    }
    
    private void whenSelectionChanged(ListSelectionEvent e)
    {
        currentUser = table.getSelectedRow() < 0? null: model.getSelectedRowData();
        boolean selectedEmpty = currentUser == null;
        modifyAction.setEnabled(!selectedEmpty);
        deleteAction.setEnabled(!selectedEmpty && currentUser.getPurview() > User.ADMIN);
    }
    
    private void select(MouseEvent e)
    {
        int row = table.rowAtPoint(e.getPoint());
        
        if(row >= 0 && SwingUtilities.isRightMouseButton(e))
        {
            table.getSelectionModel().setSelectionInterval(row, row);
        }
        else if(row < 0 && !SwingUtilities.isMiddleMouseButton(e))
        {
            table.clearSelection();
        }
    }
    
    private void showPopupMenu(MouseEvent e)
    {
        int row = table.rowAtPoint(e.getPoint());

        if(row >= 0 && SwingUtilities.isRightMouseButton(e))
        {
            popup.show((Component)e.getSource(), e.getX(), e.getY());
        }
    }
}