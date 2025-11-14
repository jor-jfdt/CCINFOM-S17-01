import javax.swing.*;
import java.awt.*;

public class AppController {
    public AppController(AppGUI appGUI, AppModel appModel) {
        this.appGUI = appGUI;
        this.appModel = appModel;
    }


    private AppGUI appGUI;
    private AppModel appModel;
}
