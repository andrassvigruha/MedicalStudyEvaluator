package ui.swing.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.matrix.Matrix;
import ui.settings.Settings;
import ui.swing.misc.FrameUtilities;
import ui.swing.palette.Palettes;

public class MainFrame extends JFrame {

    // instance
    private static MainFrame s_instance;

    // getters
    public Matrix matrix() { return table.getMatrix(); }

    // members
    private final MainMenuBar menuBar = new MainMenuBar();
    private final MainTable table = new MainTable();

    // factory
    public static MainFrame instance() {
        if (s_instance == null) {
            s_instance = new MainFrame();
        }
        return s_instance;
    }

    private MainFrame() {
        build();
        FrameUtilities.centerOnScreen(this);
        Palettes.instance().loadPalettes();
        Runtime.getRuntime().addShutdownHook(new Thread(Palettes.instance()::savePalettes));
        Settings.instance().loadSettings();
        Runtime.getRuntime().addShutdownHook(new Thread(Settings.instance()::saveSettings));
        setFontSize(Integer.parseInt(Settings.instance().properties().getProperty(Settings.FONT_SIZE)));
        setFontType(Settings.instance().properties().getProperty(Settings.FONT_TYPE));
    }

    public void createMatrix(int rowNum, int colNum) {
        table.createMatrix(rowNum, colNum);
    }

    public void editMatrix(int rowNum, int colNum) {
        table.editMatrix(rowNum, colNum);
    }

    public void setMatrix(Matrix matrix) {
        table.setMatrix(matrix);
    }

    public void redraw() {
        table.redraw();
    }
    
    public BufferedImage snapshot() {
        return table.snapshot();
    }

    public void setFontSize(int size) {
        table.setFontSize(size);
    }

    public void setFontType(String type) {
        table.setFontType(type);
    }

    private void build() {
        setJMenuBar(menuBar);
        add(createTablePanel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
    }
    
    private JPanel createTablePanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(table, BorderLayout.CENTER);
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        return panel;
    }
}
