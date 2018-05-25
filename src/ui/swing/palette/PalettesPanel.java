package ui.swing.palette;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import core.matrix.Evaluation;
import ui.swing.misc.FrameUtilities;
import ui.swing.misc.ImageChooser;

public class PalettesPanel extends JPanel {

    // members
    private final Palettes palettes = Palettes.instance();
    private final String palette;
    private final Map<Evaluation, String> paletteImages;

    // getters
    String palette() { return palette; }
    Map<Evaluation, String> paletteImages() { return paletteImages; }

    PalettesPanel(String palette, Map<Evaluation, String> paletteImages) {
        this.palette = palette;
        this.paletteImages = paletteImages;
        build();
    }

    private void build() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        for (Entry<Evaluation, String> entry : paletteImages.entrySet()) {
            final JPanel evPanel = new JPanel();
            final Evaluation evaluation = entry.getKey();
            final Image img = palettes.getImage(evaluation, palette);
            final JButton button = new JButton("Change Image");

            evPanel.setLayout(new BoxLayout(evPanel, BoxLayout.LINE_AXIS));
            button.addActionListener(e -> {
                JFileChooser fc = new ImageChooser();
                int retVal = fc.showOpenDialog(PalettesFrame.instance());
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        File directory = new File("./palette");
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        File newFile = Paths.get("./palette/" + file.getName()).toFile();
                        if (!newFile.toPath().equals(file.toPath())) {
                            if (newFile.exists()) {
                                if (!FrameUtilities.showConfirmationDlg(this, "Selected icon already exists in palette directory. Do you really want to override?")) {
                                    return;
                                }
                            } else {
                                newFile.createNewFile();
                            }
                            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                        BufferedImage newImg = ImageIO.read(file);
                        paletteImages.put(evaluation, file.getName());
                        setupPanel(evPanel, button, evaluation, newImg);
                        PalettesFrame.instance().rePack();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            setupPanel(evPanel, button, evaluation, img);
            add(new JSeparator());
            add(evPanel);
        }
    }

    private static void setupPanel(JPanel panel, JButton button, Evaluation evaluation, Image img) {
        panel.removeAll();
        panel.add(new JLabel(evaluation.displayString()));
        panel.add(Box.createHorizontalGlue());
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(img != null ? new JLabel(new ImageIcon(img)) : new JLabel("No Image"));
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(button);
    }
}
