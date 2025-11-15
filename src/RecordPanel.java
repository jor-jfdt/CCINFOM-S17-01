import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

        southButtonPanel.add(addButton);
        southButtonPanel.add(updateButton);
        southButtonPanel.add(voidButton);

        createCRUDContent();
    }

    @Override
    protected void updateComponent() {
        if (options != null) {
            int leftPanelWidth = (int)(AppGUI.screenWidth * 0.3);
            int hideButtonWidth = 25; // Account for hide button width
            int availableWidth = leftPanelWidth - hideButtonWidth - 60; // Extra margin for padding
            int buttonHeight = (int)(AppGUI.screenHeight * 0.08); // Slightly smaller height
            Dimension buttonSize = new Dimension(availableWidth, buttonHeight);

            Font buttonFont = new Font("Algerian", Font.PLAIN,
                    Math.max(AppGUI.screenWidth / 100, AppGUI.screenHeight / 60));

            for (JButton option : options) {
                option.setPreferredSize(buttonSize);
                option.setMaximumSize(buttonSize);
                option.setMinimumSize(buttonSize);
                option.setFont(buttonFont);
                option.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    @Override
    protected void createCRUDContent() {
        Map<String, JPanel> panelMap = new LinkedHashMap<>();

        panelMap.put("Clients", createCRUDPanel(
                "clients",
                "Search Client by Name:",
                new String[] { "client_id", "first_name", "last_name", "middle_initial",
                               "birth_date", "is_employee", "sex", "is_active" }
        ));

        panelMap.put("Policy", createCRUDPanel(
                "policy",
                "Search Policy by Name:",
                new String[] { "policy_id", "policy_name", "coverage_type", "coverage_limit",
                               "payment_period", "inclusion"}
        ));

        panelMap.put("Hospital", createCRUDPanel(
                "hospital",
                "Search Hospital by Name:",
                new String[] { "hospital_id", "hospital_name", "address",
                               "city", "zipcode", "contact_no", "email" }
        ));

        panelMap.put("Doctor", createCRUDPanel(
                "doctor",
                "Search Doctor by Name:",
                new String[] { "doctor_id", "first_name", "last_name", "middle_initial", "doctor_type",
                               "contact_no", "email" }
        ));

        panelMap.put("Illness", createCRUDPanel(
                "illness",
                "Search Illness by Name:",
                new String[] { "illness_id", "illness_name", "icd10_code" }
        ));

        populateCardLayout(panelMap);
    }

    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    public void addVoidButtonListener(ActionListener listener) {
        voidButton.addActionListener(listener);
    }

    private JButton addButton;
    private JButton updateButton;
    private JButton voidButton;
}