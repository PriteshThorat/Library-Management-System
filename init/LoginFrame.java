package init;

import javax.swing.*;
import java.awt.*;
import src.config.EnvConfig;

public class LoginFrame {
    public LoginFrame () {
        JFrame frame = new JFrame("Library Management System Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel title = new JLabel("Library Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        panel.add(title);
        panel.add(new JLabel(""));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        frame.add(panel);

        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.equals(EnvConfig.getUsername()) && password.equals(EnvConfig.getPassword())) {
                    JOptionPane.showMessageDialog(
                        frame, "Login Successful!"
                    );
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
        });

        frame.setVisible(true);
    }
}