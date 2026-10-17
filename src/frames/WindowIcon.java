package src.frames;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

final class WindowIcon {
  private static final int WINDOW_ICON_SIZE = 64;

  private WindowIcon() {}

  static BufferedImage image (int size) {
    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    new BookIcon(size).paintIcon(null, g2, 0, 0);
    g2.dispose();
    return image;
  }

  static void apply (JFrame frame) {
    frame.setIconImage(image(WINDOW_ICON_SIZE));
  }
}
