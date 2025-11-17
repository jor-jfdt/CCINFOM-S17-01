import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecordPanel extends BasePanel {
    public RecordPanel() {
        super("Record Management", "app_wallpaper.png");

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        voidButton = new JButton("Delete");

        UITools.styleButton(addButton);
        UITools.styleButton(updateButton);
        UITools.styleButton(voidButton);

        southButtonPanel.setLayout(new BoxLayout(southButtonPanel, BoxLayout.X_AXIS));
        southButtonPanel.add(voidButton);
        southButtonPanel.add(Box.createHorizontalGlue());
        southButtonPanel.add(updateButton);
        southButtonPanel.add(Box.createHorizontalGlue());
        southButtonPanel.add(addButton);

        createCRUDContent();
    }

    @Override
    protected void createHeaderColumns() {
        header_columns = new HashMap<>();

        header_columns.put("clients",
                new String[] { "client_id", "first_name", "last_name", "middle_initial",
                        "birth_date", "is_employee", "sex", "is_active" });

        header_columns.put("policy",
                new String[] { "policy_id", "policy_name", "coverage_type", "coverage_limit",
                        "payment_period", "inclusion" });

        header_columns.put("hospital",
                new String[] { "hospital_id", "hospital_name", "address",
                        "city", "zipcode", "contact_no", "email" });

        header_columns.put("doctor",
                new String[] { "doctor_id", "first_name", "last_name", "middle_initial", "doctor_type",
                        "contact_no", "email" });

        header_columns.put("illness",
                new String[] { "illness_id", "illness_name", "icd10_code" });
    }

    private void createCRUDContent() {
        Map<String, JPanel> panelMap = new LinkedHashMap<>();
        createHeaderColumns();

        panelMap.put("Clients", createCRUDPanel(
                "clients",
                "Search Client by Name:",
                header_columns.get("clients")
        ));

        panelMap.put("Policy", createCRUDPanel(
                "policy",
                "Search Policy by Name:",
                header_columns.get("policy")
        ));

        panelMap.put("Hospital", createCRUDPanel(
                "hospital",
                "Search Hospital by Name:",
                header_columns.get("hospital")
        ));

        panelMap.put("Doctor", createCRUDPanel(
                "doctor",
                "Search Doctor by Name:",
                header_columns.get("doctor")
        ));

        panelMap.put("Illness", createCRUDPanel(
                "illness",
                "Search Illness by Name:",
                header_columns.get("illness")
        ));

        populateCardLayout(panelMap);
    }

//    public void AddButtonFeatures(JFrame parentFrame) {
//        CRUDDialog dialog = new CRUDDialog(
//                parentFrame,
//                "Add Client",
//                BaseDialog.Mode.ADD,
//                new String[]{"first_name", "last_name", "middle_initial", "birth_date", "is_employee", "sex"}
//        );
//
//        dialog.setVisible(true);
//
//        if (dialog.isConfirmed()) {
//            System.out.println("Confirm button clicked!");
//            System.out.println("Values: " + dialog.getFieldValues());
//        } else {
//            System.out.println("Cancel button clicked!");
//        }
//    }

    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    public void addVoidButtonListener(ActionListener listener) {
        voidButton.addActionListener(listener);
    }

    public JButton getAddButton() {
        return addButton;
    }
    public JButton getUpdateButton() {
        return updateButton;
    }
    public JButton getVoidButton() {
        return voidButton;
    }

    private final JButton addButton;
    private final JButton updateButton;
    private final JButton voidButton;
}