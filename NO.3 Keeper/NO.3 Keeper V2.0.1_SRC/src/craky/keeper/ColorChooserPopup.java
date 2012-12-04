package craky.keeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.componentc.JCLabel;
import craky.componentc.JCPopupMenu;
import craky.componentc.JCSlider;
import craky.layout.LineLayout;

public class ColorChooserPopup extends JCPopupMenu implements ActionListener, ChangeListener
{
    private static final long serialVersionUID = -8966381243406755861L;
    
    private static final Font LABEL_FONT = new Font("Tahoma", Font.PLAIN, 12);
    
    private JCSlider sliderR, sliderG, sliderB;
    
    private JCLabel lbR, lbG, lbB, lbPurview;
    
    private JCButton btnOk, btnCancel;
    
    private Color oldColor, currentColor;
    
    private int r, g, b;
    
    private boolean canceled;
    
    private Action changedAction;
    
    public ColorChooserPopup()
    {
        this(new Color(0, 0, 0), null);
    }
    
    public ColorChooserPopup(Color color, Action changedAction)
    {
        super();
        this.changedAction = changedAction;
        setColor(color == null? new Color(0, 0, 0): color, false);
        initUI();
    }
    
    private void initUI()
    {
        setOpaque(false);
        setFocusable(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(236, 123));
        initChildren();
    }
    
    private void initChildren()
    {
        JImagePane mainPane = new JImagePane();
        EmptyComponent ecR = new EmptyComponent();
        EmptyComponent ecG = new EmptyComponent();
        EmptyComponent ecB = new EmptyComponent();
        EmptyComponent ecButton = new EmptyComponent();
        EmptyComponent ecPurviewAndButton = new EmptyComponent();
        JCLabel lbRT = new JCLabel("R: ");
        JCLabel lbGT = new JCLabel("G: ");
        JCLabel lbBT = new JCLabel("B: ");
        sliderR = new JCSlider(0, 255, r);
        sliderG = new JCSlider(0, 255, g);
        sliderB = new JCSlider(0, 255, b);
        lbR = new JCLabel(r + "");
        lbG = new JCLabel(g + "");
        lbB = new JCLabel(b + "");
        lbPurview = new JCLabel();
        btnOk = new JCButton("\u786E\u5B9A");
        btnCancel = new JCButton("\u53D6\u6D88");
        
        mainPane.setBackground(new Color(234, 238, 240));
        mainPane.setLayout(new LineLayout(0, LineLayout.TRAILING, LineLayout.LEADING, LineLayout.VERTICAL));
        ecR.setLayout(new LineLayout(0, 5, 5, 5, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecG.setLayout(new LineLayout(0, 5, 5, 5, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecB.setLayout(new LineLayout(0, 5, 5, 5, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecButton.setLayout(new LineLayout(3, 0, 0, 0, 0, LineLayout.LEADING, LineLayout.LEADING, LineLayout.HORIZONTAL));
        ecPurviewAndButton.setLayout(new LineLayout(5, 6, 5, 5, 5, LineLayout.LEADING, LineLayout.TRAILING, LineLayout.HORIZONTAL));
        ecR.setPreferredSize(new Dimension(-1, 25));
        ecG.setPreferredSize(ecR.getPreferredSize());
        ecB.setPreferredSize(ecR.getPreferredSize());
        ecPurviewAndButton.setPreferredSize(new Dimension(-1, 35));
        lbR.setPreferredSize(new Dimension(22, -1));
        lbG.setPreferredSize(lbR.getPreferredSize());
        lbB.setPreferredSize(lbR.getPreferredSize());
        lbRT.setFont(LABEL_FONT);
        lbGT.setFont(LABEL_FONT);
        lbBT.setFont(LABEL_FONT);
        lbR.setFont(LABEL_FONT);
        lbG.setFont(LABEL_FONT);
        lbB.setFont(LABEL_FONT);
        lbPurview.setBorder(new LineBorder(Color.BLACK));
        lbPurview.setBackgroundAlpha(1.0f);
        btnOk.setFocusable(false);
        btnOk.setPreferredSize(new Dimension(50, 20));
        btnCancel.setPreferredSize(btnOk.getPreferredSize());
        btnCancel.setFocusable(false);
        sliderR.addChangeListener(this);
        sliderG.addChangeListener(this);
        sliderB.addChangeListener(this);
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);
        
        ecR.add(lbRT, LineLayout.START_FILL);
        ecR.add(sliderR, LineLayout.MIDDLE_FILL);
        ecR.add(lbR, LineLayout.END_FILL);
        ecG.add(lbGT, LineLayout.START_FILL);
        ecG.add(sliderG, LineLayout.MIDDLE_FILL);
        ecG.add(lbG, LineLayout.END_FILL);
        ecB.add(lbBT, LineLayout.START_FILL);
        ecB.add(sliderB, LineLayout.MIDDLE_FILL);
        ecB.add(lbB, LineLayout.END_FILL);
        ecPurviewAndButton.add(lbPurview, LineLayout.MIDDLE_FILL);
        ecPurviewAndButton.add(ecButton, LineLayout.END);
        ecButton.add(btnOk, LineLayout.END_FILL);
        ecButton.add(btnCancel, LineLayout.END_FILL);
        mainPane.add(ecR, LineLayout.START_FILL);
        mainPane.add(ecG, LineLayout.START_FILL);
        mainPane.add(ecB, LineLayout.START_FILL);
        mainPane.add(ecPurviewAndButton, LineLayout.END_FILL);
        this.add(mainPane, BorderLayout.CENTER);
    }
    
    private void resetSlider()
    {
        sliderR.setValue(r);
        sliderG.setValue(g);
        sliderB.setValue(b);
    }
    
    private void resetLabel()
    {
        lbR.setText(r + "");
        lbG.setText(g + "");
        lbB.setText(b + "");
        lbPurview.setBackground(currentColor);
    }
    
    public void setColor(Color color, boolean display)
    {
        this.oldColor = this.currentColor = color;
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.canceled = true;
        
        if(display)
        {
            resetSlider();
            resetLabel();
        }
    }
    
    public Color getColor()
    {
        return canceled? oldColor: currentColor;
    }
    
    public Color getCurrentColor()
    {
        return currentColor;
    }

    public boolean isCanceled()
    {
        return canceled;
    }
    
    public void setVisible(boolean visible)
    {
        if(visible)
        {
            setColor(oldColor, true);
        }
        
        super.setVisible(visible);
    }

    public void actionPerformed(ActionEvent e)
    {
        canceled = e.getSource() != btnOk;
        setVisible(false);
    }

    public void stateChanged(ChangeEvent e)
    {
        Object source = e.getSource();
        
        if(source == sliderR)
        {
            r = sliderR.getValue();
        }
        else if(source == sliderG)
        {
            g = sliderG.getValue();
        }
        else if(source == sliderB)
        {
            b = sliderB.getValue();
        }
        
        currentColor = new Color(r, g, b);
        resetLabel();
        
        if(changedAction != null)
        {
            changedAction.actionPerformed(new ActionEvent(this, 0, null));
        }
    }
}