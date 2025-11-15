import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(
                getClass().getResourceAsStream("/" + imagePath)
            ));
        } catch (IOException e) {
            System.err.println("Could not load background image: " + imagePath);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Background image not found: " + imagePath);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}