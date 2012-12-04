package craky.keeper.skin;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.awt.AWTUtilities;

import craky.componentc.CRootPaneUI;
import craky.componentc.CRootPaneUI.ImageDisplayMode;
import craky.componentc.JCDialog;
import craky.componentc.JCFrame;
import craky.componentc.JCMessageBox;
import craky.componentc.JCSlider;
import craky.componentc.JCToggleButton;
import craky.keeper.KeeperApp;
import craky.keeper.KeeperConst;
import craky.keeper.util.KeeperUtil;
import craky.util.UIUtil;

public class SkinMgr implements MouseListener, ChangeListener, ActionListener, PopupMenuListener
{
    private KeeperApp keeper;
    
    private SkinMenu menu;
    
    private File currentDir;
    
    public SkinMgr(KeeperApp keeper, SkinMenu menu)
    {
        this.keeper = keeper;
        this.menu = menu;
    }
    
    public void removeSkin(Skin skin, boolean showConfirm)
    {
        if(skin != null)
        {
            if(showConfirm)
            {
                menu.showPane(false, skin);
            }
            else
            {
                String name = skin.getName();
                String current = keeper.getCurrentSkin().getName();
                keeper.getAllSkins().remove(name.toUpperCase());
                menu.resetSkins();
                keeper.changeSkin(keeper.getSkin(current), true);
                KeeperUtil.deleteDirectory(new File(KeeperConst.SKIN_DIR + KeeperConst.FILE_SEP + name));
            }
        }
    }
    
    public void addSkin()
    {
        menu.getModel().setSelected(false);
        menu.setPopupMenuVisible(false);
        JFileChooser chooser = new JFileChooser();
        String filterDes = "\u56FE\u50CF\u6587\u4EF6(*.bmp;*.jpg;*.jpeg;*.png)";
        FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDes, "bmp", "jpg", "jpeg", "png");
        chooser.setApproveButtonMnemonic('O');
        chooser.setApproveButtonText("\u6253\u5F00(O)");
        chooser.setCurrentDirectory(currentDir);
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        int ret = chooser.showOpenDialog(keeper.getMainFrame());

