package jworkspace.ui.widgets;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import lombok.Getter;
import lombok.Setter;

/**
 * Avatar component thanks to <a href="https://github.com/DJ-Raven/java-swing-image-avatar-v2">
 *     https://github.com/DJ-Raven/java-swing-image-avatar-v2
 * </a>
 */
@Getter
@Setter
public class Avatar extends JComponent {

    private ImageIcon icon;

    private int borderSize;

    @Override
    protected void paintComponent(Graphics grphcs) {
        if (icon != null) {

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height);

            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;

            int border = borderSize * 2;

            diameter -= border;

            Rectangle size = getAutoSize(icon, diameter);
            BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D imageG2D = img.createGraphics();
            imageG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            imageG2D.fillOval(0, 0, diameter, diameter);

            Composite composite = imageG2D.getComposite();
            imageG2D.setComposite(AlphaComposite.SrcIn);
            imageG2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            imageG2D.drawImage(icon.getImage(), size.x, size.y, size.width, size.height, null);
            imageG2D.setComposite(composite);
            imageG2D.dispose();

            Graphics2D g2 = (Graphics2D) grphcs;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (borderSize > 0) {
                diameter += border;
                g2.setColor(getForeground());
                g2.fillOval(x, y, diameter, diameter);
            }

            if (isOpaque()) {
                g2.setColor(getBackground());
                diameter -= border;
                g2.fillOval(x + borderSize, y + borderSize, diameter, diameter);
            }
            g2.drawImage(img, x + borderSize, y + borderSize, null);
        }
        super.paintComponent(grphcs);
    }

    private Rectangle getAutoSize(Icon image, int size) {

        int iw = image.getIconWidth();
        int ih = image.getIconHeight();

        double xScale = (double) size / iw;
        double yScale = (double) size / ih;
        double scale = Math.max(xScale, yScale);

        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }

        int x = (size - width) / 2;
        int y = (size - height) / 2;

        return new Rectangle(new Point(x, y), new Dimension(width, height));
    }

}