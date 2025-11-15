import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CRUDDialog extends JDialog {
    public enum Mode {
        ADD,
        UPDATE,
        DELETE
    }

    public CRUDDialog(JFrame parent, Mode mode, String[] columns, String title) {
        super(parent, title, true);
        this.mode = mode;
        this.columns = columns;
        this.inputFields = new java.util.HashMap<>();
        this.confirmed = false;

        int calculatedHeight = Math.min(600, 150 + (columns.length * 40));
        this.setPreferredSize(new Dimension(400, calculatedHeight));
        this.setResizable(false);

        initializeDialog();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeDialog() {
        setLayout(new BorderLayout(10,10));

        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5,5,5,5);

        for (String column : columns) {
            JLabel label = new JLabel(formatColumnName(column) + ":");
            label.setFont(UITools.getLabelFont());

            gbc.gridx = 0;
            gbc.weightx = 0.3;
            panel.add(label, gbc);

            JComponent inputField = createInputField(column);
            inputFields.put(column, inputField);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(inputField, gbc);

            gbc.gridy++;
        }

        if (mode == Mode.DELETE) {
            for (JComponent field : inputFields.values()) {
                field.setEnabled(false);
            }
        }

        return panel;
    }

    private JComponent createInputField(String column) {
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

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton confirmButton = new JButton(mode == Mode.DELETE ? "Delete" : "Confirm");
        JButton cancelButton = new JButton("Cancel");

        UITools.styleButton(confirmButton);
        UITools.styleButton(cancelButton);

        if (mode == Mode.DELETE) {
            confirmButton.setBackground(Color.RED);
        }

        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        panel.add(cancelButton);
        panel.add(confirmButton);

        return panel;
    }

    private String formatColumnName(String column) {
        return column.replace("_", " ")
                .substring(0, 1).toUpperCase() +
                column.replace("_", " ").substring(1);
    }

    public void setFieldValue(String column, String value) {
        JComponent field = inputFields.get(column);
        if (field instanceof JTextField) {
            ((JTextField) field).setText(value);
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setSelectedItem(value);
        }
    }

    public String getFieldValue(String column) {
        JComponent field = inputFields.get(column);
        if (field instanceof JTextField) {
            return ((JTextField) field).getText();
        } else if (field instanceof JComboBox) {
            return (String) ((JComboBox<?>) field).getSelectedItem();
        }
        return "";
    }

    public Map<String, String> getFieldValues() {
        Map<String, String> field = new HashMap<>();
        for (String column : columns) {
            field.put(column, getFieldValue(column));
        }
        return field;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private final Mode mode;
    private final String[] columns;
    private final Map<String, JComponent> inputFields;
    private boolean confirmed;
}