        if(ret == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            currentDir = file.getParentFile();
            String name = file.getName();
            int lastDotIndex = name.lastIndexOf('.');
            name = lastDotIndex > 0? name.substring(0, lastDotIndex): name;
            
            if(keeper.getSkin(name) != null)
            {
                String title = "\u6DFB\u52A0\u76AE\u80A4";
                String message = "\u76AE\u80A4\u201C" + name + "\u201D\u5DF2\u7ECF\u5B58\u5728\uFF0C\u662F\u5426\u66FF\u6362\uFF1F";
                JCMessageBox confirm = JCMessageBox.createQuestionMessageBox(keeper.getMainFrame(), title, message);
                
                if(confirm.open() != JCMessageBox.YES_OPTION)
                {
                    return;
                }
            }
            
            try
            {
                BufferedImage image = ImageIO.read(file);
                BufferedImage blurImage = UIUtil.createEdgeBlurryImage(image, CRootPaneUI.BLUR_SIZE, CRootPaneUI.BLUR_FILTER, null);
                BufferedImage previewImage = createPreviewImage(image);
                Skin skin = new Skin(name);
                String skinDirPath = KeeperConst.SKIN_DIR + KeeperConst.FILE_SEP + name;
                File skinDir = new File(skinDirPath);
                
                if(!skinDir.exists())
                {
                    skinDir.mkdirs();
                }
                
                ImageIO.write(image, KeeperConst.PNG, new File(skinDirPath + KeeperConst.FILE_SEP + KeeperConst.SKIN_NORMAL_FILE_NAME));
                ImageIO.write(blurImage, KeeperConst.PNG, new File(skinDirPath + KeeperConst.FILE_SEP + KeeperConst.SKIN_BLUR_FILE_NAME));
                ImageIO.write(previewImage, KeeperConst.PNG, new File(skinDirPath + KeeperConst.FILE_SEP + KeeperConst.SKIN_PREVIEW_FILE_NAME));
                keeper.getAllSkins().put(skin.getName().toUpperCase(), skin);
                menu.resetSkins();
                keeper.changeSkin(skin, true);
                image = null;
                blurImage = null;
                previewImage = null;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private BufferedImage createPreviewImage(BufferedImage image)
    {
        BufferedImage previewImage = UIUtil.getGraphicsConfiguration(null).createCompatibleImage(30, 41, Transparency.TRANSLUCENT);
        Graphics2D g2d = previewImage.createGraphics();
        g2d.drawImage(image, 0, 0, previewImage.getWidth(), previewImage.getHeight(), null);
        g2d.dispose();
        return previewImage;
    }
    
    public boolean needChange(Point point, Container source)
    {
        int x = point.x + source.getX();
        int y = point.y + source.getY();
        
        while(!(source instanceof SkinUnit))
        {
            source = source.getParent();
            x += source.getX();
            y += source.getY();
        }
        
        return !menu.getSkinListBounds().contains(x, y);
    }
    
    public void mouseReleased(MouseEvent e)
    {
        Object source = e.getSource();

        if(source instanceof JComponent)
        {
            JComponent c = (JComponent)source;
            Object eventKey = c.getClientProperty(KeeperConst.EVENT_KEY);
            
            if(eventKey.equals(KeeperConst.SKIN_ALPHA_CHANGED))
            {
                keeper.getConfig().savePropertie(KeeperConst.SKIN_ALPHA, String.valueOf(keeper.getMainFrame().getImageAlpha()));
            }
            else if(eventKey.equals(KeeperConst.WINDOW_ALPHA_CHANGED) && UIUtil.isTranslucencySupported())
            {
                keeper.getConfig().savePropertie(KeeperConst.WINDOW_ALPHA, String.valueOf(AWTUtilities.getWindowOpacity(keeper.getMainFrame())));
            }
        }
    }

    public void mouseEntered(MouseEvent e)
    {
        Object source = e.getSource();
        
        if(source instanceof JComponent)
        {
            JComponent c = (JComponent)source;
            Object eventKey = c.getClientProperty(KeeperConst.EVENT_KEY);
            
            if(eventKey.equals(KeeperConst.SKIN_MODE_PREVIEW))
            {
                menu.modePreview((Image)c.getClientProperty(KeeperConst.BIG_IMAGE_KEY), false);
            }
        }
    }

    public void mouseExited(MouseEvent e)
    {
        Object source = e.getSource();
        
        if(source instanceof JComponent)
        {
            JComponent c = (JComponent)source;
            Object eventKey = c.getClientProperty(KeeperConst.EVENT_KEY);
            
            if(eventKey.equals(KeeperConst.SKIN_MODE_PREVIEW))
            {
                menu.modePreview((Image)c.getClientProperty(KeeperConst.BIG_IMAGE_KEY), true);
            }
        }
    }

    public void stateChanged(ChangeEvent e)
    {
        Object source = e.getSource();

        if(source instanceof JComponent)
        {
            JComponent c = (JComponent)source;
            Object eventKey = c.getClientProperty(KeeperConst.EVENT_KEY);
            
            if(eventKey.equals(KeeperConst.SKIN_ALPHA_CHANGED))
            {
                float alpha = ((JCSlider)c).getValue() / 100.0f;
                
                for(Window win: Window.getWindows())
                {
                    if(win.isDisplayable())
                    {
                        if(win instanceof JCFrame)
                        {
                            ((JCFrame)win).setImageAlpha(alpha);
                        }
                        else if(win instanceof JCDialog)
                        {
                            ((JCDialog)win).setImageAlpha(alpha);
                        }
                    }
                }
            }
            else if(eventKey.equals(KeeperConst.WINDOW_ALPHA_CHANGED) && UIUtil.isTranslucencySupported())
            {
                float alpha = ((JCSlider)c).getValue() / 100.0f;
                
                for(Window win: Window.getWindows())
                {
                    if(win.isDisplayable() && (win instanceof JFrame || win instanceof JDialog))
                    {
                        AWTUtilities.setWindowOpacity(win, alpha);
                    }
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        
        if(source instanceof JComponent)
        {
            JComponent c = (JComponent)source;
            Object eventKey = c.getClientProperty(KeeperConst.EVENT_KEY);
            
            if(eventKey.equals(KeeperConst.SKIN_MODE_PREVIEW) && ((JCToggleButton)c).isSelected())
            {
                keeper.getMainFrame().setImageDisplayMode((ImageDisplayMode)c.getClientProperty(KeeperConst.SKIN_MODE_VALUE));
                keeper.getConfig().savePropertie(KeeperConst.SKIN_MODE, String.valueOf(keeper.getMainFrame().getImageDisplayMode()));
            }
            else if(eventKey.equals(KeeperConst.DELETE_SKIN_OK))
            {
                removeSkin(((DeleteConfirmPane)c.getParent()).getSkin(), false);
                menu.showPane(true, null);
            }
            else if(eventKey.equals(KeeperConst.DELETE_SKIN_CANCEL))
            {
                menu.showPane(true, null);
            }
        }
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent e)
    {
        menu.showPane(true, null);
    }
    
    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

    public void popupMenuCanceled(PopupMenuEvent e) {}
}