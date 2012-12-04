package craky.keeper.skin;

import java.awt.Image;

import javax.swing.ImageIcon;

import craky.keeper.KeeperConst;
import craky.keeper.util.KeeperUtil;

public class Skin
{
    private String name;
    
    private boolean selected;
    
    private Boolean isDefault;
    
    private SkinUnit unit;
    
    public Skin(String name)
    {
        this.name = name;
    }
    
    public Image getImage()
    {
        return isDefault()? null: new ImageIcon(KeeperConst.SKIN_DIR + KeeperConst.FILE_SEP + name +
                        KeeperConst.FILE_SEP + KeeperConst.SKIN_NORMAL_FILE_NAME).getImage();
    }

    public Image getBlurImage()
    {
        return isDefault()? null: new ImageIcon(KeeperConst.SKIN_DIR + KeeperConst.FILE_SEP + name +
                        KeeperConst.FILE_SEP + KeeperConst.SKIN_BLUR_FILE_NAME).getImage();
    }

    public Image getPreviewImage()
    {
        return isDefault()? KeeperUtil.getImage("default_skin_preview.png"): new ImageIcon(KeeperConst.SKIN_DIR +
                        KeeperConst.FILE_SEP + name + KeeperConst.FILE_SEP + KeeperConst.SKIN_PREVIEW_FILE_NAME).getImage();
    }

    public String getName()
    {
        return name;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
        unit.repaint();
    }

    public SkinUnit getUnit()
    {
        return unit;
    }

    public void setUnit(SkinUnit unit)
    {
        this.unit = unit;
    }
    
    public boolean isDefault()
    {
        if(isDefault == null)
        {
            isDefault = name.equals(KeeperConst.DEFAULT_SKIN_NAME);
        }
        
        return isDefault;
    }
    
    public String toString()
    {
        return name;
    }
}