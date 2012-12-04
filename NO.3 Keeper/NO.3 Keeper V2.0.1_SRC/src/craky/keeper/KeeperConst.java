package craky.keeper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class KeeperConst
{
    public static final int MAX_SKIN_COUNT = 14;
    
    public static final int MAX_HISTORY_USER_COUNT = 10;
    
    public static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#,##0.00");
    
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
    
    public static final String FILE_SEP = System.getProperty("file.separator");
    
    public static final String USER_DIR = System.getProperty("user.dir");
    
    public static final String SKIN_DIR = USER_DIR + FILE_SEP + "skin";
    
    public static final String DATA_DIR = USER_DIR + FILE_SEP + "data";
    
    public static final String IMAGE_DIR = USER_DIR + FILE_SEP + "image";
    
    public static final String BANNER_DIR = IMAGE_DIR + FILE_SEP + "banner";
    
    public static final String BANNER_BG_DIR = IMAGE_DIR + FILE_SEP + "banner_bg";
    
    public static final String CONFIG_DIR = USER_DIR + FILE_SEP + "config";
    
    public static final String CONFIG_PATH = CONFIG_DIR + FILE_SEP + "config.properties";
    
    public static final String LOGIN_CONFIG_PATH = CONFIG_DIR + FILE_SEP + "login_config.properties";
    
    public static final String HIBERNATE_CONFIG_PATH = CONFIG_DIR + FILE_SEP + "hibernate.cfg.xml";
    
    public static final String USER_LIST_SEP = ",";
    
    public static final String EVENT_KEY = "EventKey";
    
    public static final String SKIN_MODE_PREVIEW = "SkinModePreview";
    
    public static final String SKIN_ALPHA_CHANGED = "SkinAlphaChanged";
    
    public static final String WINDOW_ALPHA_CHANGED = "WinAlphaChanged";
    
    public static final String SKIN_MODE_VALUE = "SkinModeValue";
    
    public static final String BIG_IMAGE_KEY = "BigImage";
    
    public static final String DELETE_SKIN_OK = "DelSkinOK";
    
    public static final String DELETE_SKIN_CANCEL = "DelSkinCancel";
    
    public static final String DEFAULT_SKIN_NAME = "\u9ED8\u8BA4\u76AE\u80A4";
    
    public static final String CMD_CANCEL_AUTO_LOGIN = "CancelAutoLogin";
    
    public static final String CMD_CANCEL_SAVED_PASSWORD = "CancelSavedPassword";
    
    public static final String CMD_SHOW_KEEPER_PANE = "ShowKeeperPane";
    
    public static final String CMD_RELOAD = "Reload";
    
    public static final String CMD_SETTING = "Setting";
    
    public static final String LINKED_COMPONENT = "LinkedComponent";
    
    public static final String PNG = "png";
    
    public static final String DOT_PNG = ".png";
    
    public static final String SKIN_NORMAL_FILE_NAME = "normal.png";
    
    public static final String SKIN_BLUR_FILE_NAME = "blur.png";
    
    public static final String SKIN_PREVIEW_FILE_NAME = "preview.png";
    
    public static final String APP_VERSION = "2.0.1";

    public static final String APP_NAME = "\u4E09\u53F7\u7BA1\u5BB6(NO.3 Keeper)";

    public static final String APP_TITLE = "\u4E09\u53F7\u7BA1\u5BB6";
    
    public static final String SKIN_NAME = "Skin_Name";
    
    public static final String SKIN_POPUP_X = "Skin_Popup_X";
    
    public static final String WINDOW_ALPHA = "Win_Alpha";
    
    public static final String TITLE_OPAQUE = "Title_Opaque";
    
    public static final String SHOW_DETAIL = "Show_Detail";
    
    public static final String SHOW_FIND = "Show_Find";
    
    public static final String SKIN_MODE = "Skin_Mode";
    
    public static final String SKIN_ALPHA = "Skin_Alpha";
    
    public static final String GC_PERIOD = "GC_Period";
    
    public static final String TABS_FOREGROUND = "Tabs_Foreground";
    
    public static final String STATUS_FOREGROUND = "Status_Foreground";
    
    public static final String LOGIN_HISTORY = "Login_History";
    
    public static final String WINDOW_SIZE = "Win_Size";
    
    public static final String WINDOW_MAXIMIZED = "Win_Maximized";
    
    public static final String AUTO_LOGIN_KEY = ".auto";
    
    public static final String PASSWORD_KEY = ".password";
}