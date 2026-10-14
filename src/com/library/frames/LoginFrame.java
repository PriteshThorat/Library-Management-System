package com.library.frames;

import com.library.config.EnvConfig;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
    private static final Color BLUE = new Color(26, 115, 232);
    private static final Color BLUE_HOVER = new Color(20, 92, 190);
    private static final Color FIELD_BG = new Color(245, 247, 250);
    private static final Color TEXT = new Color(33, 37, 41);
    private static final Color MUTED = new Color(150, 156, 165);
    private final JTextField username = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);

    public LoginFrame() {
        super("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new javax.swing.border.EmptyBorder(34, 48, 34, 48));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.add(new JLabel(new BookIcon(42)));

        JLabel title = new JLabel("Library Management System");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT);
        heading.add(title);

        c.gridwidth = 2;
        content.add(heading, at(c, 0, 0));

        style(username);
        style(password);
        username.setText("Enter username");
        password.setText("Enter password");
        password.setEchoChar((char) 0);
        addHint(username, "Enter username");
        addHint(password, "Enter password");

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        content.add(label("Username"), at(c, 0, 2));

        c.anchor = GridBagConstraints.WEST;
        content.add(username, at(c, 1, 2));

        c.anchor = GridBagConstraints.EAST;
        content.add(label("Password"), at(c, 0, 3));

        c.anchor = GridBagConstraints.WEST;
        content.add(password, at(c, 1, 3));

        JButton login = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setColor(getModel().isRollover() ? BLUE_HOVER : BLUE);
                g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        login.setForeground(Color.WHITE);
        login.setFont(new Font("Arial", Font.BOLD, 15));
        login.setFocusPainted(false);
        login.setBorderPainted(false);
        login.setContentAreaFilled(false);
        login.setOpaque(false);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        login.setPreferredSize(new Dimension(220, 42));
        c.gridy = 4;
        c.gridx = 1;
        content.add(login, c);
        login.addActionListener(event -> signIn());
        add(content, BorderLayout.CENTER);
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text + ":");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT);
        return label;
    }

    private void style(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT);
        field.setBorder(new javax.swing.border.EmptyBorder(8, 12, 8, 12));
        field.setPreferredSize(new Dimension(300, 38));
    }

    private void addHint(JTextField field, String hint) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(TEXT);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('\u2022');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(MUTED);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
        field.setForeground(MUTED);
    }

    private GridBagConstraints at(GridBagConstraints source, int x, int y) {
        GridBagConstraints copy = (GridBagConstraints) source.clone();
        copy.gridx = x;
        copy.gridy = y;
        return copy;
    }
    private void signIn() {
        boolean validUsername = username.getText().trim().equals(EnvConfig.getUsername());
        boolean validPassword = new String(password.getPassword()).equals(EnvConfig.getPassword());

        if (validUsername && validPassword) {
            dispose();
            new DashboardFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class BookIcon implements Icon {
        private final int size;
        BookIcon(int size) { this.size = size; }
        public int getIconWidth() { return size; }
        public int getIconHeight() { return size; }
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color[] colors = { new Color(66, 133, 244), new Color(52, 168, 83), new Color(251, 188, 5) };
            for (int i = 0; i < 3; i++) {
                int height = (size - 10) / 3;
                int width = size - 8 - i * 8;
                int offset = i * 4;
                int top = y + 3 + i * (height + 2);
                g.setColor(colors[i]);
                g.fillRoundRect(x + offset, top, width, height, 5, 5);
                g.setColor(new Color(255, 255, 255, 140));
                g.fillRoundRect(x + offset + 4, top + 3, 3, height - 6, 2, 2);
            }
            g.dispose();
        }
    }
}