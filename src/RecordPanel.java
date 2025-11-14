import javax.swing.*;
import java.awt.*;

public class RecordPanel extends BasePanel {
    public RecordPanel() {
        super("Record Management", "app_wallpaper.png");
        createButtons();
    }

    private void createButtons() {
        // Set a fixed size for the left panel to constrain it to 30% width
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        options = new JButton[5];
        options[0] = new JButton("Client");
        options[1] = new JButton("Company Policy");
        options[2] = new JButton("Illness");
        options[3] = new JButton("Hospital");
        options[4] = new JButton("Doctor");

        for (int i = 0; i < options.length; i++) {
            leftPanel.add(options[i]);
            if (i < options.length - 1) {
                leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        updateComponent();
    }

    @Override
    protected void updateComponent() {
        if (options != null) {
            int leftPanelWidth = (int)(AppGUI.screenWidth * 0.3);
            int hideButtonWidth = 25; // Account for hide button width
            int availableWidth = leftPanelWidth - hideButtonWidth - 60; // Extra margin for padding
            int buttonHeight = (int)(AppGUI.screenHeight * 0.08); // Slightly smaller height
            Dimension buttonSize = new Dimension(availableWidth, buttonHeight);

            Font buttonFont = new Font("Algerian", Font.PLAIN,
                    Math.max(AppGUI.screenWidth / 100, AppGUI.screenHeight / 60));

            for (JButton option : options) {
                option.setPreferredSize(buttonSize);
                option.setMaximumSize(buttonSize);
                option.setMinimumSize(buttonSize);
                option.setFont(buttonFont);
                option.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }
        leftPanel.revalidate();
        leftPanel.repaint();
    }
}