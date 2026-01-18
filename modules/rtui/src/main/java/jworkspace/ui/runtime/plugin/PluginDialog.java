package jworkspace.ui.runtime.plugin;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.ui.WorkspaceGUI;

public class PluginDialog extends ComponentDialog {

    PluginPropertiesPanel panel;

    @SuppressWarnings("checkstyle:MagicNumber")
    public PluginDialog(Frame parent) {
        super(parent, "New Plugin Properties", true);
        setPreferredSize(new Dimension(550, 450));
        setComment(new ImageIcon(WorkspaceGUI.getResourceManager().getImage("plugin_big.png")),
            "Installing A New Plugin..."
        );
    }

    protected boolean accept() {
        return panel.syncData();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    protected JComponent buildDialogUI() {
        setComment(null);
        commentLabel.setVerticalAlignment(SwingConstants.CENTER);
        commentLabel.setHorizontalAlignment(SwingConstants.LEFT);
        commentLabel.setOpaque(true);
        panel = new PluginPropertiesPanel();
        return panel;
    }

    public void setData(Plugin data) {
        panel.setData(data);
    }

    protected boolean isAllUsers() {
        return panel.isAllUsers();
    }
}
