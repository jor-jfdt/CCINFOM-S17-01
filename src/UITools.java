import com.toedter.calendar.JYearChooser;

import java.awt.*;
import javax.swing.*;

public class UITools {
    private static final Color PRIMARY_COLOR = new Color(0, 105, 55);

    public static Font getTitleFont() {
        int fontSize = Math.max(AppGUI.screenWidth / 50, AppGUI.screenHeight / 30);
        return new Font("Tahoma", Font.BOLD, fontSize);
    }

    public static Font getButtonFont() {
        int fontSize = Math.max(AppGUI.screenWidth / 90, AppGUI.screenHeight / 50);
        return new Font("Tahoma", Font.BOLD, fontSize);
    }

    public static Font getLeftPanelButtonFont() {
        int fontSize = Math.max(AppGUI.screenWidth / 90, AppGUI.screenHeight / 50);
        return new Font("Tahoma", Font.PLAIN, fontSize);
    }

    public static Font getLabelFont() {
        return new Font("Tahoma", Font.PLAIN, 13);
    }

    public static Dimension getMainMenuButtonSize() {
        return new Dimension(AppGUI.screenWidth / 4, AppGUI.screenHeight / 8);
    }
    public static Dimension getHideButtonSize() {
        int buttonHeight = (int)(AppGUI.screenHeight * 0.08);
        int hideButtonWidth = Math.max(42, AppGUI.screenWidth / 40);
        return new Dimension(hideButtonWidth, buttonHeight);
    }

    public static Dimension getLeftPanelButtonSize() {
        int leftPanelWidth = (int)(AppGUI.screenWidth * 0.3);
        int hideButtonWidth = 25;
        int availableWidth = leftPanelWidth - hideButtonWidth - 60;
        int buttonHeight = (int)(AppGUI.screenHeight * 0.08);
        return new Dimension(availableWidth, buttonHeight);
    }

    public static Color getPrimaryColor() {
        return PRIMARY_COLOR;
    }

    public static void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Tahoma", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
    }

        public static void styleComboBox(JComboBox<String> monthChooser) {
        monthChooser.setBackground(Color.WHITE);
        monthChooser.setForeground(PRIMARY_COLOR);
        monthChooser.setFont(new Font("Tahoma", Font.PLAIN, 13));
    }

    public static void styleYearChooser(JYearChooser yearChooser) {
        yearChooser.setBackground(Color.WHITE);
        yearChooser.setForeground(PRIMARY_COLOR);
        yearChooser.setFont(new Font("Tahoma", Font.PLAIN, 13));
    }
}
