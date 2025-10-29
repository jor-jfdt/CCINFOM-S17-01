import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AppGUI extends JFrame {
    public AppGUI(String title) {
        super(title);
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        prevHeight = this.getHeight();
        prevWidth = this.getWidth();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!isResizing)
                maintainAspectRatio();
            }
        });

        this.setMinimumSize(new Dimension(640, 360));
        this.setMaximumSize(new Dimension(1920, 1080));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void maintainAspectRatio() {
        int ap_width;
        int ap_height;

        isResizing = true;

        Rectangle bounds = getBounds();
        int width = bounds.width;
        int height = bounds.height;

        if (width == prevWidth && height == prevHeight) {
            isResizing = false;
            return;
        }

        if (Math.abs(width - prevWidth) > Math.abs(height - prevHeight)) {
            ap_width = width;
            ap_height =  (int) (ap_width / ASPECT_RATIO);
        } else {
            ap_height = height;
            ap_width = (int) (ap_height * ASPECT_RATIO);
        }

        Rectangle newBounds = getBounds();
        prevWidth = ap_width;
        prevHeight = ap_height;

        SwingUtilities.invokeLater(() -> {
            setBounds(newBounds.x, newBounds.y, prevWidth, prevHeight);
        });
        isResizing = false;
    }

    private boolean isResizing = false;
    private int prevWidth;
    private int prevHeight;

    private static final double ASPECT_RATIO = 16.0 / 9.0;
    private static final int screenWidth = 1280;
    private static final int screenHeight = (int) (screenWidth / ASPECT_RATIO);
}
