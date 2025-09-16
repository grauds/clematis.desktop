package jworkspace.ui.desktop.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalButtonUI;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;

import static jworkspace.ui.api.Constants.BOTTOM_LEFT_CORNER_IMAGE;
import static jworkspace.ui.api.Constants.BOTTOM_RIGHT_CORNER_IMAGE;
import static jworkspace.ui.api.Constants.CENTER_IMAGE;
import static jworkspace.ui.api.Constants.STRETCH_IMAGE;
import static jworkspace.ui.api.Constants.TILE_IMAGE;
import static jworkspace.ui.api.Constants.TOP_LEFT_CORNER_IMAGE;
import static jworkspace.ui.api.Constants.TOP_RIGHT_CORNER_IMAGE;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.PropertiesPanel;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.widgets.ClassCache;
import jworkspace.ui.widgets.ImageRenderer;
import lombok.extern.java.Log;

/**
 * Desktop property panel
 */
@Log
@SuppressWarnings("MagicNumber")
public class DesktopBackgroundPanel extends KPanel implements ActionListener, PropertiesPanel {

    private static final String DESKTOP_BG_PANEL_CENTER = "DesktopBgPanel.center";
    private static final String DESKTOP_BG_PANEL_TILE = "DesktopBgPanel.tile";
    private static final String DESKTOP_BG_PANEL_STRETCH = "DesktopBgPanel.stretch";
    private static final String DESKTOP_BG_PANEL_TOP_LEFT_CORNER = "DesktopBgPanel.top_Left_Corner";
    private static final String DESKTOP_BG_PANEL_TOP_RIGHT_CORNER = "DesktopBgPanel.top_Right_Corner";
    private static final String DESKTOP_BG_PANEL_BOTTOM_LEFT_CORNER = "DesktopBgPanel.bottom_Left_Corner";
    private static final String DESKTOP_BG_PANEL_BOTTOM_RIGHT_CORNER = "DesktopBgPanel.bottom_Right_Corner";
    /**
     * Path to image
     */
    private String pathToImage;
    /**
     * Desktop render style
     */
    private JComboBox<String> cbStyle;
    /**
     * Path field
     */
    private final JTextField pathField = new JTextField(15);
    /**
     * Name of desktop
     */
    private JTextField tName;
    /**
     * Desktop that has to be edited
     */
    private final Desktop desktop;
    /**
     * Current image render mode
     */
    private int renderMode;
    /**
     * Image cover is visible?
     */
    private boolean coverVisible;
    /**
     * Make cover visible
     */
    private final JCheckBox coverVisibleCheckbox = new JCheckBox();
    /**
     * Gradient fill switch
     */
    private final JCheckBox gradient = new JCheckBox();
    /**
     * Gradient fill flag.
     */
    private boolean gradientFill;
    /**
     * Image icon - desktop wallpaper candidate.
     */
    private ImageIcon cover;
    /**
     * Color of desktop.
     */
    private Color bgColor;
    /**
     * Button to toggle first color
     */
    private JButton bBrowseTop;
    /**
     * 2nd color of desktop.
     */
    private Color bgColor2;
    /**
     * Button to toggle second color
     */
    private JButton bBrowseBottom;

    public DesktopBackgroundPanel(Desktop desktop) {
        super();
        setLayout(new BorderLayout());
        setName(WorkspaceResourceAnchor.getString("DesktopBgPanel.title"));
        setOpaque(false);

        bgColor = desktop.getBackground();
        bgColor2 = desktop.getTheme().getSecondaryBackground();
        cover = desktop.getTheme().getCover();
        coverVisible = desktop.getTheme().isCoverVisible();
        renderMode = desktop.getTheme().getRenderMode();
        gradientFill = desktop.getTheme().isGradientFill();
        pathToImage = desktop.getTheme().getPathToImage();

        this.desktop = desktop;

        ImageRenderer imageRenderer = new ThisPanelImageRenderer();
        imageRenderer.setImage(WorkspaceGUI.getResourceManager().
            getImage("desktop/monitor.gif"));
        imageRenderer.setBorder(new EmptyBorder(5, 0, 0, 0));
        imageRenderer.setOpaque(false);
        this.add(imageRenderer, BorderLayout.CENTER);
        this.add(createControlsPanel(), BorderLayout.SOUTH);
    }

