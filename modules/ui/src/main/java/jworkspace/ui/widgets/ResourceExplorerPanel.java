package jworkspace.ui.widgets;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

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

   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Scrollable;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;
import com.hyperrealm.kiwi.util.ResourceManager;
import com.hyperrealm.kiwi.util.ResourceNotFoundException;
import com.hyperrealm.kiwi.util.Task;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.config.DesktopServiceLocator;
import lombok.Getter;

/**
 * This is a graphic resources explorer panel. Graphic resources can reside inside of a jar file or directory.
 * This panel traverses directory or jar file recursively and shows all graphic files.
 *
 * @author Anton Troshin
 */
@SuppressWarnings("MagicNumber")
public class ResourceExplorerPanel extends KPanel implements Scrollable, ComponentListener {

    private static final String LOAD_FAILED_MESSAGE =
        WorkspaceResourceAnchor.getString("ResourceExplorerPanel.load.failed");

    private static final Logger LOG = LoggerFactory.getLogger(ResourceExplorerPanel.class);
    private static final Dimension THUMBNAIL_DIMENSION;
    private static final Border UNSELECTED_THUMBNAIL_BORDER;
    private static final Border SELECTED_THUMBNAIL_BORDER;

    // We only need one copy of these for all instances
    static {
        UNSELECTED_THUMBNAIL_BORDER = BorderFactory.createRaisedBevelBorder();
        SELECTED_THUMBNAIL_BORDER = BorderFactory.createLoweredBevelBorder();
        THUMBNAIL_DIMENSION = new Dimension(96, 96);
    }

    /**
     * This dialog can choose textures or desktop icons in workspace
     * Hint prevents KIWI textures from loading into desktop icons chooser
     */
    boolean isTextureChooser = true;
    /**
     * Path to repository
     * -- GETTER --
     *  Get path to repository

     */
    @Getter
    private String path = null;
    /**
     * List of all Thumbnails in this panel
     */
    private final List<Thumbnail> list = new Vector<>();
    /**
     * Width of this panel
     */
    private int setWidth = 450;

    private ProgressDialog pr = null;

    ResourceExplorerPanel() {
        super();

        // Align to left, 5 pixels spacing H and V
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addComponentListener(this); // catch resize events
        setPreferredDimension();
    }

    /**
     * In order to do flow layout properly, we must compute
     * the height. This panel must be at it's set width so
     * that all components fit into it.
     */
    private void setPreferredDimension() {
        int w, h;
        w = setWidth; // this is fixed by user resizing the panel
        // We should compute this, based upon knowledge of FlowLayout
        int componentCount = getComponentCount();
        if (componentCount > 0 && isVisible()) {
            Component lastcomponent = getComponent(componentCount - 1);
            Point p = lastcomponent.getLocation();
            h = p.y; // top of last component
            h += THUMBNAIL_DIMENSION.height;
            h += 5; // plus y border size for neatness
        } else {
            h = 300; // no components - probably best if it's a nonzero size
        }
        setPreferredSize(new Dimension(w, h));
        revalidate();
    }

