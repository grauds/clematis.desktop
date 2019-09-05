package jworkspace.ui;

import java.io.IOException;

import jworkspace.ui.cpanel.CButton;

/**
 * @author Anton Troshin
 */
public class TestShell implements ITestShell {

    /**
     * Get all Control Panel buttons for this shell
     */
    public CButton[] getButtons() {
        return new CButton[0];
    }

    /**
     * Load shell from disk
     */
    public void load() {

    }

    /**
     * Reset the state of shell
     */
    public void reset() {

    }

    /**
     * Save all settings to default path
     */
    public void save() throws IOException {

    }

    /**
     * Returns a relative path for saving component data.
     */
    public String getPath() {
        return null;
    }

    /**
     * Sets a relative path for saving component data.
     *
     * @param path
     */
    public void setPath(String path) {

    }
}
