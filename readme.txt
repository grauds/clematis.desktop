Copyright 1999 - 2003 Troshin Anton
Version 1.0.3 Clematis

What's new in Clematis 1.0.3?

[           Legend:          ]
[ + Added feature            ]
[ * Improved/changed feature ]
[ - Bug fixed (we hope)      ]

1.0.3

[+] Added sources for jwNet package along with build scripts.
[+] Added sources for Runtime UI package along with build scripts.
[+] Added sources for Installer UI package along with build scripts.
[+] Added "clean build products" script file.
[+] Added new painting modes to desktop wallpaper.
[*] Source editor saves font settings.
[*] Updated syntax component with mouse wheel support.
[*] Network plugin updated. Reimplemented logging via new logging mechanism.
[*] User logout procedure empties plugin cache. So plugins can be redeployed after logout and will be updated after logon.
[*] Updated Kiwi library 1.4.3
[-] Fixed missing string in /config/strings.properties
[-] Fixed key shortcut issues in source editor - for saving documents (Ctrl+S), searching the same phrase once again (F3) and etc.
[-] Source editor now opens last used directory.
[-] Fixed paste and undo functionality in source editor.
[-] Fixed issue with look and feel of login dialog. Now it is always the same and has native look.

---- History of changes ------------------------------------------------------

1.0.2

[+] Added possibility to set undecorated Clematis frame. This feature is experimental and this version does not provide GUI switch for it.
[+] Added signal number 1003 allowing to display external frame
[+] Recreated logging ability via JDK 1.4.x logging mechanism. This limits Clematis only to run with JDK 1.4.x or better
[+] Added logger window in Clematis GUI
[*] Improved font chooser dialog and source editor font management.
[*] Separate install of Doxygen documentation bundle to decrease the size of downloadable installation package.
[-] Removed odd "Full screen" menu item in Clematis frame
[-] Fixed compilation scripts (added "build" task in Clematis "build.xml" file)
[-] Fixed ability to load if no plaf class is found in classpath
[-] Small bugfixes in log messages and code
[-] Fixed some XML API methods and cleaned javadocs

1.0.1

[*] Message connect library updated 1.21 
[*] Bean Shell library updated 2.0b1.1
[*] Kiwi library updated 1.4.2 
[*] Jdom library updated 1.0b9 
[*] Wutka DTD parser updated 1.21
[*] XPath support in Clematis
[*] New compilation scripts in /bin directory
[*] The default name "localroot" is truncated to "root"
[*] Fully compatible new version counting, 1.0.1 project version is separate from kernel version 0.92.2
[*] Added catching of throwable while activating views in views manager.
[*] Added ability for launcher to seek for patches in /lib directory on startup. All patches file names should start with _ letter.

1.0 

[+] A lot of new stuff.
[*] Improved compound dialog layout in Kiwi
[-] Fixed bug with LAF manager. It now follows user login/logouts.

0.91.1

[+] New look and feel configuration
[*] Improved GUI consistency
[*] GUI now scrolls its views, if they preferred sizes are greater, that the size of viewable area
[-] Fixed some layout bugs in Kiwi compound dialogs

0.91

[+] New system message queue enables plugins and all other dynamically connected parts of the system to communicate with each other with a help of message queues, local or remote. This work is based on MsgConnect version 1.0.7 of EldoS company.
[+] Added library JDOM to the distribution package
[*] Refactored kernel packages for more reasonable API.
[*] Refactored ui packages for more reasonable API, plus new mechanism of loading shells and views, added some shells and views primitives.
[-] Fixed bug with incorrect layout on control panel.

0.90

