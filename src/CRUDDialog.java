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
            comboBox.addItem("Bank Transfer");
            comboBox.addItem("Cash");
            comboBox.addItem("Check");
            comboBox.addItem("Mobile Payment");
            return comboBox;
        }else {
            return new JTextField(20);
        }
    }
}
