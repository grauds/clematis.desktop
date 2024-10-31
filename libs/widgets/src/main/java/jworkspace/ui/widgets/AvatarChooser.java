package jworkspace.ui.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.hyperrealm.kiwi.ui.ButtonPanel;
import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

public class AvatarChooser extends KPanel implements ActionListener {

    private static final int MAX_PORTRAIT_WIDTH = 250;

    private static final int MAX_PORTRAIT_HEIGHT = 250;

    private final ResourceManager kresmgr = KiwiUtils.getResourceManager();

    private KButton addButton;

    private KButton deleteButton;

    private Avatar avatar;

    private final ImageIcon noIcon;

    public AvatarChooser(ImageIcon noIcon) {

        this.noIcon = noIcon;

        setLayout(new BorderLayout());

        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.addButton(getAddButton());
        buttonPanel.addButton(getDeleteButton());

        add(getAvatar(), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public KButton getAddButton() {
        if (addButton == null) {
            addButton = new KButton(new ImageIcon(kresmgr.getImage("plus.png")));
            addButton.addActionListener(this);
            addButton.setDefaultCapable(false);
        }
        return addButton;
    }

    public KButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new KButton(new ImageIcon(kresmgr.getImage("minus.png")));
            deleteButton.addActionListener(this);
            deleteButton.setDefaultCapable(false);
        }
        return deleteButton;
    }

    public Avatar getAvatar() {
        if (avatar == null) {
            avatar = new Avatar();
            avatar.setPreferredSize(new Dimension(MAX_PORTRAIT_WIDTH, MAX_PORTRAIT_HEIGHT));
            avatar.setIcon(noIcon);
        }
        return avatar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.addButton) {
            Image image = ClassCache.chooseImage(this);
            getAvatar().setIcon(new ImageIcon(image));
        } else if (e.getSource() == this.deleteButton) {
            getAvatar().setIcon(noIcon);
        }
        repaint();
    }

    public void setIcon(ImageIcon icon) {
        scaleImage(icon);
    }

    private void scaleImage(ImageIcon photo) {
        if (photo.getIconHeight() > MAX_PORTRAIT_HEIGHT) {
            getAvatar().setIcon(
                new ImageIcon(photo.getImage().getScaledInstance(-1, MAX_PORTRAIT_HEIGHT, Image.SCALE_SMOOTH))
            );
        } else if (photo.getIconWidth() > MAX_PORTRAIT_WIDTH) {
            getAvatar().setIcon(
                new ImageIcon(photo.getImage().getScaledInstance(MAX_PORTRAIT_WIDTH, -1, Image.SCALE_SMOOTH))
            );
        } else {
            getAvatar().setIcon(photo);
        }
    }

    public ImageIcon getIcon() {
        return getAvatar().getIcon();
    }
}