[+] Support for users photos
[+] New users management routine
[+] New Workspace Installer
[+] New Workspace Runtime Manager.
[+] Shells and services are replaced with Kiwi plugin mechanism. No more XML files for shells and services that need to be edited.
[+] New repository explorer dialog for choosing textures, desktop icons, etc.
[+] New html help system.
[+] Added GUI settings dialog to configure look and feel and textures.
[+] New Kiwi Library v 1.3.3 release is now included with following patches:
    1. Dialog sets are removed and substituted by common Swing JOptionPane dialogs. This is made to ensure uniform style of message boxes in Java Workspace.
    2. Added new graphics to comply with Java Workspace design.
    3. Fixed some compilation bugs.
    4. Improved layout in ComponentDialog class.
    5. ComponentDialog now listens for Escape and Enter keys.
    6. Added default border layout to KFrame class.
    7. KDialog now centers itself on screen.
    8. Russian locale is added to strings resources.
    9. Removed CWD class.
    10. JIMI support in ResourceDecoder.
    11. ResourceManager now tries to find resource in KIWI library if first attept to locate required resource is failed.
[+] New option in desktop properties dialog. This option allows switching of a desktop drag mode: outline or live.
[*] Java Workspace distribution package does not contain library xalan.jar any more, as it is not used in default configuration.
[*] Changed LAF configuration routine. LAF is no more configured in /config/jwconfig.xml file. Java Workspace now always loads Metal LAF on startup, but changes it after user logs into system if other is configured for this user.
[-] Fixed bug with desktop properties dialog. In some cases it hanged the system if there is an invalid background image specified.
[-] Fixed multidesktop system current view saving and loading. There was a mess with directories, there view data is stored in these operations.
[-] Fixed transparency for runtime manager.
[-] Fixed desktop icon data transfer while copying or dragging. It now includes and preserves working directory for native command if desktop icon is configured to launch native commands.
[-] Fixed transparency bug with control panel buttons and some panels in dialog.
[-] Fixed border appearance on control panel buttons for Motif and Windows LAFs.
[-] Strings resource loader now safely warns user if specified string is not found instead of hanging the workspace because of uncaught exception.
[-] Library chooser dialog in installer now chooses all selected libraries.
[-] Removed error dialog in installer if new installation entry file name dialog is cancelled.

0.82.1

[+] Java Workspace now takes advantage of its logging framework, that has not been used in previous editions. Use "-log" command line parameter to direct logging into file.
[+] Multidesktop Manager now offers manual means to load and save current view whenever user needs to do this.
[+] International support. New directory i18n should contain string file for every desirable locale.
[+] Java Workspace can print its own version if command line parameter "-version" is specified.
[+] Java Workspace can print list of available locales if command line parameter "-locales" is specified.
[*] New scheme of views manipulation in Multidesktop Manager on workspace startup. Now all view, that are not unique, but left open in previous session, are restored without start button. All unique views in similar situation are restored with start button in control panel. After that, all views are loaded from user xml file as usual.
[-] Fixed bug in Installer that prevented application from being started if user left "working directory" field blank in application property dialog.

0.82

[+] Added possibility to configure types of text files, which installer will load into viewer. New configuration file resides in /users/{user name}/installer/ext.cfg
[+] New key navigation among desktop icons. Added shortcuts to desktop operations.
[+] New property panel for desktop
[+] Multidesktop Manager can show view's property panels now. Such panels are packed into tab control
[*] Workspace now warns user if GUI has some unsaved data and if user chooses to save, stops exiting or logging out. If your shell has some data, which must be saved in user defined file, be sure to override method isModified() for your shell for it to return true in all nessesary cases.
[*] Public interface IContentManager does not exists any more. Use abstract class WorkspaceContentManager instead
[*] Profile now operates with kiwi.util.Config class instead of java.util.Hashtable to store optional user variables. Thus, methods put() and get() for class Profile are removed. Use method getParameters() instead
[*] Added password related methods to interface jworkspace.kernel.IUsersEngine
[-] Corrected problem with control panel scrolling, now all buttons on control panel get preferred size from the largest button and scrolling works as expected
[-] Corrected bug with working directory for native commands in workspace kernel. Now workspace also changes current working directory for a while, then launching external command
[-] Closed bug with unability of Java Workspace to login under default user name localroot if there no file /users/localroot/profile.dat is found. Added "Set password" button, text field for comments and user variables editor in User Properties Dialog
[-] Desktop icon properties dialog does not flicker no more. Corrected layout bug


