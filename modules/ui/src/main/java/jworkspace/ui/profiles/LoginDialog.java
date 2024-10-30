package jworkspace.ui.profiles;

import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

import jworkspace.users.LoginValidator;
/**
 * A general-purpose login dialog. The login dialog includes text fields for
 * the entry of a username and password and <i>OK</i> and <i>Cancel</i>
 * buttons. Validation is performed using the provided
 * <code>LoginValidator</code>. If a validation succeeds, the login dialog
 * disappears. Otherwise, a warning dialog is displayed, and once it's
 * dismissed by the user, control returns to the login dialog. The dialog is
 * unconditionally modal.
 *
 * <p><center>
 * <img src="snapshot/LoginDialog.gif"><br>
 * <i>An example LoginDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 * @author PING Software Group
 */

public class LoginDialog extends ComponentDialog {

    private final LoginValidator validator;

    private LoginForm main;


    /**
     * Construct a new <code>LoginDialog</code> with a default title.
     *
     * @param parent    The parent frame.
     * @param comment   A comment string to display in the upper portion of the
     *                  window.
     * @param validator The validator to check username/password pairs against.
     */

    public LoginDialog(Frame parent, String comment, LoginValidator validator) {
        this(parent, "", comment, null, validator, true);
    }

    /**
     * Construct a new <code>LoginDialog</code>.
     *
     * @param parent    The parent frame.
     * @param title     The dialog window's title.
     * @param comment   A comment string to display in the upper portion of the
     *                  window.
     * @param validator The validator to check username/password pairs against.
     */

    public LoginDialog(Frame parent, String title, String comment,
                       LoginValidator validator
    ) {
        this(parent, title, comment, null, validator, true);
    }

    /*
     */
    public LoginDialog(Frame parent, String title, String comment,
                       Icon icon,
                       LoginValidator validator
    ) {
        this(parent, title, comment, icon, validator, true);
    }

    /*
     */
    public LoginDialog(Frame parent, String title, String comment,
                       Icon icon,
                       LoginValidator validator,
                       boolean hasCancel
    ) {
        super(parent, title, true, hasCancel);
        this.validator = validator;
        setResizable(false);
        if (icon != null) {
            setTopIcon(icon);
        }
        setComment(comment);
        setIcon(KiwiUtils.getResourceManager().getIcon("keylock.gif"));
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    protected JComponent buildDialogUI() {

        if (main == null) {
            main = new LoginForm();
            registerTextInputComponent(main.getPasswordField());
            if (getTitle().isEmpty()) {
                LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
                setTitle(loc.getMessage("kiwi.dialog.title.login"));
            }
        }

        return main;
    }

    /*
     */
    protected boolean accept() {

        boolean validated = validator.validate(
            main.getUserName(),
            main.getPassword()
        );

        if (!validated) {
            JOptionPane.showMessageDialog(this, main.getLoginFailedMessage());
        }

        return validated;
    }

    /*
     */
    protected void cancel() {}

    /**
     * Show or hide the dialog.
     */
    public void setVisible(boolean flag) {
        if (flag) {
            main.setUserName("");
            main.setPassword("");
            main.requestFocus();
        }
        super.setVisible(flag);
    }

}