    // We implement ComponentListener for resize events
    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        Dimension d = this.getSize();
        setWidth = d.width;
        // important, now resize the panel with this width -
        // adjust height so that all component fit
        setPreferredDimension();
    }

    public void componentShown(ComponentEvent e) {
    }

    // Methods for implementing Scrollable
    // We only wanted this one - makes this pane follow the width of
    // the scrollpane it lives in
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    // But scroll vertically
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        return visibleRect.height / 2;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction) {
        return (96 + 5) / 2; // The height of a thumbnail plus the spacing
        // between them
    }

    /**
     * Get selected icons
     */
    synchronized ImageIcon[] getSelectedImages() {
        List<ImageIcon> icons = new Vector<>();
        for (Thumbnail tt : list) {
            if (tt.isSelected()) {
                icons.add(tt.doGetIcon());
            }
        }
        ImageIcon[] mass = new ImageIcon[icons.size()];
        for (int i = 0; i < icons.size(); i++) {
            mass[i] = icons.get(i);
        }
        return mass;
    }

    /**
     * Set path to repository
     */
    public void setPath(String path) {

        this.path = path;

        removeAll();
        list.clear();

        revalidate();
        pr = new ProgressDialog(DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
            WorkspaceResourceAnchor.getString("ResourceExplorerPanel.loadingRep"), true);
        Loader loader = new Loader();
        pr.track(loader);
        setPreferredDimension();
    }

    /**
     * This method loads KIWI textures if specified flag is set.
     */
    private void enumLibTextures() throws IOException {
        InputStream is;
        try {
            is = ResourceManager.getKiwiResourceManager().getStream("textures/_index.txt");
        } catch (ResourceNotFoundException ex) {
            LOG.warn("Resource library is not found", ex);
            return;
        }
        String s;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        /*
         * Read next line - this is a texture file name
         */
        while ((s = reader.readLine()) != null) {
            try {
                Image image = ResourceManager.getKiwiResourceManager().getTexture(s);
                ImageIcon imageIcon = new ImageIcon(image);
                if (imageIcon.getIconHeight() != -1 && imageIcon.getIconWidth() != -1) {

                    Thumbnail thumb = new Thumbnail();
                    thumb.doSetIcon(imageIcon);
                    thumb.setText(s);
                    list.add(thumb);
                    this.add(thumb);
                    revalidate();
                }
            } catch (ResourceNotFoundException e) {
                LOG.warn("Resource is not found", e);
            }
        }
        reader.close();
        is.close();
    }

    /**
     * Unselect all thumbnails
     */
    private void deselectAll() {
        for (Thumbnail thumbnail : list) {
            thumbnail.setSelected(false);
        }
    }

    /**
     * Inner class to show progress dialog
     */
    class Loader extends Task {

        private Loader() {
            super();
            addProgressObserver(pr);
        }

        public void run() {
            /*
             * First get all KIWI textures if specified flag is
             * set.
             */
            WorkspaceGUI wgui = DesktopServiceLocator.getInstance().getWorkspaceGUI();
            if (wgui != null && wgui.isKiwiTextureVisible() && isTextureChooser) {
                try {
                    enumLibTextures();
                } catch (IOException ex) {
                    WorkspaceError.exception(LOAD_FAILED_MESSAGE, ex);
                }
            }

            File file = getFile();
            if (file != null) {
                /*
                 * Now determine if it is a directory or a jar file
                 */
                if (file.isDirectory() && scanDirectory(file)) {
                    return;
                } else if (!file.isDirectory() && path.endsWith("jar")) {
                    scanArchive(file);
                }
                revalidate();
                pr.setProgress(100);
            }
        }

        private void scanArchive(File file) {
            try (ZipInputStream is = new ZipInputStream(new FileInputStream(file));
                 ZipFile zfile = new ZipFile(file)) {
                
                ZipEntry ze;
                /*
                 * Get through all entries
                 */
                while ((ze = is.getNextEntry()) != null) {
                    if (!ze.isDirectory()) {
                        InputStream zip = zfile.getInputStream(ze);
                        /*
                         * Now try to load it with JIMI
                         */
                        loadZipImage(ze, zip);
                    }
                }
            } catch (IOException ex) {
                WorkspaceError.exception(LOAD_FAILED_MESSAGE, ex);
            }
        }

        private boolean scanDirectory(File file) {
            /*
             * List files
             */
            File[] files = file.listFiles();
            if (files == null) {
                return true;
            }
            float step = 100 / (float) files.length;
            float progress = 0;
            /*
             * Resource explorer does not iterate through subdirectories.
             */
            for (File value : files) {
                if (!value.isDirectory()) {
                    progress = loadImage(step, progress, value);
                }
            }
            return false;
        }

        private void loadZipImage(ZipEntry ze, InputStream zip) throws IOException {

            Image image = ImageIO.read(zip);
            ImageIcon imageIcon = new ImageIcon(image);

            if (imageIcon.getIconHeight() != -1 && imageIcon.getIconWidth() != -1) {

                Thumbnail thumb = new Thumbnail();
                thumb.doSetIcon(imageIcon);
                int index = ze.getName().lastIndexOf('/');
                if (index != -1) {
                    thumb.setText(ze.getName().substring(index + 1));
                }
                list.add(thumb);
                add(thumb);
            }
        }

        private float loadImage(float step, float progress, File value) {
            try {
                Image image = ImageIO.read(value);
                ImageIcon imageIcon = new ImageIcon(image);

                if (imageIcon.getIconHeight() != -1 && imageIcon.getIconWidth() != -1) {

                    Thumbnail thumb = new Thumbnail();
                    thumb.doSetIcon(imageIcon);
                    thumb.setText(value.getName());
                    list.add(thumb);
                    add(thumb);
                }
                pr.setProgress(Math.round(progress + step));
            } catch (IOException ignored) {

            }
            return progress + step;
        }

        private File getFile() {

            if (!Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS)) {
                pr.setProgress(100);
                return null;
            }

            return new File(path);
        }
    }

    /**
     * This is a representation of a single square thumbnail for
     * separate ICON.
     */
    static class Thumbnail extends JLabel implements MouseListener {

        @Getter
        private boolean selected = false;

        private ImageIcon imageIcon = null;

        private Thumbnail() {
            super();
            setPreferredSize(THUMBNAIL_DIMENSION);
            setSelected(false);
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setHorizontalAlignment(JLabel.CENTER);
            addMouseListener(this);
        }

        public void setSelected(boolean b) {
            selected = b;
            if (selected) {
                setBorder(SELECTED_THUMBNAIL_BORDER);
            } else {
                setBorder(UNSELECTED_THUMBNAIL_BORDER);
            }
        }

        void doSetIcon(ImageIcon i) {
            imageIcon = i;
            Image image = i.getImage();

            int h, w;
            h = i.getIconHeight();
            w = i.getIconWidth();

            if (w > h && w > 64) {
                image = image.getScaledInstance(64, -1, Image.SCALE_FAST);
            } else if (w < h && h > 64) {
                image = image.getScaledInstance(-1, 64, Image.SCALE_FAST);
            }

            setIcon(new ImageIcon(image));
            revalidate();
        }

        ImageIcon doGetIcon() {
            return imageIcon;
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            Container c = getParent();

            if (!(c instanceof ResourceExplorerPanel tp)) {
                return;
            }

            if (e.getClickCount() == 1) {
                if (!e.isControlDown()) {
                    tp.deselectAll(); // unselect all thumbnails
                }
                setSelected(!isSelected()); // add this one
                revalidate();
            }
        }
    }
}
