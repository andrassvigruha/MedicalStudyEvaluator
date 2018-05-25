package ui.swing.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import core.matrix.Evaluation;
import ui.swing.palette.Palettes;

public class MainComboRenderer extends DefaultListCellRenderer {

    // members
    private final Palettes palettes;
    private final JPanel panel;

    public MainComboRenderer() {
        palettes = Palettes.instance();
        panel = new JPanel(new BorderLayout());
        panel.setLayout(new BorderLayout());
    }

    @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Evaluation && palettes != null) {
            Evaluation evaluation = (Evaluation)value;
            Image img = palettes.getImage(evaluation);
            if (img != null) {
                JLabel icon = new JLabel(new ImageIcon(img));
                if (index >= 0) {
                    icon.setText(evaluation.toString());
                    panel.removeAll();
                    panel.add(icon, BorderLayout.WEST);
                    return panel;
                } else {
                    return icon;
                }
            }
            return super.getListCellRendererComponent(list, evaluation.displayString(), index, isSelected, cellHasFocus);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
