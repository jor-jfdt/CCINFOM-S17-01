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
        backButton = new JButton("◀");
        updateFont();

        backButton.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
        backButton.setForeground(new Color(0, 105, 55));

        backButton.setOpaque(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(backButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(titleLabel, gbc);
    }

    private void addLeftPanel(GridBagConstraints gbc) {
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);

        hideButton = createHideButton();

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        eastPanel = new JPanel(new BorderLayout());
        eastPanel.setOpaque(false);
        eastPanel.add(separator, BorderLayout.WEST);
        eastPanel.add(hideButton, BorderLayout.CENTER);

        //Made this into a class variable so that it can be accessed in other methods
        leftContainer.add(leftPanel, BorderLayout.CENTER);
        leftContainer.add(eastPanel, BorderLayout.EAST);

        leftContainer.setPreferredSize(new Dimension(1, 1));

        gbcLeftConstraints(gbc);
        add(leftContainer, gbc);
    }

    private JButton createHideButton() {
        JButton button = new JButton("◀");

        button.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
        button.setForeground(new Color(0, 105, 55));

        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setPreferredSize(new Dimension(40, 40));
        button.setMinimumSize(new Dimension(40, 40));
        return button;
    }

    protected void initializeEmptyCard() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);

        JLabel emptyLabel = new JLabel("Select an option.");
        emptyLabel.setFont(UITools.getTitleFont());
        emptyLabel.setForeground(UITools.getPrimaryColor());

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

    protected void addCardPanel(String key, JPanel panel, int index) {
        centerCardPanel.add(panel, key);

        JButton button = new JButton(key);
        button.putClientProperty("cardKey", key);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = index;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        gbc.insets = new Insets(0, 0, (index < options.length - 1) ? 10 : 0, 0);

        leftPanel.add(button, gbc);

        options[index] = button;
    }

    protected void addRightPanel(GridBagConstraints gbc) {
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

        rightPanel.setPreferredSize(new Dimension(1, 1));

        gbcRightConstraints(gbc);
        add(rightPanel, gbc);
    }

    protected JPanel createCRUDPanel(String key, String searchLabel, String[] header) {
        JPanel crudPanel = new JPanel(new BorderLayout(10,10));
        crudPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        crudPanel.setOpaque(false);

        topCardPanel = new JPanel(new BorderLayout(5, 0));
        topCardPanel.setOpaque(false);

        JLabel label = new JLabel(searchLabel + " ");
        label.setForeground(UITools.getPrimaryColor());
        label.setFont(UITools.getLabelFont());

        topCardPanel.add(label, BorderLayout.WEST);

        JTextField searchField = new JTextField();
        topCardPanel.add(searchField, BorderLayout.CENTER);

        crudPanel.add(topCardPanel, BorderLayout.NORTH);

        JTable table = new JTable(new DefaultTableModel(new Object[][] {}, header));

        tableMap.put(key, table);
        table.setFont(UITools.getLabelFont());

        table.getTableHeader().setFont(UITools.getLabelFont());
        table.getTableHeader().setForeground(UITools.getPrimaryColor());

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

    protected abstract void createHeaderColumns();

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

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public Map<String, String[]> getHeaderColumns() {
        return header_columns;
    }

    protected void updateComponent() {
        if (options != null) {
            Dimension buttonSize = UITools.getLeftPanelButtonSize();
            Font buttonFont = UITools.getLeftPanelButtonFont();

            for (JButton option : options) {
                option.setFont(buttonFont);
                option.setPreferredSize(null);
                option.setMinimumSize(new Dimension(0, buttonSize.height));
                option.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonSize.height));
            }
        }

        if (hideButton != null) {
            Dimension hideButtonSize = UITools.getHideButtonSize();
            hideButton.setPreferredSize(hideButtonSize);
            hideButton.setMinimumSize(hideButtonSize);
            hideButton.setMaximumSize(hideButtonSize);
            hideButton.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
        }

        leftPanel.revalidate();
        leftPanel.repaint();
    }
    public void toggleOptionsVisibility() {
        optionsVisible = !optionsVisible;
        GridBagLayout layout = (GridBagLayout) getLayout();
        GridBagConstraints gbcLeft = layout.getConstraints(leftContainer);
        GridBagConstraints gbcRight = layout.getConstraints(rightPanel);

        if (!optionsVisible) {
            leftPanel.setVisible(false);
            //balik muna sa zero dimension
            leftPanel.setPreferredSize(new Dimension(0,0));

            //remove eastPanel(which holds the hide button)
            // move it to the far left side
            leftContainer.remove(eastPanel);
            leftContainer.add(hideButton, BorderLayout.WEST);

            Dimension hideButtonSize = new Dimension(40, 40);
            //unting space para sa seperator yung 4
            leftContainer.setPreferredSize(new Dimension(hideButtonSize.width+ 4, hideButtonSize.height));
            gbcLeft.weightx = 0.0;
            gbcRight.weightx = 1.0;

            hideButton.setText("▶");

        } else {
            leftContainer.remove(hideButton);
            eastPanel.add(hideButton, BorderLayout.CENTER);

            leftPanel.setVisible(true);
            leftPanel.setPreferredSize(null);
            leftContainer.setPreferredSize(new Dimension(1, 1));

            leftContainer.add(leftPanel, BorderLayout.CENTER);
            leftContainer.add(eastPanel, BorderLayout.EAST);

            gbcLeftConstraints(gbcLeft);

            gbcRightConstraints(gbcRight);
            hideButton.setText("◀");
        }
        layout.setConstraints(leftContainer, gbcLeft);
        layout.setConstraints(rightPanel, gbcRight);

        if (options != null) {
            for (JButton option : options) {
                option.setVisible(optionsVisible);
            }
        }
        updateComponent();
        leftContainer.revalidate();
        leftContainer.repaint();
        revalidate();
        repaint();
    }

    private void gbcRightConstraints(GridBagConstraints gbcRight) {
        gbcRight.gridx = 1;
        gbcRight.gridy = 1;
        gbcRight.gridwidth = 1;
        gbcRight.weightx = 0.6;
        gbcRight.weighty = 0.9;
        gbcRight.fill = GridBagConstraints.BOTH;
        gbcRight.insets = new Insets(10, 5, 10, 10);
    }

    private void gbcLeftConstraints(GridBagConstraints gbcLeft) {
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 1;
        gbcLeft.gridwidth = 1;
        gbcLeft.weightx = 0.4;
        gbcLeft.weighty = 0.9;
        gbcLeft.fill = GridBagConstraints.BOTH;
        gbcLeft.insets = new Insets(10, 10, 10, 5);
    }

    public JButton getHideButton() {
        return hideButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton[] getOptions() { return options;}

    protected  boolean optionsVisible = true;
    protected String activePanelKey;
    protected Map<String, JTable> tableMap;
    protected Map<String, String[]> header_columns;

    protected String title;
    protected JLabel titleLabel;
    protected JPanel leftPanel;
    protected JPanel leftContainer;
    protected JPanel eastPanel;
    protected JPanel rightPanel;
    protected JButton[] options;
    protected JButton hideButton;
    protected JButton backButton;

    protected CardLayout rightCardLayout;
    protected JPanel topCardPanel;
    protected JPanel centerCardPanel;
    protected JPanel southButtonPanel;
}