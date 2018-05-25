package ui.swing.palette;

import java.awt.Dimension;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import core.matrix.Evaluation;
import ui.swing.misc.FrameUtilities;

public class PalettesFrame extends JFrame {

    // instance
    private static PalettesFrame s_instance;

    // members
    private final Palettes palettes = Palettes.instance();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JButton newPaletteButton = new JButton("New Palette");
    private final JButton deletePaletteButton = new JButton("Delete Palette");
    private final JButton saveButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Cancel");

    // factory
    public static PalettesFrame instance() {
        if (s_instance == null) {
            s_instance = new PalettesFrame();
        }
        return s_instance;
    }

    {
        newPaletteButton.addActionListener(e -> {
            String palette = JOptionPane.showInputDialog("Please enter new palette name:");
            Map<Evaluation, String> paletteImages = new TreeMap<>();
            paletteImages.put(Evaluation.PASSED, " ");
            paletteImages.put(Evaluation.FAILED, " ");
            paletteImages.put(Evaluation.UNDECIDABLE, " ");
            tabbedPane.addTab(palette, new PalettesPanel(palette, paletteImages));
            rePack();
        });
        deletePaletteButton.addActionListener(e -> {
            if (FrameUtilities.showConfirmationDlg(PalettesFrame.this, "Do you really want to delete this palette?")) {
                tabbedPane.remove(tabbedPane.getSelectedIndex());
                rePack();
            }
        });
        saveButton.addActionListener(e -> {
            palettes.palettes().clear();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                PalettesPanel palettesPanel = (PalettesPanel)tabbedPane.getComponentAt(i);
                palettes.palettes().put(palettesPanel.palette(), palettesPanel.paletteImages());
            }
            setVisible(false);
        });
        cancelButton.addActionListener(e -> setVisible(false));
    }

    private PalettesFrame() {
        // NOOP
    }

    @Override public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            build();
        }
    }

    void rePack() {
        revalidate();
        repaint();
        pack();
    }

    private void build() {
        getContentPane().removeAll();
        
        tabbedPane.removeAll();
        for (Entry<String, Map<Evaluation, String>> entry : palettes.palettes().entrySet()) {
            tabbedPane.addTab(entry.getKey(), new PalettesPanel(entry.getKey(), new TreeMap<>(entry.getValue())));
        }
        
        JPanel topButtonsPanel = new JPanel();
        topButtonsPanel.setLayout(new BoxLayout(topButtonsPanel, BoxLayout.LINE_AXIS));
        topButtonsPanel.add(Box.createHorizontalGlue());
        topButtonsPanel.add(newPaletteButton);
        topButtonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topButtonsPanel.add(deletePaletteButton);

        JPanel bottomButtonsPanel = new JPanel();
        bottomButtonsPanel.setLayout(new BoxLayout(bottomButtonsPanel, BoxLayout.LINE_AXIS));
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(saveButton);
        bottomButtonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomButtonsPanel.add(cancelButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(topButtonsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(tabbedPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(bottomButtonsPanel);
        
        getContentPane().add(mainPanel);
        pack();
        FrameUtilities.centerOnScreen(this);
    }
}
