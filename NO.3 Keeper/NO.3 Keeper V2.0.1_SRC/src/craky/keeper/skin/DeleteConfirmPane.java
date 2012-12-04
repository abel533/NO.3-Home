package craky.keeper.skin;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import craky.component.JImagePane;
import craky.componentc.JCButton;
import craky.keeper.KeeperConst;
import craky.keeper.util.KeeperUtil;
import craky.util.UIUtil;

public class DeleteConfirmPane extends JImagePane
{
    private static final long serialVersionUID = 8218959983027142762L;
    
    private static final Image BG_IMAGE = KeeperUtil.getImage("skin_list_bg.png");
    
    private AlphaComposite COMPOSITE = AlphaComposite.SrcOver.derive(0.75f);
    
    private SkinMgr skinMgr;
    
    private JLabel lbInfo;
    
    private JCButton btnOk, btnCancel;
    
    private Skin skin;

    public DeleteConfirmPane(SkinMgr skinMgr)
    {
        this.skinMgr = skinMgr;
        lbInfo = new JLabel("\u60A8\u786E\u5B9A\u8981\u5220\u9664\u8FD9\u4E2A\u76AE\u80A4\u5417\uFF1F");
        btnOk = new JCButton();
        btnCancel = new JCButton();
        initUI();
    }
    
    private void initUI()
    {
        setImageOnly(true);
        setLayout(null);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        lbInfo.setBounds(22, 17, 200, 15);
        lbInfo.setOpaque(false);
        lbInfo.setForeground(new Color(253, 253, 253));
        lbInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
        lbInfo.setFont(new Font("\u5B8B\u4F53", Font.PLAIN, 12));
        lbInfo.setHorizontalAlignment(JLabel.LEFT);
        lbInfo.setVerticalAlignment(JLabel.TOP);
        btnOk.setBounds(81, 61, 65, 21);
        btnOk.setFocusable(false);
        btnOk.putClientProperty(KeeperConst.EVENT_KEY, KeeperConst.DELETE_SKIN_OK);
        btnOk.setImage(KeeperUtil.getImage("skin_del_ok.png", true));
        btnOk.setRolloverImage(KeeperUtil.getImage("skin_del_ok_rollover.png", true));
        btnOk.setPressedImage(btnOk.getRolloverImage());
        btnOk.addActionListener(skinMgr);
        btnCancel.setBounds(158, 61, 65, 21);
        btnCancel.setFocusable(false);
        btnCancel.putClientProperty(KeeperConst.EVENT_KEY, KeeperConst.DELETE_SKIN_CANCEL);
        btnCancel.setImage(KeeperUtil.getImage("skin_del_cancel.png", true));
        btnCancel.setRolloverImage(KeeperUtil.getImage("skin_del_cancel_rollover.png", true));
        btnCancel.setPressedImage(btnCancel.getRolloverImage());
        btnCancel.addActionListener(skinMgr);
        add(lbInfo);
        add(btnOk);
        add(btnCancel);
    }
    
    public void updateBackground(Skin skin, JComponent sameAs)
    {
        this.skin = skin;
        BufferedImage image = UIUtil.getGraphicsConfiguration(this).createCompatibleImage(sameAs.getWidth(),
                        sameAs.getHeight(), Transparency.TRANSLUCENT);
        Graphics2D imageG = image.createGraphics();
        sameAs.paint(imageG);
        imageG.setComposite(COMPOSITE);
        imageG.drawImage(BG_IMAGE, 2, 2, null);
        imageG.dispose();
        setImage(image);
    }

    public Skin getSkin()
    {
        return skin;
    }
}