package ui.swing.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import ui.settings.Settings;
import ui.swing.main.MainFrame;
import ui.swing.misc.FrameUtilities;
import ui.swing.misc.NumberTextField;
import ui.swing.palette.Palettes;

public class SettingsFrame extends JFrame {

    // instance
    private static SettingsFrame s_instance;
    
    // members
    private final JComboBox<String> palettesCombo = new JComboBox<>(Palettes.instance().getPaletteNames());
    private final NumberTextField imgScaleField = new NumberTextField(0, 100);
    private final JSlider imgScaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
    private final JComboBox<Integer> fontSizeCombo = new JComboBox<>(new Integer[]{8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72});
    private final JComboBox<String> fontTypeCombo = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    private final NumberTextField fontRotateField = new NumberTextField(0, 360);
    private final JCheckBox snapBordersCheckbox = new JCheckBox();
    private final JButton saveButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Cancel");

    {
        imgScaleSlider.setMajorTickSpacing(10);
        imgScaleSlider.setMinorTickSpacing(1);
        imgScaleSlider.setPaintTicks(true);
        imgScaleSlider.setPaintLabels(true);
        imgScaleSlider.addChangeListener(e -> {
            imgScaleField.setText(Integer.toString(imgScaleSlider.getValue()));
        });
        imgScaleField.setMaximumSize(imgScaleField.getPreferredSize());
        imgScaleField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                imgScaleSlider.setValue(imgScaleField.getIntValue());
            }
        });
//        fontSizeCombo.setMaximumSize(fontSizeCombo.getPreferredSize());
//        fontTypeCombo.setMaximumSize(fontTypeCombo.getPreferredSize());
        fontRotateField.setMaximumSize(fontRotateField.getPreferredSize());
        saveButton.addActionListener(e -> {
            Settings.instance().properties().put(Settings.PALETTE, String.valueOf(palettesCombo.getSelectedItem()));
            Settings.instance().properties().put(Settings.IMAGE_SCALE, Integer.toString(imgScaleField.getIntValue()));
            Settings.instance().properties().put(Settings.FONT_SIZE, Integer.toString((int)fontSizeCombo.getSelectedItem()));
            Settings.instance().properties().put(Settings.FONT_TYPE, String.valueOf(fontTypeCombo.getSelectedItem()));
            Settings.instance().properties().put(Settings.FONT_ROTATE, Integer.toString(fontRotateField.getIntValue()));
            Settings.instance().properties().put(Settings.SNAPSHOT_BORDERS, Boolean.toString(snapBordersCheckbox.isSelected()));
            // TODO : property change listener
            MainFrame.instance().setFontSize(Integer.parseInt(Settings.instance().properties().getProperty(Settings.FONT_SIZE)));
            MainFrame.instance().setFontType(Settings.instance().properties().getProperty(Settings.FONT_TYPE));
            MainFrame.instance().redraw();
            setVisible(false);
        });
        cancelButton.addActionListener(e -> setVisible(false));
    }

    // factory
    public static SettingsFrame instance() {
        if (s_instance == null) {
            s_instance = new SettingsFrame();
        }
        return s_instance;
    }

    private SettingsFrame() {
        build();
    }
    
    @Override public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            build();
        }
    }
    
    private void build() {
        getContentPane().removeAll();
        
        palettesCombo.setSelectedItem(Settings.instance().properties().getProperty(Settings.PALETTE));
        imgScaleField.setText(Settings.instance().properties().getProperty(Settings.IMAGE_SCALE));
        imgScaleSlider.setValue(Integer.valueOf(Settings.instance().properties().getProperty(Settings.IMAGE_SCALE)));
        fontSizeCombo.setSelectedItem(Integer.valueOf(Settings.instance().properties().getProperty(Settings.FONT_SIZE)));
        fontTypeCombo.setSelectedItem(Settings.instance().properties().getProperty(Settings.FONT_TYPE));
        fontRotateField.setText(Settings.instance().properties().getProperty(Settings.FONT_ROTATE));
        snapBordersCheckbox.setSelected(Boolean.valueOf(Settings.instance().properties().getProperty(Settings.SNAPSHOT_BORDERS)));
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonsPanel.add(cancelButton);
        
        JPanel palettePanel = new JPanel();
        palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.PAGE_AXIS));
        palettePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), "Palette and Icon"));
        palettePanel.add(wrapComponents(new JLabel("Palettes"), palettesCombo));
        palettePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        palettePanel.add(wrapComponents(new JLabel("Icon scale"), wrapsSliderComponents(imgScaleField, imgScaleSlider)));
        palettePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel fontPanel = new JPanel();
        fontPanel.setLayout(new BoxLayout(fontPanel, BoxLayout.PAGE_AXIS));
        fontPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), "Font"));
        fontPanel.add(wrapComponents(new JLabel("Font size"), fontSizeCombo));
        fontPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        fontPanel.add(wrapComponents2(new JLabel("Font type"), glue(), filler(10, 0), fontTypeCombo));
        fontPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        fontPanel.add(wrapComponents2(new JLabel("Font rotate"), glue(), filler(10, 0), fontRotateField, new JLabel("°")));
        fontPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel snapshotPanel = new JPanel();
        snapshotPanel.setLayout(new BoxLayout(snapshotPanel, BoxLayout.PAGE_AXIS));
        snapshotPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), "Snapshot"));
        snapshotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        snapshotPanel.add(wrapComponents(new JLabel("Remove borders on snapshot"), snapBordersCheckbox));
        snapshotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(palettePanel);
        mainPanel.add(fontPanel);
        mainPanel.add(snapshotPanel);
        mainPanel.add(buttonsPanel);
        
        getContentPane().add(mainPanel);
        setResizable(false);
        pack();
        FrameUtilities.centerOnScreen(this);
    }
    
    private static JPanel wrapComponents(JLabel label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(label);
        panel.add(Box.createHorizontalGlue());
        panel.add(component);
        return panel;
    }
    
    private static JPanel wrapsSliderComponents(JTextField textField, JSlider slider) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(textField);
        panel.add(new JLabel("%"));
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(slider);
        return panel;
    }
    
    private static JPanel wrapComponents2(Component... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        for (Component component : components) {
            panel.add(component);
        }
        return panel;
    }
    
    private static Component glue() {
        return Box.createHorizontalGlue();
    }

    private static Component filler(int x, int y) {
        return Box.createRigidArea(new Dimension(x, y));
    }
}
