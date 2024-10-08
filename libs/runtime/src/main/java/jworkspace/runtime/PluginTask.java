package jworkspace.runtime;

/**
 * Plugin objects should extend this class to be able to run in a separate thread
 *
 * @author Anton Troshin
 */
public abstract class PluginTask extends AbstractTask {

    public PluginTask(String name) {
        super(name);
    }

}
