import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppController implements ActionListener {
    public AppController(AppGUI appGUI, AppModel appModel) {
        this.appGUI = appGUI;
        this.appModel = appModel;
        this.appGUI.addMenuButtonListener(this);
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

    }
    private final AppGUI appGUI;
    private final AppModel appModel;
}
