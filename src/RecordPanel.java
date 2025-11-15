import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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

    @Override
    protected void updateComponent() {
        if (options != null) {
            Dimension buttonSize = UITools.getLeftPanelButtonSize();
            Font buttonFont = UITools.getLeftPanelButtonFont();

            for (JButton option : options) {
                option.setFont(buttonFont);
                Dimension prefSize = new Dimension(100, buttonSize.height);
                option.setPreferredSize(prefSize);
            }
        }

        if (hideButton != null) {
            Dimension hideButtonSize = UITools.getHideButtonSize();
            hideButton.setPreferredSize(hideButtonSize);
            hideButton.setMinimumSize(hideButtonSize);
            hideButton.setMaximumSize(hideButtonSize);
            hideButton.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
        }

        leftPanel.revalidate();
        leftPanel.repaint();
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