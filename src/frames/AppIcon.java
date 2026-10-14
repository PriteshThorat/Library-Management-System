package src.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

class AppIcon implements Icon {
    enum Type {
        BOOKS, MEMBERS, USER, ISSUE, OVERDUE, DASHBOARD, EXIT, LOGOUT
    }

    private final int size;
    private final Color color;
    private final Type type;

    AppIcon(int size, Type type) {
        this(size, Color.WHITE, type);
    }

    AppIcon(int size, Color color, Type type) {
        this.size = size;
        this.color = color;
        this.type = type;
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
        g2.setColor(color);
        g2.translate(x, y);
        g2.setStroke(new BasicStroke(Math.max(1.6f, size / 12f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        double s = size;
        switch (type) {
            case BOOKS: {
                Path2D p = new Path2D.Float();
                p.moveTo(s * 0.5, s * 0.28);
                p.lineTo(s * 0.16, s * 0.36);
                p.lineTo(s * 0.16, s * 0.78);
                p.lineTo(s * 0.5, s * 0.7);
                p.moveTo(s * 0.5, s * 0.28);
                p.lineTo(s * 0.84, s * 0.36);
                p.lineTo(s * 0.84, s * 0.78);
                p.lineTo(s * 0.5, s * 0.7);
                g2.draw(p);
                g2.drawLine((int) (s * 0.5), (int) (s * 0.28), (int) (s * 0.5), (int) (s * 0.7));
                break;
            }
            case MEMBERS: {
                g2.fillOval((int) (s * 0.54), (int) (s * 0.24), (int) (s * 0.22), (int) (s * 0.22));
                g2.fillRoundRect((int) (s * 0.48), (int) (s * 0.52), (int) (s * 0.34), (int) (s * 0.26), 10, 10);
                g2.fillOval((int) (s * 0.18), (int) (s * 0.2), (int) (s * 0.3), (int) (s * 0.3));
                g2.fillRoundRect((int) (s * 0.12), (int) (s * 0.54), (int) (s * 0.42), (int) (s * 0.28), 12, 12);
                break;
            }
            case USER: {
                g2.fillOval((int) (s * 0.32), (int) (s * 0.18), (int) (s * 0.36), (int) (s * 0.36));
                g2.fillRoundRect((int) (s * 0.2), (int) (s * 0.58), (int) (s * 0.6), (int) (s * 0.3),
                        (int) (s * 0.15), (int) (s * 0.15));
                break;
            }
            case ISSUE: {
                g2.drawRoundRect((int) (s * 0.16), (int) (s * 0.18), (int) (s * 0.44), (int) (s * 0.64), 4, 4);
                Path2D arrow = new Path2D.Float();
                arrow.moveTo(s * 0.58, s * 0.6);
                arrow.lineTo(s * 0.86, s * 0.36);
                arrow.moveTo(s * 0.62, s * 0.36);
                arrow.lineTo(s * 0.86, s * 0.36);
                arrow.lineTo(s * 0.86, s * 0.6);
                g2.draw(arrow);
                break;
            }
            case OVERDUE: {
                Path2D warning = new Path2D.Float();
                warning.moveTo(s * 0.5, s * 0.14);
                warning.lineTo(s * 0.9, s * 0.84);
                warning.lineTo(s * 0.1, s * 0.84);
                warning.closePath();
                g2.draw(warning);
                g2.drawLine((int) (s * 0.5), (int) (s * 0.42), (int) (s * 0.5), (int) (s * 0.62));
                g2.fillOval((int) (s * 0.45), (int) (s * 0.69), (int) (s * 0.1), (int) (s * 0.1));
                break;
            }
            case DASHBOARD: {
                int box = (int) (s * 0.3);
                int off = (int) (s * 0.16);
                int gap = (int) (s * 0.08);
                g2.fillRoundRect(off, off, box, box, 4, 4);
                g2.fillRoundRect(off + box + gap, off, box, box, 4, 4);
                g2.fillRoundRect(off, off + box + gap, box, box, 4, 4);
                g2.fillRoundRect(off + box + gap, off + box + gap, box, box, 4, 4);
                break;
            }
            case EXIT: {
                g2.drawRoundRect((int) (s * 0.16), (int) (s * 0.18), (int) (s * 0.4), (int) (s * 0.64), 4, 4);
                Path2D arrow = new Path2D.Float();
                arrow.moveTo(s * 0.56, s * 0.5);
                arrow.lineTo(s * 0.84, s * 0.5);
                arrow.moveTo(s * 0.7, s * 0.34);
                arrow.lineTo(s * 0.86, s * 0.5);
                arrow.lineTo(s * 0.7, s * 0.66);
                g2.draw(arrow);
                break;
            }
            case LOGOUT: {
                Path2D door = new Path2D.Float();
                door.moveTo(s * 0.48, s * 0.16);
                door.lineTo(s * 0.16, s * 0.16);
                door.lineTo(s * 0.16, s * 0.84);
                door.lineTo(s * 0.48, s * 0.84);
                g2.draw(door);
                g2.drawLine((int) (s * 0.42), (int) (s * 0.5), (int) (s * 0.86), (int) (s * 0.5));
                Path2D head = new Path2D.Float();
                head.moveTo(s * 0.68, s * 0.34);
                head.lineTo(s * 0.86, s * 0.5);
                head.lineTo(s * 0.68, s * 0.66);
                g2.draw(head);
                break;
            }
        }
        g2.dispose();
    }
}
