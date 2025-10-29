import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AppGUI extends JFrame {
    public AppGUI(String title) {
        super(title);
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        prevHeight = this.getHeight();
        prevWidth = this.getWidth();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!isResizing)
                maintainAspectRatio();
            }
        });

        createCenterPanel();

        this.setMinimumSize(new Dimension(640, 360));
        this.setMaximumSize(new Dimension(1920, 1080));
        this.pack();

        updateScreenDimensions(this.getWidth(), this.getHeight());

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createCenterPanel() {
        int curWidth = this.getWidth();
        int curHeight = this.getHeight();

        fontSize = Math.max(curWidth / 40, curHeight / 25);
        buttonFontSize = Math.max(curWidth / 80, curHeight / 40);
        buttonWidth = curWidth / 4;
        buttonHeight = curHeight / 8;

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);

        centerPanel =  new JPanel();
        buttonPanel = new JPanel();
        menuButtons = new JButton[3];
        JLabel applicationName = new JLabel("HMO Monitoring System");

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(applicationName);
        centerPanel.add(Box.createRigidArea(new Dimension(0,curHeight / 20)));

        applicationName.setFont(new Font("Algerian", Font.BOLD, fontSize));
        applicationName.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createHorizontalBox());

        menuButtons[0] = new JButton("Record Management");
        menuButtons[1]  = new JButton("Manage Transaction");
        menuButtons[2]  = new JButton("Generate Report");

        for (int i = 0; i < menuButtons.length; i++) {
            JButton button = menuButtons[i];
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setFont(new Font("Algerian", Font.PLAIN, buttonFontSize));

            buttonPanel.add(button);
            if (i < menuButtons.length - 1) {
                buttonPanel.add(Box.createRigidArea(new Dimension(curWidth / 100, 0))); // Horizontal spacing
            }
        }
        buttonPanel.add(Box.createHorizontalBox());

        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        this.add(centerPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    private void maintainAspectRatio() {
        int ap_width;
        int ap_height;

        isResizing = true;

        Rectangle bounds = getBounds();
        int width = bounds.width;
        int height = bounds.height;

        if (width == prevWidth && height == prevHeight) {
            isResizing = false;
            return;
        }

        if (Math.abs(width - prevWidth) > Math.abs(height - prevHeight)) {
            ap_width = width;
            ap_height =  (int) (ap_width / ASPECT_RATIO);
        } else {
            ap_height = height;
            ap_width = (int) (ap_height * ASPECT_RATIO);
        }

        Rectangle newBounds = getBounds();
        prevWidth = ap_width;
        prevHeight = ap_height;

        updateScreenDimensions(ap_width, ap_height);

        SwingUtilities.invokeLater(() -> {
            setBounds(newBounds.x, newBounds.y, prevWidth, prevHeight);

            getContentPane().removeAll();
            createCenterPanel();
            revalidate();
            repaint();

            isResizing = false;
        });
    }

    public static void updateScreenDimensions(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    public JPanel getCenterPanel() {
        return centerPanel;
    }

    public JButton[] getMenuButtons() {
        return menuButtons;
    }

    private JPanel centerPanel;
    private JPanel buttonPanel;
    private JButton[] menuButtons;

    private boolean isResizing = false;
    private static int prevWidth;
    private static int prevHeight;

    public static int fontSize;
    public static int buttonFontSize;
    public static int buttonWidth;
    public static int buttonHeight;

    public static final double ASPECT_RATIO = 16.0 / 9.0;
    public static int screenWidth = 1280;
    public static int screenHeight = (int) (screenWidth / ASPECT_RATIO);
}
