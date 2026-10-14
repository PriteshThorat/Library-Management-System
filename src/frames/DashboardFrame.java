package src.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.geom.Path2D;

import src.service.BooksService;
import src.service.IssueRecordsService;
import src.service.MembersService;

public class DashboardFrame {
    private static final Color SIDEBAR_BG = new Color(33, 47, 61);
    private static final Color SIDEBAR_HOVER = new Color(255, 255, 255, 22);
    private static final Color BLUE = new Color(26, 115, 232);
    private static final Color CONTENT_BG = new Color(245, 247, 250);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT = new Color(33, 37, 41);
    private static final Color MUTED = new Color(120, 130, 145);
    private static final Color NAV_TEXT = new Color(200, 208, 218);

    private static final Color ACCENT_BLUE = new Color(26, 115, 232);
    private static final Color ACCENT_GREEN = new Color(52, 168, 83);
    private static final Color ACCENT_ORANGE = new Color(245, 124, 0);
    private static final Color ACCENT_RED = new Color(220, 53, 69);

    private final JFrame frame;
    private final NavButton dashboardButton;
    private final NavButton booksButton;
    private final NavButton membersButton;
    private final NavButton issueButton;
    private final NavButton usersButton;
    private final NavButton logoutButton;

    public DashboardFrame() throws Exception {
        frame = new JFrame("Library Management System - Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        dashboardButton = new NavButton("Dashboard", new AppIcon(20, AppIcon.Type.DASHBOARD));
        booksButton = new NavButton("Books", new AppIcon(20, AppIcon.Type.BOOKS));
        membersButton = new NavButton("Members", new AppIcon(20, AppIcon.Type.MEMBERS));
        issueButton = new NavButton("Issue / Return", new AppIcon(20, AppIcon.Type.ISSUE));
        usersButton = new NavButton("Users", new AppIcon(20, AppIcon.Type.USER));
        logoutButton = new NavButton("Logout", new AppIcon(20, AppIcon.Type.LOGOUT));
        NavButton exitButton = new NavButton("Exit", new AppIcon(20, AppIcon.Type.EXIT));
        dashboardButton.setActive(true);

        frame.add(createHeader(), BorderLayout.NORTH);
        frame.add(createSidebar(exitButton), BorderLayout.WEST);
        frame.add(createContent(), BorderLayout.CENTER);

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginFrame();
        });

        dashboardButton.addActionListener(e -> {
            setActive(dashboardButton);
        });
        booksButton.addActionListener(e -> {
            setActive(booksButton);

            new BookFrame();

            //frame.dispose();
            //JOptionPane.showMessageDialog(frame, "Manage Books selected.");
        });
        membersButton.addActionListener(e -> {
            setActive(membersButton);

            new MemberFrame();
            //JOptionPane.showMessageDialog(frame, "Manage Members selected.");
        });
        issueButton.addActionListener(e -> {
            setActive(issueButton);

            new IssueReturnFrame();
            //JOptionPane.showMessageDialog(frame, "Issue / Return Books selected.");
        });
        usersButton.addActionListener(e -> {
            setActive(usersButton);
            //
        });
        exitButton.addActionListener(e -> frame.dispose());

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private void setActive(NavButton selected) {
        for (NavButton button : new NavButton[] { dashboardButton, booksButton, membersButton, issueButton, usersButton, logoutButton }) {
            button.setActive(button == selected);
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(0, 26, 0, 26)
        ));
        header.setPreferredSize(new Dimension(0, 64));

        JLabel title = new JLabel("Library Management System - Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title, BorderLayout.CENTER);

        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(110, 38));

        return header;
    }

    private JPanel createSidebar(NavButton exitButton) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(24, 0, 24, 0));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(0, 24, 0, 0));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        brand.add(new JLabel(new AppIcon(26, Color.WHITE, AppIcon.Type.BOOKS)));

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(26));
        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(booksButton);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(membersButton);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(issueButton);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(usersButton);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JPanel createContent() throws Exception {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(CONTENT_BG);
        content.setBorder(new EmptyBorder(30, 34, 30, 34));

        JLabel welcome = new JLabel("Welcome, Admin");
        welcome.setFont(new Font("Arial", Font.BOLD, 26));
        welcome.setForeground(TEXT);

        JPanel cards = new JPanel(new GridLayout(2, 2, 22, 22));
        cards.setOpaque(false);
        cards.add(createStatCard("Total Books", Integer.toString(BooksService.getAllBooksCount()),
            AppIcon.Type.BOOKS, ACCENT_BLUE));
        cards.add(createStatCard("Total Members", Integer.toString(MembersService.getAllMembersCount()),
            AppIcon.Type.MEMBERS, ACCENT_GREEN));
        cards.add(createStatCard("Issued Books", Integer.toString(IssueRecordsService.getAllIssueRecordsCount()),
            AppIcon.Type.ISSUE, ACCENT_ORANGE));
        cards.add(createStatCard("Overdue Books", Integer.toString(IssueRecordsService.getAllOverdueRecordsCount()),
            AppIcon.Type.OVERDUE, ACCENT_RED));

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(24, 0, 0, 0);
        centerWrap.add(cards, c);

        content.add(welcome, BorderLayout.NORTH);
        content.add(centerWrap, BorderLayout.CENTER);

        return content;
    }

    private JPanel createStatCard(String heading, String value, AppIcon.Type iconType, Color accent) {
        RoundedPanel card = new RoundedPanel(18, Color.WHITE, BORDER);
        card.setLayout(new BorderLayout(24, 0));
        card.setBorder(new EmptyBorder(28, 32, 28, 32));

        RoundedPanel badge = new RoundedPanel(18, accent, null);
        badge.setPreferredSize(new Dimension(84, 84));
        badge.setLayout(new GridBagLayout());
        badge.add(new JLabel(new AppIcon(42, Color.WHITE, iconType)));

        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);
        card.add(badgeWrap, BorderLayout.WEST);

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        headingLabel.setForeground(MUTED);
        headingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 44));
        valueLabel.setForeground(TEXT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBox.add(Box.createVerticalGlue());
        textBox.add(headingLabel);
        textBox.add(Box.createVerticalStrut(6));
        textBox.add(valueLabel);
        textBox.add(Box.createVerticalGlue());

        card.add(textBox, BorderLayout.CENTER);

        return card;
    }

    private static class NavButton extends JButton {
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

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color border;

        RoundedPanel(int radius, Color fill, Color border) {
            this.radius = radius;
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            if (border != null) {
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class AppIcon implements Icon {
        enum Type { BOOKS, MEMBERS, USER, ISSUE, OVERDUE, DASHBOARD, EXIT, LOGOUT }

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
}