    @Override
    public boolean syncData() {
        desktop.getTheme().setRenderMode(renderMode);
        desktop.setName(tName.getText());
        desktop.getTheme().setCover(pathToImage);
        desktop.getTheme().setGradientFill(gradient.isSelected());
        desktop.getTheme().setCoverVisible(coverVisibleCheckbox.isSelected());
        desktop.setBackground(bgColor);
        desktop.getTheme().setSecondaryBackground(bgColor2);
        return true;
    }

    @Override
    public boolean setData() {
        return true;
    }

    @SuppressWarnings("CyclomaticComplexity")
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        switch (command) {
            case Constants.CHOOSE_BACKGROUND_IMAGE:
                JFileChooser fch = ClassCache.
                    getIconChooser(pathToImage == null ? " " : pathToImage);
                if (fch.showOpenDialog(
                    DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()) != JFileChooser.APPROVE_OPTION
                ) {
                    return;
                }
                File imf = fch.getSelectedFile();
                if (imf != null) {
                    String testPath = imf.getAbsolutePath();
                    ImageIcon testCover;
                    try {
                        testCover = new ImageIcon(ImageIO.read(imf));

                        if (testCover.getIconHeight() != -1 && testCover.getIconWidth() != -1) {
                            pathToImage = testPath;
                            cover = testCover;
                            pathField.setText(testPath);
                            repaint();
                        }
                    } catch (IOException e) {
                        log.severe("Can't load desktop wallpaper: " + e.getMessage());
                    }
                }
                break;
            case "comboBoxChanged":
                Object selected = cbStyle.getSelectedItem();
                if (selected != null) {
                    if (selected.equals(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_CENTER))) {
                        renderMode = CENTER_IMAGE;
                    } else if (selected.equals(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TILE))) {
                        renderMode = TILE_IMAGE;
                    } else if (selected.equals(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_STRETCH))) {
                        renderMode = STRETCH_IMAGE;
                    } else if (selected.equals(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TOP_LEFT_CORNER))) {
                        renderMode = TOP_LEFT_CORNER_IMAGE;
                    } else if (selected.equals(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TOP_RIGHT_CORNER))) {
                        renderMode = TOP_RIGHT_CORNER_IMAGE;
                    } else if (selected.equals(WorkspaceResourceAnchor.getString(
                        DESKTOP_BG_PANEL_BOTTOM_LEFT_CORNER))) {
                        renderMode = BOTTOM_LEFT_CORNER_IMAGE;
                    } else if (selected.equals(WorkspaceResourceAnchor.getString(
                        DESKTOP_BG_PANEL_BOTTOM_RIGHT_CORNER))) {
                        renderMode = BOTTOM_RIGHT_CORNER_IMAGE;
                    }
                }
                repaint();
                break;
            case Constants.TOGGLE_GRADIENT:
                gradientFill = !gradientFill;
                repaint();
                break;
            case Constants.TOGGLE_WALLPAPER:
                coverVisible = !coverVisible;
                repaint();
                break;
            case Constants.CHOOSE_GRADIENT_COLOR_1: {
                Color color = JColorChooser.showDialog(DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                    WorkspaceResourceAnchor.getString("DesktopBgPanel.chooseBg1"),
                    desktop.getBackground());
                if (color != null) {
                    bgColor = color;
                    bBrowseTop.setBackground(bgColor);
                }
                repaint();
                break;
            }
            case Constants.CHOOSE_GRADIENT_COLOR_2: {
                Color color = JColorChooser.showDialog(DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                    WorkspaceResourceAnchor.getString("DesktopBgPanel.chooseBg2"),
                    desktop.getTheme().getSecondaryBackground());
                if (color != null) {
                    bgColor2 = color;
                    bBrowseBottom.setBackground(bgColor2);
                }
                repaint();
                break;
            }
            default:
        }
    }

    @SuppressWarnings({"CyclomaticComplexity", "MethodLength"})
    private KPanel createControlsPanel() {
        KPanel controls = new KPanel();
        controls.setOpaque(false);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        controls.setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        JLabel l;

        l = new JLabel(WorkspaceResourceAnchor.getString("DesktopBgPanel.name"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        controls.add(l, gbc);

        tName = new JTextField(20);
       // tName.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        tName.setText(desktop.getName());
        controls.add(tName, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("DesktopBgPanel.cover"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        controls.add(l, gbc);

        KPanel p0 = new KPanel();
        p0.setLayout(new BorderLayout(5, 5));
       // p0.setPreferredSize(new Dimension(150, 20));

        JButton bBrowse = new JButton("...");
        bBrowse.setDefaultCapable(false);
        bBrowse.setActionCommand(Constants.CHOOSE_BACKGROUND_IMAGE);
        bBrowse.addActionListener(this);

        if (pathToImage != null) {
            pathField.setText(pathToImage);
        } else {
            pathField.setText(WorkspaceResourceAnchor.getString("DesktopBgPanel.noCover"));
        }

     //   pathField.setPreferredSize(new Dimension(150, 20));
        pathField.setEditable(false);

        p0.add(pathField, BorderLayout.CENTER);
        p0.add(bBrowse, BorderLayout.EAST);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        controls.add(p0, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("DesktopBgPanel.coverPosition"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        controls.add(l, gbc);

        cbStyle = new JComboBox<>(new String[] {
                WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_CENTER),
                WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_STRETCH),
                WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TILE),
                WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TOP_LEFT_CORNER),
                WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TOP_RIGHT_CORNER),
                WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_BOTTOM_LEFT_CORNER),
                WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_BOTTOM_RIGHT_CORNER)
            });
        cbStyle.setEditable(false);
        switch (desktop.getTheme().getRenderMode()) {
            case TILE_IMAGE:
                cbStyle.setSelectedItem(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TILE));
                break;
            case STRETCH_IMAGE:
                cbStyle.setSelectedItem(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_STRETCH));
                break;
            case TOP_LEFT_CORNER_IMAGE:
                cbStyle.setSelectedItem(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TOP_LEFT_CORNER));
                break;
            case TOP_RIGHT_CORNER_IMAGE:
                cbStyle.setSelectedItem(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_TOP_RIGHT_CORNER));
                break;
            case BOTTOM_LEFT_CORNER_IMAGE:
                cbStyle.setSelectedItem(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_BOTTOM_LEFT_CORNER));
                break;
            case BOTTOM_RIGHT_CORNER_IMAGE:
                cbStyle.setSelectedItem(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_BOTTOM_RIGHT_CORNER));
                break;
            case CENTER_IMAGE:
            default:
                cbStyle.setSelectedItem(WorkspaceResourceAnchor.getString(DESKTOP_BG_PANEL_CENTER));
                break;
        }
      //  cbStyle.setPreferredSize(new Dimension(60, 20));
        cbStyle.addActionListener(this);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        controls.add(cbStyle, gbc);

        l = new JLabel("");
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        controls.add(l, gbc);

        coverVisibleCheckbox.setText(WorkspaceResourceAnchor.getString("DesktopBgPanel.coverVisible"));
        coverVisibleCheckbox.addActionListener(this);
        coverVisibleCheckbox.setOpaque(false);
        coverVisibleCheckbox.setActionCommand(Constants.TOGGLE_WALLPAPER);
      //  coverVisibleCheckbox.setPreferredSize(new Dimension(60, 20));
        if (desktop.getTheme().isCoverVisible()) {
            coverVisibleCheckbox.setSelected(true);
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        controls.add(coverVisibleCheckbox, gbc);

        l = new JLabel("");
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        controls.add(l, gbc);

        gradient.setText(WorkspaceResourceAnchor.getString("DesktopBgPanel.gradient"));
        gradient.addActionListener(this);
        gradient.setActionCommand(Constants.TOGGLE_GRADIENT);
      //  gradient.setPreferredSize(new Dimension(60, 20));
        gradient.setOpaque(false);
        if (desktop.getTheme().isGradientFill()) {
            gradient.setSelected(true);
        }

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        controls.add(gradient, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("DesktopBgPanel.gradColors"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        controls.add(l, gbc);

        KPanel chooser = new KPanel();
        chooser.setLayout(new BorderLayout());

        KPanel kp1 = new KPanel();
        kp1.setLayout(new BorderLayout(5, 0));
        kp1.setBorder(new EmptyBorder(0, 2, 0, 2));

        l = new JLabel(WorkspaceResourceAnchor.getString("DesktopBgPanel.gradTop"));
        kp1.add(l, BorderLayout.WEST);

        bBrowseTop = new JButton();
        bBrowseTop.setDefaultCapable(false);
        bBrowseTop.setActionCommand(Constants.CHOOSE_GRADIENT_COLOR_1);
        bBrowseTop.addActionListener(this);
        bBrowseTop.setBackground(bgColor);
        bBrowseTop.setUI(new MetalButtonUI());
        bBrowseTop.setPreferredSize(new Dimension(20, 20));
        bBrowseTop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        kp1.add(bBrowseTop, BorderLayout.EAST);

        KPanel kp2 = new KPanel();
        kp2.setLayout(new BorderLayout(5, 0));
        kp2.setBorder(new EmptyBorder(0, 2, 0, 2));

        l = new JLabel(WorkspaceResourceAnchor.getString("DesktopBgPanel.gradBottom"));
        kp2.add(l, BorderLayout.WEST);

        bBrowseBottom = new JButton();
        bBrowseBottom.setDefaultCapable(false);
        bBrowseBottom.setActionCommand(Constants.CHOOSE_GRADIENT_COLOR_2);
        bBrowseBottom.addActionListener(this);
        bBrowseBottom.setBackground(bgColor2);
        bBrowseBottom.setUI(new MetalButtonUI());
        bBrowseBottom.setPreferredSize(new Dimension(20, 20));
        bBrowseBottom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        kp2.add(bBrowseBottom, BorderLayout.EAST);

        //   chooser.setPreferredSize(new Dimension(150, 20));
        chooser.add(kp1, BorderLayout.WEST);
        chooser.add(kp2, BorderLayout.EAST);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        controls.add(chooser, gbc);

        controls.setBorder(new EmptyBorder(5, 5, 5, 5));

        return controls;
    }

    class ThisPanelImageRenderer extends ImageRenderer {

        int picWidth = 156;
        int picHeight = 111;
        double picXScale = 1;
        double picYScale = 1;

        public void paintComponent(Graphics g) {
            if (image != null) {
                g.drawImage(image.getImage(), (getWidth() - image.getIconWidth()) / 2,
                    (getHeight() - image.getIconHeight()) / 2, this);
            }
            g.translate(getWidth() / 2 - 80, 11);
            g.setClip(0, 0, picWidth, picHeight);
            picXScale = (double) picWidth / (double) desktop.getWidth();
            picYScale = (double) picHeight / (double) desktop.getHeight();
            render(g);
        }

        void render(Graphics g) {
            g.setColor(bgColor);
            g.fillRect(0, 0, picWidth, picHeight);
            if (gradientFill) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, bgColor, 0, picHeight, bgColor2));
                g2.fill(new Rectangle(0, 0, picWidth, picHeight));
            }
            if (cover != null && coverVisible) {
                if (renderMode == CENTER_IMAGE) {
                    g.drawImage(cover.getImage(),
                        (picWidth - (int) (cover.getIconWidth() * picXScale)) / 2,
                        (picHeight - (int) (cover.getIconHeight() * picYScale)) / 2,
                        (int) (cover.getIconWidth() * picXScale),
                        (int) (cover.getIconHeight() * picYScale),
                        this);
                } else if (renderMode == STRETCH_IMAGE) {
                    g.drawImage(cover.getImage(), 0, 0, picWidth, picHeight, this);
                } else if (renderMode == TILE_IMAGE) {
                    int x = 0, y = 0;
                    while (x < picWidth) {
                        while (y < picHeight) {
                            g.drawImage(cover.getImage(), x, y,
                                (int) (cover.getIconWidth() * picXScale),
                                (int) (cover.getIconHeight() * picYScale), this);
                            y += (int) (cover.getIconHeight() * picYScale);
                        }
                        x += (int) (cover.getIconWidth() * picXScale);
                        y = 0;
                    }
                } else if (renderMode == TOP_LEFT_CORNER_IMAGE) {
                    g.drawImage(cover.getImage(), 0, 0,
                        (int) (cover.getIconWidth() * picXScale),
                        (int) (cover.getIconHeight() * picYScale), this);
                } else if (renderMode == TOP_RIGHT_CORNER_IMAGE) {
                    g.drawImage(cover.getImage(),
                        picWidth - (int) (cover.getIconWidth() * picXScale), 0,
                        (int) (cover.getIconWidth() * picXScale),
                        (int) (cover.getIconHeight() * picYScale), this);
                } else if (renderMode == BOTTOM_LEFT_CORNER_IMAGE) {
                    g.drawImage(cover.getImage(), 0,
                        picHeight - (int) (cover.getIconHeight() * picYScale),
                        (int) (cover.getIconWidth() * picXScale),
                        (int) (cover.getIconHeight() * picYScale), this);
                } else if (renderMode == BOTTOM_RIGHT_CORNER_IMAGE) {
                    g.drawImage(cover.getImage(), picWidth - (int) (cover.getIconWidth() * picXScale),
                        picHeight - (int) (cover.getIconHeight() * picYScale),
                        (int) (cover.getIconWidth() * picXScale),
                        (int) (cover.getIconHeight() * picYScale), this);
                }
            }
        }
    }
}
