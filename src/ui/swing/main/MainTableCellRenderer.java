package ui.swing.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import core.matrix.Evaluation;
import ui.settings.Settings;
import ui.swing.palette.Palettes;

public class MainTableCellRenderer extends DefaultTableCellRenderer {

    // constants
    private static final int MARGIN = 6;

    // members
    private final Palettes palettes;
    private final Map<Integer, Integer> maxRowHeight = new HashMap<>();
    private final Map<Integer, Integer> columnWidths = new HashMap<>();

    public MainTableCellRenderer() {
        palettes = Palettes.instance();
    }

    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Evaluation && palettes != null) {
            Evaluation evaluation = (Evaluation)value;
            Image img = palettes.getImage(evaluation);
            if (img != null) {
                JLabel icon = new JLabel(new ImageIcon(img));
                if (icon != null) {
                    adjustRowHeightIfNeeded(table, row, img.getHeight(null) + MARGIN);
                    adjustColWidthIfNeeded(table, column, img.getWidth(null) + MARGIN);
                }
                return icon;
            } else {
                value = evaluation != Evaluation.NONE ? evaluation.displayString() : "";
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        } else {
            int angle = Integer.valueOf(Settings.instance().properties().getProperty(Settings.FONT_ROTATE));
            if (angle > 0 && angle < 360 && MainTableModel.translateRowIndex(table.getModel(), row) == 0 && value != null && !value.toString().isEmpty()) {
                return new RotatedLabel(table, row, value.toString(), angle);
            }
            FontMetrics fm = getFontMetrics(table.getFont());
            if (value != null && !((String)value).isEmpty()) {
                adjustRowHeightIfNeeded(table, row, fm.getHeight());
                adjustColWidthIfNeeded(table, column, fm.stringWidth(value.toString()));
            }
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        if (MainTableModel.translateRowIndex(table.getModel(), row) == 0) {
            setHorizontalAlignment(JLabel.CENTER);
        } else {
            setHorizontalAlignment(JLabel.LEFT);
        }
        setBackground(Color.WHITE);
        setBorder(noFocusBorder);
        return this;
    }

    public void reset() {
        maxRowHeight.clear();
        columnWidths.clear();
    }
    
    private void adjustRowHeightIfNeeded(JTable table, int row, int height) {
        Integer maxHeight = maxRowHeight.get(row);
        if (maxHeight == null || maxHeight.intValue() < height) {
            maxHeight = height;
            maxRowHeight.put(row, maxHeight);
            if (maxHeight > 0) {
                table.setRowHeight(row, maxHeight);
            }
        }
    }
    
    private void adjustColWidthIfNeeded(JTable table, int column, int width) {
        if (column == 0) {
            Integer maxWidth = columnWidths.get(column);
            if (maxWidth == null || maxWidth.intValue() < width) {
                maxWidth = width;
                columnWidths.put(column, maxWidth);
                if (maxWidth > 0) {
                    adjustColWidth(table, width);
                }
            }
        }
    }
    
    private static void adjustColWidth(JTable table, int width) {
        TableColumnModel colModel = table.getColumnModel();
        int colCount = colModel.getColumnCount();
        int restWidth = (table.getWidth() - width) / colCount;
        
        colModel.getColumn(0).setPreferredWidth(width);
        for (int i = 1; i < colCount; i++) {
            colModel.getColumn(i).setPreferredWidth(restWidth);
        }
    }
    
    private class RotatedLabel extends JPanel {
        
        // members
        private final JTable table;
        private final int row;
        private final String str;
        private final int angle;
        
        private RotatedLabel(JTable table, int row, String str, int angle) {
            this.table = table;
            this.row = row;
            this.str = str;
            this.angle = angle;
        }
        
        @Override public void paint(Graphics g) {
            final Graphics2D g2d = (Graphics2D)g;
            final Rectangle rect = getBounds();
            final Font font = table.getFont();
            final FontRenderContext frc = g2d.getFontRenderContext();
            final TextLayout layout = new TextLayout(str, font, frc);
            final int sw = (int)layout.getBounds().getWidth();
            final int sh = (int)layout.getBounds().getHeight();
            final AffineTransform at = new AffineTransform();
            
            at.translate(rect.width / 2 + rect.getX(), rect.height / 2 + rect.getY());
            at.rotate(Math.toRadians(angle), 0, 0);
            
            g2d.setFont(font);
            g2d.setColor(table.getForeground());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setTransform(at);
            g2d.drawString(str, -sw / 2, sh / 2);
            g2d.dispose();
            
            adjustRowHeightIfNeeded(table, row, cellHeight(sw, sh));
        }
    
        private int cellHeight(int sw, int sh) {
            if (angle == 180) {
                return sh + MARGIN;
            }
            else if (angle == 90 || angle == 270) {
                return sw + MARGIN;
            }
            else if (angle < 90) {
                return (int) Math.round(sw * Math.sin(Math.toRadians(angle))) + (int) Math.round(sh * Math.cos(Math.toRadians(angle))) + MARGIN;
            }
            else if (angle < 180) {
                return (int) Math.round(sh * Math.sin(Math.toRadians(angle - 90))) + (int) Math.round(sw * Math.cos(Math.toRadians(angle - 90))) + MARGIN;
            }
            else if (angle < 270) {
                return (int) Math.round(sw * Math.sin(Math.toRadians(angle - 180))) + (int) Math.round(sh * Math.cos(Math.toRadians(angle - 180))) + MARGIN;
            }
            else {
                return (int) Math.round(sh * Math.sin(Math.toRadians(angle - 270))) + (int) Math.round(sw * Math.cos(Math.toRadians(angle - 270))) + MARGIN;
            }
        }
    }
}
