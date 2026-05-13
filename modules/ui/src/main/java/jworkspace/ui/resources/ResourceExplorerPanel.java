package jworkspace.ui.resources;

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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.hyperrealm.kiwi.runtime.Task;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;

import jworkspace.ui.util.ResourceScanner;
import lombok.Getter;
import lombok.extern.java.Log;


@Log
@SuppressWarnings("MagicNumber")
public class ResourceExplorerPanel extends KPanel implements Scrollable {

    private static final Dimension THUMBNAIL_DIMENSION;
    private static final Border UNSELECTED_THUMBNAIL_BORDER;
    private static final Border SELECTED_THUMBNAIL_BORDER;

    static {
        UNSELECTED_THUMBNAIL_BORDER = BorderFactory.createRaisedBevelBorder();
        SELECTED_THUMBNAIL_BORDER = BorderFactory.createLoweredBevelBorder();
        THUMBNAIL_DIMENSION = new Dimension(96, 96);
    }

    @Getter
    private String path = null;

    private final Dialog parent;
    private final JTextField searchField;
    private final JPanel gridContainer;
    private final List<Thumbnail> list = new ArrayList<>();
    private final ResourceScanner scanner = new ResourceScanner();
    private int setWidth = 450;
    private ProgressDialog pr = null;

    ResourceExplorerPanel(Dialog parent) {
        super();
        this.parent = parent;
        setLayout(new BorderLayout());

        searchField = new JTextField();
        searchField.setToolTipText("Type to filter images by name...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterThumbnails();
            }
            public void removeUpdate(DocumentEvent e) {
                filterThumbnails();
            }
            public void changedUpdate(DocumentEvent e) {
                filterThumbnails();
            }
        });

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        gridContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        add(gridContainer, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setWidth = getSize().width;
                setPreferredDimension();
            }
        });
        setPreferredDimension();
    }

    private void setPreferredDimension() {
        int w = setWidth;
        int h;
        int componentCount = gridContainer.getComponentCount();
        if (componentCount > 0 && gridContainer.isVisible()) {
            Component lastcomponent = gridContainer.getComponent(componentCount - 1);
            h = lastcomponent.getLocation().y
                + THUMBNAIL_DIMENSION.height
                + 5
                + searchField.getParent().getPreferredSize().height;
        } else {
            h = 300;
        }
        setPreferredSize(new Dimension(w, h));
        revalidate();
    }

    private void filterThumbnails() {
        String query = searchField.getText().trim().toLowerCase();
        gridContainer.removeAll();
        for (Thumbnail thumb : list) {
            if (query.isEmpty() || thumb.getDisplayName().toLowerCase().contains(query)) {
                gridContainer.add(thumb);
            }
        }
        gridContainer.revalidate();
        gridContainer.repaint();
        setPreferredDimension();
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
        return r.height / 2;
    }
    public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
        return (96 + 5) / 2;
    }

    synchronized ImageIcon[] getSelectedImages() {
        List<ImageIcon> icons = new ArrayList<>();
        for (Thumbnail tt : list) {
            if (tt.isSelected()) {
                icons.add(tt.doGetIcon());
            }
        }
        return icons.toArray(new ImageIcon[0]);
    }

    public void setPath(String path) {
        this.path = path;
        searchField.setText("");
        gridContainer.removeAll();
        list.clear();
        revalidate();

        pr = new ProgressDialog(parent, "Loading...", true);
        pr.setLocation(
            parent.getX() + (parent.getWidth() - pr.getWidth()) / 2,
            parent.getY() + (parent.getHeight() - pr.getHeight()) / 2
        );

        Loader loader = new Loader();
        pr.track(loader);
        setPreferredDimension();
    }

    private void loadAndRegisterThumbnail(InputStream is, String filename) throws IOException {
        Image image = ImageIO.read(is);
        if (image != null) {
            ImageIcon imageIcon = new ImageIcon(image);
            if (imageIcon.getIconHeight() != -1 && imageIcon.getIconWidth() != -1) {
                SwingUtilities.invokeLater(() -> {
                    Thumbnail thumb = new Thumbnail(filename);
                    thumb.doSetIcon(imageIcon);
                    list.add(thumb);
                    gridContainer.add(thumb);
                    gridContainer.revalidate();
                });
            }
        }
    }

    private void deselectAll() {
        for (Thumbnail thumbnail : list) {
            thumbnail.setSelected(false);
        }
    }

    static class Thumbnail extends JLabel {
        @Getter private final String displayName;
        @Getter private boolean selected = false;
        private ImageIcon imageIcon = null;

        private Thumbnail(String name) {
            super();
            this.displayName = name;
            setText(name);
            setPreferredSize(THUMBNAIL_DIMENSION);
            setSelected(false);
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setHorizontalAlignment(JLabel.CENTER);
            addMouseListener(new MouseAdapter() {
                @SuppressWarnings("checkstyle:ReturnCount")
                public void mouseClicked(MouseEvent e) {
                    Container c = getParent();
                    if (c == null) {
                        return;
                    }
                    if (!(c instanceof ResourceExplorerPanel) && c.getParent() instanceof ResourceExplorerPanel) {
                        c = c.getParent();
                    }
                    if (!(c instanceof ResourceExplorerPanel tp)) {
                        return;
                    }

                    if (e.getClickCount() == 1) {
                        if (!e.isControlDown()) {
                            tp.deselectAll();
                        }
                        setSelected(!isSelected());
                        revalidate();
                    }
                }
            });
        }

        public void setSelected(boolean b) {
            selected = b;
            setBorder(selected ? SELECTED_THUMBNAIL_BORDER : UNSELECTED_THUMBNAIL_BORDER);
        }

        void doSetIcon(ImageIcon i) {
            imageIcon = i;
            Image img = i.getImage();
            int h = i.getIconHeight();
            int w = i.getIconWidth();

            if (w > h && w > 64) {
                img = img.getScaledInstance(64, -1, Image.SCALE_FAST);
            } else if (w < h && h > 64) {
                img = img.getScaledInstance(-1, 64, Image.SCALE_FAST);
            }

            setIcon(new ImageIcon(img));
            revalidate();
        }

        ImageIcon doGetIcon() {
            return imageIcon;
        }
    }

    class Loader extends Task {
        private Loader() {
            super();
            addProgressObserver(pr);
        }

        public void run() {

            scanner.scanClasspathFolder(
                ResourceExplorerPanel.this.path,
                ResourceExplorerPanel.this::loadAndRegisterThumbnail
            );


            File file = getFile();
            if (file != null) {
                scanner.scanFile(file,
                    (progress, _) -> pr.setProgress(Math.round(progress)),
                    ResourceExplorerPanel.this::loadAndRegisterThumbnail
                );

                SwingUtilities.invokeLater(() -> {
                    gridContainer.revalidate();
                    gridContainer.repaint();
                    setPreferredDimension();
                });
                pr.setProgress(100);
            }

        }

        private File getFile() {
            if (path == null || !Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS)) {
                pr.setProgress(100);
                return null;
            }
            return new File(path);
        }
    }
}
