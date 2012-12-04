package craky.keeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;

import craky.component.CommonTableModel;
import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.componentc.JCCalendarComboBox;
import craky.componentc.JCCheckBox;
import craky.componentc.JCCheckBoxList;
import craky.componentc.JCCheckedComboBox;
import craky.componentc.JCComboBox;
import craky.componentc.JCLabel;
import craky.componentc.JCList;
import craky.componentc.JCMenuItem;
import craky.componentc.JCMessageBox;
import craky.componentc.JCNumberField;
import craky.componentc.JCPopupMenu;
import craky.componentc.JCScrollList;
import craky.componentc.JCScrollTable;
import craky.componentc.JCScrollText;
import craky.componentc.JCStringField;
import craky.componentc.JCTable;
import craky.componentc.JCTableHeader;
import craky.componentc.JCTextField;
import craky.keeper.category.Category;
import craky.keeper.category.CategoryDAO;
import craky.keeper.extra.ExtraPane;
import craky.keeper.income.Income;
import craky.keeper.income.IncomeDAO;
import craky.keeper.pay.Pay;
import craky.keeper.pay.PayDAO;
import craky.keeper.sql.Criterion;
import craky.keeper.sql.Restrictions;
import craky.keeper.user.User;
import craky.keeper.util.KeeperUtil;
import craky.layout.LineLayout;
import craky.util.IOUtil;

public abstract class KeeperPane extends JImagePane
{
    private static final long serialVersionUID = 4659142438655902812L;
    
    private static final String SHOW_LIST = "\u663E\u793A\u6708\u4EFD";
    
    private static final String HIDE_LIST = "\u9690\u85CF\u6708\u4EFD";
    
    protected static final String SHOW_STAT = "\u663E\u793A\u7EDF\u8BA1";
    
    private static final String HIDE_STAT = "\u9690\u85CF\u7EDF\u8BA1";
    
    private static final String CMD_INSERT = "Insert";
    
    private static final String CMD_MODIFY = "Modify";
    
    private static final String EXTRA = "\u989D\u5916";
    
    private static final String[] COLUMNS_NAME = {"\u6D88\u8D39\u65E5\u671F", "\u91D1\u989D(\u5143)", "\u6458\u8981", "\u7C7B\u522B",
                                                  "\u660E\u7EC6", "\u5907\u6CE8", "\u5165\u8D26\u65F6\u95F4", "\u5165\u8D26\u7528\u6237"};
    
    protected KeeperApp keeper;
    
    protected KeeperMgr mgr;
    
    protected User currentUser;
    
    protected CommonTableModel<KeeperObject> model;
    
    protected List<KeeperObject> data;
    
    protected KeeperObject currentData;
    
    protected Action insertAction, modifyAction, deleteAction, submitAction, cancelAction;
    
    protected JCTable table;
    
    protected JCPopupMenu popup;
    
    protected JComponent left, right, top, bottom, center;
    
    protected JComponent ecFStartDate, ecFEndDate, ecFMinAmount, ecFMaxAmount, ecFSummary, ecFType, ecFDR;
    
    protected JComponent ecDate, ecAmount, ecSummary, ecType, ecDetail, ecRemark, ecRecordTime, ecRecorder;
    
    protected JCCalendarComboBox cpDate, cpFStartDate, cpFEndDate;
    
    protected JCNumberField cpAmount, cpFMinAmount, cpFMaxAmount;
    
    protected JCTextField cpSummary, cpRecordTime, cpRecorder, cpFSummary, cpFDR;
    
    protected JCComboBox cpType;
    
    protected JCCheckedComboBox cpFType;
    
    protected JCScrollText cpDetail, cpRemark;
    
    protected JCCheckBox btnShowLeft, btnShowRight;
    
    private JCButton btnModify, btnDelete, btnInsert, btnSubmit, btnCancel;
    
    private JCList dateList;
    
    private Map<String, Category> categoryMap;
    
    protected Icon splitLeft, splitRight, emptyIcon;
    
    private boolean showDetail;
    
    private boolean showFind;
    
    private boolean showList;
    
    private boolean showStat;
    
    private boolean inited;
    
    private boolean modifying;
    
    private boolean loadImmediately;
    
    private int selectedIndex;
    
    protected SplitListener splitListener;
    
    private ListSelectionEvent lastDateListSelectionEvent;
    
    public KeeperPane(KeeperApp keeper, KeeperMgr mgr)
    {
        this.keeper = keeper;
        this.mgr = mgr;
        this.categoryMap = keeper.getCategoryMap(isPay());
        this.currentUser = keeper.getCurrentUser();
        this.splitListener = new SplitListener();
        this.splitLeft = KeeperUtil.getIcon("split_left.png");
        this.splitRight = KeeperUtil.getIcon("split_right.png");
        this.emptyIcon = KeeperUtil.getIcon("split_empty.png");
        this.selectedIndex = -1;
        setImageOnly(true);
        setName(getClass().getSimpleName());
        initActions();
        initUI();
        resetTypes();
        dateList.setSelectedIndex(dateList.getModel().getSize() - 1);
    }
    
