package ui.settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JLabel;

public class Settings {

    // constants
    private static final Properties DEFAULTS = new Properties();
    public static final String UI = "ui";
    public static final String PALETTE = "palette";
    public static final String LAST_DIR = "lastdir";
    public static final String IMAGE_SCALE = "imgscale";
    public static final String FONT_SIZE = "fontsize";
    public static final String FONT_TYPE = "fonttype";
    public static final String FONT_ROTATE = "fontrotate";
    public static final String STUDY_CELL_ALIGNMENT = "stdcellagmt";
    public static final String CRITERIA_CELL_ALIGNMENT = "crtcellagmt";
    public static final String CRITERIA_ROW_ALIGNMENT = "crtrowagmt";
    public static final String SNAPSHOT_BORDERS = "snapborders";
    
    // instance
    private static Settings s_instance;
    
    // members
    private final Properties properties = new Properties(DEFAULTS);

    // getters
    public Properties properties() { return properties; }

    // factory
    public static final Settings instance() {
        if (s_instance == null) {
            s_instance = new Settings();
        }
        return s_instance;
    }

    static {
        DEFAULTS.put(UI, "swing");
        DEFAULTS.put(LAST_DIR, "C://");
        DEFAULTS.put(IMAGE_SCALE, Integer.toString(100));
        DEFAULTS.put(FONT_SIZE, Integer.toString(12));
        DEFAULTS.put(FONT_TYPE, "Calibri");
        DEFAULTS.put(FONT_ROTATE, Integer.toString(0));
        DEFAULTS.put(STUDY_CELL_ALIGNMENT, Integer.toString(JLabel.LEFT));
        DEFAULTS.put(CRITERIA_CELL_ALIGNMENT, Integer.toString(JLabel.CENTER));
        DEFAULTS.put(CRITERIA_ROW_ALIGNMENT, Integer.toString(JLabel.BOTTOM));
        DEFAULTS.put(SNAPSHOT_BORDERS, Boolean.toString(true));
    }

    private Settings() {
        // NOOP
    }
    
    public void loadSettings() {
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveSettings() {
        try (OutputStream output = new FileOutputStream("config.properties")) {
            properties.store(output, "---Settings---");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
