package craky.keeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import craky.chart.Bar3DChart;
import craky.chart.Chart3D;
import craky.chart.Pie3DChart;
import craky.componentc.JCCheckBox;
import craky.keeper.sql.Criterion;

public abstract class StatVisibleKeeperPane extends KeeperPane
{
    private static final long serialVersionUID = -9141515276942689520L;
    
    private Map<String, Number> pieMap, barMap;
    
    private Chart3D pieChart, barChart;
    
    public StatVisibleKeeperPane(KeeperApp keeper, KeeperMgr mgr)
    {
        super(keeper, mgr);
    }
    
    protected JComponent createRight()
    {
        right = new JPanel();
        pieChart = new Pie3DChart(pieMap = new HashMap<String, Number>(), 10, getPieChartTitle());
        barChart = new Bar3DChart(barMap = new HashMap<String, Number>(), 10, 10, getBarChartTitle());
        right.setPreferredSize(new Dimension(400, -1));
        right.setBorder(new CompoundBorder(new LineBorder(new Color(84, 165, 213)), new EmptyBorder(5, 3, 5, 3)));
        right.setLayout(null);
        right.setBackground(Color.WHITE);
        right.setOpaque(true);
        ((Pie3DChart)pieChart).setDataFormat(KeeperConst.AMOUNT_FORMAT);
        pieChart.setMaximumSize(new Dimension(1000, 300));
        ((Bar3DChart)barChart).setFormat(KeeperConst.AMOUNT_FORMAT);
        right.add(pieChart);
        right.add(barChart);
        right.setVisible(false);
        right.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
                layoutStat();
            }
        });
        
        return right;
    }
    
    private void layoutStat()
    {
        final int gap = 20;
        Insets insets = right.getInsets();
        int width = right.getWidth() - insets.left - insets.right;
        int height = right.getHeight() - insets.top - insets.bottom;
        int maxPieHeight = pieChart.getMaximumSize().height;
        int pieHeight = 0, barHeight = 0;
        
        if(!barChart.isVisible())
        {
            pieHeight = Math.min(height, maxPieHeight);
        }
        else
        {
            pieHeight = Math.min(maxPieHeight, (height - gap) / 2);
            barHeight = height - pieHeight - gap;
        }
        
        pieChart.setBounds(insets.left, insets.top, width, pieHeight);
        barChart.setBounds(insets.left, insets.top + pieHeight + gap, width, barHeight);
    }
    
    protected Criterion[] createCriterions(String type)
    {
        return super.createCriterions(null);
    }
    
    protected void resetRowValues(KeeperObject keeperObject)
    {
        super.resetRowValues(keeperObject);
        keeperObject.setType(String.valueOf(cpType.getSelectedItem()));
    }
    
    protected JCCheckBox createRightSplitButton()
    {
        btnShowRight = new JCCheckBox();
        btnShowRight.setPreferredSize(new Dimension(6, -1));
        btnShowRight.setIcon(emptyIcon);
        btnShowRight.setSelectedIcon(emptyIcon);
        btnShowRight.setRolloverIcon(splitRight);
        btnShowRight.setRolloverSelectedIcon(splitLeft);
        btnShowRight.setPressedIcon(splitRight);
        btnShowRight.setPressedSelectedIcon(splitLeft);
        btnShowRight.setFocusable(false);
        btnShowRight.setToolTipText(SHOW_STAT);
        btnShowRight.addItemListener(splitListener);
        return btnShowRight;
    }
    
    public void updateCategory()
    {
        resetTypes();
    }
    
    public void refreshStat()
    {
        if(!isShowStat())
        {
            return;
        }
        
        pieMap.clear();
        barMap.clear();
        String type;
        String date;
        float amount;
        
        for(KeeperObject keeperObject: data)
        {
            amount = keeperObject.getAmount();
            type = keeperObject.getType();
            date = KeeperConst.YEAR_MONTH_FORMAT.format(keeperObject.getDate());
            
            if(pieMap.containsKey(type))
            {
                pieMap.put(type, pieMap.get(type).doubleValue() + amount);
            }
            else
            {
                pieMap.put(type, amount);
            }
            
            if(barMap.containsKey(date))
            {
                barMap.put(date, barMap.get(date).doubleValue() + amount);
            }
            else
            {
                barMap.put(date, amount);
            }
        }
        
        pieChart.setDatasAndRepaint(pieMap);
        barChart.setDatasAndRepaint(barMap);
    }
    
    protected abstract String getPieChartTitle();
    
    protected abstract String getBarChartTitle();
}