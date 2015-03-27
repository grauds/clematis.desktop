package jworkspace.ui.desktop;

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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import com.sun.jimi.core.Jimi;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.WorkspaceClassCache;
import jworkspace.ui.widgets.ImageRenderer;
import kiwi.ui.KPanel;
import kiwi.util.KiwiUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Desktop property panel
 */
public class DesktopBackgroundPanel extends KPanel implements ActionListener
{
    /**
     * Desktop render style
     */
    JComboBox cb_style;
    /**
     * Path field
     */
    JTextField path_field = new JTextField(15);
    /**
     * Name of desktop
     */
    JTextField t_name;
    /**
     * Desktop that has to be edited
     */
    Desktop desktop;
    /**
     * Current image render mode
     */
    private int render_mode = 2;
    /**
     * Image cover is visible?
     */
    private boolean coverVisible = true;
    /**
     * Make cover visible
     */
    private JCheckBox cover_visible = new JCheckBox();
    /**
     * Gradient fill switch
     */
    private JCheckBox gradient = new JCheckBox();
    /**
     * Path to image
     */
    protected String path_to_image = null;
    /**
     * Gradient fill flag.
     */
    private boolean gradientFill = false;
    /**
     * Image icon - desktop wallpaper candidate.
     */
    private ImageIcon cover = null;
    /**
     * Color of desktop.
     */
    private Color bg_color = UIManager.getColor("desktop");
    /**
     * Button to toggle first color
     */
    private JButton b_browse_top;
    /**
     * 2nd color of desktop.
     */
    private Color bg_color_2 = UIManager.getColor("desktop");
    /**
     * Button to toggle second color
     */
    private JButton b_browse_bottom;
    /**
     * Action
     */
    public static final String CHOOSE_BACKGROUND_IMAGE = "CHOOSE_BACKGROUND_IMAGE";
    public static final String TOGGLE_WALLPAPER = "TOGGLE_WALLPAPER";
    public static final String TOGGLE_GRADIENT = "TOGGLE_GRADIENT";
    public static final String CHOOSE_GRADIENT_COLOR_1 = "CHOOSE_GRADIENT_COLOR_1";
    public static final String CHOOSE_GRADIENT_COLOR_2 = "CHOOSE_GRADIENT_COLOR_2";

