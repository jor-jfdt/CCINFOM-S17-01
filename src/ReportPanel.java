import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;



public class ReportPanel extends BasePanel {
    public ReportPanel() {
        super("Generate Report", "app_wallpaper.png");

        createReportOptions();

        showCard("financial");

        updateComponent();
    }

    private JPanel createSelectionPanel() {
        topCardPanel = new JPanel();
        topCardPanel.setOpaque(false);

        JLabel monthLabel = new JLabel("Select Month:");
        monthLabel.setForeground(UITools.getPrimaryColor());
        monthLabel.setFont(UITools.getLabelFont());

        JMonthChooser monthChooser = new JMonthChooser();
        monthChooser.setMonth(-1);

        JLabel yearLabel = new JLabel("Select Year:");
        yearLabel.setForeground(UITools.getPrimaryColor());
        yearLabel.setFont(UITools.getLabelFont());

        JYearChooser yearChooser = new JYearChooser();
        yearChooser.setPreferredSize(new Dimension(100, yearChooser.getPreferredSize().height));

        generateButton = new JButton("Generate Report");
        UITools.styleButton(generateButton);

        topCardPanel.add(monthLabel);
        topCardPanel.add(monthChooser);
        topCardPanel.add(Box.createHorizontalStrut(50));
        topCardPanel.add(yearLabel);
        topCardPanel.add(yearChooser);
        topCardPanel.add(Box.createHorizontalStrut(50));
        topCardPanel.add(generateButton);

        return topCardPanel;
    }

    private JPanel createReportPanel() {
        JPanel reportPanel = new JPanel(new BorderLayout(10,10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        reportPanel.setOpaque(false);

        reportPanel.add(createSelectionPanel(), BorderLayout.NORTH);

        JPanel reportDisplayPanel = new JPanel(new BorderLayout());
        reportDisplayPanel.setOpaque(false);

        reportDisplay = new JEditorPane();
        reportDisplay.setEditable(false);
        reportDisplay.setContentType("text/html");
        reportDisplay.getCaret().setVisible(false);

        JScrollPane scrollPane = new JScrollPane(reportDisplay);
        reportDisplayPanel.add(scrollPane, BorderLayout.CENTER);

        reportPanel.add(reportDisplayPanel, BorderLayout.CENTER);

        return reportPanel;
    }

    private void createReportOptions() {
        Map<String, JPanel> panelMap = new LinkedHashMap<>();

        panelMap.put("Financial Report", createReportPanel());
        panelMap.put("Health Provider Report", createReportPanel());
        panelMap.put("Policy Report", createReportPanel());
        panelMap.put("Illness Trend", createReportPanel());

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
        reportDisplay.setText(htmlContent);
        reportDisplay.setCaretPosition(0);
    }

    public String getReportContent() {
        return reportDisplay.getText();
    }

    public JEditorPane getReportEditorPane() {
        return reportDisplay;
    }

    public Integer getSelectedMonth() {
        int month = monthChooser.getMonth();
        return (month >= 0) ? month + 1 : null;
    }

    public int getSelectedYear() {
        return yearChooser.getYear();
    }

    private JEditorPane reportDisplay;
    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private JButton exportButton;
    private JButton generateButton;
}
