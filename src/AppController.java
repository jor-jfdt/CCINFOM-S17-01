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
import java.util.HashMap;
import java.util.Map;

public class AppController implements ActionListener {
    public AppController(AppGUI appGUI, AppModel appModel) {
        this.appGUI = appGUI;
        this.appModel = appModel;
        this.appGUI.addMenuButtonListener(this);
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
                    List<Map<String, Object>> queryResult;
                    if (panelKey.equals("doctor_consultation_claim")) {
                        queryResult = appModel.processQuery(
                                "SELECT * FROM claim WHERE service_type = ?;", "Consultation"
                        );
                    } else if (panelKey.equals("hospitalization_claim")) {
                        queryResult = appModel.processQuery(
                                "SELECT * FROM claim WHERE service_type <> ?;", "Consultation"
                        );
                    } else if (panelKey.equals("payout_to_doctor")) {
                        queryResult = appModel.processQuery(
                                "SELECT payout.* FROM payout " +
                                        "JOIN claim ON payout.claim_id = claim.claim_id " +
                                        "WHERE claim.service_type = ?;", "Consultation"
                        );
                    } else if (panelKey.equals("payout_to_hospital")) {
                        queryResult = appModel.processQuery(
                                "SELECT payout.* FROM payout " +
                                        "JOIN claim ON payout.claim_id = claim.claim_id " +
                                        "WHERE claim.service_type <> ?;", "Consultation"
                        );
                    } else {
                        queryResult = appModel.getTableEntries(tableName, "*");
                    }
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
                        // Refresh table
                        Map<String, Object> field_values = dialog.getFieldValues();
                        String[] attributes = appModel.getDatabaseTableAttributes(currentRecordType).keySet().toArray(new String[0]);

                        if (attributes[attributes.length - 1].equalsIgnoreCase("data_status")) field_values.put("data_status", true);

                        if (validateFields(attributes, field_values, true)) {
                            //abort the insertion if validation fails
                            appModel.insertIntoTable(currentRecordType.toLowerCase(), Arrays.copyOfRange(field_values.values().toArray(), 1, field_values.size()));
                            List<Map<String, Object>> queryResult = appModel.getTableEntriesInverted(currentRecordType.toLowerCase(), "data_status", true, "data_status");
                            DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                            appGUI.getRecordPanel().setTable(currentRecordType.toLowerCase(), dtm);
                        }
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
                    if (dialog.isConfirmed()) {
                        System.out.println("Confirm button clicked!");
                        System.out.println("Values: " + dialog.getFieldValues());
                        try {
                            Map<String, Object> field_values = dialog.getFieldValues();
                            String[] attributes = appModel.getDatabaseTableAttributes(currentRecordType).keySet().toArray(new String[0]);
                            if (attributes[attributes.length - 1].equalsIgnoreCase("data_status")) field_values.put("data_status", true);
                            Object[] values = field_values.values().toArray();
                            //convert into prehistoric for loop
                            for (int i = 1; i < currentColumns.length; i++) {
                                System.out.println("Updating " + currentColumns[i] + " to value: " + values[i]);
                                appModel.updateColumnValueOfId(currentRecordType.toLowerCase(), currentColumns[i], Integer.parseInt((String)values[0]) , values[i]);
                            }
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
                    Object[] rowData = new Object[currentColumns.length];
                    for (int i = 0; i < currentColumns.length; i++) {
                        rowData[i] = appGUI.getRecordPanel().getTableMap().get(currentRecordType).getValueAt(selectedRow, i);
                        System.out.println("rowData[" + i + "]: " + rowData[i]);
                    }
                    CRUDDialog dialog = new CRUDDialog(
                            appGUI,
                            "Delete " + currentRecordType,
                            BaseDialog.Mode.DELETE,
                            currentColumns
                    );
                    for (String col : currentColumns) {
                        dialog.setFieldValue(col, rowData[Arrays.asList(currentColumns).indexOf(col)].toString());
                        System.out.println("Pre-fill " + col + " with value: " + rowData[Arrays.asList(currentColumns).indexOf(col)]);
                    }
                    dialog.setVisible(true);
                    if (dialog.isConfirmed()) {
                        System.out.println("Confirm button clicked!");
                        System.out.println("Values: " + dialog.getFieldValues());
                        try {
                            Integer primaryKeyRow = Integer.parseInt(dialog.getFieldValue(currentColumns[0]).toString());
                            appModel.updateColumnValueOfId(currentRecordType.toLowerCase(), "data_status", primaryKeyRow, false);
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

                if (dialog.isConfirmed()) {
                    try {
                        Map<String, Object> field_values = dialog.getFieldValues();
                        // Exclude PK if needed (usually first column)
                        Object[] values = Arrays.copyOfRange(field_values.values().toArray(), 1, field_values.size());
                        for (Object v : values) {
                            if (v == null || v.toString().trim().isEmpty()) {
                                JOptionPane.showMessageDialog(appGUI, "Please fill all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        appModel.insertIntoTable(baseKey, values);
                        List<Map<String, Object>> queryResult = appModel.getTableEntries(baseKey, "*");
                        DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                        appGUI.getTransactionPanel().setTable(currentTransactionType, dtm);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(appGUI, "Error updating record: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            System.out.println("Transaction Add Button Clicked");
        }
        else if (e.getSource() == appGUI.getTransactionPanel().getUpdateButton()) {
            if (currentTransactionType == null) {
                JOptionPane.showMessageDialog(appGUI, "Please select a transaction option first.", "Select Option", JOptionPane.WARNING_MESSAGE);
            } else {
                JTable table = appGUI.getTransactionPanel().getTableMap().get(currentTransactionType);
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(appGUI, "Please select a record to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                } else {
                    String baseKey = appGUI.getTransactionPanel().getPanelToTableKey().get(currentTransactionType);
                    String[] headers = appGUI.getTransactionPanel().getHeaderColumns().get(baseKey);
                    CRUDDialog dialog = new CRUDDialog(
                            appGUI,
                            "Update Transaction",
                            BaseDialog.Mode.UPDATE,
                            headers
                    );
                    // Pre-fill dialog fields with selected row values
                    for (int i = 0; i < headers.length; i++) {
                        Object value = table.getValueAt(selectedRow, i);
                        dialog.setFieldValue(headers[i], value != null ? value.toString() : "");
                    }
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        try {
                            Map<String, Object> field_values = dialog.getFieldValues();
                            Object[] values = field_values.values().toArray();
                            for (int i = 1; i < headers.length; i++) {
                                appModel.updateColumnValueOfId(baseKey, headers[i], Integer.parseInt(values[0].toString()), values[i]);
                            }
                            List<Map<String, Object>> queryResult = appModel.getTableEntries(baseKey, "*");
                            DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                            appGUI.getTransactionPanel().setTable(currentTransactionType, dtm);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(appGUI, "Error updating transaction: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
        if (e.getSource() == appGUI.getReportPanel().getGenerateButton()) {
            if (appGUI.getReportPanel().getActivePanelKey() == "empty")
                JOptionPane.showMessageDialog(appGUI, "Please select a report option first.", "Select Option", JOptionPane.WARNING_MESSAGE);
            else
                generateReport();
            System.out.println("Generate Report Button Clicked");
        }
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
        String date_filters_specific_oc = "(MONTH(cpay.payment_date) = ? AND YEAR(cpay.payment_date) = ?)";
        String date_filters_all_oc = "(MONTH(cpay.payment_date) <= ? AND YEAR(cpay.payment_date) = ?)";
        String date_filters_specific_p = "(MONTH(payout_date) = ? AND YEAR(payout_date) = ?)";
        String date_filters_all_p = "(MONTH(payout_date) <= ? AND YEAR(payout_date) = ?)";
        Double overdues = (Double)(appModel.processQuery(
                "SELECT COALESCE(SUM(amount), 0) AS total_overdue_payments " +
                        "FROM client_policy cp JOIN client_payment cpay ON cp.client_plan_id = cpay.client_plan_id " +
                        "WHERE LOWER(premium_payment_status) = ? AND " + (month == null ? date_filters_all_oc : date_filters_specific_oc) + ";",
                "overdue", month == null ? 12 : month, year
        ).get(1).get("total_overdue_payments"));
        Double completes = (Double)(appModel.processQuery(
                "SELECT COALESCE(SUM(amount), 0) AS total_complete_payments " +
                        "FROM client_policy cp JOIN client_payment cpay ON cp.client_plan_id = cpay.client_plan_id " +
                        "WHERE LOWER(premium_payment_status) = ? AND " + (month == null ? date_filters_all_oc : date_filters_specific_oc) + ";",
                "complete", month == null ? 12 : month, year
        ).get(1).get("total_complete_payments"));
        Double payouts = (Double)(appModel.processQuery(
                "SELECT COALESCE(SUM(payout_amount), 0) AS total_payouts " +
                        "FROM payout " +
                        "WHERE LOWER(payout_status) = ? AND " + (month == null ? date_filters_all_p : date_filters_specific_p) + ";",
                "complete", month == null ? 12 : month, year
        ).get(1).get("total_payouts"));

        double revenue = completes != null ? completes : 0.0;
        double expenses = payouts != null ? payouts : 0.0;
        double overDue = overdues != null ? overdues : 0.0;
        double netIncome = revenue - expenses;

        return ReportGenerator.generateFinancialReport(month, year, revenue, expenses, netIncome, overDue);
    }

    private String generateHealthProviderReport(Integer month, int year) throws Exception {
        String date_filters_specific = "(MONTH(cr.service_date) = ? AND YEAR(cr.service_date) = ?)";
        String date_filters_all = "(MONTH(cr.service_date) <= ? AND YEAR(cr.service_date) = ?)";
        List<Map<String, Object>> matrix = appModel.processQuery(
                "SELECT hr.hospital_name, COUNT(claim_id) AS hospital_claims " +
                        "FROM claim cr " +
                        "JOIN hospital hr ON cr.hospital_id = hr.hospital_id " +
                        "WHERE LOWER(claim_status) = ? AND " + (month == null ? date_filters_all : date_filters_specific) + " " +
                        "GROUP BY hr.hospital_id " +
                        "ORDER BY hospital_claims DESC;",
                "complete", month == null ? 12 : month, year
        );
        String[] headers = {"Hospital Name", "Claims Count"};
        HashMap<String, String> correspondents = new HashMap<>() {{
            put(headers[0], "hospital_name");
            put(headers[1], "hospital_claims");
        }};
        Object[][] data = new Object[matrix.size() - 1][headers.length];
        for (int i = 1; i < matrix.size(); i++) {
            Map<String, Object> row = matrix.get(i);
            for (int j = 0; j < headers.length; j++) {
                data[i - 1][j] = row.get(correspondents.get(headers[j]));
            }
        }
        String title = (month != null) ?
                String.format("Health Provider Report - %s %d", new java.text.DateFormatSymbols().getMonths()[month - 1], year) :
                String.format("Health Provider Report - %d", year);
        return ReportGenerator.generateTableReport(title, headers, data);
    }

    private String generatePolicyReport(Integer month, int year) throws Exception {
        String date_filters_specific = "(MONTH(claim.service_date) = ? AND YEAR(claim.service_date) = ?)";
        String date_filters_all = "(MONTH(claim.service_date) <= ? AND YEAR(claim.service_date) = ?)";
        List<Map<String, Object>> matrix = appModel.processQuery(
                "SELECT policy.plan_name, " +
                        "COUNT(claim_id) AS policy_claims, " +
                        "COALESCE(SUM(service_amount), 0) AS total_service_amount, " +
                        "COALESCE(SUM(covered_amount), 0) AS total_covered_amount " +
                        "FROM claim " +
                        "JOIN client_policy ON claim.client_plan_id = client_policy.client_plan_id " +
                        "JOIN policy ON policy.plan_id = client_policy.plan_id " +
                        "WHERE LOWER(claim_status) = ? AND " + (month == null ? date_filters_all : date_filters_specific) + " " +
                        "GROUP BY policy.plan_id " +
                        "ORDER BY policy_claims DESC, total_service_amount DESC, total_covered_amount DESC; ",
                "complete", month == null ? 12 : month, year
        );
        String[] headers = {"Policy Name", "Claims Count", "Total Service Amount", "Total Covered Amount"};
        HashMap<String, String> correspondents = new HashMap<>() {{
            put(headers[0], "plan_name");
            put(headers[1], "policy_claims");
            put(headers[2], "total_service_amount");
            put(headers[3], "total_covered_amount");
        }};
        Object[][] data = new Object[matrix.size() - 1][headers.length];
        for (int i = 1; i < matrix.size(); i++) {
            Map<String, Object> row = matrix.get(i);
            for (int j = 0; j < headers.length; j++) {
                data[i - 1][j] = row.get(correspondents.get(headers[j]));
            }
        }
        String title = (month != null) ?
                String.format("Policy Report - %s %d", new java.text.DateFormatSymbols().getMonths()[month - 1], year) :
                String.format("Policy Report - %d", year);
        return ReportGenerator.generateTableReport(title, headers, data);
    }

    private String generateIllnessTrendReport(Integer month, int year) throws Exception {
        String date_filters_specific = "(MONTH(claim.service_date) = ? AND YEAR(claim.service_date) = ?)";
        String date_filters_all = "(MONTH(claim.service_date) <= ? AND YEAR(claim.service_date) = ?)";
        List<Map<String, Object>> matrix = appModel.processQuery(
                "SELECT illness.illness_name, " +
                        "COUNT(claim_id) AS illness_claims, " +
                        "COALESCE(SUM(service_amount), 0) AS total_service_amount, " +
                        "COALESCE(SUM(covered_amount), 0) AS total_covered_amount " +
                        "FROM claim " +
                        "JOIN illness ON claim.illness_id = illness.illness_id " +
                        "WHERE LOWER(claim_status) = ? AND " + (month == null ? date_filters_all : date_filters_specific) + " " +
                        "GROUP BY illness.illness_id " +
                        "ORDER BY illness_claims DESC, total_service_amount DESC, total_covered_amount DESC;",
                "complete", month == null ? 12 : month, year
        );
        String[] headers = {"Illness Name", "Claims Count", "Total Service Amount", "Total Covered Amount"};
        HashMap<String, String> correspondents = new HashMap<>() {{
            put(headers[0], "illness_name");
            put(headers[1], "illness_claims");
            put(headers[2], "total_service_amount");
            put(headers[3], "total_covered_amount");
        }};
        Object[][] data = new Object[matrix.size() - 1][headers.length];
        for (int i = 1; i < matrix.size(); i++) {
            Map<String, Object> row = matrix.get(i);
            for (int j = 0; j < headers.length; j++) {
                data[i - 1][j] = row.get(correspondents.get(headers[j]));
            }
        }
        String title = (month != null) ?
                String.format("Illness Trend Report - %s %d", new java.text.DateFormatSymbols().getMonths()[month - 1], year) :
                String.format("Illness Trend Report - %d", year);
        return ReportGenerator.generateTableReport(title, headers, data);
    }

    private boolean validateFields(String[] headers, Map<String, Object> fieldValues, boolean isInsert) {
        for (int i = 0; i < headers.length; i++) {
            if (isInsert && i == 0) continue;
            Object value = fieldValues.get(headers[i]);
            if (value == null || value.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(appGUI, "Please fill all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (headers[i].contains("date")) {
                try {
                    if (AppModel.SQLUtils.dateIsValid(value.toString())) {
                        // Valid
                        System.out.println("Valid date: " + value);
                    } else {
                        AppModel.SQLUtils.stringToDate(value.toString());
                        JOptionPane.showMessageDialog(appGUI, "Invalid date format for " + headers[i], "Input Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(appGUI, "Invalid date format for " + headers[i], "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                continue;
            }
            if (headers[i].toLowerCase().endsWith("_id")) {
                try {
                    int fk = Integer.parseInt(value.toString());
                    if (fk <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(appGUI, "Invalid foreign key value for " + headers[i], "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            if (headers[i].contains("name")) {
                String name = value.toString();
                try {

                if (!name.matches("^[a-zA-Z .'-]+$") || !AppModel.SQLUtils.stringFitsShort(name)) {
                    JOptionPane.showMessageDialog(appGUI, "Invalid characters in name for " + headers[i], "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(appGUI, "Invalid characters in name for " + headers[i], "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            if (headers[i].contains("amount") || headers[i].contains("limit")) {
                try {
                    double amount = Double.parseDouble(value.toString());
                    if (amount < 0 || value.toString().matches("^([-+]?)((\\.?\\d+)|(\\d+\\.?(\\d+)?))$")) {
                        JOptionPane.showMessageDialog(appGUI, "Amount cannot be negative for " + headers[i], "Input Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(appGUI, "Invalid amount format for " + headers[i], "Input Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            }
        return true;
    }
    private String currentTransactionType;
    private String currentRecordType;
    private String[] currentColumns;
    private final AppGUI appGUI;
    private final AppModel appModel;
}
