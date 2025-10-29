import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

public class AppController implements ComponentListener {
    public AppController(AppGUI appGUI, AppModel appModel) {
        this.appGUI = appGUI;
        this.appModel = appModel;
        this.isResizing = false;

        prevHeight = appGUI.getHeight();
        prevWidth = appGUI.getWidth();

        appGUI.addComponentListener(this);
    }

    private void maintainAspectRatio() {
        if (isResizing) return;
        isResizing = true;

        Rectangle bounds = appGUI.getBounds();
        int width = bounds.width;
        int height = bounds.height;

        if (width == prevWidth && height == prevHeight) {
            isResizing = false;
            return;
        }

        int ap_width, ap_height;
        if (Math.abs(width - prevWidth) > Math.abs(height - prevHeight)) {
            ap_width = width;
            ap_height = (int) (ap_width / AppGUI.ASPECT_RATIO);
        } else {
            ap_height = height;
            ap_width = (int) (ap_height * AppGUI.ASPECT_RATIO);
        }

        prevWidth = ap_width;
        prevHeight = ap_height;
        AppGUI.updateScreenDimensions(ap_width, ap_height);

        SwingUtilities.invokeLater(() -> {
            appGUI.setBounds(bounds.x, bounds.y, ap_width, ap_height);
            appGUI.update();
            appGUI.revalidate();
            appGUI.repaint();
            isResizing = false;
        });
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (!isResizing) {
            maintainAspectRatio();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    private AppGUI appGUI;
    private AppModel appModel;
    private boolean isResizing;
    private int prevWidth;
    private int prevHeight;
}
