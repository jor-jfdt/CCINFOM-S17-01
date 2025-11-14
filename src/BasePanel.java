import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends BackgroundPanel {
    public BasePanel(String title, String backgroundImage) {
        super(backgroundImage);
        this.title = title;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        titleLabel = new JLabel(title, SwingConstants.CENTER);
        updateFont();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(titleLabel, gbc);

        leftPanel = new JPanel();
        leftPanel.setOpaque(false); // Make transparent to show background
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setOpaque(false);
        leftScrollPane.getViewport().setOpaque(false);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        hideButton = new JButton("◀");
        hideButton.setPreferredSize(new Dimension(25, 50));
        hideButton.setMinimumSize(new Dimension(25, 30));
        hideButton.setBackground(Color.LIGHT_GRAY);
        hideButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);
        leftContainer.add(leftScrollPane, BorderLayout.CENTER);
        leftContainer.add(hideButton, BorderLayout.EAST);
        leftContainer.setPreferredSize(new Dimension(100, 100));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 5);
        add(leftContainer, gbc);

        rightPanel = new JPanel();
        rightPanel.setOpaque(true);
        rightPanel.setPreferredSize(new Dimension(233, 100));
        rightPanel.setBackground(Color.LIGHT_GRAY);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 5, 10, 10);
        add(rightPanel, gbc);
    }

    protected void update() {
        updateFont();
        updateComponent();
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    public void updateFont() {
        titleLabel.setFont(new Font("Algerian", Font.BOLD,
                Math.max(AppGUI.screenWidth / 40, AppGUI.screenHeight / 25)));
    }

    protected abstract void updateComponent();

    protected String title;
    protected JLabel titleLabel;
    protected JPanel leftPanel;
    protected JPanel rightPanel;
    protected JButton[] options;
    protected JButton hideButton;
}