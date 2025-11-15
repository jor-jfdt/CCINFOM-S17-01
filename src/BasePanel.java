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

        addTitleSection(gbc);
        addLeftPanel(gbc);
        addRightPanel(gbc);
    }

    private void addTitleSection(GridBagConstraints gbc) {
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        updateFont();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(titleLabel, gbc);
    }

    private void addLeftPanel(GridBagConstraints gbc) {
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);

        JScrollPane leftScrollPane = createScrollPane(leftPanel);
        hideButton = createHideButton();

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setOpaque(false);
        eastPanel.add(separator, BorderLayout.WEST);
        eastPanel.add(hideButton, BorderLayout.CENTER);

        leftContainer.add(leftScrollPane, BorderLayout.CENTER);
        leftContainer.add(eastPanel, BorderLayout.EAST);

        // GridBagConstraints for the main panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 5);
        add(leftContainer, gbc);
    }

    private JScrollPane createScrollPane(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JButton createHideButton() {
        JButton button = new JButton("◀");

        button.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
        button.setForeground(new Color(0, 105, 55));

        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setPreferredSize(new Dimension(30, 40));
        button.setMinimumSize(new Dimension(30, 30));
        return button;
    }

    private void initializeEmptyCard() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);

        JLabel emptyLabel = new JLabel("Select an option.");
        emptyLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));

        emptyPanel.add(emptyLabel, new GridBagConstraints());
        centerCardPanel.add(emptyPanel, "empty");
        rightCardLayout.show(centerCardPanel, "empty");
    }

    protected void populateCardLayout(Map<String, JPanel> cardPanelMap) {
        options = new JButton[cardPanelMap.size()];
        int index = 0;

        for (Map.Entry<String, JPanel> entry : cardPanelMap.entrySet()) {
            addCardPanel(entry.getKey(), entry.getValue(), index);
            index++;
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = index;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        leftPanel.add(Box.createVerticalGlue(), gbc);
    }

    private void addCardPanel(String key, JPanel panel, int index) {
        centerCardPanel.add(panel, key);

        JButton button = new JButton(key);
        button.putClientProperty("cardKey", key);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = index;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(0, 0, (index < options.length - 1) ? 10 : 0, 0);

        leftPanel.add(button, gbc);

        options[index] = button;
    }

    private void addRightPanel(GridBagConstraints gbc) {
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        centerCardPanel = new JPanel();
        centerCardPanel.setOpaque(false);
        rightCardLayout = new CardLayout();
        centerCardPanel.setLayout(rightCardLayout);

        southButtonPanel = new JPanel(new BorderLayout(10, 10));
        southButtonPanel.setOpaque(false);

        rightPanel.add(centerCardPanel, BorderLayout.CENTER);
        rightPanel.add(southButtonPanel, BorderLayout.SOUTH);

        initializeEmptyCard();

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.9;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 5, 10, 10);
        add(rightPanel, gbc);
    }

    protected JPanel createCRUDPanel(String key, String searchLabel, String[] header) {
        JPanel crudPanel = new JPanel(new BorderLayout(10,10));
        crudPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));

        JLabel label = new JLabel(searchLabel + " ");
        label.setForeground(UITools.getPrimaryColor());
        label.setFont(UITools.getLabelFont());

        searchPanel.add(label, BorderLayout.WEST);

        JTextField searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);

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
        titleLabel.setFont(UITools.getTitleFont());
        titleLabel.setForeground(UITools.getPrimaryColor());
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