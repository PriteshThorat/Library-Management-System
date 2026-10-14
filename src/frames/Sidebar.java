package src.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;

public class Sidebar extends JPanel {
    private static final Color SIDEBAR_BG = new Color(33, 47, 61);
    private static final Color SIDEBAR_HOVER = new Color(255, 255, 255, 22);
    private static final Color BLUE = new Color(26, 115, 232);
    private static final Color NAV_TEXT = new Color(200, 208, 218);

    private final String activePage;

    public Sidebar(JFrame owner, String activePage) {
        this.activePage = activePage;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SIDEBAR_BG);
        setPreferredSize(new Dimension(230, 0));
        setBorder(new EmptyBorder(24, 0, 24, 0));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(0, 24, 0, 0));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        brand.add(new JLabel(new AppIcon(26, Color.WHITE, AppIcon.Type.BOOKS)));

        add(brand);
        add(Box.createVerticalStrut(26));

        add(nav("Dashboard", AppIcon.Type.DASHBOARD, owner));
        add(Box.createVerticalStrut(6));
        add(nav("Books", AppIcon.Type.BOOKS, owner));
        add(Box.createVerticalStrut(6));
        add(nav("Members", AppIcon.Type.MEMBERS, owner));
        add(Box.createVerticalStrut(6));
        add(nav("Issue / Return", AppIcon.Type.ISSUE, owner));
        add(Box.createVerticalStrut(6));
        add(nav("Logout", AppIcon.Type.LOGOUT, owner));
    }

    private NavButton nav(String text, AppIcon.Type iconType, JFrame owner) {
        NavButton btn = new NavButton(text, new AppIcon(20, iconType));
        btn.setActive(text.equals(activePage));
        btn.addActionListener(e -> navigate(text, owner));
        return btn;
    }

    private void navigate(String text, JFrame owner) {
        if (text.equals(activePage)) {
            return;
        }
        owner.dispose();
        try {
            switch (text) {
                case "Dashboard":
                    new DashboardFrame();
                    break;
                case "Books":
                    new BookFrame();
                    break;
                case "Members":
                    new MemberFrame();
                    break;
                case "Issue / Return":
                    new IssueReturnFrame();
                    break;
                case "Logout":
                    new LoginFrame();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static class NavButton extends JButton {
        private boolean active;

        NavButton(String text, Icon icon) {
            super(text, icon);
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(14);
            setFont(new Font("Arial", Font.PLAIN, 15));
            setForeground(NAV_TEXT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 24, 0, 20));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setPreferredSize(new Dimension(230, 48));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        }

        public void setActive(boolean active) {
            this.active = active;
            setForeground(active ? Color.WHITE : NAV_TEXT);
            repaint();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(BLUE);
                g2.fillRoundRect(10, 4, getWidth() - 20, getHeight() - 8, 12, 12);
            } else if (getModel().isRollover()) {
                g2.setColor(SIDEBAR_HOVER);
                g2.fillRoundRect(10, 4, getWidth() - 20, getHeight() - 8, 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
