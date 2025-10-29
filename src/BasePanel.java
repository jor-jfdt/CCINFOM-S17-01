import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {
    public BasePanel(String title) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Algerian", Font.BOLD, AppGUI.fontSize));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(titleLabel, gbc);

        leftPanel = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        add(leftPanel, gbc);

        rightPanel = new JPanel();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public JPanel getLeftPanel() {
        return leftPanel;
    }

    public JPanel getRightPanel() {
        return rightPanel;
    }

    protected JLabel titleLabel;
    protected JPanel leftPanel;
    protected JPanel rightPanel;
    protected JButton[] options;
}