    ImageRenderer l_image = new ImageRenderer()
    {
        int pic_width = 156;
        int pic_height = 111;
        double pic_x_scale = 1;
        double pic_y_scale = 1;

        public void paintComponent(Graphics g)
        {
            if (image != null)
            {
                g.drawImage(image.getImage(), (getWidth() - image.getIconWidth()) / 2,
                            (getHeight() - image.getIconHeight()) / 2, this);
            }
            g.translate(getWidth() / 2 - 80, 11);
            g.setClip(0, 0, pic_width, pic_height);
            pic_x_scale = (double) pic_width / (double) desktop.getWidth();
            pic_y_scale = (double) pic_height / (double) desktop.getHeight();
            render(g);
        }

        /**
         * Paints preview as Desktop does
         */
        void render(Graphics g)
        {
            g.setColor(bg_color);
            g.fillRect(0, 0, pic_width, pic_height);
            if (gradientFill)
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint
                        (new GradientPaint(0, 0, bg_color, 0, pic_height, bg_color_2));
                g2.fill(new Rectangle(0, 0, pic_width, pic_height));
            }
            if (cover != null && coverVisible)
            {
                /**
                 * Drawing of desktop image can occur in several rendering modes.
                 */
                if (render_mode == Desktop.CENTER_IMAGE)
                {
                    g.drawImage(cover.getImage(),
                                (pic_width - (int) (cover.getIconWidth() * pic_x_scale)) / 2,
                                (pic_height - (int) (cover.getIconHeight() * pic_y_scale)) / 2,
                                (int) (cover.getIconWidth() * pic_x_scale),
                                (int) (cover.getIconHeight() * pic_y_scale),
                                this);
                }
                else if (render_mode == Desktop.STRETCH_IMAGE)
                {
                    g.drawImage(cover.getImage(), 0, 0, pic_width, pic_height, this);
                }
                else if (render_mode == Desktop.TILE_IMAGE)
                {
                    int x = 0, y = 0;
                    while (x < pic_width)
                    {
                        while (y < pic_height)
                        {
                            g.drawImage(cover.getImage(), x, y,
                                        (int) (cover.getIconWidth() * pic_x_scale),
                                        (int) (cover.getIconHeight() * pic_y_scale), this);
                            y += cover.getIconHeight() * pic_y_scale;
                        }
                        x += cover.getIconWidth() * pic_x_scale;
                        y = 0;
                    }
                }
                else if (render_mode == Desktop.TOP_LEFT_CORNER_IMAGE)
                {
                    g.drawImage(cover.getImage(), 0, 0,
                                (int) (cover.getIconWidth() * pic_x_scale),
                                (int) (cover.getIconHeight() * pic_y_scale), this);
                }
                else if (render_mode == Desktop.TOP_RIGHT_CORNER_IMAGE)
                {
                    g.drawImage(cover.getImage(),
                                pic_width - (int) (cover.getIconWidth() * pic_x_scale), 0,
                                (int) (cover.getIconWidth() * pic_x_scale),
                                (int) (cover.getIconHeight() * pic_y_scale), this);
                }
                else if (render_mode == Desktop.BOTTOM_LEFT_CORNER_IMAGE)
                {
                    g.drawImage(cover.getImage(), 0,
                                pic_height - (int) (cover.getIconHeight() * pic_y_scale),
                                (int) (cover.getIconWidth() * pic_x_scale),
                                (int) (cover.getIconHeight() * pic_y_scale), this);
                }
                else if (render_mode == Desktop.BOTTOM_RIGHT_CORNER_IMAGE)
                {
                    g.drawImage(cover.getImage(), pic_width - (int) (cover.getIconWidth() * pic_x_scale),
                                pic_height - (int) (cover.getIconHeight() * pic_y_scale),
                                (int) (cover.getIconWidth() * pic_x_scale),
                                (int) (cover.getIconHeight() * pic_y_scale), this);
                }
            }
        }
    };

    public DesktopBackgroundPanel(Desktop desktop)
    {
        super();
        setLayout(new BorderLayout());
        setName(LangResource.getString("DesktopBgPanel.title"));

        bg_color = desktop.getBackground();
        bg_color_2 = desktop.getSecondBackground();
        cover = desktop.getCover();
        coverVisible = desktop.isCoverVisible();
        render_mode = desktop.getRenderMode();
        gradientFill = desktop.isGradientFill();
        path_to_image = desktop.path_to_image;

        this.desktop = desktop;

        l_image.setImage(Workspace.getResourceManager().
                         getImage("desktop/monitor.gif"));
        l_image.setBorder(new EmptyBorder(5, 0, 0, 0));
        l_image.setOpaque(false);
        this.add(l_image, BorderLayout.CENTER);
        this.add(createControlsPanel(), BorderLayout.SOUTH);
    }

    public boolean syncData()
    {
        desktop.setRenderMode(render_mode);
        desktop.setName(t_name.getText());
        desktop.setCover(path_to_image);
        desktop.setGradientFill(gradient.isSelected());
        desktop.setCoverVisible(cover_visible.isSelected());
        desktop.setBackground(bg_color);
        desktop.setSecondBackground(bg_color_2);
        return true;
    }

    public void actionPerformed(ActionEvent evt)
    {
        String command = evt.getActionCommand();
        if (command.equals(DesktopBackgroundPanel.CHOOSE_BACKGROUND_IMAGE))
        {
            JFileChooser fch = WorkspaceClassCache.
                    getIconChooser(path_to_image == null ? " " : path_to_image);
            if (fch.showOpenDialog(Workspace.getUI().getFrame())
                    != JFileChooser.APPROVE_OPTION)
                return;
            File imf = fch.getSelectedFile();
            if (imf != null)
            {
                String test_path = imf.getAbsolutePath();
                ImageIcon test_cover =
                        new ImageIcon(Jimi.getImage(test_path));
                if (test_cover != null
                        && test_cover.getIconHeight() != -1
                        && test_cover.getIconWidth() != -1)
                {
                    path_to_image = test_path;
                    cover = test_cover;
                    path_field.setText(test_path);
                    repaint();
                }
            }
        }
        else if (command.equals("comboBoxChanged"))
        {
            Object selected = cb_style.getSelectedItem();
            if (selected.equals
                    (LangResource.getString("DesktopBgPanel.center")))
            {
                render_mode = Desktop.CENTER_IMAGE;
            }
            else if (selected.equals
                    (LangResource.getString("DesktopBgPanel.tile")))
            {
                render_mode = Desktop.TILE_IMAGE;
            }
            else if (selected.equals
                    (LangResource.getString("DesktopBgPanel.stretch")))
            {
                render_mode = Desktop.STRETCH_IMAGE;
            }
            else if (selected.equals(LangResource.getString
                                     ("DesktopBgPanel.top_Left_Corner")))
            {
                render_mode = Desktop.TOP_LEFT_CORNER_IMAGE;
            }
            else if (selected.equals(LangResource.getString
                                     ("DesktopBgPanel.top_Right_Corner")))
            {
                render_mode = Desktop.TOP_RIGHT_CORNER_IMAGE;
            }
            else if (selected.equals(LangResource.getString
                                     ("DesktopBgPanel.bottom_Left_Corner")))
            {
                render_mode = Desktop.BOTTOM_LEFT_CORNER_IMAGE;
            }
            else if (selected.equals(LangResource.getString
                                     ("DesktopBgPanel.bottom_Right_Corner")))
            {
                render_mode = Desktop.BOTTOM_RIGHT_CORNER_IMAGE;
            }
            repaint();
        }
        else if (command.equals(DesktopBackgroundPanel.TOGGLE_GRADIENT))
        {
            gradientFill = !gradientFill;
            repaint();
        }
        else if (command.equals(DesktopBackgroundPanel.TOGGLE_WALLPAPER))
        {
            coverVisible = !coverVisible;
            repaint();
        }
        else if (command.equals(DesktopBackgroundPanel.CHOOSE_GRADIENT_COLOR_1))
        {
            Color color = JColorChooser.showDialog
                    (Workspace.getUI().getFrame(),
                     LangResource.getString("DesktopBgPanel.chooseBg1"),
                     desktop.getBackground());
            if (color != null)
            {
                bg_color = color;
                b_browse_top.setBackground(bg_color);
            }
            repaint();
        }
        else if (command.equals(DesktopBackgroundPanel.CHOOSE_GRADIENT_COLOR_2))
        {
            Color color = JColorChooser.showDialog
                    (Workspace.getUI().getFrame(),
                     LangResource.getString("DesktopBgPanel.chooseBg2"),
                     desktop.getSecondBackground());
            if (color != null)
            {
                bg_color_2 = color;
                b_browse_bottom.setBackground(bg_color_2);
            }
            repaint();
        }
    }

    KPanel createControlsPanel()
    {
        KPanel controls = new KPanel();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        controls.setLayout(gb);

        gbc.anchor = gbc.NORTHWEST;
        gbc.fill = gbc.HORIZONTAL;
        gbc.weightx = 0;

        JLabel l;

        l = new JLabel(LangResource.getString("DesktopBgPanel.name"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        controls.add(l, gbc);

        t_name = new JTextField(20);
        t_name.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        t_name.setText(desktop.getName());
        controls.add(t_name, gbc);

        l = new JLabel(LangResource.getString("DesktopBgPanel.cover"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        controls.add(l, gbc);

        KPanel p0 = new KPanel();
        p0.setLayout(new BorderLayout(5, 5));
        p0.setPreferredSize(new Dimension(150, 20));

        JButton b_browse = new JButton("...");
        b_browse.setDefaultCapable(false);
        b_browse.setActionCommand(DesktopBackgroundPanel.CHOOSE_BACKGROUND_IMAGE);
        b_browse.addActionListener(this);

        if (path_to_image != null)
        {
            path_field.setText(path_to_image);
        }
        else
        {
            path_field.setText(LangResource.getString("DesktopBgPanel.noCover"));
        }

        path_field.setPreferredSize(new Dimension(150, 20));
        path_field.setEditable(false);

        p0.add(path_field, BorderLayout.CENTER);
        p0.add(b_browse, BorderLayout.EAST);

        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        controls.add(p0, gbc);

        l = new JLabel(LangResource.getString("DesktopBgPanel.coverPosition"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        controls.add(l, gbc);

        cb_style = new JComboBox(new Object[]
        {
            LangResource.getString("DesktopBgPanel.center"),
            LangResource.getString("DesktopBgPanel.stretch"),
            LangResource.getString("DesktopBgPanel.tile"),
            LangResource.getString("DesktopBgPanel.top_Left_Corner"),
            LangResource.getString("DesktopBgPanel.top_Right_Corner"),
            LangResource.getString("DesktopBgPanel.bottom_Left_Corner"),
            LangResource.getString("DesktopBgPanel.bottom_Right_Corner")
        });
        cb_style.setEditable(false);
        switch (desktop.getRenderMode())
        {
            case Desktop.TILE_IMAGE:
                cb_style.setSelectedItem
                        (LangResource.getString("DesktopBgPanel.tile"));
                break;
            case Desktop.STRETCH_IMAGE:
                cb_style.setSelectedItem
                        (LangResource.getString("DesktopBgPanel.stretch"));
                break;
            case Desktop.TOP_LEFT_CORNER_IMAGE:
                cb_style.setSelectedItem
                        (LangResource.getString("DesktopBgPanel.top_Left_Corner"));
                break;
            case Desktop.TOP_RIGHT_CORNER_IMAGE:
                cb_style.setSelectedItem
                        (LangResource.getString("DesktopBgPanel.top_Right_Corner"));
                break;
            case Desktop.BOTTOM_LEFT_CORNER_IMAGE:
                cb_style.setSelectedItem
                        (LangResource.getString("DesktopBgPanel.bottom_Left_Corner"));
                break;
            case Desktop.BOTTOM_RIGHT_CORNER_IMAGE:
                cb_style.setSelectedItem
                        (LangResource.getString("DesktopBgPanel.bottom_Right_Corner"));
                break;
            case Desktop.CENTER_IMAGE:
            default:
                cb_style.setSelectedItem
                        (LangResource.getString("DesktopBgPanel.center"));
                break;
        }
        cb_style.setPreferredSize(new Dimension(60, 20));
        cb_style.addActionListener(this);
        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        controls.add(cb_style, gbc);

        l = new JLabel("");
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        controls.add(l, gbc);

        cover_visible.setText
                (LangResource.getString("DesktopBgPanel.coverVisible"));
        cover_visible.addActionListener(this);
        cover_visible.setOpaque(false);
        cover_visible.setActionCommand(DesktopBackgroundPanel.TOGGLE_WALLPAPER);
        cover_visible.setPreferredSize(new Dimension(60, 20));
        if (desktop.isCoverVisible())
            cover_visible.setSelected(true);

        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        controls.add(cover_visible, gbc);

        l = new JLabel("");
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        controls.add(l, gbc);

        gradient.setText(LangResource.getString("DesktopBgPanel.gradient"));
        gradient.addActionListener(this);
        gradient.setActionCommand(DesktopBackgroundPanel.TOGGLE_GRADIENT);
        gradient.setPreferredSize(new Dimension(60, 20));
        gradient.setOpaque(false);
        if (desktop.isGradientFill())
            gradient.setSelected(true);

        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        controls.add(gradient, gbc);

        l = new JLabel(LangResource.getString("DesktopBgPanel.gradColors"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        controls.add(l, gbc);

        KPanel chooser = new KPanel();
        chooser.setLayout(new BorderLayout());

        KPanel kp1 = new KPanel();
        kp1.setLayout(new BorderLayout(5, 0));
        kp1.setBorder(new EmptyBorder(0, 2, 0, 2));

        l = new JLabel(LangResource.getString("DesktopBgPanel.gradTop"));
        kp1.add(l, BorderLayout.WEST);

        b_browse_top = new JButton();
        b_browse_top.setDefaultCapable(false);
        b_browse_top.setActionCommand
                (DesktopBackgroundPanel.CHOOSE_GRADIENT_COLOR_1);
        b_browse_top.addActionListener(this);
        b_browse_top.setBackground(bg_color);
        b_browse_top.setPreferredSize(new Dimension(20, 20));
        kp1.add(b_browse_top, BorderLayout.EAST);

        KPanel kp2 = new KPanel();
        kp2.setLayout(new BorderLayout(5, 0));
        kp2.setBorder(new EmptyBorder(0, 2, 0, 2));

        l = new JLabel(LangResource.getString("DesktopBgPanel.gradBottom"));
        kp2.add(l, BorderLayout.WEST);

        b_browse_bottom = new JButton();
        b_browse_bottom.setDefaultCapable(false);
        b_browse_bottom.setActionCommand
                (DesktopBackgroundPanel.CHOOSE_GRADIENT_COLOR_2);
        b_browse_bottom.addActionListener(this);
        b_browse_bottom.setBackground(bg_color_2);
        b_browse_bottom.setPreferredSize(new Dimension(20, 20));
        kp2.add(b_browse_bottom, BorderLayout.EAST);

     //   chooser.setPreferredSize(new Dimension(150, 20));
        chooser.add(kp1, BorderLayout.WEST);
        chooser.add(kp2, BorderLayout.EAST);

        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        controls.add(chooser, gbc);

        controls.setBorder(new EmptyBorder(5, 5, 5, 5));

        return controls;
    }
}