    private void initActions()
    {
        insertAction = new AbstractAction("\u6DFB\u52A0")
        {
            private static final long serialVersionUID = 5056698643009336063L;

            public void actionPerformed(ActionEvent e)
            {
                insertOrModify(true);
            }
        };
        
        modifyAction = new AbstractAction("\u4FEE\u6539")
        {
            private static final long serialVersionUID = 8222836515926767063L;

            public void actionPerformed(ActionEvent e)
            {
                insertOrModify(false);
            }
        };
        
        deleteAction = new AbstractAction("\u5220\u9664")
        {
            private static final long serialVersionUID = -4608405524916997614L;

            public void actionPerformed(ActionEvent e)
            {
                delete();
            }
        };
        
        submitAction = new AbstractAction("\u786E\u5B9A")
        {
            private static final long serialVersionUID = 6989199497316998154L;

            public void actionPerformed(ActionEvent e)
            {
                submit();
            }
        };
        
        cancelAction = new AbstractAction("\u53D6\u6D88")
        {
            private static final long serialVersionUID = 6989199497316998154L;

            public void actionPerformed(ActionEvent e)
            {
                cancel();
            }
        };
        
        modifyAction.setEnabled(false);
        deleteAction.setEnabled(false);
    }
    
    private void createPopup(int purview)
    {
        if(purview <= User.USER)
        {
            popup = new JCPopupMenu();
            JCMenuItem miInsert = new JCMenuItem(insertAction);
            miInsert.setMnemonic('A');
            miInsert.setText(miInsert.getText() + "(A)");
            popup.add(miInsert);
            
            if(purview <= User.ADMIN)
            {
                JCMenuItem miModify = new JCMenuItem(modifyAction);
                JCMenuItem miDelete = new JCMenuItem(deleteAction);
                miModify.setMnemonic('M');
                miModify.setText(miModify.getText() + "(M)");
                miDelete.setMnemonic('D');
                miDelete.setText(miDelete.getText() + "(D)");
                popup.add(miModify);
                popup.add(miDelete);
            }
        }
    }
    
    private void initUI()
    {
        int purview = currentUser.getPurview();
        btnModify = new JCButton(modifyAction);
        btnDelete = new JCButton(deleteAction);
        btnInsert = new JCButton(insertAction);
        btnSubmit = new JCButton(submitAction);
        btnCancel = new JCButton(cancelAction);
        btnModify.setPreferredSize(new Dimension(-1, 21));
        btnDelete.setPreferredSize(btnModify.getPreferredSize());
        btnInsert.setPreferredSize(btnModify.getPreferredSize());
        btnSubmit.setPreferredSize(btnModify.getPreferredSize());
        btnCancel.setPreferredSize(btnModify.getPreferredSize());
        btnSubmit.setVisible(false);
        btnCancel.setVisible(false);
        createPopup(purview);
        EmptyComponent centerParent = new EmptyComponent();
        setLayout(new BorderLayout());
        centerParent.setLayout(new BorderLayout(0, 3));
        createCenter();
        createTop();
        createBottom();
        createLeft();
        createRight();
        this.add(centerParent, BorderLayout.CENTER);
        
        if(center != null)
        {
            centerParent.add(center, BorderLayout.CENTER);
        }
        
        if(top != null)
        {
            centerParent.add(top, BorderLayout.NORTH);
        }
        
        if(bottom != null)
        {
            centerParent.add(bottom, BorderLayout.SOUTH);
        }
        
        if(left != null)
        {
            this.add(left, BorderLayout.WEST);
        }
        
        if(right != null)
        {
            this.add(right, BorderLayout.EAST);
        }
        
        if(btnShowLeft != null)
        {
            btnShowLeft.setSelected(true);
        }
    }
    
    protected Criterion[] createCriterions(String type)
    {
        JCCheckBoxList checkList = cpFType.getCheckedList();
        List<Criterion> list = new ArrayList<Criterion>();
        Calendar startDate = cpFStartDate.getDate();
        Calendar endDate = cpFEndDate.getDate();
        String minAmountStr = cpFMinAmount.getText();
        String maxAmountStr = cpFMaxAmount.getText();
        Number minAmount = minAmountStr == null || minAmountStr.isEmpty()? null: cpFMinAmount.getNumber();
        Number maxAmount = maxAmountStr == null || maxAmountStr.isEmpty()? null: cpFMaxAmount.getNumber();
        String summary = cpFSummary.getText();
        String dr = cpFDR.getText();
        List<?> types = (checkList.isSelectedAll() || checkList.isSelectedEmpty())? null: cpFType.getSelectedItems();
        
        if(startDate != null)
        {
            list.add(Restrictions.ge("date", new Date(startDate.getTimeInMillis())));
        }
        
        if(endDate != null)
        {
            list.add(Restrictions.le("date", new Date(endDate.getTimeInMillis())));
        }
        
        if(minAmount != null)
        {
            list.add(Restrictions.ge("amount", minAmount.floatValue()));
        }
        
        if(maxAmount != null)
        {
            list.add(Restrictions.le("amount", maxAmount.floatValue()));
        }
        
        if(type != null)
        {
            list.add(Restrictions.eq("type", type));
        }
        else if(types != null)
        {
            list.add(Restrictions.in("type", types));
        }
        else if(currentUser.getPurview() >= User.VISITOR)
        {
            list.add(Restrictions.ne("type", ExtraPane.TYPE));
        }
        
        if(summary != null && !summary.trim().isEmpty())
        {
            list.add(Restrictions.like("summary", '%' + summary + '%'));
        }
        
        if(dr != null && !dr.trim().isEmpty())
        {
            dr = '%' + dr + '%';
            list.add(Restrictions.or(Restrictions.like("detail", dr), Restrictions.like("remark", dr)));
        }
        
        return list.toArray(new Criterion[list.size()]);
    }
    
