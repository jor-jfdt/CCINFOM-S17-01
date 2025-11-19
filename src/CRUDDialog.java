import javax.swing.*;
import java.awt.*;

public class CRUDDialog extends BaseDialog {
    public CRUDDialog(JFrame parent, String title, BaseDialog.Mode mode, String[] columns) {
        super(parent, title, mode, columns,400, Math.min(600, 150 + (columns.length * 40)));
        this.mode = mode;
        this.columns = columns;
    }

    @Override
    protected JComponent createInputField(String column) {
        if (column.toLowerCase().endsWith("_id") ){
            return new JTextField(10);
        }
        if (column.toLowerCase().contains("date"))
            return new JTextField(10);
        else if (column.toLowerCase().contains("is_")){
            JComboBox<Boolean> comboBox = new JComboBox<>();
            comboBox.addItem(true);
            comboBox.addItem(false);
            return comboBox;
        }
        else if (column.equalsIgnoreCase("sex")) {
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem("M");
            comboBox.addItem("F");
            return comboBox;
        }
        else if (column.equalsIgnoreCase("coverage_type")) {
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem("Comprehensive");
            comboBox.addItem("In-Patient");
            comboBox.addItem("Out-Patient");
            comboBox.addItem("Emergency Only");
            comboBox.addItem("All-Access");
            return comboBox;
        } else if (column.equalsIgnoreCase("payment_period")){
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem("Monthly");
            comboBox.addItem("Quarterly");
            comboBox.addItem("Semi-Annual");
            comboBox.addItem("Annual");
            return comboBox;
        } else if (column.equalsIgnoreCase("payment_method")) {
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem("Credit Card");
            comboBox.addItem("Debit Card");
            comboBox.addItem("Online Payment");
            comboBox.addItem("Bank Transfer");
            comboBox.addItem("Cash");
            comboBox.addItem("Check");
            comboBox.addItem("N/A");
            return comboBox;
        }else if (column.contains("status")) {
            if (column.contains("claim") ||
                column.contains("loa")){
                JComboBox<String> comboBox = new JComboBox<>();
                comboBox.addItem("Pending");
                comboBox.addItem("Approved");
                comboBox.addItem("Denied");
                return comboBox;
            } else if (column.contains("payout") ||
                       column.contains("payment")) {
                JComboBox<String> comboBox = new JComboBox<>();
                comboBox.addItem("Complete");
                comboBox.addItem("Pending");
                comboBox.addItem("Partial");
                comboBox.addItem("Processing");
                comboBox.addItem("Overdue");
                return comboBox;
            }
            else {
                JComboBox<String> comboBox = new JComboBox<>();
                comboBox.addItem("Active");
                comboBox.addItem("Inactive");
                return comboBox;
            }
        }
        else {
            return new JTextField(20);
        }
    }
}
