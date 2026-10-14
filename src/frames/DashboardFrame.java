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
    private static final Color CONTENT_BG = new Color(245, 247, 250);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT = new Color(33, 37, 41);
    private static final Color MUTED = new Color(120, 130, 145);

    private static final Color ACCENT_BLUE = new Color(26, 115, 232);
    private static final Color ACCENT_GREEN = new Color(52, 168, 83);
    private static final Color ACCENT_ORANGE = new Color(245, 124, 0);
    private static final Color ACCENT_RED = new Color(220, 53, 69);

    private final JFrame frame;


    public DashboardFrame() throws Exception {
        frame = new JFrame("Library Management System - Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(createHeader(), BorderLayout.NORTH);
        frame.add(new Sidebar(frame, "Dashboard"), BorderLayout.WEST);
        frame.add(createContent(), BorderLayout.CENTER);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
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

        return header;
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


}
