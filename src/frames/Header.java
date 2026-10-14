package src.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class Header extends JPanel {
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT = new Color(33, 37, 41);

    public Header(String title) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(0, 26, 0, 26)));
        setPreferredSize(new Dimension(0, 64));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(TEXT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label, BorderLayout.WEST);
    }
}
