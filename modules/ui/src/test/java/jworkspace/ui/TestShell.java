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
    @Override
    public CButton[] getButtons() {
        return new CButton[0];
    }

    /**
     * Load shell from disk
     */
    @Override
    public void load() throws IOException {

    }

    /**
     * Reset the state of shell
     */
    @Override
    public void reset() {

    }

    /**
     * Save all settings to default path
     */
    @Override
    public void save() throws IOException {

    }

    /**
     * Returns a relative path for saving component data.
     */
    @Override
    public String getPath() {
        return null;
    }

    /**
     * Sets a relative path for saving component data.
     *
     * @param path
     */
    @Override
    public void setPath(String path) {

    }
}
