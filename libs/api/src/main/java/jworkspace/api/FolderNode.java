package jworkspace.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import lombok.extern.java.Log;

/**
 * A node for organization of hierarchical structure
 */
@Log
public class FolderNode extends DefinitionNode {

    public FolderNode(DefinitionNode parent, File file) {
        super(parent, file);
    }

    @Override
    public void load() {
        // directory doesn't load anything
    }

    @Override
    public void save() throws IOException {

        // create a directory if it doesn't exist in the parent's folder if the parent exists
        if (getFile() != null && !getFile().exists()) {

            File dir = this.getParent() != null
                ? Path.of(getParent().getFile().getAbsolutePath()
                + File.separator
                + getFile().getName()
            ).toFile()
                : getFile();

            if (!dir.mkdirs()) {
                log.warning("Couldn't create directories for: " + dir.getAbsolutePath());
            }
        }
    }
}
