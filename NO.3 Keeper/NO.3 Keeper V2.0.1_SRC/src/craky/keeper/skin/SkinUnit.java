package craky.keeper.skin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import craky.componentc.JCButton;
import craky.keeper.EmptyComponent;
import craky.keeper.KeeperApp;
import craky.keeper.util.KeeperUtil;

public class SkinUnit extends JComponent
{
    private static final long serialVersionUID = 4145544490659464008L;
    
    private static final Color SHADOW_COLOR = new Color(255, 255, 255, 60);
    
    private static final Color SELECTED_COLOR = new Color(255, 255, 255);
    
    private KeeperApp keeper;
    
    private SkinMgr skinMgr;
    
    private Skin skin;
    
    private JCButton btnAdd, btnRemove;
    
    private EmptyComponent ec;
    
    private boolean first, last, mouseIn;
    
    public SkinUnit(KeeperApp keeper, SkinMgr skinMgr)
    {
        this.keeper = keeper;
        this.skinMgr = skinMgr;
        btnAdd = new JCButton();
        btnRemove = new JCButton();
        ec = new EmptyComponent();
        ActionListener action = new ActionHandle();
        MouseListener mouseHandle = new MouseHandle();
        this.init();
        this.addMouseListener(mouseHandle);
        btnAdd.addActionListener(action);
        btnAdd.addMouseListener(mouseHandle);
        btnRemove.addActionListener(action);
        btnRemove.addMouseListener(mouseHandle);
    }
    
    private void init()
    {
        this.setLayout(new BorderLayout());
        ec.setLayout(new BorderLayout());
        ec.setPreferredSize(new Dimension(-1, 13));
        ec.setVisible(false);
        btnAdd.setToolTipText("\u8BF7\u70B9\u51FB\u9009\u62E9\u4E00\u5F20\u56FE\u7247\uFF0C\u751F" +
        		"\u6210\u5C5E\u4E8E\u60A8\u81EA\u5DF1\u98CE\u683C\u7684\u76AE\u80A4\u3002");
        btnAdd.setFocusable(false);
        btnAdd.setImage(KeeperUtil.getImage("skin_add_normal.png", true));
        btnAdd.setRolloverImage(KeeperUtil.getImage("skin_add_rollover.png", true));
        btnAdd.setPressedImage(btnAdd.getRolloverImage());
        btnAdd.setVisible(false);
        btnRemove.setToolTipText("\u5220\u9664");
        btnRemove.setPreferredSize(new Dimension(13, -1));
        btnRemove.setRolloverEnabled(false);
        btnRemove.setFocusable(false);
        btnRemove.setImage(KeeperUtil.getImage("skin_delete.png", true));
        btnRemove.setPressedImage(null);
        ec.add(btnRemove, BorderLayout.EAST);
        this.add(btnAdd, BorderLayout.CENTER);
        this.add(ec, BorderLayout.NORTH);
    }

    protected void paintComponent(Graphics g)
    {
        if(skin != null)
        {
            int width = this.getWidth();
            int height = this.getHeight();
            Image image = skin.getPreviewImage();
            g.drawImage(image, 0, 0, width, height, this);
            image = null;
            
            if(mouseIn)
            {
                g.setColor(SHADOW_COLOR);
                g.fillRect(0, 0, width, height);
            }
            
            if(skin.isSelected())
            {
                g.setColor(SELECTED_COLOR);
                g.drawRect(0, 0, width - 1, height - 1);
            }
        }
    }

    public Skin getSkin()
    {
        return skin;
    }

    public void setSkin(Skin skin)
    {
        this.skin = skin;
        this.setToolTipText(skin != null? skin.getName(): null);
        btnAdd.setVisible(last && skin == null);
        this.repaint();
        
        if(skin != null)
        {
            skin.setUnit(this);
            this.setFirst(skin.isDefault());
        }
    }
    
    public boolean isFirst()
    {
        return first;
    }

    public void setFirst(boolean first)
    {
        this.first = first;
    }
    
    public boolean isLast()
    {
        return last;
    }

    public void setLast(boolean last)
    {
        this.last = last;
        btnAdd.setVisible(last && skin == null);
    }
    
    private class ActionHandle implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            
            if(source == btnAdd)
            {
                KeeperUtil.switchToSystemLAFTemp(new AbstractAction()
                {
                    private static final long serialVersionUID = 5648508246819086152L;

                    public void actionPerformed(ActionEvent e)
                    {
                        skinMgr.addSkin();
                    }
                });
            }
            else if(source == btnRemove)
            {
                skinMgr.removeSkin(skin, true);
            }
        }
    }
    
    private class MouseHandle extends MouseAdapter
    {
        public void mouseEntered(MouseEvent e)
        {
            Object source = e.getSource();
            mouseIn = true;
            ec.setVisible(skin != null && !first);
            
            if(skin != null)
            {
                repaint();
            }
            
            if(source == SkinUnit.this || source == btnAdd)
            {
                keeper.changeSkin(skin == null? keeper.getCurrentSkin(): skin, false);
            }
        }

        public void mouseExited(MouseEvent e)
        {
            Object source = e.getSource();
            mouseIn = false;
            ec.setVisible(false);
            
            if(skin != null)
            {
                repaint();
                
                if((source == SkinUnit.this || source == btnRemove) && skinMgr.needChange(e.getPoint(), (Container)source))
                {
                    keeper.changeSkin(keeper.getCurrentSkin(), false);
                }
            }
        }
        
        public void mousePressed(MouseEvent e)
        {
            if(skin != null && e.getSource() == SkinUnit.this && SwingUtilities.isLeftMouseButton(e))
            {
                keeper.changeSkin(skin, true);
            }
        }
    }
}