package src.frames;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame {
    public DashboardFrame () {
        JFrame frame = new JFrame("Library Management System - Dashboard");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setPreferredSize(new Dimension(900, 70));

        JLabel title = new JLabel("  Library Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton logoutButton = new JButton("Logout");

        header.add(title, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        JPanel sidebar = new JPanel(new GridLayout(5, 1, 5, 5));
        sidebar.setBackground(new Color(52, 73, 94));
        sidebar.setPreferredSize(new Dimension(180, 0));

        JButton dashboardButton = new JButton("Dashboard");
        JButton booksButton = new JButton("Manage Books");
        JButton membersButton = new JButton("Manage Members");
        JButton issueButton = new JButton("Issue / Return Books");
        JButton exitButton = new JButton("Exit");

        sidebar.add(dashboardButton);
        sidebar.add(booksButton);
        sidebar.add(membersButton);
        sidebar.add(issueButton);
        sidebar.add(exitButton);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        content.setBackground(new Color(245, 247, 250));

        JLabel welcome = new JLabel("Welcome to the Library Dashboard");
        welcome.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 15));
        cards.setOpaque(false);

        cards.add(createCard("Total Books", "0"));
        cards.add(createCard("Total Members", "0"));
        cards.add(createCard("Books Issued", "0"));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 25));
        centerPanel.setOpaque(false);
        centerPanel.add(welcome, BorderLayout.NORTH);
        centerPanel.add(cards, BorderLayout.CENTER);

        content.add(centerPanel, BorderLayout.CENTER);

        frame.add(header, BorderLayout.NORTH);
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(content, BorderLayout.CENTER);

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginFrame();
        });

        exitButton.addActionListener(e -> {
            frame.dispose();
        });

        dashboardButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "You are on the Dashboard.");
        });

        booksButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Manage Books selected.");
        });

        membersButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Manage Members selected.");
        });

        issueButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Issue / Return Books selected.");
        });

        frame.setVisible(true);
    }

    private static JPanel createCard(String heading, String value) {

        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230)),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 30));
        valueLabel.setForeground(new Color(41, 128, 185));

        card.add(headingLabel);
        card.add(valueLabel);

        return card;
    }
}