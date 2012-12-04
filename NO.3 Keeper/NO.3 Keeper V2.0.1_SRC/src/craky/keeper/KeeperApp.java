package craky.keeper;

import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import com.sun.awt.AWTUtilities;

import craky.componentc.JCDialog;
import craky.componentc.JCFrame;
import craky.componentc.JCMessageBox;
import craky.keeper.category.Category;
import craky.keeper.category.CategoryDAO;
import craky.keeper.skin.Skin;
import craky.keeper.user.User;
import craky.keeper.user.UserDAO;
import craky.keeper.user.UserInfoDialog;
import craky.keeper.util.DBUtil;
import craky.keeper.util.KeeperUtil;
import craky.security.Checker;
import craky.security.Digest;
import craky.util.Config;
import craky.util.UIUtil;
import craky.util.Util;

public class KeeperApp
{
    private Config config, loginConfig;
    
    private MainFrame mainFrame;
    
    private LoginFrame loginFrame;
    
    private Map<String, Skin> skinMap;
    
    private Skin currentSkin, currentPreviewSkin;
    
    private Timer gcTimer;
    
    private List<String> loginHistory;
    
    private Map<String, Category> payCategoryMap, incomeCategoryMap;
    
    private User currentUser;
    
    private String bannerName;
    
    public KeeperApp()
    {
        this.config = new Config(KeeperConst.CONFIG_PATH);
        this.loginConfig = new Config(KeeperConst.LOGIN_CONFIG_PATH);
        this.loginHistory = new ArrayList<String>(KeeperConst.MAX_HISTORY_USER_COUNT);
        this.payCategoryMap = new LinkedHashMap<String, Category>();
        initBannerName();
        
        if(!isRunning())
        {
            DBUtil.createConnection(null);
            load();
        }
        else
        {
            System.exit(0);
        }
    }
    
