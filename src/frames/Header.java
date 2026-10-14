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

        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 10, 0, 0);
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.VERTICAL;
        gc.gridx = 0;
        gc.gridy = 0;
        left.add(new JLabel(new BookIcon(28)), gc);
        gc.gridx = 1;
        gc.insets = new Insets(0, 10, 0, 0);
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(TEXT);
        left.add(label, gc);
        add(left, BorderLayout.WEST);
    }
}
