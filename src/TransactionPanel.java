import javax.swing.*;
import java.awt.*;

public class TransactionPanel extends BasePanel {
    public TransactionPanel() {
        super("Manage Transaction");

        options = new JButton[7];
        options[0] = new JButton("Buying of Company Policy Plan");
        options[1] = new JButton("Client Payment of Premium");
        options[2] = new JButton("Consultation with Doctor");
        options[3] = new JButton("Hospitalization");
        options[4] = new JButton("Payout to Hospital");
        options[5] = new JButton("Payout to Doctor");
        options[6] = new JButton("Request of Letter of Authorization");

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
