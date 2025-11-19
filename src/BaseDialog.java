import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseDialog extends JDialog {
    public enum Mode {
        ADD,
        UPDATE,
        DELETE
    }

    public BaseDialog(JFrame parent, String title, Mode mode, String[] columns, int width, int height) {
        super(parent, title, true);
        this.confirmed = false;
        this.mode = mode;
        this.columns = columns;
        this.inputFields = new java.util.HashMap<>();

        this.setPreferredSize(new Dimension(width, height));
        this.setResizable(false);

        initializeDialog();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeDialog() {
        setLayout(new BorderLayout(10, 10));

        contentPanel = createFormPanel();

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        buttonPanel = createButtonPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }

    private String formatColumnName(String column) {
        return column.replace("_", " ")
                .substring(0, 1).toUpperCase() +
                column.replace("_", " ").substring(1);
    }

    protected JPanel createFormPanel() {
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

        if (mode == BaseDialog.Mode.DELETE) {
            for (JComponent field : inputFields.values()) {
                field.setEnabled(false);
            }
        }

        return panel;
    }

    protected JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");

        UITools.styleButton(confirmButton);
        UITools.styleButton(cancelButton);

        panel.add(cancelButton);
        panel.add(confirmButton);

        return panel;
    }

    protected abstract JComponent createInputField(String column);

    public void setFieldValue(String column, String value) {
        JComponent field = inputFields.get(column);
        if (field instanceof JTextField) {
            ((JTextField) field).setText(value);
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setSelectedItem(value);
        }
    }

    public Object getFieldValue(String column) {
        JComponent field = inputFields.get(column);
        if (field instanceof JTextField) {
            return ((JTextField) field).getText();
        } else if (field instanceof JComboBox) {
            return ((JComboBox<?>) field).getSelectedItem();
        }
        return "";
    }

    public Map<String, Object> getFieldValues() {
        Map<String, Object> field = new LinkedHashMap<>();
        for (String column : columns) {
            field.put(column, getFieldValue(column));
        }
        return field;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    protected JButton confirmButton;
    protected JButton cancelButton;
    protected JPanel contentPanel;
    protected JPanel buttonPanel;
    protected BaseDialog.Mode mode;
    protected String[] columns;
    protected Map<String, JComponent> inputFields;
    protected boolean confirmed;
}
