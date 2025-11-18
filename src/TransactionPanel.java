import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionPanel extends BasePanel {
    public TransactionPanel() {
        super("Manage Transaction", "app_wallpaper.png");

        addButton = new JButton("Add Transaction");
        updateButton = new JButton("Update Transaction");

        UITools.styleButton(addButton);
        UITools.styleButton(updateButton);

        southButtonPanel.setLayout(new BoxLayout(southButtonPanel, BoxLayout.X_AXIS));
        southButtonPanel.add(Box.createHorizontalGlue());
        southButtonPanel.add(updateButton);
        southButtonPanel.add(Box.createHorizontalGlue());
        southButtonPanel.add(addButton);

        createTransactionOptions();
    }

    @Override
    protected void createHeaderColumns() {
        header_columns = new HashMap<>();

        header_columns.put("client_policy",
                new String[] { "client_plan_id", "policy_id", "member_id", "preexisting_illness", "effective_Date",
                "expiry_date", "stats" }
        );

        header_columns.put("client_payment",
                new String[] { "payment_id", "client_plan_id", "amount", "payment_date", "payment_method", "status" }
        );

        header_columns.put("claim",
                new String[] { "claim_id", "client_plan_id", "illness_id", "claim_date", "hospital_id",
                        "doctor_id", "service_date", "service_type", "covered_amount", "status" }
        );

        header_columns.put("payout",
                new String[] { "payout_id", "claim_id", "client_plan_id", "payout_date", "payout_amount", "status" }
        );

        header_columns.put("loa", //valid until is good also
                new String[] { "request_id", "client_plan_id", "hospital_id", "doctor_id", "illness_id",
                        "service_type","valid_until", "status" }
        );
    }

    private void createTransactionOptions() {
        Map<String, JPanel> panelMap = new LinkedHashMap<>();
        createHeaderColumns();

        panelMap.put("Buy Client Policy", createCRUDPanel("buy_client_policy",
                "Search Transaction by Client Name:",
                header_columns.get("client_policy")));
        panelMap.put("Client Payment of Premium", createCRUDPanel("client_payment_of_premium",
                "Search Payment by Client Name:",
                header_columns.get("payment")));
        panelMap.put("Doctor Consultation Claim", createCRUDPanel("doctor_consultation_claim",
                "Search Consultation by Client Name:",
                header_columns.get("claim")));
        panelMap.put("Hospitalization Claim", createCRUDPanel("hospitalization_claim",
                "Search Hospitalization by Client Name:",
                header_columns.get("claim")));
        panelMap.put("Payout to Hospital", createCRUDPanel("payout_to_hospital",
                "Search Payout by Hospital Name:",
                header_columns.get("payout")));
        panelMap.put("Payout to Doctor", createCRUDPanel("payout_to_doctor",
                "Search Payout by Doctor Name:",
                header_columns.get("payout")));
        panelMap.put("Request LOA", createCRUDPanel("request_loa",
                "Search LOA by Client Name:",
                header_columns.get("loa")));

        populateCardLayout(panelMap);
    }

    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    private JButton addButton;
    private JButton updateButton;
}


