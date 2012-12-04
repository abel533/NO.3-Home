package craky.keeper.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import craky.keeper.KeeperConst;
import craky.util.SwingResourceManager;
import craky.util.Util;

public class KeeperUtil
{
    public static Image getImage(String name)
    {
        return SwingResourceManager.getImage(KeeperConst.IMAGE_DIR + KeeperConst.FILE_SEP + name);
    }
    
    public static Image getImage(String name, boolean createdByIcon)
    {
        if(createdByIcon)
        {
            return new ImageIcon(KeeperConst.IMAGE_DIR + KeeperConst.FILE_SEP + name).getImage();
        }
        else
        {
            return getImage(name);
        }
    }
    
    public static Icon getIcon(String name)
    {
        return SwingResourceManager.getIcon(KeeperConst.IMAGE_DIR + KeeperConst.FILE_SEP + name);
    }
    
    public static Icon getIcon(String name, boolean createdByIcon)
    {
        if(createdByIcon)
        {
            return new ImageIcon(KeeperConst.IMAGE_DIR + KeeperConst.FILE_SEP + name);
        }
        else
        {
            return getIcon(name);
        }
    }
    
    public static void deleteDirectory(File directory)
    {
        if(directory != null && directory.exists() && directory.isDirectory())
        {
            for(File file: directory.listFiles())
            {
                if(file.isDirectory())
                {
                    deleteDirectory(file);
                }
                else
                {
                    file.delete();
                }
            }
        }
        
        directory.delete();
    }
    
    public static Dimension stringToSize(String sizeDes)
    {
        String[] weights = sizeDes.split(",");
        Dimension size = new Dimension();
        
        if(weights.length == 2)
        {
            try
            {
                size.setSize(Integer.parseInt(weights[0]), Integer.parseInt(weights[1]));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return size;
    }
    
    public static String sizeToString(Dimension size)
    {
        return size.width + "," + size.height;
    }
    
    public static Color stringToColor(String colorDes)
    {
        String[] weights = colorDes.split(",");
        Color color = null;
        
        if(weights.length == 3)
        {
            try
            {
                color = new Color(Integer.parseInt(weights[0]), Integer.parseInt(weights[1]), Integer.parseInt(weights[2]));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return color;
    }
    
    public static String colorToString(Color color)
    {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
    
    public static void switchToSystemLAFTemp(Action action)
    {
        if(!Util.isWindows())
        {
            action.actionPerformed(null);
            return;
        }
        
        try
        {
            String sysLafclassName = UIManager.getSystemLookAndFeelClassName();
            String currentLafClassName = UIManager.getLookAndFeel().getClass().getName();

            if(!sysLafclassName.equals(currentLafClassName))
            {
                UIDefaults uid = UIManager.getLookAndFeelDefaults();
                HashMap<Object, Object> oldDefaultsMap = new HashMap<Object, Object>();
                Class<?> clazz = Class.forName(sysLafclassName);
                LookAndFeel sysLaf = (LookAndFeel)clazz.newInstance();
                oldDefaultsMap.putAll(uid);
                uid.clear();
                uid.putAll(sysLaf.getDefaults());
                action.actionPerformed(null);
                uid.clear();
                uid.putAll(oldDefaultsMap);
                oldDefaultsMap.clear();
                oldDefaultsMap = null;
            }
            else
            {
                action.actionPerformed(null);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}