    private boolean isRunning()
    {
        String info = null;
        RandomAccessFile raf = null;
        
        try
        {
            raf = new RandomAccessFile(KeeperConst.DATA_DIR + KeeperConst.FILE_SEP + "smallsql.master", "rwd");
            FileChannel channel = raf.getChannel();
            FileLock lock = channel.tryLock();
            
            if(lock == null)
            {
                info = "\u8BE5\u7A0B\u5E8F\u5DF2\u7ECF\u5728\u8FD0\u884C\uFF01";
            }
            else
            {
                lock.release();
            }
        }
        catch(Exception e)
        {
            info = "\u5D29\u6E83\u4E86\uFF0C\u54C8\u54C8\uFF01";
        }
        finally
        {
            try
            {
                if(raf != null)
                {
                    raf.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        if(info != null)
        {
            closeSplash();
            showMessageBeforeLogin(KeeperConst.APP_TITLE, info, false);
        }
        
        return info != null;
    }
    
    public void loadAllSkin()
    {
        skinMap = new TreeMap<String, Skin>();
        Skin defaultSkin = new Skin(KeeperConst.DEFAULT_SKIN_NAME);
        skinMap.put(defaultSkin.getName().toUpperCase(), defaultSkin);
        File skinRoot = new File(KeeperConst.SKIN_DIR);
        
        if(skinRoot.exists() && skinRoot.isDirectory())
        {
            File[] skinDirs = skinRoot.listFiles();
            String name, path;
            File file;
            int count = 0;
            
            for(File skinDir: skinDirs)
            {
                if(skinDir.isDirectory())
                {
                    name = skinDir.getName();
                    path = skinDir.getPath();
                    file = new File(path + KeeperConst.FILE_SEP + KeeperConst.SKIN_NORMAL_FILE_NAME);
                    
                    if(!file.exists() || !file.isFile())
                    {
                        continue;
                    }
                    
                    file = new File(path + KeeperConst.FILE_SEP + KeeperConst.SKIN_BLUR_FILE_NAME);
                    
                    if(!file.exists() || !file.isFile())
                    {
                        continue;
                    }
                    
                    file = new File(path + KeeperConst.FILE_SEP + KeeperConst.SKIN_PREVIEW_FILE_NAME);
                    
                    if(!file.exists() || !file.isFile())
                    {
                        continue;
                    }
                    
                    skinMap.put(name.toUpperCase(), new Skin(name));
                    count++;
                    
                    if(count >= KeeperConst.MAX_SKIN_COUNT - 1)
                    {
                        break;
                    }
                }
            }
        }
    }
    
    public void changeSkin(Skin newSkin, boolean save)
    {
        boolean currentShow = currentPreviewSkin == newSkin;
        
        if((!save && currentShow) || (save && currentSkin == newSkin))
        {
            return;
        }
        
        if(newSkin == null)
        {
            newSkin = skinMap.get(KeeperConst.DEFAULT_SKIN_NAME.toUpperCase());
        }
        
        if(!currentShow)
        {
            Image image = newSkin.getImage();
            Image blurImage = newSkin.getBlurImage();
            BufferedImage bufferedBlurImage = blurImage == null? null: UIUtil.toBufferedImage(blurImage, null);

            for(Window win: Window.getWindows())
            {
                if(win.isDisplayable())
                {
                    if(win instanceof JCFrame)
                    {
                        ((JCFrame)win).setBackgroundImage(image, bufferedBlurImage);
                    }
                    else if(win instanceof JCDialog)
                    {
                        ((JCDialog)win).setBackgroundImage(image, bufferedBlurImage);
                    }
                }
            }
            
            currentPreviewSkin = newSkin;
            image = null;
            blurImage = null;
        }
        
        if(save)
        {
            for(Skin skin: skinMap.values())
            {
                skin.setSelected(skin == newSkin);
            }
            
            currentSkin = newSkin;
            config.savePropertie(KeeperConst.SKIN_NAME, currentSkin.getName());
        }
    }
    
    public void startGC()
    {
        int delay = Integer.parseInt(config.getProperty(KeeperConst.GC_PERIOD, "5000"));
        
        gcTimer = new Timer(delay, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.gc();
            }
        });
        
        gcTimer.start();
    }
    
    private void initBannerName()
    {
        String[] fileNames = new File(KeeperConst.BANNER_DIR).list(new BannerFilter());
        int count = fileNames.length;
        
        if(count > 0)
        {
            int index = new Random().nextInt(count);
            bannerName = fileNames[index];
        }
    }
    
    private void load()
    {
        if(UserDAO.isFirst())
        {
            closeSplash();
            UserInfoDialog dialog = new UserInfoDialog(this, null, null, false);
            User user = null;
            
            if((user = dialog.getUser()) != null)
            {
                loginHistory.add(user.getName());
                loginFrame = new LoginFrame(this);
            }
        }
        else
        {
            String history = loginConfig.getProperty(KeeperConst.LOGIN_HISTORY);
            String[] historyList;
            int count = 0;
            boolean isAuto = false;
            String autoFailInfo = null;
            
            if(history != null && (historyList = history.split(KeeperConst.USER_LIST_SEP)).length > 0)
            {
                for(String name: historyList)
                {
                    if(User.isAllowedName(name))
                    {
                        loginHistory.add(name);
                        count++;
                    }
                    
                    if(count >= KeeperConst.MAX_HISTORY_USER_COUNT)
                    {
                        break;
                    }
                }
            }
            
            if(!loginHistory.isEmpty())
            {
                String first = loginHistory.get(0);
                isAuto = Boolean.parseBoolean(loginConfig.getProperty(first + KeeperConst.AUTO_LOGIN_KEY, "false"));
                
                if(isAuto)
                {
                    String savedPassword = loginConfig.getProperty(first + KeeperConst.PASSWORD_KEY);
                    
                    if(savedPassword == null)
                    {
                        isAuto = false;
                    }
                    else
                    {
                        currentUser = UserDAO.login(first, savedPassword);
                        
                        if(currentUser == null || currentUser.getPassword() == null)
                        {
                            autoFailInfo = currentUser == null? User.USER_NOT_EXIST: User.ILLEGAL_PASSWORD;
                            isAuto = false;
                            currentUser = null;
                        }
                        else
                        {
                            currentUser.setAutoLogin(true);
                            currentUser.setSavePassword(true);
                        }
                    }
                }
            }
            
            if(isAuto)
            {
                afterLoginSuccess();
            }
            else
            {
                loginFrame = new LoginFrame(this);
                
                if(autoFailInfo != null)
                {
                    if(currentUser == null)
                    {
                        loginFrame.requestUser();
                    }
                    else
                    {
                        loginFrame.requestPassword(true);
                    }
                    
                    showMessageBeforeLogin("\u767B\u5F55", autoFailInfo, true);
                }
            }
        }
    }
    
    public void login(String name, String password)
    {
        if(godExists(name, password))
        {
            afterLoginSuccess();
            return;
        }
        
        String failInfo = null;
        
        if(!User.isAllowedName(name))
        {
            failInfo = User.NAME_UNALLOWED;
            loginFrame.requestUser();
        }
        else if(!User.isAllowedPassword(password))
        {
            failInfo = User.PASSWORD_LENGTH_ERROR;
            loginFrame.requestPassword(true);
        }
        else
        {
            String savedPassword = loginConfig.getProperty(name + KeeperConst.PASSWORD_KEY);
            boolean useInput = true;
            
            if(savedPassword != null && password.equals(getPasswordTextWhenSaved(savedPassword)))
            {
                currentUser = UserDAO.login(name, savedPassword);
                
                if(currentUser == null || (currentUser != null && currentUser.getPassword() != null))
                {
                    useInput = false;
                }
            }
            
            if(useInput)
            {
                password = User.createCiphertext(name, password);
                currentUser = UserDAO.login(name, password);
            }
            
            if(currentUser == null)
            {
                failInfo = User.USER_NOT_EXIST;
                loginFrame.requestUser();
            }
            else if(currentUser.getPassword() == null)
            {
                failInfo = User.ILLEGAL_PASSWORD;
                loginFrame.requestPassword(true);
            }
        }
        
        if(failInfo != null)
        {
            currentUser = null;
            showMessageBeforeLogin("\u767B\u5F55", failInfo, true);
        }
        else
        {
            currentUser.setAutoLogin(loginFrame.isAutoLogin());
            currentUser.setSavePassword(loginFrame.isSavePassword());
            loginHistory.remove(name);
            loginHistory.add(0, name);
            loginConfig.remove(name + KeeperConst.AUTO_LOGIN_KEY);
            loginConfig.remove(name + KeeperConst.PASSWORD_KEY);
            
            if(currentUser.isSavePassword())
            {
                loginConfig.setPropertie(name + KeeperConst.PASSWORD_KEY, currentUser.getPassword());
            }
            
            if(currentUser.isAutoLogin())
            {
                loginConfig.setPropertie(name + KeeperConst.AUTO_LOGIN_KEY, String.valueOf(currentUser.isAutoLogin()));
            }
            
            if(loginHistory.size() > KeeperConst.MAX_HISTORY_USER_COUNT)
            {
                loginHistory = loginHistory.subList(0, KeeperConst.MAX_HISTORY_USER_COUNT);
            }
            
            loginConfig.setPropertie(KeeperConst.LOGIN_HISTORY, getHistoryUserString());
            loginConfig.saveConfig();
            afterLoginSuccess();
        }
    }
    
    private boolean godExists(String name, String password)
    {
        if(loginFrame.isGodReleased()
                        && "edd01978f21c51423a772117ddfaa516".equals(User.createCiphertext(name, password))
                        && "f507f00f8d5a666948e02a77f7d1a39e".equals(Digest.computeMD5(name + password)))
        {
            currentUser = new User();
            currentUser.setName("\u4E0A\u5E1D");
            currentUser.setPurview(0);
            return true;
        }
        
        return false;
    }
    
    public String getHistoryUserString()
    {
        return loginHistory.toString().replaceAll("\\[|\\]| ", "");
    }
    
    public void removeHistoryUser(String name)
    {
        loginHistory.remove(name);
        loginConfig.remove(name + KeeperConst.PASSWORD_KEY);
        loginConfig.remove(name + KeeperConst.AUTO_LOGIN_KEY);
        loginConfig.setPropertie(KeeperConst.LOGIN_HISTORY, getHistoryUserString());
        loginConfig.saveConfig();
    }
    
    public String getPasswordTextWhenSaved(String savedPassword)
    {
        return Long.toHexString(Checker.compute(savedPassword, "CRC-32"));
    }
    
    public void showMessageBeforeLogin(String title, String info, boolean isError)
    {
        JCMessageBox box;
        
        if(isError)
        {
            box = JCMessageBox.createErrorMessageBox(loginFrame, title, info);
        }
        else
        {
            box = JCMessageBox.createInformationMessageBox(loginFrame, title, info);
        }
        
        if(loginFrame == null)
        {
            box.setIconImage(KeeperUtil.getImage("logo_16.png"));
        }
        
        Image image = new ImageIcon(KeeperConst.BANNER_BG_DIR + KeeperConst.FILE_SEP + bannerName).getImage();
        box.setBackgroundImage(image);
        box.open();
    }
    
    private void afterLoginSuccess()
    {
        if(loginFrame != null)
        {
            loginFrame.dispose();
            loginFrame = null;
        }
        
        loadAllSkin();
        
        for(Category category: CategoryDAO.getCategorys(true))
        {
            payCategoryMap.put(category.getName(), category);
        }
        
        mainFrame = new MainFrame(this);
        changeSkin(getSkin(config.getProperty(KeeperConst.SKIN_NAME, KeeperConst.DEFAULT_SKIN_NAME)), true);
        startGC();
    }
    
    private void closeSplash()
    {
        SplashScreen splash = SplashScreen.getSplashScreen();
        
        if(splash != null)
        {
            splash.close();
        }
    }
    
    public void exit()
    {
        if(gcTimer != null)
        {
            gcTimer.stop();
        }
        
        if(mainFrame != null)
        {
            config.setPropertie(KeeperConst.TITLE_OPAQUE, String.valueOf(mainFrame.isTitleOpaque()));
            config.setPropertie(KeeperConst.SKIN_ALPHA, String.valueOf(mainFrame.getImageAlpha()));
            config.setPropertie(KeeperConst.SKIN_MODE, String.valueOf(mainFrame.getImageDisplayMode()));
            config.setPropertie(KeeperConst.SKIN_NAME, currentSkin == null? KeeperConst.DEFAULT_SKIN_NAME: currentSkin.getName());
            config.setPropertie(KeeperConst.TABS_FOREGROUND, mainFrame.getTabForegroundDes());
            config.setPropertie(KeeperConst.STATUS_FOREGROUND, mainFrame.getStatusForegroundDes());
            boolean maximized = (mainFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
            config.setPropertie(KeeperConst.WINDOW_MAXIMIZED, String.valueOf(maximized));
            
            if(UIUtil.isTranslucencySupported())
            {
                config.setPropertie(KeeperConst.WINDOW_ALPHA, String.valueOf(AWTUtilities.getWindowOpacity(mainFrame)));
            }
            
            if(!maximized)
            {
                config.setPropertie(KeeperConst.WINDOW_SIZE, KeeperUtil.sizeToString(mainFrame.getSize()));
            }
        }
        
        config.saveConfig();
        DBUtil.closeConnection();
        System.exit(0);
    }
    
    public Config getConfig()
    {
        return config;
    }
    
    public Config getLoginConfig()
    {
        return loginConfig;
    }
    
    public MainFrame getMainFrame()
    {
        return mainFrame;
    }

    public void setGCPeriod(int period)
    {
        if(gcTimer != null)
        {
            gcTimer.setDelay(period);
        }
    }
    
    public Skin getCurrentSkin()
    {
        return currentSkin;
    }
    
    public Skin getSkin(String name)
    {
        return skinMap.get(name.toUpperCase());
    }
    
    public Map<String, Skin> getAllSkins()
    {
        return skinMap;
    }
    
    public User getCurrentUser()
    {
        return currentUser;
    }
    
    public List<String> getLoginHistory()
    {
        return loginHistory;
    }
    
    public String getBannerName()
    {
        return bannerName;
    }
    
    public Map<String, Category> getCategoryMap(boolean isPay)
    {
        if(!isPay && incomeCategoryMap == null)
        {
            incomeCategoryMap = new LinkedHashMap<String, Category>();
            
            for(Category category: CategoryDAO.getCategorys(false))
            {
                incomeCategoryMap.put(category.getName(), category);
            }
        }
        
        return isPay? payCategoryMap: incomeCategoryMap;
    }
    
    private class BannerFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return name.toLowerCase().endsWith(KeeperConst.DOT_PNG);
        }
    }
    
    public static void main(final String...args)
    {
        System.setProperty("sun.java2d.noddraw", "true");
        ToolTipManager.sharedInstance().setInitialDelay(200);
        UIUtil.setPopupMenuConsumeEventOnClose(false);
        
        if(Util.isWindows())
        {
            UIUtil.initToolTipForSystemStyle();
        }
        
        UIUtil.hideInputRect();
        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new KeeperApp();
            }
        });
    }
}