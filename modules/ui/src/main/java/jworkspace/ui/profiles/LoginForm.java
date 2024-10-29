package jworkspace.ui.profiles;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

import lombok.Getter;


@Getter
public class LoginForm extends KPanel {

    private final JTextField userField;
    private JPasswordField passwordField;
    private final String loginFailedMessage;
    private final KLabel usernameLabel;
    private final KLabel passwordLabel;

    @SuppressWarnings("checkstyle:MagicNumber")
    public LoginForm() {
        super();

        LocaleData loc = LocaleManager.getDefault() .getLocaleData("KiwiDialogs");
        loginFailedMessage = loc.getMessage("kiwi.dialog.message.login_failed");

        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        Insets insets0 = new Insets(5, 0, 5, 5);
        Insets insets1 = new Insets(0, 0, 0, 0);

        usernameLabel = new KLabel(loc.getMessage("kiwi.dialog.prompt.username"));
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.insets = insets0;
        add(usernameLabel, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        userField = new JTextField(10);
        userField.addActionListener(evt -> passwordField.requestFocus());
        gbc.insets = insets1;
        add(userField, gbc);

        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.weightx = 0;
        gbc.insets = insets0;
        passwordLabel = new KLabel(loc.getMessage("kiwi.dialog.prompt.password"));
        add(passwordLabel, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = insets1;
        passwordField = new JPasswordField(10);
        add(passwordField, gbc);

        setBackground(Color.WHITE);
        setOpaque(true);

        setBorder(new EmptyBorder(0, 10, 10, 0));
    }

    public void setVisible(boolean flag) {
        if (flag) {
            userField.setText("");
            passwordField.setText("");
            userField.requestFocus();
        }
        super.setVisible(flag);
    }

    public String getUserName() {
        return userField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        userField.requestFocus();
    }

    public void setUserName(String s) {
        userField.setText(s);
    }

    public void setPassword(String s) {
        passwordField.setText(s);
    }

    /**
     * Set the text for the username prompt label.
     *
     * @param text The new text for the label
     * @since Kiwi 1.3.3
     */
    public void setUsernamePromptText(String text) {
        if (text != null) {
            usernameLabel.setText(text);
        }
    }

    /**
     * Set the text for the password prompt label.
     *
     * @param text The new text for the label
     * @since Kiwi 1.3.3
     */
    public void setPasswordPromptText(String text) {
        if (text != null) {
            passwordLabel.setText(text);
        }
    }
}
