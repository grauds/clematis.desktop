# Runtime Module

This module has two tasks: discover and load plugins and launch preconfigured external 
processes, either native or java ones. For the latter, it's required that a java program is 
configured beforehand in Installer module. Runtime module then would take a ready to use
command line with classpath, main class, parameters and arguments and would run it as a native command as well.


<img src="./doc/runtime.png" alt="drawing" width="800px"/>


## Plugins

This module provides mechanism to load plugins from any directory and possibly
for any plugin which would like to have it's own plugins:


<img src="./doc/plugins_chaining.png" alt="drawing" width="600px"/>

### Shared Context 

Each plugin gets an instance of <code>WorkspacePluginContext</code> which 
holds read-only information for every plugin, like a currect user directory for a plugin
to be able to load and save its data from a proper folder during workspace lifecycle.

### Events

If a plugin object is an instance of <code>Runnable</code> it is started in a separate thread
as soon as plugin is loaded and its main class is instantiated. 

There are two events are sent via <code>EventDispatcher</code>:

1. BEFORE_EXECUTE_EVENT - the parameters are two references, the first is to the <code>Thread</code> created to run the second argument <code>Runnable</code>
2. AFTER_EXECUTE_EVENT - the parameters are two references, the first is to the <code>Thread</code>, 
as in the first event, the second optional parameter would be a <code>Throwable</code> if the runnable not finished normally.

### Logging

TBA

## Third-party Applications

Running third party applications are usually initiated by a user via user interface. Workspace
installer is able to provide a command line to be executed by this module. 

Runtime manager attaches itself to the input stream connected to the normal output of the process to monitor
the logs. So it is important that the application writes to its output stream, not only to filesystem.

