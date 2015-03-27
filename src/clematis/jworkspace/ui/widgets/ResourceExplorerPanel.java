package jworkspace.ui.widgets;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin

   This file is part of Java Workspace.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Author may be contacted at:

   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/

import com.sun.jimi.core.Jimi;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.util.WorkspaceError;
import kiwi.ui.KPanel;
import kiwi.ui.dialog.ProgressDialog;
import kiwi.util.ResourceNotFoundException;
import kiwi.util.Task;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * This is a graphic resources explorer panel. Graphic resources
 * can reside inside of a jar file or directory. This panel traverses
 * directory or jar file recursively and shows all graphic files.
 * JIMI library is used.
 */
public class ResourceExplorerPanel extends KPanel
        implements Scrollable, ComponentListener
{
    /**
     * Path to repository
     */
    String path = null;
    /**
     * List of all Thumbnails in this panel
     */
    Vector list = new Vector();
    /**
     * Width of this panel
     */
    int setWidth = 450;
    /**
     * This dialog can choose textures or desktop icons in workspace
     * Hint prevents KIWI textures from loading into desktop icons
     * chooser
     */
    protected boolean isTextureChooser = true;
    /**
     * Instance of workspace gui
     */
    private WorkspaceGUI wgui = null;

    static Dimension thumbnailDimension = null;
    static Border unselectedThumbnailBorder;
    static Border selectedThumbnailBorder;

    private ProgressDialog pr = null;

    // We only need one copy of these for all instances
    static
    {
        unselectedThumbnailBorder = BorderFactory.createRaisedBevelBorder();
        selectedThumbnailBorder = BorderFactory.createLoweredBevelBorder();
        thumbnailDimension = new Dimension(96, 96);
    }

    /**
     * Inner class to show progress dialog
     */
    class Loader extends Task
    {
        public Loader()
        {
            super();
            addProgressObserver(pr);
        }

        public void run()
        {
            /**
             * First get all KIWI textures if specified flag is
             * set.
             */
            if (wgui != null && wgui.isKiwiTextureVisible() && isTextureChooser)
            {
                try
                {
                    enumLibTextures();
                }
                catch (IOException ex)
                {
                    WorkspaceError.exception
                            (LangResource.getString("ResourceExplorerPanel.load.failed"), ex);
                }
            }
            /**
             * Check if path valid
             */
            File file = null;
            if (path == null || !(file = new File(path)).exists())
            {
                pr.setProgress(100);
                return;
            }
            /**
             * Now determine if it is a directory
             * or a jar file
             */
            if (file.isDirectory())
            {
                /**
                 * List files
                 */
                File[] files = file.listFiles();
                float step = 100 / files.length;
                float progress = 0;
                /**
                 * Resource explorer does not iterate throught
                 * subdirectories.
                 */
                for (int i = 0; i < files.length; i++)
                {
                    if (!files[i].isDirectory())
                    {
                        Image image = Jimi.getImage(files[i].getAbsolutePath());
                        ImageIcon im_icon = new ImageIcon(image);
                        if (image != null && im_icon.getIconHeight() != -1
                                && im_icon.getIconWidth() != -1)
                        {
                            Thumbnail thumb = new Thumbnail();
                            thumb._setIcon(im_icon);
                            thumb.setText(files[i].getName());
                            list.addElement(thumb);
                            add(thumb);
                        }
                        progress += step;
                        pr.setProgress(Math.round(progress));
                    }
                }
            }
            else if (!file.isDirectory() && path.endsWith("jar"))
            {
                try
                {
                    ZipInputStream is = new ZipInputStream(new FileInputStream(file));
                    ZipFile zfile = new ZipFile(file);
                    ZipEntry ze = null;
                    /**
                     * Get througth all entries
                     */
                    while ((ze = is.getNextEntry()) != null)
                    {
                        if (!ze.isDirectory())
                        {
                            InputStream zip = zfile.getInputStream(ze);
                            /**
                             * Now try to load it with JIMI
                             */
                            Image image = Jimi.getImage(zip);
                            ImageIcon im_icon = new ImageIcon(image);
                            if (image != null
                                    && im_icon.getIconHeight() != -1
                                    && im_icon.getIconWidth() != -1)
                            {
                                Thumbnail thumb = new Thumbnail();
                                thumb._setIcon(im_icon);
                                int index = ze.getName().lastIndexOf('/');
                                if (index != -1)
                                    thumb.setText(ze.getName().substring(index + 1,
                                                                         ze.getName().length()));
                                list.addElement(thumb);
                                add(thumb);
                            }
                        }
                    }
                }
                catch (IOException ex)
                {
                    WorkspaceError.exception
                            (LangResource.getString("ResourceExplorerPanel.load.failed"), ex);
                }
            }
            revalidate();
            pr.setProgress(100);
        }
    }

    /**
     * This is a representation of a single square thumbnail for
     * separate icon.
     */
    class Thumbnail extends JLabel implements MouseListener
    {
        private boolean isselected = false;
        private ImageIcon image_icon = null;

        public Thumbnail()
        {
            super();
            setPreferredSize(thumbnailDimension);
            setSelected(false);
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setHorizontalAlignment(JLabel.CENTER);
            addMouseListener(this);
        }

        public boolean isSelected()
        {
            return isselected;
        }

        public void setSelected(boolean b)
        {
            isselected = b;
            if (isselected)
                setBorder(selectedThumbnailBorder);
            else
                setBorder(unselectedThumbnailBorder);
        }

        public void _setIcon(ImageIcon i)
        {
            image_icon = i;
            Image image = i.getImage();

            int h, w;
            h = i.getIconHeight();
            w = i.getIconWidth();

            if (w > h && w > 64)
            {
                image = image.getScaledInstance(64, -1, Image.SCALE_FAST);
            }
            else if (w < h && h > 64)
            {
                image = image.getScaledInstance(-1, 64, Image.SCALE_FAST);
            }

            setIcon(new ImageIcon(image));
            revalidate();
        }

        public ImageIcon _getIcon()
        {
            return image_icon;
        }

        public void mousePressed(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }

        public void mouseClicked(MouseEvent e)
        {
            Container c = getParent();
            if (c instanceof ResourceExplorerPanel)
            {
                ResourceExplorerPanel tp = (ResourceExplorerPanel) c;
                if (e.getClickCount() == 1)
                {
                    if (!e.isControlDown())
                        tp.unselectAll(); // unselect all thumbnails
                    setSelected(!isSelected()); // add this one
                    revalidate();
                }
            }
        }
    }

    public ResourceExplorerPanel()
    {
        super();
        // Align to left, 5 pixels spacing H and V
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addComponentListener(this); // catch resize events
        setPreferredDimension();
        if (Workspace.getUI() instanceof WorkspaceGUI)
        {
            wgui = (WorkspaceGUI) Workspace.getUI();
        }
    }

    /**
     *  In order to do flow layout properly, we must compute
     *  the height. This panel must be at it's set width so
     *  that all components fit into it.
     */
    void setPreferredDimension()
    {
        int w,h;
        w = setWidth; // this is fixed by user resizing the panel
        // We should compute this, based upon knowledge of FlowLayout
        int componentcount = getComponentCount();
        if (componentcount > 0 && isVisible())
        {
            Component lastcomponent = getComponent(componentcount - 1);
            Point p = lastcomponent.getLocation();
            h = p.y; // top of last component
            h += thumbnailDimension.height;
            h += 5; // plus y border size for neatness
        }
        else
        {
            h = 300; // no components - probably best if it's a nonzero size
        }
        setPreferredSize(new Dimension(w, h));
        revalidate();
    }

    // We implement ComponenetListener for resize events
    public void componentHidden(ComponentEvent e)
    {
        ;
    }

    public void componentMoved(ComponentEvent e)
    {
        ;
    }

    public void componentResized(ComponentEvent e)
    {
        Dimension d = this.getSize();
        setWidth = d.width;
        // important, now resize the panel with this width -
        // adjust height so that all component fit
        setPreferredDimension();
    }

    public void componentShown(ComponentEvent e)
    {
        ;
    }

    // Methods for implementing Scrollable
    // We only wanted this one - makes this pane follow the width of
    // the scrollpane it lives in
    public boolean getScrollableTracksViewportWidth()
    {
        return true;
    }

    // But scroll vertically
    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }

    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction)
    {
        return visibleRect.height / 2;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction)
    {
        return (96 + 5) / 2; // The height of a thumbnail plus the spacing
        // between them
    }

    /**
     * Get selected icons
     */
    public synchronized ImageIcon[] getSelectedImages()
    {
        Vector icons = new Vector();
        for (int i = 0; i < list.size(); i++)
        {
            Thumbnail tt = (Thumbnail) list.elementAt(i);
            if (tt.isSelected())
            {
                icons.addElement(tt._getIcon());
            }
        }
        ImageIcon[] mass = new ImageIcon[icons.size()];
        for (int i = 0; i < icons.size(); i++)
            mass[i] = (ImageIcon) icons.elementAt(i);
        return mass;
    }

    /**
     * Get path to repository
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Set path to repository
     */
    public void setPath(String path)
    {
        if (path != null)
            this.path = new String(path);
        else
            this.path = null;

        removeAll();
        list.removeAllElements();
        revalidate();
        pr = new ProgressDialog(Workspace.getUI().getFrame(),
                                LangResource.getString("ResourceExplorerPanel.loadingRep"), true);
        Loader loader = new Loader();
        pr.centerDialog();
        pr.track(loader);
        setPreferredDimension();
    }

    /**
     * This method loads KIWI textures if specified flag is set.
     */
    void enumLibTextures() throws IOException
    {
        InputStream is = null;
        try
        {
            is = Workspace.getResourceManager().getKiwiResourceManager().
                    getStream("textures/_index.txt");
        }
        catch (ResourceNotFoundException ex)
        {
            return;
        }
        String s;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        /**
         * Read next line - this is a texture file name
         */
        while ((s = reader.readLine()) != null)
        {
            try
            {
                Image image = Workspace.getResourceManager().
                        getKiwiResourceManager().getTexture(s);
                ImageIcon im_icon = new ImageIcon(image);
                if (image != null
                        && im_icon.getIconHeight() != -1
                        && im_icon.getIconWidth() != -1)
                {
                    Thumbnail thumb = new Thumbnail();
                    thumb._setIcon(im_icon);
                    thumb.setText(s);
                    list.addElement(thumb);
                    this.add(thumb);
                    revalidate();
                }
            }
            catch (ResourceNotFoundException ex)
            {
                continue;
            }
        }
        reader.close();
        is.close();
    }

    /**
     * Unselect all thumbnails
     */
    void unselectAll()
    {
        for (int i = 0; i < list.size(); i++)
        {
            Thumbnail tt = (Thumbnail) list.elementAt(i);
            tt.setSelected(false);
        }
    }
}
