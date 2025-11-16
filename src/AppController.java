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
                    //case 2 -> appGUI.showPanel("Report");
                }
            }
        }
    }
    private final AppGUI appGUI;
    private final AppModel appModel;
}
