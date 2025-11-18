import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AppController implements ActionListener {
    public AppController(AppGUI appGUI, AppModel appModel) {
        this.appGUI = appGUI;
        this.appModel = appModel;
        this.appGUI.addMenuButtonListener(this);
    }
    public void connectToDatabase() throws SQLException, IOException, ClassNotFoundException {
        appModel.enterDatabase();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Controller for Menu Buttons
        for (int i = 0; i < appGUI.getMenuButtons().length; i++) {
            if (e.getSource() == appGUI.getMenuButtons()[i]) {
                System.out.println(appGUI.getMenuButtons()[i].getText() + " Menu Button is Clicked.");
                switch (i) {
                    case 0 -> appGUI.showPanel("Record");
                    case 1 -> appGUI.showPanel("Transaction");
                    case 2 -> appGUI.showPanel("Report");
                }
            }
        }
        //later on add Report Panel back button controller
        if (e.getSource() == appGUI.getRecordPanel().getBackButton() || e.getSource() == appGUI.getTransactionPanel().getBackButton() || e.getSource() == appGUI.getReportPanel().getBackButton()) {
            System.out.println("Back to Main Menu");
            appGUI.showPanel("main");
        }
        if (e.getSource() == appGUI.getRecordPanel().getHideButton()) {
            System.out.println("Hide Button in Record Pressed");
            appGUI.getRecordPanel().toggleOptionsVisibility();
        }
        else if (e.getSource() == appGUI.getTransactionPanel().getHideButton()) {
            System.out.println("Hide Button in Transaction Pressed");
            appGUI.getTransactionPanel().toggleOptionsVisibility();
        }
        else if (e.getSource() == appGUI.getReportPanel().getHideButton()) {
            System.out.println("Hide Button in Report Pressed");
            appGUI.getReportPanel().toggleOptionsVisibility();
        }
        for (int i = 0; i < appGUI.getRecordPanel().getOptions().length; i++) {
            if (e.getSource() == appGUI.getRecordPanel().getOptions()[i]) {
                System.out.println("Record Option " + appGUI.getRecordPanel().getOptions()[i].getText() + " is Clicked.");
                appGUI.getRecordPanel().showCard(appGUI.getRecordPanel().getOptions()[i].getText());
                try {
                    List<Map<String, Object>> queryResult = appModel.getTableEntries(appGUI.getRecordPanel().getOptions()[i].getText(), "*");
                    DefaultTableModel dtm = appModel.makeTableModel(queryResult);
                    appGUI.getRecordPanel().setTable(appGUI.getRecordPanel().getOptions()[i].getText().toLowerCase(), dtm);
                } catch(SQLException ex) {
                    ex.printStackTrace();

                }
            }
        }
        for (int i = 0; i < appGUI.getTransactionPanel().getOptions().length; i++) {
            if (e.getSource() == appGUI.getTransactionPanel().getOptions()[i]) {
                System.out.println("Transaction Option " + appGUI.getTransactionPanel().getOptions()[i].getText() + " is Clicked.");
                appGUI.getTransactionPanel().showCard(appGUI.getTransactionPanel().getOptions()[i].getText());
            }
        }
        for (int i = 0; i < appGUI.getReportPanel().getOptions().length; i++) {
            if (e.getSource() == appGUI.getReportPanel().getOptions()[i]) {
                System.out.println("Report Option " + appGUI.getReportPanel().getOptions()[i].getText() + " is Clicked.");
                appGUI.getReportPanel().showCard(appGUI.getReportPanel().getOptions()[i].getText());
            }
        }

        //insert logic for determining what option was clicked in Record Panel to determine the fields to show in Add/Update Dialog
        if (e.getSource() == appGUI.getRecordPanel().getAddButton()) {
            // Open Add Dialog
            //appGUI.getRecordPanel().AddButtonFeatures(appGUI);
            System.out.println("Add Button Clicked");
        } else if (e.getSource() == appGUI.getRecordPanel().getUpdateButton()) {
            // Open Update Dialog
            System.out.println("Update Button Clicked");
        } else if (e.getSource() == appGUI.getRecordPanel().getVoidButton()) {
            // Perform deletion
            System.out.println("Void Button Clicked");
        }

    }
    private final AppGUI appGUI;
    private final AppModel appModel;
}
