import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ReportGenerator {
    public static String generateFinancialReport(Integer month, int year, double revenue,
                                                 double expenses, double netIncome, double overDue) {
        String period = (month != null) ?
                String.format("%s %d",
                    new java.text.DateFormatSymbols().getMonths()[month - 1], year) :
                String.valueOf(year);
        String netIncomeColor = netIncome >= 0 ? "green" : "red";

        StringBuilder report = new StringBuilder();
        report.append("<html><head>").append(getStyles()).append("</head><body>");
        report.append("<h1>Financial Report</h1>");
        report.append("<h2>").append(period).append("</h2>");

        report.append("<table class='kpi-table'>");

        report.append("<tr>");
        report.append("<td><h3>Total Revenue</h3><p class='money pos'>")
                .append(currencyFormatter.format(revenue)).append("</p></td>");
        report.append("<td><h3>Total Expenses</h3><p class='money neg'>")
                .append(currencyFormatter.format(expenses)).append("</p></td>");
        report.append("</tr>");

        report.append("<tr>");
        report.append("<td><h3>Net Income</h3><p class='money' style='color:").append(netIncomeColor).append("'>")
                .append(currencyFormatter.format(netIncome)).append("</p></td>");
        report.append("<td><h3>Overdue Payments</h3><p class='money neg'>")
                .append(currencyFormatter.format(overDue)).append("</p></td>");
        report.append("</tr>");

        report.append("</table>");

        report.append("</body></html>");
        return report.toString();
    }

    public static String generateTableReport(String title, String[] headers, Object[][] data) {
        StringBuilder report = new StringBuilder();
        report.append("<html><head>").append(getStyles()).append("</head><body>");
        report.append("<h1>").append(title).append("</h1>");

        try {
            // Convert Object[][] to List<List<Object>> for the helper
            List<List<Object>> dataList = new ArrayList<>();
            for (Object[] rowArray : data) {
                dataList.add(Arrays.asList(rowArray));
            }

            // Call the new helper method to build the table
            report.append(buildHtmlTable(headers, dataList));

        } catch (Exception e) {
            e.printStackTrace();
            report.append("<p>Error generating table report.</p>");
        }

        return report.toString();
    }

    private static String buildHtmlTable(String[] headers, List<List<Object>> data) {
        StringBuilder table = new StringBuilder();
        try {
            table.append("<table>");

            table.append("<tr>");
            for (String header : headers) {
                table.append("<th>").append(header).append("</th>");
            }
            table.append("</tr>");

            for (List<Object> row : data) {
                table.append("<tr>");
                for (int i = 0; i < headers.length; i++) {
                    Object obj = row.get(i);
                    String val = (obj != null) ? obj.toString() : "";

                    // Format currency
                    if (headers[i].contains("Amount")) {
                        try {
                            val = currencyFormatter.format(Double.parseDouble(val));
                        } catch (Exception e) {
                            // was not a number, just use the string
                        }
                    }
                    table.append("<td>").append(val).append("</td>");
                }
                table.append("</tr>");
            }
            table.append("</table>");

        } catch (Exception e) {
            e.printStackTrace();
            table.append("<p>Error building HTML table.</p>");
        }
        return table.toString();
    }

    private static String getStyles() {
        return "<style>" +
                "body { font-family: 'Tahoma', sans-serif; background-color: #f4f4f4; }" +
                "h1 { color: #006937; }" +
                "h2 { color: #333; }" +
                "table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
                "th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }" +
                "th { background-color: #006937; color: white; }" +
                "tr:nth-child(even) { background-color: #f9f9f9; }" +
                ".kpi-container { display: flex; flex-wrap: wrap; justify-content: space-around; }" +
                ".kpi-box { background-color: #fff; border: 1px solid #ddd; border-radius: 8px; " +
                "box-shadow: 0 2px 4px rgba(0,0,0,0.1); width: 200px; padding: 20px; margin: 10px; text-align: center; }" +
                ".kpi-box h3 { margin-top: 0; color: #555; }" +
                ".kpi-box p.money { font-size: 1.5em; font-weight: bold; margin-bottom: 0; }" +
                "p.pos { color: green; }" +
                "p.neg { color: red; }" +
                "</style>";
    }

    private static final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
}
