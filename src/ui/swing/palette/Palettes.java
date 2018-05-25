package ui.swing.palette;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import core.matrix.Evaluation;
import ui.settings.Settings;

public class Palettes {

    // instance
    private static Palettes s_instance;

    // members
    private final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
    private final Map<String, Image> images = new HashMap<>();
    private final Map<String, Map<Evaluation, String>> palettes = new LinkedHashMap<>();

    // getters
    Map<String, Map<Evaluation, String>> palettes() { return palettes; }

    // factory
    public static Palettes instance() {
        if (s_instance == null) {
            s_instance = new Palettes();
        }
        return s_instance;
    }

    public void loadPalettes() {
        loadImages();
        loadPalettesImpl();
    }

    public void savePalettes() {
        savePalettesImpl();
    }

    private Palettes() {
        // NOOP
    }

    public String[] getPaletteNames() {
        return palettes.keySet().toArray(new String[]{});
    }

    public Image getImage(Evaluation evaluation) {
        String palette = Settings.instance().properties().getProperty(Settings.PALETTE);
        return getImage(evaluation, palette);
    }

    public Image getImage(Evaluation evaluation, String palette) {
        if (palette != null) {
            Map<Evaluation, String> paletteImages = palettes.get(palette);
            if (paletteImages != null) {
                double scale = (double)Integer.parseInt(Settings.instance().properties().getProperty(Settings.IMAGE_SCALE)) / 100;
                String paletteImage = paletteImages.get(evaluation);
                Image image = paletteImage != null
                    ? images.get(paletteImage)
                    : null;
                return image != null
                    ? image.getScaledInstance((int)(image.getWidth(null) * scale), (int)(image.getHeight(null) * scale), Image.SCALE_SMOOTH)
                    : null;
            }
        }
        return null;
    }

    private void loadImages() {
        File directory = new File("./palette");
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file != null) {
                    String mimetype = fileTypeMap.getContentType(file);
                    String type = mimetype.split("/")[0];
                    //                    if(type.equals("image")) {
                    try {
                        Image img = ImageIO.read(file);
                        images.put(file.getName(), img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //                    }
                }
            }
        }
    }

    private void loadPalettesImpl() {
        File directory = new File("./palette");
        if (directory.exists()) {
            try (Stream<String> cfgLines = Files.lines(Paths.get("./palette/palette.cfg"))) {
                cfgLines.forEach(cfgLine -> parsePaletteCfgLine(cfgLine));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parsePaletteCfgLine(String cfgLine) {
        Map<Evaluation, String> paletteImages = new LinkedHashMap<>();
        String cfgLineArray[] = cfgLine.split(";");
        String name = cfgLineArray[0];

        palettes.put(name, paletteImages);

        String passed[] = cfgLineArray[1].split("=");
        String failed[] = cfgLineArray[2].split("=");
        String undecidable[] = cfgLineArray[3].split("=");

        paletteImages.put(Evaluation.valueOf(passed[0]), passed[1]);
        paletteImages.put(Evaluation.valueOf(failed[0]), failed[1]);
        paletteImages.put(Evaluation.valueOf(undecidable[0]), undecidable[1]);
    }

    private void savePalettesImpl() {
        File directory = new File("./palette");
        if (!directory.exists()) {
            directory.mkdir();
        }
        try {
            Files.write(Paths.get("./palette/palette.cfg"), createPaletteCfgLines());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> createPaletteCfgLines() {
        List<String> cfgLines = new ArrayList<>();
        for (Entry<String, Map<Evaluation, String>> entry : palettes.entrySet()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey());
            sb.append(";");
            sb.append(entry.getValue().entrySet().stream().
                map(e -> e.getKey() + "=" + e.getValue()).
                collect(Collectors.joining(";")));
            cfgLines.add(sb.toString());
        }
        return cfgLines;
    }
}
