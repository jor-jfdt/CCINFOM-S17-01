import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AppController implements ActionListener {
    public AppController(AppGUI appGUI, AppModel appModel) {
        this.appGUI = appGUI;
        this.appModel = appModel;
        this.appGUI.addMenuButtonListener(this);

        setupReportPanel();
    }
    public void connectToDatabase() throws SQLException, IOException, ClassNotFoundException {
        appModel.enterDatabase();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String tableName = new String();
        // Controller for Menu Buttons
        for (int i = 0; i < appGUI.getMenuButtons().length; i++) {
            if (e.getSource() == appGUI.getMenuButtons()[i]) {
                System.out.println(appGUI.getMenuButtons()[i].getText() + " Menu Button is Clicked.");
                switch (i) {
                    case 0 -> appGUI.showPanel("Record");
                    case 1 -> appGUI.showPanel("Transaction");
                    case 2 -> appGUI.showPanel("Report");
                }
            }
        }
        //later on add Report Panel back button controller
        if (e.getSource() == appGUI.getRecordPanel().getBackButton() || e.getSource() == appGUI.getTransactionPanel().getBackButton() || e.getSource() == appGUI.getReportPanel().getBackButton()) {
            System.out.println("Back to Main Menu");
            appGUI.showPanel("main");
        }
        if (e.getSource() == appGUI.getRecordPanel().getHideButton()) {
            System.out.println("Hide Button in Record Pressed");
            appGUI.getRecordPanel().toggleOptionsVisibility();
        }
        else if (e.getSource() == appGUI.getTransactionPanel().getHideButton()) {
            System.out.println("Hide Button in Transaction Pressed");
            appGUI.getTransactionPanel().toggleOptionsVisibility();
        }
        else if (e.getSource() == appGUI.getReportPanel().getHideButton()) {
            System.out.println("Hide Button in Report Pressed");
            appGUI.getReportPanel().toggleOptionsVisibility();
        }
        for (int i = 0; i < appGUI.getRecordPanel().getOptions().length; i++) {
            if (e.getSource() == appGUI.getRecordPanel().getOptions()[i]) {
                currentRecordType = appGUI.getRecordPanel().getOptions()[i].getText().toLowerCase();
                System.out.println("Record Option " + appGUI.getRecordPanel().getOptions()[i].getText() + " is Clicked.");
                appGUI.getRecordPanel().showCard(appGUI.getRecordPanel().getOptions()[i].getText());

                try {
                    List<Map<String, Object>> queryResult = appModel.getTableEntriesInverted(appGUI.getRecordPanel().getOptions()[i].getText().toLowerCase(), "data_status", true, "data_status");
                    currentColumns = queryResult.get(0).keySet().toArray(new String[0]);
                    DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                    appGUI.getRecordPanel().setTable(appGUI.getRecordPanel().getOptions()[i].getText().toLowerCase(), dtm);
                } catch(SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        //Connect Transaction Panel Options to Database
        for (int i = 0; i < appGUI.getTransactionPanel().getOptions().length; i++) {
            if (e.getSource() == appGUI.getTransactionPanel().getOptions()[i]) {
                String panelKey = appGUI.getTransactionPanel().getOptions()[i].getText().replace(" ", "_").toLowerCase();
                currentTransactionType = panelKey;

                tableName = appGUI.getTransactionPanel().getPanelToTableKey().get(panelKey);

                try {
                    List<Map<String, Object>> queryResult = appModel.getTableEntries(tableName, "*");
                    DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                    appGUI.getTransactionPanel().setTable(panelKey, dtm);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                System.out.println("Transaction Option " + appGUI.getTransactionPanel().getOptions()[i].getText() + " is Clicked.");
                appGUI.getTransactionPanel().showCard(appGUI.getTransactionPanel().getOptions()[i].getText());
            }
        }
        for (int i = 0; i < appGUI.getReportPanel().getOptions().length; i++) {
            if (e.getSource() == appGUI.getReportPanel().getOptions()[i]) {
                System.out.println("Report Option " + appGUI.getReportPanel().getOptions()[i].getText() + " is Clicked.");
                appGUI.getReportPanel().showCard(appGUI.getReportPanel().getOptions()[i].getText());
            }
        }

        //insert logic for determining what option was clicked in Record Panel to determine the fields to show in Add/Update Dialog
        //save state here
        if (e.getSource() == appGUI.getRecordPanel().getAddButton()) {
            // Open Add Dialog
            //appGUI.getRecordPanel().AddButtonFeatures(appGUI);
            if (currentRecordType == null) {
                JOptionPane.showMessageDialog(appGUI, "Please select a record option first.", "Select Option", JOptionPane.WARNING_MESSAGE);
            } else {
                CRUDDialog dialog = new CRUDDialog(
                        appGUI,
                        "Add " + currentRecordType,
                        BaseDialog.Mode.ADD,
                        currentColumns
                );

                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    System.out.println("Confirm button clicked!");
                    System.out.println("Values: " + dialog.getFieldValues());
                    try {
                        //appModel.insertIntoTable(currentRecordType.toLowerCase(), Arrays.copyOfRange(dialog.getFieldValues().values().toArray(), 1, currentColumns.length));
                        // Refresh table
                        Map<String, Object> field_values = dialog.getFieldValues();
                        String[] attributes = appModel.getDatabaseTableAttributes(currentRecordType).keySet().toArray(new String[0]);
                        if (attributes[attributes.length - 1].equalsIgnoreCase("data_status")) field_values.put("data_status", true);
                        appModel.insertIntoTable(currentRecordType.toLowerCase(), Arrays.copyOfRange(field_values.values().toArray(), 1, field_values.size()));
                        List<Map<String, Object>> queryResult = appModel.getTableEntriesInverted(currentRecordType.toLowerCase(), "data_status", true, "data_status");
                        DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                        appGUI.getRecordPanel().setTable(currentRecordType.toLowerCase(), dtm);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(appGUI, "Error inserting record: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Cancel button clicked!");
                }
            }
        } else if (e.getSource() == appGUI.getRecordPanel().getUpdateButton()) {
            if (currentRecordType == null) {
                JOptionPane.showMessageDialog(appGUI, "Please select a record option first.", "Select Option", JOptionPane.WARNING_MESSAGE);
            } else {
                if (appGUI.getRecordPanel().getTableMap().get(currentRecordType).getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(appGUI, "Please select a record to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                } else {
                    int selectedRow = appGUI.getRecordPanel().getTableMap().get(currentRecordType).getSelectedRow();
                    Object[] rowData = new Object[currentColumns.length];
                    for (int i = 0; i < currentColumns.length; i++) {
                        rowData[i] = appGUI.getRecordPanel().getTableMap().get(currentRecordType).getValueAt(selectedRow, i);
                        System.out.println("rowData[" + i + "]: " + rowData[i]);
                    }
                    CRUDDialog dialog = new CRUDDialog(
                            appGUI,
                            "Update " + currentRecordType,
                            BaseDialog.Mode.UPDATE,
                            currentColumns
                    );
                    for (String col : currentColumns) {
                        dialog.setFieldValue(col, rowData[Arrays.asList(currentColumns).indexOf(col)].toString());
                        System.out.println("Pre-fill " + col + " with value: " + rowData[Arrays.asList(currentColumns).indexOf(col)]);
                    }
                    dialog.setVisible(true);
                }
            }
            System.out.println("Update Button Clicked");
        } else if (e.getSource() == appGUI.getRecordPanel().getVoidButton()) {
            if (currentRecordType == null) {
                JOptionPane.showMessageDialog(appGUI, "Please select a record option first.", "Select Option", JOptionPane.WARNING_MESSAGE);
            } else {
                if (appGUI.getRecordPanel().getTableMap().get(currentRecordType).getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(appGUI, "Please select a record to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                } else {
                    int selectedRow = appGUI.getRecordPanel().getTableMap().get(currentRecordType).getSelectedRow();
                    CRUDDialog dialog = new CRUDDialog(
                            appGUI,
                            "Delete " + currentRecordType,
                            BaseDialog.Mode.DELETE,
                            currentColumns
                    );
                    dialog.setVisible(true);
                }
            }
            System.out.println("Delete Button Clicked");
        }
        if (e.getSource() == appGUI.getTransactionPanel().getAddButton()) {
            if (currentTransactionType == null) {
                JOptionPane.showMessageDialog(appGUI, "Please select a transaction option first.", "Select Option", JOptionPane.WARNING_MESSAGE);
            } else {
                String baseKey = appGUI.getTransactionPanel().getPanelToTableKey().get(currentTransactionType);
                String[] headers = appGUI.getTransactionPanel().getHeaderColumns().get(baseKey);
                if (baseKey == null || headers == null) {
                    JOptionPane.showMessageDialog(appGUI, "Invalid transaction type or header columns not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CRUDDialog dialog = new CRUDDialog(
                        appGUI,
                        "Add Transaction",
                        BaseDialog.Mode.ADD,
                        headers
                );
                dialog.setVisible(true);
            }
            System.out.println("Transaction Add Button Clicked");
        }
        else if (e.getSource() == appGUI.getTransactionPanel().getUpdateButton()) {
            if (currentTransactionType == null) {
                JOptionPane.showMessageDialog(appGUI, "Please select a transaction option first.", "Select Option", JOptionPane.WARNING_MESSAGE);
            } else {
                JTable table = appGUI.getTransactionPanel().getTableMap().get(currentTransactionType);
                if (table.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(appGUI, "Please select a record to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                } else {
                    String baseKey = appGUI.getTransactionPanel().getPanelToTableKey().get(currentTransactionType);
                    CRUDDialog dialog = new CRUDDialog(
                            appGUI,
                            "Update Transaction",
                            BaseDialog.Mode.UPDATE,
                            appGUI.getTransactionPanel().getHeaderColumns().get(baseKey)
                    );
                    dialog.setVisible(true);
                }
            }
        }
    }

    private void setupReportPanel() {
        ReportPanel reportPanel = appGUI.getReportPanel();

        reportPanel.addGenerateReportListener(e -> generateReport());
    }

    private void generateReport() {
        ReportPanel reportPanel = appGUI.getReportPanel();
        String reportType = reportPanel.getActivePanelKey();
        Integer selectedMonth = reportPanel.getSelectedMonth(); // Can be null for yearly
        int selectedYear = reportPanel.getSelectedYear();

        String reportContent = "";

        try {
            switch (reportType) {
                case "Financial Report":
                    reportContent = generateFinancialReport(selectedMonth, selectedYear);
                    break;
                case "Health Provider Report":
                    reportContent = generateHealthProviderReport(selectedMonth, selectedYear);
                    break;
                case "Policy Report":
                    reportContent = generatePolicyReport(selectedMonth, selectedYear);
                    break;
                case "Illness Trend":
                    reportContent = generateIllnessTrendReport(selectedMonth, selectedYear);
                    break;
                default:
                    reportContent = "<html><body><p>Unknown report type.</p></body></html>";
            }

            reportPanel.setReportContent(reportContent);

        } catch (Exception e) {
            e.printStackTrace();
            reportPanel.setReportContent("<html><body><p>Error generating report: " +
                    e.getMessage() + "</p></body></html>");
        }
    }

    private String generateFinancialReport(Integer month, int year) throws Exception {
		String date_filters = "MONTH(cp.payment_date) = ? AND YEAR(cp.payment_date) = ?";
		Double overdues = (Double)appModel.processQuery(
			"SELECT COALESCE(SUM(amount), 0) AS total_overdue_payments " + 
			"FROM clients c JOIN client_payment cp " +
			"ON c.member_id = cp.member_id " +
			"WHERE LOWER(cp.premium_payment_status) = ? AND " + date_filters + ";"
		, "overdue", month, year).get(1).get("total_overdue_payments");
		Double completes = (Double)appModel.processQuery(
			"SELECT COALESCE(SUM(amount), 0) AS total_overdue_payments " + 
			"FROM clients c JOIN client_payment cp " +
			"ON c.member_id = cp.member_id " +
			"WHERE LOWER(cp.premium_payment_status) = ? AND " + date_filters + ";"
		, "complete", month, year).get(1).get("total_complete_payments");
		Double payouts = (Double)appModel.processQuery(
			"SELECT COALESCE(SUM(amount), 0) AS total_complete_payments " +
			"FROM clients c JOIN client_payment cp " +
			"ON c.member_id = cp.member_id " +
			"WHERE LOWER(premium_payment_status) = ? AND " + date_filters + ";"
		, "complete", month, year).get(1).get("total_payouts");
		
        // Placeholder values - replace with actual database queries later
        double revenue = 0.0;
        double expenses = 0.0;
        double netIncome = 0.0;
        double overDue = 0.0;
		
        return ReportGenerator.generateFinancialReport(month, year, revenue, expenses, netIncome, overDue);
    }
	
    private String generateHealthProviderReport(Integer month, int year) throws Exception {
		String date_filters = "MONTH(cr.service_date) = ? AND YEAR(cr.service_date) = ?";
        // Note: This list is one-based, i.e. the index starts at 1.
		// Returns List<   Map<         String,          Object>>
		//          ^       ^             ^                ^
		//          |       |             |                |
		//      list of   rows with  columns of   corresponding values
		List<Map<String, Object>> matrix = appModel.processQuery(
			"SELECT hr.hospital_name, COUNT(claim_id) AS hospital_claims " +
			"FROM claim cr " +
			"JOIN hospital hr ON cr.hospital_id = hr.hospital_id " +
			"WHERE LOWER(claim_status) = ? AND " + date_filters + " " +
			"GROUP BY hr.hospital_id " +
			"ORDER BY hospital_claims DESC;"
		, "complete", month, year);
		Object[] values;
		int i, j;
		// Placeholder - replace with actual database queries later
        String[] headers = {"Hospital Name", "Claims Count"};
        Object[][] data = new Object[matrix.size()][headers.length];
		for (i = 1; i < matrix.size(); i++) {
			for (j = 0, values = matrix.get(i).values().toArray(new Object[0]); j < headers.length; j++) {
				System.out.println(values[j]);
				data[i][j] = values[j];
			}
		}
		
        String title = (month != null) ?
                String.format("Health Provider Report - %s %d",
                        new java.text.DateFormatSymbols().getMonths()[month - 1], year) :
                String.format("Health Provider Report - %d", year);
		
        return ReportGenerator.generateTableReport(title, headers, data);
    }

    private String generatePolicyReport(Integer month, int year) throws Exception {
		String date_filters = "MONTH(claim.service_date) = ? AND YEAR(claim.service_date) = ?";
		// Note: This list is one-based, i.e. the index starts at 1.
		// Returns List<   Map<         String,          Object>>
		//          ^       ^             ^                ^
		//          |       |             |                |
		//      list of   rows with  columns of   corresponding values
		List<Map<String, Object>> matrix = appModel.processQuery(
			"SELECT policy.plan_name, " +
			"COUNT(claim_id) AS policy_claims, " +
			"COALESCE(SUM(service_amount), 0) AS total_service_amount, " +
			"COALESCE(SUM(covered_amount), 0) AS total_covered_amount " +
			"FROM claim " +
			"JOIN client_policy ON claim.member_id = client_policy.member_id " +
			"JOIN policy ON policy.plan_id = client_policy.plan_id " +
			"WHERE LOWER(claim_status) = ? AND " + date_filters + " " +
			"GROUP BY policy.plan_id " +
			"ORDER BY policy_claims DESC, total_service_amount DESC, total_covered_amount DESC; "
		, "complete", month, year);
        Object[] values;
		int i, j;
		// Placeholder - replace with actual database queries later
        String[] headers = {"Policy Name", "Claims Count", "Total Service Amount", "Total Covered Amount"};
        Object[][] data = new Object[matrix.size()][headers.length];
		for (i = 1; i < matrix.size(); i++) {
			for (j = 0, values = matrix.get(i).values().toArray(); j < headers.length; j++) {
				System.out.println(values[j]);
				data[i][j] = values[j];
			}
		}
		
        String title = (month != null) ?
                String.format("Policy Report - %s %d",
                        new java.text.DateFormatSymbols().getMonths()[month - 1], year) :
                String.format("Policy Report - %d", year);
		
        return ReportGenerator.generateTableReport(title, headers, data);
    }

    private String generateIllnessTrendReport(Integer month, int year) throws Exception {
		String date_filters = "MONTH(claim.service_date) = ? AND YEAR(claim.service_date) = ?";
		// Note: This list is one-based, i.e. the index starts at 1.
		// Returns List<   Map<         String,          Object>>
		//          ^       ^             ^                ^
		//          |       |             |                |
		//      list of   rows with  columns of   corresponding values
		List<Map<String, Object>> matrix = appModel.processQuery(
			"SELECT " +
			"illness.illness_name, " +
			"COUNT(claim_id) AS illness_claims, " +
			"COALESCE(SUM(service_amount), 0) AS total_service_amount, " +
			"COALESCE(SUM(covered_amount), 0) AS total_covered_amount " +
			"FROM claim " +
			"JOIN illness ON claim.illness_id = illness.illness_id " +
			"WHERE LOWER(claim_status) = ? AND " + date_filters + " " +
			"GROUP BY illness.illness_id " +
			"ORDER BY illness_claims DESC, total_service_amount DESC, total_covered_amount DESC; "
		, "complete", month, year);
        Object[] values;
		int i, j;
        // Placeholder - replace with actual database queries later
        String[] headers = {"Illness Name", "Claims Count", "Total Service Amount", "Total Covered Amount"};
        Object[][] data = new Object[matrix.size()][headers.length];
		for (i = 1; i < matrix.size(); i++) {
			for (j = 0, values = matrix.get(i).values().toArray(); j < headers.length; j++) {
				System.out.println(values[j]);
				data[i][j] = values[j];
			}
		}
		
        String title = (month != null) ?
                String.format("Illness Trend Report - %s %d",
                        new java.text.DateFormatSymbols().getMonths()[month - 1], year) :
                String.format("Illness Trend Report - %d", year);
		
        return ReportGenerator.generateTableReport(title, headers, data);
    }

    private String currentTransactionType;
    private String currentRecordType;
    private String[] currentColumns;
    private final AppGUI appGUI;
    private final AppModel appModel;
}
