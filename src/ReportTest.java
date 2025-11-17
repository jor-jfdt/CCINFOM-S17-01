import javax.swing.*;
import java.awt.*;

public class ReportTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Report Generator Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);

            ReportPanel reportPanel = new ReportPanel();

            // Add test buttons
            JPanel testPanel = new JPanel(new FlowLayout());

            JButton testFinancialBtn = new JButton("Test Financial Report");
            testFinancialBtn.addActionListener(e -> testFinancialReport(reportPanel));

            JButton testTableBtn = new JButton("Test Table Report");
            testTableBtn.addActionListener(e -> testTableReport(reportPanel));

            JButton testNegativeBtn = new JButton("Test Negative Income");
            testNegativeBtn.addActionListener(e -> testNegativeIncome(reportPanel));

            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(e -> {
                reportPanel.showCard("empty");
                // The "empty" card in ReportPanel now has default text,
                // so we don't even need to call setReportContent.
            });

            testPanel.add(testFinancialBtn);
            testPanel.add(testTableBtn);
            testPanel.add(testNegativeBtn);
            testPanel.add(clearBtn);

            frame.add(testPanel, BorderLayout.SOUTH);
            frame.add(reportPanel, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void testFinancialReport(ReportPanel panel) {
        panel.showCard("Financial Report");

        // Assuming HtmlReportGenerator is named ReportGenerator
        String report = ReportGenerator.generateFinancialReport(
                3, 2024,
                1500000.50,
                950000.75,
                550000.25,
                120000.00
        );
        panel.setReportContent(report);
        System.out.println("Financial Report Generated (March 2024)");
    }

    private static void testNegativeIncome(ReportPanel panel) {
        panel.showCard("Financial Report");

        String report = ReportGenerator.generateFinancialReport(
                6, 2024,
                750000.00,
                1000000.00,
                -250000.00,
                80000.00
        );
        panel.setReportContent(report);
        System.out.println("Financial Report Generated with Negative Income");
    }

    /**
     * --- FIXED: This method is now simple ---
     * It no longer needs MockResultSet.
     */

    private static void testTableReport(ReportPanel panel) {
        panel.showCard("Health Provider Report");

        try {
            // 1. Define simple headers
            String[] headers = {"Provider ID", "Provider Name", "Total Claims", "Total Amount"};

            // 2. Define simple data
            Object[][] data = {
                    {1, "City Hospital", 25, 1250000.50},
                    {2, "General Clinic", 18, 875000.00},
                    {3, "Medical Center", 32, 1950000.75},
                    {4, "Rural Health Center", 12, 345000.25}
            };

            // 3. Call the new, test-friendly method
            // Assuming HtmlReportGenerator is named ReportGenerator
            String report = ReportGenerator.generateTableReport("Health Provider Summary", headers, data);

            panel.setReportContent(report);
            System.out.println("Table Report Generated");

        } catch (Exception e) {
            panel.setReportContent("<html><body><p style='color:red'>Error: " + e.getMessage() + "</p></body></html>");
            e.printStackTrace();
        }
    }

    // --- All MockResultSet and MockResultSetMetaData classes are DELETED ---
}