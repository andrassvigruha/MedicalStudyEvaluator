package ui.swing.misc;

import java.io.File;

import javax.swing.JFileChooser;

import ui.settings.Settings;

public class LastSelectionChooser extends JFileChooser {

    public LastSelectionChooser() {
        super((String)Settings.instance().properties().get(Settings.LAST_DIR));
        addActionListener(e -> {
            File currentDirectory = getCurrentDirectory();
            if (currentDirectory != null) {
                Settings.instance().properties().put(Settings.LAST_DIR, currentDirectory.getAbsolutePath());
            }
        });
    }
}
