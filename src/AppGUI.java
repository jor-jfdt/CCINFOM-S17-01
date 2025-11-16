import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class AppGUI extends JFrame {
    public AppGUI(String title) {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardContainer = new JPanel(new CardLayout());

        this.setMinimumSize(new Dimension(1280, 720));
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
        showPanel("record");

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        SwingUtilities.invokeLater(this::testDialog);
    }

    //ai test dialog cuz i am fking lazy
    private void testDialog() {
        // Test ADD mode
        JButton testButton = new JButton("Test ADD Dialog");
        testButton.addActionListener(e -> {
            CRUDDialog dialog = new CRUDDialog(
                    this,
                    CRUDDialog.Mode.ADD,
                    new String[]{"first_name", "last_name", "middle_initial", "birth_date", "is_employee", "sex"},
                    "Add Client"
            );
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                System.out.println("Dialog confirmed!");
                System.out.println("Values: " + dialog.getFieldValues());
            } else {
                System.out.println("Dialog cancelled");
            }
        });

        // Test UPDATE mode
        JButton testUpdateButton = new JButton("Test UPDATE Dialog");
        testUpdateButton.addActionListener(e -> {
            CRUDDialog dialog = new CRUDDialog(
                    this,
                    CRUDDialog.Mode.UPDATE,
                    new String[]{"first_name", "last_name", "middle_initial", "birth_date", "is_employee", "sex"},
                    "Update Client"
            );

            // Pre-populate with test data
            dialog.setFieldValue("first_name", "John");
            dialog.setFieldValue("last_name", "Doe");
            dialog.setFieldValue("middle_initial", "A");
            dialog.setFieldValue("birth_date", "1990-01-01");
            dialog.setFieldValue("is_employee", "true");
            dialog.setFieldValue("sex", "M");

            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                System.out.println("Updated values: " + dialog.getFieldValues());
            }
        });

        // Test DELETE mode
        JButton testDeleteButton = new JButton("Test DELETE Dialog");
        testDeleteButton.addActionListener(e -> {
            CRUDDialog dialog = new CRUDDialog(
                    this,
                    CRUDDialog.Mode.DELETE,
                    new String[]{"first_name", "last_name", "middle_initial", "birth_date", "is_employee", "sex"},
                    "Delete Client"
            );

            dialog.setFieldValue("first_name", "John");
            dialog.setFieldValue("last_name", "Doe");
            dialog.setFieldValue("sex", "M");

            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                System.out.println("Record deleted!");
            }
        });

        // Add test buttons to main panel
        JPanel testPanel = new JPanel(new FlowLayout());
        testPanel.setOpaque(false);
        testPanel.add(testButton);
        testPanel.add(testUpdateButton);
        testPanel.add(testDeleteButton);

        mainPanel.add(testPanel);
        mainPanel.revalidate();
    }

    private void createMainPanel() {
        JPanel buttonPanel = new JPanel();
        mainPanel = new BackgroundPanel("app_wallpaper.png");
        menuButtons = new JButton[3];

        try {
            BufferedImage logoImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/resources/hmo_logo.png")
            ));
            applicationLogoLabel = new JLabel(new ImageIcon(logoImage));
        } catch (IOException e) {
            e.printStackTrace();
            applicationLogoLabel = new JLabel(); // Empty label if image fails
        }

        applicationTitleLabel = new JLabel("HMO Monitoring System");

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalGlue());

        mainPanel.add(applicationLogoLabel);
        applicationLogoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createRigidArea(new Dimension(0, getHeight() / 40))); // Spacing between logo and title

        mainPanel.add(applicationTitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, getHeight() / 20)));

        applicationTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setOpaque(false);
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
        //transactionPanel = new TransactionPanel();
        //reportPanel = new ReportPanel();

        cardContainer.add(mainPanel, "main");
        cardContainer.add(recordPanel, "record");
        //cardContainer.add(transactionPanel, "transaction");
        //cardContainer.add(reportPanel, "report");

        this.add(cardContainer, BorderLayout.CENTER);
    }

    public void showPanel(String panelType) {
        CardLayout cardLayout = (CardLayout) cardContainer.getLayout();

        switch (panelType.toLowerCase()) {
            case "main":
                cardLayout.show(cardContainer, "main");
                break;
            case "record":
                cardLayout.show(cardContainer, "record");
                break;
            case "transaction":
                cardLayout.show(cardContainer, "transaction");
                break;
            case "report":
                cardLayout.show(cardContainer, "report");
                break;
        }
    }

    public void update() {
        int logoSize = Math.min(screenWidth / 8, screenHeight / 6);

        if (applicationLogoLabel != null && applicationLogoLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) applicationLogoLabel.getIcon();
            Image scaledImage = icon.getImage().getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
            applicationLogoLabel.setIcon(new ImageIcon(scaledImage));
        }

        applicationTitleLabel.setFont(UITools.getTitleFont());
        applicationTitleLabel.setForeground(UITools.getPrimaryColor());

        Dimension buttonSize = UITools.getMainMenuButtonSize();
        Font buttonFont = UITools.getButtonFont();
        for (JButton button : menuButtons) {
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setFont(buttonFont);
        }

        if (recordPanel != null)
            recordPanel.update();
        //if (transactionPanel != null)
        //    transactionPanel.update();
        //if (reportPanel != null)
        //    reportPanel.update();
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

    public void addMenuButtonListener(ActionListener listener) {
        for (JButton button : menuButtons) {
            button.addActionListener(listener);
        }
    }

    private final JPanel cardContainer;
    private JLabel applicationTitleLabel;
    private JLabel applicationLogoLabel;
    private JPanel mainPanel;
    private RecordPanel recordPanel;
    //private TransactionPanel transactionPanel;
    //private ReportPanel reportPanel;
    private JButton[] menuButtons;

    public static final double ASPECT_RATIO = 16.0 / 9.0;
    public static int screenWidth = 1280;
    public static int screenHeight = (int) (screenWidth / ASPECT_RATIO);
}
