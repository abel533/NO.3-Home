package craky.keeper.category;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class CategoryListDialog extends JCDialog
{
    private static final long serialVersionUID = 8014695984667696577L;

    private KeeperApp keeper;
    
    private JComponent content;
    
    private Action addAction, deleteAction, cancelAction;
    
    private CommonTableModel<Category> model;
    
    private JCTable table;
    
    private JCPopupMenu popup;
    
    private Map<String, Category> categoryMap;
    
    private Category currentCategory;
    
    private boolean isPay;
    
    private boolean categoryUpdated;

    public CategoryListDialog(KeeperApp keeper, boolean isPay)
    {
        super(keeper.getMainFrame(), isPay? "\u6D88\u8D39\u7C7B\u522B": "\u6536\u5165\u7C7B\u522B", ModalityType.DOCUMENT_MODAL);
        this.keeper = keeper;
        this.isPay = isPay;
        this.categoryMap = keeper.getCategoryMap(isPay);
        initActions();
        initUI();
        setVisible(true);
    }
    
    private void initActions()
    {
        addAction = new AbstractAction("\u6DFB\u52A0")
        {
            private static final long serialVersionUID = 5023433496740700046L;

            public void actionPerformed(ActionEvent e)
            {
                CategoryInfoDialog infoDialog = new CategoryInfoDialog(keeper, CategoryListDialog.this, null, isPay);
                Category category = infoDialog.getCategory();
                
                if(category != null)
                {
                    model.insertRowDataAt(model.getRowCount() - 2, category);
                    Map<String, Category> oldMap = new LinkedHashMap<String, Category>(categoryMap);
                    int oldCount = oldMap.size();
                    int index = 0;
                    categoryMap.clear();
                    
                    for(String categoryName: oldMap.keySet())
                    {
                        categoryMap.put(categoryName, oldMap.get(categoryName));
                        index++;
                        
                        if(index == oldCount - 2)
                        {
                            categoryMap.put(category.getName(), category);
                        }
                    }
                    
                    categoryUpdated = true;
                }
            }
        };
        
        deleteAction = new AbstractAction("\u5220\u9664")
        {
            private static final long serialVersionUID = -568444344420944269L;

            public void actionPerformed(ActionEvent e)
            {
                CategoryListDialog dialog = CategoryListDialog.this;
                String title = "\u5220\u9664" + (isPay? "\u6D88\u8D39\u7C7B\u522B": "\u6536\u5165\u7C7B\u522B");
                String info = "\u786E\u5B9A\u8981\u5220\u9664\u8BE5" + (isPay? "\u6D88\u8D39\u7C7B\u522B": "\u6536\u5165\u7C7B\u522B") + "\u5417\uFF1F";
                
                if(JCMessageBox.createQuestionMessageBox(dialog, title, info).open() == JCMessageBox.YES_OPTION)
                {
                    info = (isPay? "\u6D88\u8D39\u7C7B\u522B": "\u6536\u5165\u7C7B\u522B") + "\u5220\u9664\u6210\u529F\uFF01";
                    CategoryDAO.deleteCategory(currentCategory);
                    categoryMap.remove(currentCategory.getName());
                    model.delRowData(currentCategory);
                    JCMessageBox.createInformationMessageBox(dialog, title, info).open();
                    categoryUpdated = true;
                }
            }
        };
        
        cancelAction = new AbstractAction("\u53D6\u6D88")
        {
            private static final long serialVersionUID = -568444344420944269L;

            public void actionPerformed(ActionEvent e)
            {
                close();
            }
        };
        
        deleteAction.setEnabled(false);
    }
    
    private void initUI()
    {
        final String[] columnsName = {"\u540D\u79F0", "\u7C7B\u578B"};
        final Class<?>[] columnsClass = {String.class, String.class};
        final String[] getMethodsName = {"getName", "getTypeName"};
        final String[] setMethodsName = {"setName", "setTypeName"};
        JImagePane buttonPane = new JImagePane();
        content = (JComponent)this.getContentPane();
        JCButton btnAdd = new JCButton(addAction);
        JCButton btnDelete = new JCButton(deleteAction);
        JCButton btnCancel = new JCButton(cancelAction);
        List<Category> categoryList = new ArrayList<Category>(categoryMap.values());
        JCScrollTable scTable = new JCScrollTable();
        table = scTable.getTable();
        JCTableHeader header = (JCTableHeader)table.getTableHeader();
        model = new CommonTableModel<Category>(scTable.getTable(), columnsName, columnsClass, getMethodsName, setMethodsName, categoryList);
        popup = new JCPopupMenu();
        JCMenuItem miDelete = new JCMenuItem(deleteAction);
        
        content.setPreferredSize(new Dimension(300, 350));
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
        btnDelete.setPreferredSize(btnAdd.getPreferredSize());
        btnCancel.setPreferredSize(btnAdd.getPreferredSize());
        miDelete.setMnemonic('D');
        miDelete.setText(miDelete.getText() + "(D)");
        
        popup.add(miDelete);
        buttonPane.add(btnDelete, LineLayout.END);
        buttonPane.add(btnAdd, LineLayout.END);
        buttonPane.add(btnCancel, LineLayout.END);
        content.add(buttonPane, BorderLayout.SOUTH);
        content.add(scTable, BorderLayout.CENTER);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner());
        UIUtil.escAndEnterAction(this, btnAdd, cancelAction);
        
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
        
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                close();
            }
        });
    }
    
    private void close()
    {
        if(categoryUpdated)
        {
            keeper.getMainFrame().updateCategory(isPay);
            categoryUpdated = false;
        }
        
        dispose();
    }
    
    private void whenSelectionChanged(ListSelectionEvent e)
    {
        currentCategory = table.getSelectedRow() < 0? null: model.getSelectedRowData();
        deleteAction.setEnabled(currentCategory != null && currentCategory.getType() % 100 != 0 &&
                        currentCategory.getCount() == 0);
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