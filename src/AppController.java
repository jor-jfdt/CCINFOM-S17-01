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
                currentRecordType = appGUI.getRecordPanel().getOptions()[i].getText();
                System.out.println("Record Option " + appGUI.getRecordPanel().getOptions()[i].getText() + " is Clicked.");
                appGUI.getRecordPanel().showCard(appGUI.getRecordPanel().getOptions()[i].getText());

                try {
                    List<Map<String, Object>> queryResult = appModel.getTableEntriesInverted(appGUI.getRecordPanel().getOptions()[i].getText().toLowerCase(), "data_status", true, "data_status");
                    currentColumns = queryResult.getFirst().keySet().toArray(new String[0]);
                    DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                    appGUI.getRecordPanel().setTable(appGUI.getRecordPanel().getOptions()[i].getText().toLowerCase(), dtm);
                } catch(SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        for (int i = 0; i < appGUI.getTransactionPanel().getOptions().length; i++) {
            if (e.getSource() == appGUI.getTransactionPanel().getOptions()[i]) {
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
            // Open Update Dialog
            System.out.println("Update Button Clicked");
        } else if (e.getSource() == appGUI.getRecordPanel().getVoidButton()) {
            // Perform deletion
            System.out.println("Void Button Clicked");
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
			for (j = 0, values = matrix.get(i).values().toArray(); j < headers.length; j++) {
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
    private String currentRecordType;
    private String[] currentColumns;
    private final AppGUI appGUI;
    private final AppModel appModel;
}
