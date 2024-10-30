package jworkspace.ui.profiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
import jworkspace.users.LoginValidator;

/**
 * Login panel for the main frame
 */
public class LoginPanel extends KPanel {

    private final LoginForm loginForm = new LoginForm();

    private final KButton bOk;

    private final List<ActionListener> listeners = new ArrayList<>();

    private final LoginValidator validator;

    @SuppressWarnings("checkstyle:MagicNumber")
    public LoginPanel(LoginValidator validator) {

        this.validator = validator;

        setLayout(new GridBagLayout());
        //  setAlpha(0.2F);
        setBackground(Color.GRAY);
        setOpaque(true);

        LoginActionListener loginActionListener = new LoginActionListener();

        KPanel centrePanel = new KPanel();
        centrePanel.setLayout(new BorderLayout());
        centrePanel.setOpaque(true);
        centrePanel.setBackground(Color.WHITE);

        KLabel iconLabel = new KLabel();
        iconLabel.setBorder(new EmptyBorder(0, DEFAULT_PADDING, 0, DEFAULT_PADDING * 3));
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setIcon(new ImageIcon(new ResourceManager(WorkspaceGUI.class).getImage("user_change.png")));

        this.bOk = new KButton("Login");
        this.bOk.addActionListener(loginActionListener);

        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.addButton(bOk);

        centrePanel.add(WEST_POSITION, iconLabel);
        centrePanel.add(CENTER_POSITION, this.loginForm);
        centrePanel.add(SOUTH_POSITION, buttonPanel);

        loginForm.getPasswordField().addActionListener(loginActionListener);

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

    public void addListener(ActionListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ActionListener listener) {
        this.listeners.remove(listener);
    }

    /*
     */
    protected boolean accept() {

        boolean validated = validator.validate(
            loginForm.getUserName(),
            loginForm.getPassword()
        );

        if (!validated) {
            JOptionPane.showMessageDialog(this, loginForm.getLoginFailedMessage());
        }

        return validated;
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            if ((o == bOk) || (o == loginForm.getPasswordField())) {
                listeners.forEach(l -> l.actionPerformed(e));
                accept();
            }
        }
    }
}
