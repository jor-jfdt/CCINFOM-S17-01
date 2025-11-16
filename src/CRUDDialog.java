import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CRUDDialog extends BaseDialog {
    public CRUDDialog(JFrame parent, String title, BaseDialog.Mode mode, String[] columns) {
        super(parent, title, mode, columns,400, Math.min(600, 150 + (columns.length * 40)));
        this.mode = mode;
        this.columns = columns;
    }

    @Override
    protected JComponent createInputField(String column) {
        if (column.toLowerCase().contains("date"))
            return new JTextField(10);
        else if (column.toLowerCase().contains("is_") ||
                 column.equalsIgnoreCase("sex") ||
                 column.toLowerCase().contains("status")) {
            JComboBox<String> comboBox = new JComboBox<>();
            if (column.equals("sex")) {
                comboBox.addItem("M");
                comboBox.addItem("F");
            } else {
                comboBox.addItem("true");
                comboBox.addItem("false");
            }
            return comboBox;
        } else {
            return new JTextField(20);
        }
    }
}
