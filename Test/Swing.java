package Test;

import javax.swing.*;

public class Swing {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My First Swing App");

        JLabel label = new JLabel("Welcome to Java Swing!");
        JButton button = new JButton("Click Me");

        button.addActionListener(e -> {
            label.setText("Button clicked!");
        });

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(button);

        frame.add(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}