    protected void resetTypes()
    {
        JCCheckBoxList checkList = cpFType.getCheckedList();
        boolean isVisitor = currentUser.getPurview() >= User.VISITOR;
        boolean selectEmpty = checkList.isSelectedEmpty();
        Object oldSelType = cpType.getSelectedItem();
        List<?> oldUnselFTypes = checkList.getAllItems();
        oldUnselFTypes.removeAll(cpFType.getSelectedItems());
        String typeName;
        cpType.removeAllItems();
        cpFType.removeAllItems();
        
        for(Category category: categoryMap.values())
        {
            typeName = category.getName();
            
            if(!isVisitor || !typeName.equals(EXTRA))
            {
                cpType.addItem(typeName);
                cpFType.addItem(typeName);
            }
        }
        
        cpType.setSelectedItem(oldSelType);
        
        if(!selectEmpty)
        {
            List<?> newSelFTypes = checkList.getAllItems();
            newSelFTypes.removeAll(oldUnselFTypes);
            cpFType.setSelectedItems(newSelFTypes);
        }
    }
    
    public void load()
    {
        data.clear();
        
        if(isPay())
        {
            data.addAll(PayDAO.getPays(createCriterions(mgr.getCurrentPane().getType())));
        }
        else
        {
            data.addAll(IncomeDAO.getIncomes(createCriterions(null)));
        }
        
        model.refreshUI();
        mgr.resetTotal();
        refreshStat();
        inited = true;
    }
    
    public void reload()
    {
        if(dateList.getSelectedIndex() >= 0)
        {
            quickFind(lastDateListSelectionEvent);
        }
        else
        {
            load();
        }
    }
    
    public boolean isShowDetail()
    {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail)
    {
        this.showDetail = showDetail;
        
        if(bottom != null)
        {
            bottom.setVisible(showDetail);
        }
        
        if(showDetail)
        {
            resetDetailValues();
        }
    }
    
    public boolean isShowFind()
    {
        return showFind;
    }

    public void setShowFind(boolean showFind)
    {
        this.showFind = showFind;
        
        if(top != null)
        {
            top.setVisible(showFind);
        }
    }
    
    public boolean isShowList()
    {
        return showList;
    }

    public void setShowList(boolean showList)
    {
        this.showList = showList;
        
        if(left != null)
        {
            left.setVisible(showList);
            this.revalidate();
        }
    }

    public boolean isShowStat()
    {
        return showStat;
    }

    public void setShowStat(boolean showStat)
    {
        boolean needRefresh = showStat && !this.showStat;
        this.showStat = showStat;
        
        if(right != null)
        {
            right.setVisible(showStat);
            
            if(needRefresh)
            {
                refreshStat();
            }
        }
    }

    public boolean isInited()
    {
        return inited;
    }
    
    protected JComponent createLeft()
    {
        Vector<DateListItem> items = new Vector<DateListItem>();
        
        for(int i = 0; i < 12; i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, i - 11);
            items.add(new DateListItem(calendar));
        }
        
