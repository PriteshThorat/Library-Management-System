package com.library.frames;

import com.library.service.BooksService;
import com.library.service.IssueRecordsService;
import com.library.service.MembersService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DashboardFrame extends JFrame {
    private static final Color NAV = new Color(33, 47, 61);
    private static final Color NAV_TEXT = new Color(200, 208, 218);
    private static final Color BLUE = new Color(26, 115, 232);
    private static final Color CONTENT = new Color(245, 247, 250);
    private static final Color BORDER = new Color(226, 232, 240);
    private final JLabel books = valueLabel();
    private final JLabel members = valueLabel();
    private final JLabel issued = valueLabel();
    private final JLabel overdue = valueLabel();
    private final JPanel cards = new JPanel(new GridLayout(2, 2, 22, 22));
    private NavButton active;

    public DashboardFrame() {
        super("Library Management System - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(sidebar(), BorderLayout.WEST);
        add(content(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel sidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBackground(NAV);
        panel.setPreferredSize(new Dimension(231, 0));
        panel.setBorder(new EmptyBorder(24, 0, 24, 0));
        JLabel brand = new JLabel("  LIBRARY");
        brand.setFont(new Font("Arial", Font.BOLD, 17));
        brand.setForeground(Color.WHITE);
        brand.setBorder(new EmptyBorder(0, 18, 16, 0));
        panel.add(brand);
        NavButton dashboard = button("Dashboard");
        NavButton book = button("Books");
        NavButton member = button("Members");
        NavButton issue = button("Issue / Return");
        NavButton bulkImport = button("Bulk Import");
        NavButton refresh = button("Refresh");
        NavButton logout = button("Logout");
        NavButton exit = button("Exit");
        active = dashboard;
        dashboard.setActive(true);
        panel.add(dashboard);
        panel.add(book);
        panel.add(member);
        panel.add(issue);
        panel.add(bulkImport);
        panel.add(refresh);
        panel.add(logout);
        panel.add(exit);
        dashboard.addActionListener(event -> select(dashboard));
        book.addActionListener(event -> {
            select(book);
            new BookFrame().setVisible(true);
        });
        member.addActionListener(event -> {
            select(member);
            new MemberFrame().setVisible(true);
        });
        issue.addActionListener(event -> {
            select(issue);
            new IssueFrame().setVisible(true);
        });
        bulkImport.addActionListener(event -> {
            select(bulkImport);
            new BulkImportFrame().setVisible(true);
        });
        refresh.addActionListener(event -> {
            select(refresh);
            refresh();
        });
        logout.addActionListener(event -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        
        exit.addActionListener(event -> dispose());
        return panel;
    }

    private JPanel content() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT);
        panel.setBorder(new EmptyBorder(30, 34, 10, 20));
        JLabel welcome = new JLabel("Welcome, Admin");
        welcome.setFont(new Font("Arial", Font.BOLD, 26));
        welcome.setForeground(new Color(33, 37, 41));
        cards.setOpaque(false);
        cards.add(card("Total Books", books, BLUE));
        cards.add(card("Total Members", members, new Color(52, 168, 83)));
        cards.add(card("Issued Books", issued, new Color(245, 124, 0)));
        cards.add(card("Overdue Books", overdue, new Color(220, 53, 69)));
        panel.add(welcome, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);
        return panel;
    }

    private JPanel card(String title, JLabel value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(24, 28, 24, 28)));
        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.PLAIN, 17));
        heading.setForeground(new Color(120, 130, 145));
        text.add(heading);
        text.add(value);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    private JLabel valueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Arial", Font.BOLD, 40));
        label.setForeground(new Color(33, 37, 41));
        return label;
    }

    private NavButton button(String text) {
        NavButton button = new NavButton(text);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        return button;
    }

    private void select(NavButton button) {
        active.setActive(false);
        active = button;
        active.setActive(true);
    }

    private void refresh() {
        try {
            books.setText(String.valueOf(BooksService.count()));
            members.setText(String.valueOf(MembersService.count()));
            issued.setText(String.valueOf(IssueRecordsService.count()));
            overdue.setText(String.valueOf(IssueRecordsService.overdueCount()));
            cards.revalidate();
            cards.repaint();
        } catch (IOException error) {
            System.err.println(error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class NavButton extends JButton {
        private boolean selected;

        NavButton(String text) {
            super(text);
            setHorizontalAlignment(LEFT);
            setFont(new Font("Arial", Font.PLAIN, 15));
            setForeground(NAV_TEXT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 24, 0, 20));
        }

        void setActive(boolean active) {
            selected = active;
            setForeground(active ? Color.WHITE : NAV_TEXT);
            repaint();
        }

        protected void paintComponent(java.awt.Graphics graphics) {
            java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
            if (selected || getModel().isRollover()) {
                g.setColor(selected ? BLUE : new Color(255, 255, 255, 22));
                g.fillRoundRect(10, 4, getWidth() - 20, getHeight() - 8, 12, 12);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
