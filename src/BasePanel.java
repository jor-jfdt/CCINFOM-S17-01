import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePanel extends BackgroundPanel {
    public BasePanel(String title, String backgroundImage) {
        super(backgroundImage);
        this.title = title;
        this.tableMap = new HashMap<>();
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

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(Color.LIGHT_GRAY);

        centerCardPanel = new JPanel();
        rightCardLayout = new CardLayout();
        centerCardPanel.setLayout(rightCardLayout);

        southButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        rightPanel.add(centerCardPanel, BorderLayout.CENTER);
        rightPanel.add(southButtonPanel, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 5, 10, 10);
        add(rightPanel, gbc);

        JPanel emptyPanel = new JPanel();
        emptyPanel.add(new JLabel("Select an option."));
        centerCardPanel.add(emptyPanel, "empty");
        rightCardLayout.show(centerCardPanel, "empty");
    }

    protected void populateCardLayout(Map<String, JPanel> cardPanelMap) {
        options = new JButton[cardPanelMap.size()];
        int i = 0;
        for (String key : cardPanelMap.keySet()) {
            JPanel cardPanel = cardPanelMap.get(key);
            centerCardPanel.add(cardPanel, key);
            JButton button = new JButton(key);
            leftPanel.add(button);
            options[i] = button;
            i++;
            if (i < cardPanelMap.size() - 1) {
                leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
    }

    protected JPanel createCRUDPanel(String key, String searchLabel, String[] header) {
        JPanel crudPanel = new JPanel(new BorderLayout(10,10));
        crudPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel(searchLabel + " "), BorderLayout.WEST);
        searchPanel.add(new JTextField(), BorderLayout.CENTER);
        crudPanel.add(searchPanel, BorderLayout.NORTH);

        JTable table = new JTable(new DefaultTableModel(new Object[][] {}, header));

        tableMap.put(key, table);

        JScrollPane scrollPane = new JScrollPane(table);
        crudPanel.add(scrollPane, BorderLayout.CENTER);

        return crudPanel;
    }

    protected String getActivePanelKey() {
        return activePanelKey;
    }

    protected void setTable(String key, TableModel model) {
        if (tableMap.containsKey(key))
            tableMap.get(key).setModel(model);
        else
            System.err.println("No table found for key: " + key);
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

    public void showCard(String key) {
        rightCardLayout.show(centerCardPanel, key);
        activePanelKey = key;
    }

    public void addHideButtonListener(ActionListener listener) {
        hideButton.addActionListener(listener);
    }

    public void addOptionButtonListener(int index, ActionListener listener) {
        if (options != null && index >= 0 && index < options.length) {
            options[index].addActionListener(listener);
        }
    }

    protected abstract void createCRUDContent();
    protected abstract void updateComponent();

    protected String activePanelKey;
    protected Map<String, JTable> tableMap;

    protected String title;
    protected JLabel titleLabel;
    protected JPanel leftPanel;
    protected JPanel rightPanel;
    protected JButton[] options;
    protected JButton hideButton;

    protected CardLayout rightCardLayout;
    protected JPanel centerCardPanel;
    protected JPanel southButtonPanel;
}