
public class Driver {
    public static void main(String[] args) {
        AppGUI app = new AppGUI("Health Maintenance Organization");
        AppModel model = new AppModel();
        AppController controller = new AppController(app, model);
    }
}
