package src.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

import src.config.EnvConfig;

public class LoginFrame {
    private static final Color BLUE = new Color(26, 115, 232);
    private static final Color BLUE_HOVER = new Color(20, 92, 190);
    private static final Color FIELD_BG = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color HINT_COLOR = new Color(150, 156, 165);

    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 15);

    private static final String USERNAME_HINT = "Enter username";
    private static final String PASSWORD_HINT = "Enter password";

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginFrame() {
        JFrame frame = new JFrame("Library Management System");
        WindowIcon.apply(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(30, 45, 30, 45));

        JLabel title = new JLabel("Library Management System");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_COLOR);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(new JLabel(new BookIcon(44)));
        header.add(title);

        usernameField = createTextField(USERNAME_HINT);
        passwordField = createPasswordField(PASSWORD_HINT);

        JLabel usernameLabel = createLabel("Username:");
        JLabel passwordLabel = createLabel("Password:");

        JButton loginButton = new JButton("Login") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BLUE_HOVER : BLUE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setFont(BUTTON_FONT);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(220, 42));
        loginButton.setMaximumSize(new Dimension(220, 42));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(9, 8, 9, 8);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0;
        form.add(usernameLabel, gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        form.add(usernameField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0;
        form.add(passwordLabel, gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        form.add(passwordField, gc);

        gc.gridx = 1;
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0;
        form.add(loginButton, gc);

        content.add(header);
        content.add(Box.createVerticalStrut(22));
        content.add(form);
        content.add(Box.createVerticalStrut(18));

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Color.WHITE);
        root.add(content);

        frame.add(root);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        loginButton.addActionListener(e -> handleLogin(frame));

        frame.setVisible(true);
    }

    private void handleLogin(JFrame frame) {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.equals(USERNAME_HINT)) {
                username = "";
            }
            if (password.equals(PASSWORD_HINT) || passwordField.getEchoChar() == 0) {
                password = "";
            }

            if (username.equals(EnvConfig.getUsername()) && password.equals(EnvConfig.getPassword())) {
                frame.dispose();
                
                new DashboardFrame();
            } else {
                JOptionPane.showMessageDialog(
                    frame,
                    "Invalid Username or Password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception error) {
            JOptionPane.showMessageDialog(
                frame,
                "Could not read configuration: " + error.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(FIELD_BG);
        field.setCaretColor(TEXT_COLOR);
        field.setPreferredSize(new Dimension(220, 38));
        field.setMinimumSize(new Dimension(220, 38));
        field.setBorder(new EmptyBorder(6, 12, 6, 12));
    }

    private JTextField createTextField(String hint) {
        JTextField field = new JTextField(hint);
        styleField(field);
        field.setForeground(HINT_COLOR);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(HINT_COLOR);
                }
            }
        });
        return field;
    }

    private JPasswordField createPasswordField(String hint) {
        JPasswordField field = new JPasswordField();
        styleField(field);
        field.setEchoChar((char) 0);
        field.setText(hint);
        field.setForeground(HINT_COLOR);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(hint)) {
                    field.setText("");
                    field.setEchoChar('\u2022');
                    field.setForeground(TEXT_COLOR);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(hint);
                    field.setForeground(HINT_COLOR);
                }
            }
        });
        return field;
    }

    private static class BookIcon implements Icon {
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
}
