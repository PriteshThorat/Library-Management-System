package src.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class BookIcon implements Icon {
    private final int size;

    BookIcon(int size) {
        this.size = size;
    }

    public int getIconWidth() {
        return size;
    }

    public int getIconHeight() {
        return size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color[] covers = {
                new Color(66, 133, 244),
                new Color(52, 168, 83),
                new Color(251, 188, 5)
        };

        int pad = 3;
        int bookHeight = (size - pad * 2) / 3;
        int gap = 2;
        int[] widths = { size - pad * 2, size - pad * 2 - 8, size - pad * 2 - 16 };
        int[] offsets = { 0, 4, 8 };

        for (int i = 0; i < 3; i++) {
            int bx = x + pad + offsets[i];
            int by = y + pad + i * (bookHeight + gap);
            int bw = widths[i];

            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(bx + 4, by, bw, bookHeight, 5, 5));
            g2.setColor(new Color(205, 210, 218));
            g2.draw(new RoundRectangle2D.Float(bx + 4, by, bw - 1, bookHeight - 1, 5, 5));

            g2.setColor(covers[i]);
            g2.fill(new RoundRectangle2D.Float(bx, by, bw, bookHeight, 5, 5));

            g2.setColor(new Color(255, 255, 255, 120));
            g2.fill(new RoundRectangle2D.Float(bx + 4, by + 3, 3, bookHeight - 6, 2, 2));
        }

        g2.dispose();
    }
}