        JCScrollList scList = new JCScrollList(items);
        dateList = scList.getList();
        dateList.setFixedCellHeight(30);
        dateList.setRendererOpaque(false);
        dateList.setFont(new Font("Arial", Font.BOLD, 15));
        dateList.setForeground(Color.DARK_GRAY);
        dateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scList.setHeaderVisible(true);
        scList.setHeaderText("\u5FEB\u901F\u67E5\u627E");
        scList.getHeader().setHeaderHeight(25);
        scList.setImageOnly(true);
        scList.setAlpha(0.0f);
        scList.setPreferredSize(new Dimension(75, -1));
        dateList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                quickFind(e);
            }
        });
        
        return left = scList;
    }
    
    protected JComponent createRight()
    {
        return right;
    }
    
    protected JComponent createTop()
    {
        int leftGap = btnShowLeft == null? 0: btnShowLeft.getPreferredSize().width;
        int rightGap = btnShowRight == null? 0: btnShowRight.getPreferredSize().width;
        top = new EmptyComponent();
        EmptyComponent ecFind = new EmptyComponent();
        Border border1 = new EmptyBorder(3, 0, 0, 3);
        Border border2 = new EmptyBorder(3, 0, 0, 0);
        JCButton btnFind = new JCButton("\u67E5\u627E");
        JCLabel lbStartDate = new JCLabel(getDataName() + "\u65E5\u671F\uFF1A");
        JCLabel lbEndDate = new JCLabel("\u5230");
        JCLabel lbMinAmount = new JCLabel("\u91D1\u989D(\u5143)\uFF1A");
        JCLabel lbMaxAmount = new JCLabel("\u5230");
        JCLabel lbSummary = new JCLabel("\u6458\u8981\uFF1A");
        JCLabel lbType = new JCLabel("\u7C7B\u522B\uFF1A");
        JCLabel lbDR = new JCLabel("\u660E\u7EC6/\u5907\u6CE8\uFF1A");
        cpFStartDate = new JCCalendarComboBox();
        cpFEndDate = new JCCalendarComboBox();
        cpFMinAmount = new JCNumberField(0, 2, 999999999.99);
        cpFMaxAmount = new JCNumberField(0, 2, 999999999.99);
        cpFType = new JCCheckedComboBox();
        cpFSummary = new JCStringField(25);
        cpFDR = new JCTextField();
        JComponent ecButton = createTitledComponent(null, btnFind, new EmptyBorder(5, 0, 2, 2), 0);
        
        btnFind.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                load();
                dateList.clearSelection();
            }
        });
        
        cpFMinAmount.setNonNegative(true);
        cpFMaxAmount.setNonNegative(true);
        cpFType.setText4SelectedAll("\u5168\u90E8");
        btnFind.setFocusable(false);
        btnFind.setIconTextGap(0);
        btnFind.setIcon(KeeperUtil.getIcon("find.png"));
        btnFind.setImage(KeeperUtil.getImage("common_button_normal_bg.png", true));
        btnFind.setRolloverImage(KeeperUtil.getImage("common_button_rollover_bg.png", true));
        btnFind.setPressedImage(KeeperUtil.getImage("common_button_pressed_bg", true));
        ecFind.setLayout(new BoxLayout(ecFind, BoxLayout.X_AXIS));
        ecButton.setPreferredSize(new Dimension(57, -1));
        top.setBorder(new EmptyBorder(0, leftGap, 0, rightGap));
        top.setPreferredSize(new Dimension(-1, 27));
        top.setLayout(new BorderLayout());
        cpFStartDate.setPreferredSize(new Dimension(88, -1));
        cpFEndDate.setPreferredSize(new Dimension(88, -1));
        cpFMinAmount.setPreferredSize(new Dimension(50, -1));
        cpFMaxAmount.setPreferredSize(new Dimension(50, -1));
        cpFSummary.setPreferredSize(new Dimension(45, -1));
        cpFType.setPreferredSize(new Dimension(65, -1));
        cpFDR.setPreferredSize(new Dimension(45, -1));
        ecFind.add(ecFStartDate = createTitledComponent(lbStartDate, cpFStartDate, border2, 0));
        ecFind.add(ecFEndDate = createTitledComponent(lbEndDate, cpFEndDate, border1, 0));
        ecFind.add(ecFMinAmount = createTitledComponent(lbMinAmount, cpFMinAmount, border2, 0));
        ecFind.add(ecFMaxAmount = createTitledComponent(lbMaxAmount, cpFMaxAmount, border1, 0));
        ecFind.add(ecFSummary = createTitledComponent(lbSummary, cpFSummary, border1, 0));
        ecFind.add(ecFType = createTitledComponent(lbType, cpFType, border1, 0));
        ecFind.add(ecFDR = createTitledComponent(lbDR, cpFDR, border2, 0));
        top.add(ecFind, BorderLayout.CENTER);
        top.add(ecButton, BorderLayout.EAST);
        return top;
    }
    
    protected JComponent createBottom()
    {
        int leftGap = btnShowLeft == null? 0: btnShowLeft.getPreferredSize().width;
        int rightGap = btnShowRight == null? 0: btnShowRight.getPreferredSize().width;
        int purview = currentUser.getPurview();
        bottom = new EmptyComponent();
        bottom.setBorder(new CompoundBorder(new EmptyBorder(0, leftGap, 0, rightGap), new LineBorder(new Color(84, 165, 213))));
        bottom.setPreferredSize(new Dimension(-1, 80));
        bottom.setLayout(new BorderLayout());
        bottom.add(createDetailComponent(), BorderLayout.CENTER);
        
        if(purview <= User.USER)
        {
            EmptyComponent buttonContainer = new EmptyComponent();
            buttonContainer.setLayout(new LineLayout(3, 0, 5, 0, 3, LineLayout.TRAILING, LineLayout.TRAILING, LineLayout.VERTICAL));
            buttonContainer.setPreferredSize(new Dimension(85, -1));
            buttonContainer.add(btnSubmit, LineLayout.END_FILL);
            
            if(purview <= User.ADMIN)
            {
                buttonContainer.add(btnDelete, LineLayout.END_FILL);
                buttonContainer.add(btnModify, LineLayout.END_FILL);
            }
            
            buttonContainer.add(btnInsert, LineLayout.END_FILL);
            buttonContainer.add(btnCancel, LineLayout.END_FILL);
            bottom.add(buttonContainer, BorderLayout.EAST);
        }
        
        return bottom;
    }
    
    protected JComponent createCenter()
    {
        final Class<?>[] columnsClass = {Date.class, String.class, String.class, String.class, String.class,
                        String.class, Timestamp.class, String.class};
        final String[] getMethodsName = {"getDate", "getAmountString", "getSummary", "getType", "getDetail",
                        "getRemark", "getRecordTime", "getRecorder"};
        final String[] setMethodsName = {"setDate", "setAmountString", "setSummary", "setType", "setDetail",
                        "setRemark", "setRecordTime", "setRecorder"};
        final int[] columnsPreferredWidth = {70, 60, 80, 50, 120, 120, 150, 60};
        EmptyComponent ec = new EmptyComponent();
        JCScrollTable scTable = new JCScrollTable();
        table = scTable.getTable();
        data = new ArrayList<KeeperObject>();
        model = new CommonTableModel<KeeperObject>(table, getColumnsName(), columnsClass, getMethodsName, setMethodsName, data);
        TableRowSorter<CommonTableModel<KeeperObject>> sorter = new TableRowSorter<CommonTableModel<KeeperObject>>(model);
        
        scTable.setAlpha(0.0f);
        scTable.setColumnControlEnabled(false);
        createLeftSplitButton();
        createRightSplitButton();
        ec.setLayout(new BorderLayout());
        ec.add(scTable, BorderLayout.CENTER);
        table.setModel(model);
        table.setDragEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(sorter);
        table.setRendererOpaque(false);
        ((JCTableHeader)table.getTableHeader()).setShowPopupMenu(false);
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
        sorter.setComparator(1, new Comparator<String>()
        {
            public int compare(String str1, String str2)
            {
                try
                {
                    float num1 = KeeperConst.AMOUNT_FORMAT.parse(str1).floatValue();
                    float num2 = KeeperConst.AMOUNT_FORMAT.parse(str2).floatValue();
                    return (int)((num1 - num2) * 100);
                }
                catch(Exception e)
                {
                    return str1.compareTo(str2);
                }
            }
        });
        
        for(int i = 0; i < columnsPreferredWidth.length; i++)
        {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnsPreferredWidth[i]);
        }
        
        if(btnShowLeft != null)
        {
            ec.add(btnShowLeft, BorderLayout.WEST);
        }
        
        if(btnShowRight != null)
        {
            ec.add(btnShowRight, BorderLayout.EAST);
        }
        
        return center = ec;
    }
    
    private JComponent createDetailComponent()
    {
        EmptyComponent ec = new EmptyComponent();
        EmptyComponent firstRow = new EmptyComponent();
        EmptyComponent secondRow = new EmptyComponent();
        JCLabel lbDate = new JCLabel(getDataName() + "\u65E5\u671F");
        JCLabel lbAmount = new JCLabel("\u91D1\u989D(\u5143)");
        JCLabel lbSummary = new JCLabel("\u6458\u8981");
        JCLabel lbType = new JCLabel("\u7C7B\u522B");
        JCLabel lbRecordTime = new JCLabel("\u5165\u8D26\u65F6\u95F4");
        JCLabel lbRecorder = new JCLabel("\u5165\u8D26\u7528\u6237");
        JCLabel lbDetail = new JCLabel("\u660E\u7EC6");
        JCLabel lbRemark = new JCLabel("\u5907\u6CE8");
        Border border = new EmptyBorder(0, 0, 0, 5);
        cpDate = new JCCalendarComboBox();
        cpAmount = new JCNumberField(0, 2, 999999999.99);
        cpSummary = new JCStringField(25);
        cpType = new JCComboBox();
        cpRecordTime = new JCTextField();
        cpRecorder = new JCTextField();
        cpDetail = new JCScrollText();
        cpRemark = new JCScrollText();
        
        cpAmount.setNonNegative(true);
        cpDetail.getEditor().setLineWrap(true);
        cpRemark.getEditor().setLineWrap(true);
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
        firstRow.setPreferredSize(new Dimension(-1, 25));
        secondRow.setLayout(new GridLayout(1, 2));
        firstRow.add(ecDate = createTitledComponent(lbDate, cpDate, border, 3));
        firstRow.add(ecAmount = createTitledComponent(lbAmount, cpAmount, border, 3));
        firstRow.add(ecSummary = createTitledComponent(lbSummary, cpSummary, border, 3));
        firstRow.add(ecType = createTitledComponent(lbType, cpType, border, 3));
        firstRow.add(ecRecordTime = createTitledComponent(lbRecordTime, cpRecordTime, border, 3));
        firstRow.add(ecRecorder = createTitledComponent(lbRecorder, cpRecorder, null, 3));
        firstRow.setPreferredSize(new Dimension(-1, 24));
        secondRow.add(ecDetail = createTitledComponent(lbDetail, cpDetail, border, 3));
        secondRow.add(ecRemark = createTitledComponent(lbRemark, cpRemark, new EmptyBorder(0, 0, 0, 1), 3));
        ec.setLayout(new BorderLayout(0, 5));
        ec.setBorder(new EmptyBorder(3, 5, 2, 2));
        ec.add(firstRow, BorderLayout.NORTH);
        ec.add(secondRow, BorderLayout.CENTER);
        ecDate.setPreferredSize(new Dimension(80, -1));
        ecAmount.setPreferredSize(new Dimension(50, -1));
        ecSummary.setPreferredSize(new Dimension(45, -1));
        ecType.setPreferredSize(new Dimension(28, -1));
        ecRecordTime.setPreferredSize(new Dimension(105, -1));
        ecRecorder.setPreferredSize(new Dimension(40, -1));
        cpRecordTime.setEditable(false);
        cpRecorder.setEditable(false);
        return ec;
    }
    
    protected JCCheckBox createLeftSplitButton()
    {
        btnShowLeft = new JCCheckBox();
        btnShowLeft.setPreferredSize(new Dimension(6, -1));
        btnShowLeft.setIcon(emptyIcon);
        btnShowLeft.setSelectedIcon(emptyIcon);
        btnShowLeft.setRolloverIcon(splitLeft);
        btnShowLeft.setRolloverSelectedIcon(splitRight);
        btnShowLeft.setPressedIcon(splitLeft);
        btnShowLeft.setPressedSelectedIcon(splitRight);
        btnShowLeft.setFocusable(false);
        btnShowLeft.setToolTipText(SHOW_LIST);
        btnShowLeft.addItemListener(splitListener);
        return btnShowLeft;
    }
    
    protected JCCheckBox createRightSplitButton()
    {
        return null;
    }
    
    private JComponent createTitledComponent(JComponent titleComponent, JComponent component, Border border, int hgap)
    {
        EmptyComponent ec = new EmptyComponent();
        ec.setBorder(border);
        ec.setLayout(new BorderLayout(hgap, 0));
        ec.add(component, BorderLayout.CENTER);
        
        if(titleComponent != null)
        {
            ec.add(titleComponent, BorderLayout.WEST);
        }
        
        return ec;
    }
    
    private void quickFind(ListSelectionEvent e)
    {
        if(!e.getValueIsAdjusting() && dateList.getSelectedIndex() >= 0)
        {
            lastDateListSelectionEvent = e;
            DateListItem item = (DateListItem)dateList.getSelectedValue();
            cpFStartDate.setDate(item.getStartDate());
            cpFEndDate.setDate(item.getEndDate());
            cpFMaxAmount.setText(null);
            cpFMinAmount.setText(null);
            cpFSummary.setText(null);
            cpFType.selecteAll();
            cpFDR.setText(null);
            
            if(loadImmediately)
            {
                load();
            }
            else
            {
                loadImmediately = true;
            }
        }
    }
    
    private void resetDetailValues()
    {
        if(bottom.isVisible() && !CMD_INSERT.equals(btnSubmit.getActionCommand()))
        {
            if(currentData == null)
            {
                clearValues();
            }
            else
            {
                fillValues();
            }

            resetTextStatus(false);
            resetButtonStatus(false);
        }
    }
    
    private void clearValues()
    {
        cpDate.setDate(null);
        cpAmount.setText(null);
        cpSummary.setText(null);
        cpType.setSelectedIndex(-1);
        cpDetail.getEditor().setText(null);
        cpRemark.getEditor().setText(null);
        cpRecordTime.setText(null);
        cpRecorder.setText(null);
    }
    
    private void fillValues()
    {
        Calendar date = Calendar.getInstance();
        date.setTime(currentData.getDate());
        cpDate.setDate(date);
        cpAmount.setText(currentData.getAmountString().replaceAll(",", ""));
        cpSummary.setText(currentData.getSummary());
        cpType.setSelectedItem(currentData.getType());
        cpDetail.getEditor().setText(currentData.getDetail());
        cpRemark.getEditor().setText(currentData.getRemark());
        cpRecordTime.setText(currentData.getRecordTime().toString());
        cpRecorder.setText(currentData.getRecorder());
    }
    
    private void resetTextStatus(boolean editable)
    {
        cpDate.setEditableAll(editable);
        cpAmount.setEditable(editable);
        cpSummary.setEditable(editable);
        cpType.setEditableAll(editable);
        cpDetail.setEditable(editable);
        cpRemark.setEditable(editable);
        cpRecordTime.setFocusable(!editable);
        cpRecorder.setFocusable(!editable);
    }
    
    private void resetButtonStatus(boolean modifying)
    {
        this.modifying = modifying;
        btnSubmit.setVisible(modifying);
        btnCancel.setVisible(modifying);
        btnInsert.setVisible(!modifying);
        btnModify.setVisible(!modifying);
        btnDelete.setVisible(!modifying);
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
            if(selectedIndex >= 0)
            {
                table.clearSelection();
            }
            else
            {
                cancel();
            }
        }
    }
    
    private void showPopupMenu(MouseEvent e)
    {
        if(popup != null && SwingUtilities.isRightMouseButton(e))
        {
            popup.show((Component)e.getSource(), e.getX(), e.getY());
        }
    }
    
    private void whenSelectionChanged(ListSelectionEvent e)
    {
        currentData = table.getSelectedRow() < 0? null: model.getSelectedRowData();
        boolean selectedEmpty = (selectedIndex = table.getSelectedRow()) < 0;
        modifyAction.setEnabled(!selectedEmpty);
        deleteAction.setEnabled(!selectedEmpty);
        cancel();
    }
    
    protected void resetRowValues(KeeperObject keeperObject)
    {
        keeperObject.setDate(new Date(cpDate.getDate().getTimeInMillis()));
        keeperObject.setAmount(cpAmount.getNumber().floatValue());
        keeperObject.setDetail(cpDetail.getEditor().getText());
        keeperObject.setRemark(cpRemark.getEditor().getText());
        keeperObject.setSummary(cpSummary.getText());
    }
    
    public void cancel()
    {
        btnSubmit.setActionCommand(null);
        resetDetailValues();
    }
    
    protected boolean checkInput()
    {
        String info = null;
        JComponent editor = null;
        Number amount;
        String summary;
        
        if(cpDate.getDate() == null)
        {
            info = getDataName() + "\u65E5\u671F\u4E0D\u80FD\u4E3A\u7A7A\uFF01";
            editor = cpDate;
        }
        else if((amount = cpAmount.getNumber()) == null || amount.floatValue() == 0)
        {
            info = "\u91D1\u989D\u4E0D\u80FD\u4E3A\u7A7A\u4E14\u5FC5\u987B\u5927\u4E8E0\uFF01";
            editor = cpAmount;
        }
        else if((summary = cpSummary.getText()) == null || summary.trim().isEmpty())
        {
            info = "\u6458\u8981\u4E0D\u80FD\u4E3A\u7A7A\uFF01";
            editor = cpSummary;
        }
        else if(ecType.isVisible() && cpType.getSelectedIndex() < 0)
        {
            info = "\u7C7B\u522B\u4E0D\u80FD\u4E3A\u7A7A\uFF01";
            editor = cpType;
        }
        
        if(info != null)
        {
            editor.requestFocus();
            JCMessageBox.createInformationMessageBox(keeper.getMainFrame(), "\u8F93\u5165\u9519\u8BEF", info).open();
            return false;
        }
        else
        {
            return true;
        }
    }
    
    protected boolean checkFilter(KeeperObject data)
    {
        Calendar startDate = cpFStartDate.getDate();
        Calendar endDate = cpFEndDate.getDate();
        String minAmountStr = cpFMinAmount.getText();
        String maxAmountStr = cpFMaxAmount.getText();
        Number minAmount = minAmountStr == null || minAmountStr.isEmpty()? null: cpFMinAmount.getNumber();
        Number maxAmount = maxAmountStr == null || maxAmountStr.isEmpty()? null: cpFMaxAmount.getNumber();
        String summary = cpFSummary.getText();
        String dr = cpFDR.getText();
        Date date = data.getDate();
        float amount = data.getAmount();
        java.util.Date startTime, endTime;
        
        if(startDate != null && !(date.after(startTime = startDate.getTime()) || date.equals(startTime)))
        {
            return false;
        }
        
        if(endDate != null && !(date.before(endTime = endDate.getTime()) || date.equals(endTime)))
        {
            return false;
        }
        
        if(minAmount != null && amount < minAmount.floatValue())
        {
            return false;
        }
        
        if(maxAmount != null && amount > maxAmount.floatValue())
        {
            return false;
        }
        
        if(cpFType.isVisible())
        {
            JCCheckBoxList checkList = cpFType.getCheckedList();
            List<?> typeList = checkList.isSelectedEmpty()? checkList.getAllItems(): checkList.getSelectedItems();
            
            if(!typeList.contains(data.getType()))
            {
                return false;
            }
        }
        
        if(summary != null && !(summary = summary.trim().toLowerCase()).isEmpty() && !data.getSummary().toLowerCase().contains(summary))
        {
            return false;
        }
        
        if(dr != null && !(dr = dr.trim().toLowerCase()).isEmpty()
                        && !(data.getDetail().toLowerCase().contains(dr) || data.getRemark().toLowerCase().contains(dr)))
        {
            return false;
        }
        
        return true;
    }
    
    public void submit()
    {
        if(!btnSubmit.isVisible() || !checkInput())
        {
            return;
        }
        
        String cmd = btnSubmit.getActionCommand();
        boolean insert = true;
        KeeperObject keeperObject = null;
        String info = null;
        btnSubmit.setActionCommand(null);
        
        if(CMD_INSERT.equals(cmd))
        {
            keeperObject = isPay()? new Pay(): new Income();
            keeperObject.setRecordTime(new Timestamp(System.currentTimeMillis()));
            keeperObject.setRecorder(currentUser.getName());
        }
        else if(CMD_MODIFY.equals(cmd))
        {
            keeperObject = currentData;
            insert = false;
        }
        
        String oldType = keeperObject.getType();
        resetRowValues(keeperObject);
        String type = keeperObject.getType();
        
        if(insert)
        {
            if(isPay())
            {
                PayDAO.addPay((Pay)keeperObject, categoryMap.get(type));
            }
            else
            {
                IncomeDAO.addIncome((Income)keeperObject, categoryMap.get(type));
            }
            
            if(checkFilter(keeperObject))
            {
                model.insertRowData(keeperObject);
                int row = model.convertRowIndexesToView(model.getRowCount() - 1)[0];
                table.getSelectionModel().setSelectionInterval(row, row);
            }
            else
            {
                info = "\u6DFB\u52A0\u6210\u529F\uFF0C\u7531\u4E8E\u8BE5\u8BB0\u5F55\u4E0D\u7B26\u5408\u5F53\u524D\u7684" +
                		"\u67E5\u627E\u6761\u4EF6\uFF0C\u5728\u8868\u683C\u4E2D\u4E0D\u4F1A\u663E\u793A\uFF01";
                table.clearSelection();
                clearValues();
            }
        }
        else
        {
            if(isPay())
            {
                PayDAO.updatePay((Pay)keeperObject);
            }
            else
            {
                IncomeDAO.updateIncome((Income)keeperObject);
            }
            
            if(!oldType.equals(type))
            {
                CategoryDAO.afterRemoveItem(categoryMap.get(oldType));
                CategoryDAO.afterInsertItem(categoryMap.get(type));
            }
            
            if(checkFilter(keeperObject))
            {
                model.refreshRowOnView(selectedIndex);
                fillValues();
            }
            else
            {
                info = "\u4FEE\u6539\u6210\u529F\uFF0C\u7531\u4E8E\u8BE5\u8BB0\u5F55\u4E0D\u7B26\u5408\u5F53\u524D\u7684" +
                       "\u67E5\u627E\u6761\u4EF6\uFF0C\u5728\u8868\u683C\u4E2D\u4E0D\u4F1A\u663E\u793A\uFF01";
                model.delRowData(keeperObject);
                clearValues();
            }
        }
        
        resetButtonStatus(false);
        resetTextStatus(false);
        mgr.resetTotal();
        refreshStat();
        mgr.needReload(type);
        
        if(!insert && !oldType.equals(type))
        {
            mgr.needReload(oldType);
        }
        
        if(info != null)
        {
            JCMessageBox.createInformationMessageBox(keeper.getMainFrame(), insert? "\u6DFB\u52A0": "\u4FEE\u6539", info).open();
        }
    }
    
    public void insertOrModify(boolean insert)
    {
        if(!bottom.isVisible())
        {
            mgr.showDetail(true);
        }
        
        if(modifying)
        {
            cancel();
        }
        
        btnSubmit.setActionCommand(insert? CMD_INSERT: CMD_MODIFY);
        resetTextStatus(true);
        resetButtonStatus(true);
        
        if(insert)
        {
            clearValues();
            cpDate.setDate(Calendar.getInstance());
            cpAmount.requestFocus();
        }
    }
    
    public void delete()
    {
        String message = "\u786E\u5B9A\u8981\u5220\u9664\u8BE5\u8BB0\u5F55\u5417\uFF1F";
        JCMessageBox box = JCMessageBox.createQuestionMessageBox(keeper.getMainFrame(), "\u5220\u9664", message);

        if(box.open() == JCMessageBox.YES_OPTION)
        {
            String type = currentData.getType();
            
            if(isPay())
            {
                PayDAO.deletePay((Pay)currentData, categoryMap.get(type));
            }
            else
            {
                IncomeDAO.deleteIncome((Income)currentData, categoryMap.get(type));
            }
            
            model.delRowOnViewAt(selectedIndex);
            mgr.needReload(type);
            mgr.resetTotal();
            refreshStat();
        }
    }
    
    public void needReload()
    {
        inited = false;
    }
    
    public List<KeeperObject> getAllData()
    {
        return data;
    }
    
    public void exportToCSV()
    {
        final String DOT_CSV = ".csv";
        JFileChooser chooser = new JFileChooser();
        String filterDes = "CSV (\u9017\u53F7\u5206\u9694)(*.csv)";
        FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDes, "csv");
        chooser.setFileFilter(filter);
        int ret = chooser.showSaveDialog(keeper.getMainFrame());
        
        if(ret != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        
        File file = chooser.getSelectedFile();
        String path = file.getAbsolutePath();
        String info = null;
        
        if(!path.toLowerCase().endsWith(DOT_CSV))
        {
            file = new File(path += DOT_CSV);
        }
        
        try
        {
            final String LINE_SEP = "\r\n";
            IOUtil.writeToFile(file, Arrays.toString(getColumnsName()).replaceAll("\\[|\\]| ", "") + LINE_SEP, null, false);
            
            for(KeeperObject keeperObject: data)
            {
                IOUtil.writeToFile(file, keeperObject.toCSVString() + LINE_SEP, null, true);
            }
            
            info = "<html>\u6210\u529F\u5BFC\u51FA\u5230\u6587\u4EF6\uFF1A<br>" + path;
        }
        catch(IOException e)
        {
            info = "<html>\u5BFC\u51FA\u65F6\u53D1\u751F\u5F02\u5E38\uFF0C\u8BF7\u68C0\u67E5\u662F\u5426\u5B58\u5728\u4EE5\u4E0B" +
            		"\u540C\u540D\u6587\u4EF6\u5939\u6216\u540C\u540D\u53EA\u8BFB\u6587\u4EF6\uFF01<br>" + path;
        }
        finally
        {
            if(info != null)
            {
                JCMessageBox.createInformationMessageBox(keeper.getMainFrame(), "\u5BFC\u51FA", info).open();
            }
        }
    }
    
    protected String getDataName()
    {
        return "\u6D88\u8D39";
    }
    
    protected String[] getColumnsName()
    {
        return COLUMNS_NAME;
    }
    
    public boolean isPay()
    {
        return true;
    }
    
    public void updateCategory() {}
    
    public void refreshStat() {};
    
    public abstract String getType();
    
    private class SplitListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            Object source = e.getSource();
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            
            if(source == btnShowLeft)
            {
                setShowList(selected);
                btnShowLeft.setToolTipText(selected? HIDE_LIST: SHOW_LIST);
            }
            else if(source == btnShowRight)
            {
                setShowStat(selected);
                btnShowRight.setToolTipText(selected? HIDE_STAT: SHOW_STAT);
            }
        }
    }
    
    private class DateListItem
    {
        private Calendar calendar, start, end;
        
        public DateListItem(Calendar calendar)
        {
            this.calendar = calendar;
            this.start = Calendar.getInstance();
            this.end = Calendar.getInstance();
            start.clear();
            end.clear();
            start.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
            end.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
            end.add(Calendar.MILLISECOND, -1);
        }
        
        public Calendar getStartDate()
        {
            return start;
        }
        
        public Calendar getEndDate()
        {
            return end;
        }
        
        public String toString()
        {
            return KeeperConst.YEAR_MONTH_FORMAT.format(calendar.getTime());
        }
    }
}