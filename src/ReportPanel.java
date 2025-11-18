import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.time.Year;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
        panel.add(Box.createHorizontalStrut(100));
        panel.add(yearLabel);
        panel.add(yearChooser);

        return panel;
    }

    private JEditorPane createEditorPane() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.getCaret().setVisible(false);
        editorPane.setOpaque(false);
        return editorPane;
    }

    private JPanel createReportPanel(JPanel selectionPanel, JEditorPane reportEditor) {
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        reportPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(reportEditor);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 50)));

        reportPanel.add(scrollPane, BorderLayout.CENTER);

        return reportPanel;
    }

    private void createReportOptions() {
        Map<String, JPanel> panelMap = new LinkedHashMap<>();
        reportEditors = new HashMap<>();

        String[] months = {"All Months", "January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November", "December"};
        monthChooser = new JComboBox<>(months);
        monthChooser.setSelectedIndex(0);
        UITools.styleComboBox(monthChooser);

        yearChooser = new JYearChooser();
        yearChooser.setPreferredSize(new Dimension(100, yearChooser.getPreferredSize().height));
        yearChooser.setYear(Year.now().getValue());
        UITools.styleYearChooser(yearChooser);

        generateButton = new JButton("Generate Report");
        UITools.styleButton(generateButton);

        exportButton = new JButton("Export Report");
        UITools.styleButton(exportButton);

        JPanel selectionPanel = createSelectionPanel();

        rightPanel.add(selectionPanel, BorderLayout.NORTH);

        String[] reportTypes = {
                "Financial Report",
                "Health Provider Report",
                "Policy Report",
                "Illness Trend"
        };

        for (String reportType : reportTypes) {
            JEditorPane editorPane = createEditorPane();
            reportEditors.put(reportType, editorPane);

            JPanel reportPanel = createReportPanel(selectionPanel, editorPane);
            panelMap.put(reportType, reportPanel);
        }

        populateCardLayout(panelMap);

        southButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        southButtonPanel.add(generateButton);
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

    public void addMonthChangeListener(ActionListener listener) {
        monthChooser.addActionListener(listener);
    }

    public void addYearChangeListener(PropertyChangeListener listener) {
        yearChooser.addPropertyChangeListener("year",listener);
    }

    public void enableYearSelection(boolean enable) {
        yearChooser.setEnabled(enable);
    }

    public void clearSelections() {
        monthChooser.setSelectedIndex(0);
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
        int selectedIndex = monthChooser.getSelectedIndex();
        if (selectedIndex == 0)
            return null;
        else
            return selectedIndex;
    }

    public int getSelectedYear() {
        return yearChooser.getYear();
    }

    private Map<String, JEditorPane> reportEditors;

    private JComboBox<String> monthChooser;
    private JYearChooser yearChooser;
    private JButton exportButton;
    private JButton generateButton;
}
