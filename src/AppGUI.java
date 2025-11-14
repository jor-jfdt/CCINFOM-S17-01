import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AppGUI extends JFrame {
    public AppGUI(String title) {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardContainer = new JPanel(new CardLayout());

        this.setMinimumSize(new Dimension(640, 360));
        this.setMaximumSize(new Dimension(1920, 1080));

        this.addComponentListener(new ComponentAdapter() {
            private boolean isResizing = false;

            @Override
            public void componentResized(ComponentEvent e) {
                if (isResizing) return;

                isResizing = true;

                Insets insets = getInsets();
                int contentWidth = getWidth() - insets.left - insets.right;
                int contentHeight = getHeight() - insets.top - insets.bottom;

                int newContentHeight = (int) (contentWidth / ASPECT_RATIO);

                if (Math.abs(newContentHeight - contentHeight) > 5) {
                    int newFrameHeight = newContentHeight + insets.top + insets.bottom;
                    setSize(getWidth(), newFrameHeight);
                }

                int finalContentWidth = getWidth() - getInsets().left - getInsets().right;
                int finalContentHeight = getHeight() - getInsets().top - getInsets().bottom;
                updateScreenDimensions(finalContentWidth, finalContentHeight);
                update();

                isResizing = false;
            }
        });

        initializePanels();
        showPanel("main");

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createMainPanel() {
        JPanel buttonPanel = new JPanel();
        mainPanel = new JPanel();
        menuButtons = new JButton[3];
        applicationTitleLabel = new JLabel("HMO Monitoring System"); // Store reference

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(applicationTitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, getHeight() / 20)));

        applicationTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createHorizontalBox());

        menuButtons[0] = new JButton("Record Management");
        menuButtons[1] = new JButton("Manage Transaction");
        menuButtons[2] = new JButton("Generate Report");

        for (int i = 0; i < menuButtons.length; i++) {
            buttonPanel.add(menuButtons[i]);
            if (i < menuButtons.length - 1) {
                buttonPanel.add(Box.createRigidArea(new Dimension(getWidth() / 100, 0)));
            }
        }

        buttonPanel.add(Box.createHorizontalBox());
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        update();
    }

    public void initializePanels() {
        createMainPanel();
        recordPanel = new RecordPanel();

        cardContainer.add(mainPanel, "main");
        cardContainer.add(recordPanel, "record");

        this.add(cardContainer, BorderLayout.CENTER);
    }

    public void showPanel(String panelType) {
        CardLayout cardLayout = (CardLayout) cardContainer.getLayout();

        switch (panelType.toLowerCase()) {
            case "main":
                cardLayout.show(cardContainer, "main");
                String currentPanel = "main";
                break;
            case "record":
                cardLayout.show(cardContainer, "record");
                currentPanel = "record";
                break;
        }
    }

    public void update() {
        int fontSize = Math.max(screenWidth / 40, screenHeight / 25);
        int buttonFontSize = Math.max(screenWidth / 80, screenHeight / 40);
        Dimension buttonSize = new Dimension(screenWidth / 4, screenHeight / 8);

        // Direct field access - no component tree traversal needed
        applicationTitleLabel.setFont(new Font("Algerian", Font.BOLD, fontSize));

        Font buttonFont = new Font("Algerian", Font.PLAIN, buttonFontSize);
        for (JButton button : menuButtons) {
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setFont(buttonFont);
        }

        if (recordPanel != null)
            recordPanel.update();
    }

    public static void updateScreenDimensions(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton[] getMenuButtons() {
        return menuButtons;
    }

    private final JPanel cardContainer;
    private JLabel applicationTitleLabel;
    private JPanel mainPanel;
    private RecordPanel recordPanel;
    private JButton[] menuButtons;

    public static final double ASPECT_RATIO = 16.0 / 9.0;
    public static int screenWidth = 1280;
    public static int screenHeight = (int) (screenWidth / ASPECT_RATIO);
}
