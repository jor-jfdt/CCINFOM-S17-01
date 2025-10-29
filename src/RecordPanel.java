import javax.swing.*;

public class RecordPanel extends  BasePanel{
    public RecordPanel() {
        super("Record Management");

        options = new JButton[10];
        options[0] = new JButton("Client");
        options[1] = new JButton("Company Policy");
        options[2] = new JButton("Illness");
        options[3] = new JButton("Hospital");
        options[4] = new JButton("Doctor");

        for (JButton option : options) {
            leftPanel.add(option);
        }
    }
}
