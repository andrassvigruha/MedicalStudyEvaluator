package ui.swing.misc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JOptionPane;

public class FrameUtilities {

    private FrameUtilities() {
        // NOOP
    }

    public static void centerOnScreen(Window window) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width / 2 - window.getSize().width / 2, dim.height / 2 - window.getSize().height / 2);
    }
    
    public static boolean showConfirmationDlg(Component parent, String text) {
        int result = JOptionPane.showConfirmDialog(parent, text, "Confirm", JOptionPane.YES_NO_OPTION);
        switch (result) {
        case JOptionPane.YES_OPTION:
            return true;
        case JOptionPane.NO_OPTION:
        case JOptionPane.CLOSED_OPTION:
        default:
            return false;
        
        }
    }
}
