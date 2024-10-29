package jworkspace.ui.profiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_PADDING;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.SOUTH_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.WEST_POSITION;
import com.hyperrealm.kiwi.ui.ButtonPanel;
import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceManager;

import jworkspace.ui.WorkspaceGUI;

/**
 * Login panel for the main frame
 */
public class LoginPanel extends KPanel {

    private final ButtonPanel buttonPanel = new ButtonPanel();

    private final LoginForm loginForm = new LoginForm();

    private final KButton bOk;

    private final KLabel iconLabel;

    @SuppressWarnings("checkstyle:MagicNumber")
    public LoginPanel() {

        setLayout(new GridBagLayout());
        //  setAlpha(0.2F);
        setBackground(Color.GRAY);
        setOpaque(true);

        KPanel centrePanel = new KPanel();
        centrePanel.setLayout(new BorderLayout());
        centrePanel.setOpaque(true);
        centrePanel.setBackground(Color.WHITE);

        iconLabel = new KLabel();
        iconLabel.setBorder(new EmptyBorder(0, DEFAULT_PADDING, 0, DEFAULT_PADDING * 3));
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setIcon(new ImageIcon(new ResourceManager(WorkspaceGUI.class).getImage("user_change.png")));

        bOk = new KButton("Login");
        buttonPanel.addButton(bOk);

        centrePanel.add(WEST_POSITION, iconLabel);
        centrePanel.add(CENTER_POSITION, loginForm);
        centrePanel.add(SOUTH_POSITION, buttonPanel);

        add(centrePanel, new GridBagConstraints(0,
                0,
                1,
                1,
                0,
                0,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(4, 4, 4, 4),
                0,
                0
            )
        );
        centrePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
    }
}
