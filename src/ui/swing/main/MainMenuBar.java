package ui.swing.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import core.matrix.Matrix;
import ui.swing.edit.EditFrame;
import ui.swing.misc.ImageChooser;
import ui.swing.misc.LastSelectionChooser;
import ui.swing.palette.PalettesFrame;
import ui.swing.settings.SettingsFrame;

public class MainMenuBar extends JMenuBar {

    // members

    // menus
    private final JMenu mFile = new JMenu("File");
    private final JMenu mEdit = new JMenu("Edit");
    private final JMenu mSettings = new JMenu("Settings");

    // menu items

    // file
    private final JMenuItem miNew = new JMenuItem("New");
    private final JMenuItem miOpen = new JMenuItem("Open");
    private final JMenuItem miSave = new JMenuItem("Save");
    private final JMenuItem miExit = new JMenuItem("Exit");

    // edit
    private final JMenuItem miEditMatrix = new JMenuItem("Edit Matrix");
    private final JMenuItem miCreateSnapshot = new JMenuItem("Create Snapshot");

    // config
    private final JMenuItem miConfigApplication = new JMenuItem("Configure Application");
    private final JMenuItem miConfigPalettes = new JMenuItem("Configure Palettes");

    public MainMenuBar() {
        buildMenu();
        setupMenu();
    }

    private void buildMenu() {
        mFile.add(miNew);
        mFile.add(miOpen);
        mFile.add(miSave);
        mFile.addSeparator();
        mFile.add(miExit);

        mEdit.add(miEditMatrix);
        mEdit.add(miCreateSnapshot);

        mSettings.add(miConfigApplication);
        mSettings.add(miConfigPalettes);

        add(mFile);
        add(mEdit);
        add(mSettings);
    }

    private void setupMenu() {
        miNew.addActionListener(e -> EditFrame.newInstance().setVisible(true));
        miSave.addActionListener(e -> {
            JFileChooser fc = new LastSelectionChooser();
            int retVal = fc.showSaveDialog(PalettesFrame.instance());
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (!file.getName().endsWith(".mtx")) {
                    fc.setSelectedFile(new File(file.toPath() + ".mtx"));
                }
                try {
                    Files.write(Paths.get(fc.getSelectedFile().getCanonicalPath()), Matrix.toLines(MainFrame.instance().matrix()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miOpen.addActionListener(e -> {
            JFileChooser fc = new LastSelectionChooser();
            int retVal = fc.showOpenDialog(PalettesFrame.instance());
            if (retVal == JFileChooser.APPROVE_OPTION) {
                try (Stream<String> lines = Files.lines(Paths.get(fc.getSelectedFile().getCanonicalPath()))) {
                    Matrix evMatrix = Matrix.fromLines(lines.collect(Collectors.toList()));
                    if (evMatrix != null) {
                        MainFrame.instance().setMatrix(evMatrix);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miExit.addActionListener(e -> System.exit(-1));

        miEditMatrix.addActionListener(e -> EditFrame.editInstance().setVisible(true));
        miCreateSnapshot.addActionListener(e -> {
            JFileChooser fc = new ImageChooser();
            int retVal = fc.showSaveDialog(PalettesFrame.instance());
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (!file.getName().endsWith(".png")) {
                    fc.setSelectedFile(new File(file.toPath() + ".png"));
                }
                try {
                    ImageIO.write(MainFrame.instance().snapshot(), "PNG", fc.getSelectedFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        miConfigApplication.addActionListener(e -> SettingsFrame.instance().setVisible(true));
        miConfigPalettes.addActionListener(e -> PalettesFrame.instance().setVisible(true));
    }
}
