import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.time.Year;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;



public class ReportPanel extends BasePanel {
    public ReportPanel() {
        super("Generate Report", "app_wallpaper.png");

        createReportOptions();

        showCard("empty");

        updateComponent();
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JLabel monthLabel = new JLabel("Select Month:");
        monthLabel.setForeground(UITools.getPrimaryColor());
        monthLabel.setFont(UITools.getLabelFont());

        JLabel yearLabel = new JLabel("Select Year:");
        yearLabel.setForeground(UITools.getPrimaryColor());
        yearLabel.setFont(UITools.getLabelFont());

        panel.add(monthLabel);
        panel.add(monthChooser);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(yearLabel);
        panel.add(yearChooser);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(generateButton);

        return panel;
    }

    private JEditorPane createEditorPane() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.getCaret().setVisible(false);

        return editorPane;
    }

    private JPanel createReportPanel(JEditorPane editorPane) {
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        reportPanel.setOpaque(false);

        reportPanel.add(createSelectionPanel(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,50)));

        reportPanel.add(scrollPane, BorderLayout.CENTER);

        return reportPanel;
    }

    private void createReportOptions() {
        Map<String, JPanel> panelMap = new LinkedHashMap<>();
        reportEditors = new HashMap<>();
        // Create shared selection components FIRST
        monthChooser = new JMonthChooser();
        monthChooser.setMonth(-1);

        yearChooser = new JYearChooser();
        yearChooser.setPreferredSize(new Dimension(100, yearChooser.getPreferredSize().height));

        generateButton = new JButton("Generate Report");
        UITools.styleButton(generateButton);

        JEditorPane financialPane = createEditorPane();
        reportEditors.put("Financial Report", financialPane);
        panelMap.put("Financial Report", createReportPanel(financialPane));

        JEditorPane providerPane = createEditorPane();
        reportEditors.put("Health Provider Report", providerPane);
        panelMap.put("Health Provider Report", createReportPanel(providerPane));

        JEditorPane policyPane = createEditorPane();
        reportEditors.put("Policy Report", policyPane);
        panelMap.put("Policy Report", createReportPanel(policyPane));

        JEditorPane illnessPane = createEditorPane();
        reportEditors.put("Illness Trend", illnessPane);
        panelMap.put("Illness Trend", createReportPanel(illnessPane));

        populateCardLayout(panelMap);

        exportButton = new JButton("Export Report");
        UITools.styleButton(exportButton);

        southButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        southButtonPanel.add(exportButton);
    }


    //no header columns needed for report panel
    @Override
    protected void createHeaderColumns() { }

    public void addGenerateReportListener(ActionListener listener) {
        generateButton.addActionListener(listener);
    }

    public void addExportReportListener(ActionListener listener) {
        exportButton.addActionListener(listener);
    }

    public void addMonthChangeListener(PropertyChangeListener listener) {
        monthChooser.addPropertyChangeListener("month",listener);
    }

    public void addYearChangeListener(PropertyChangeListener listener) {
        yearChooser.addPropertyChangeListener("year",listener);
    }

    public void enableMonthSelection(boolean enable) {
        monthChooser.setEnabled(enable);
    }

    public void enableYearSelection(boolean enable) {
        yearChooser.setEnabled(enable);
    }

    public void clearSelections() {
        monthChooser.setMonth(-1);
        yearChooser.setYear(Year.now().getValue());
        monthChooser.setEnabled(true);
        yearChooser.setEnabled(true);
    }

    public void setReportContent(String htmlContent) {
        JEditorPane activePane = getReportEditorPane();
        if (activePane != null) {
            activePane.setText(htmlContent);
            activePane.setCaretPosition(0);
        } else {
            System.err.println("Error: Could not find active editor pane for key: " + getActivePanelKey());
        }
    }

    public String getReportContent() {
        JEditorPane activePane = getReportEditorPane();
        if (activePane != null) {
            return activePane.getText();
        }
        return "";
    }

    public JEditorPane getReportEditorPane() {
        return reportEditors.get(getActivePanelKey());
    }

    public Integer getSelectedMonth() {
        int month = monthChooser.getMonth();
        return (month >= 0) ? month + 1 : null;
    }

    public int getSelectedYear() {
        return yearChooser.getYear();
    }

    private Map<String, JEditorPane> reportEditors;

    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private JButton exportButton;
    private JButton generateButton;
}
