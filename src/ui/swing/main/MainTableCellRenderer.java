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
    private static final int MARGIN = 3;

    // members
    private final Palettes palettes;
    private final Map<Integer, Integer> maxRowHeight = new HashMap<>();
    private final Map<Integer, Integer> columnWidths = new HashMap<>();

    public MainTableCellRenderer() {
        palettes = Palettes.instance();
    }

    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component ret = null;
        if (value instanceof Evaluation) {
            ret = renderEvaluation(table, (Evaluation)value, isSelected, hasFocus, row, column);
        } else if (column == 0) {
            ret = renderStudy(table, value, isSelected, hasFocus, row, column);
        } else if (MainTableModel.translateRowIndex(table.getModel(), row) == 0) {
            ret = renderCriteria(table, value, isSelected, hasFocus, row, column);
        } else {
            ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        setBackground(Color.WHITE);
        setBorder(noFocusBorder);
        return ret;
    }

    public void reset() {
        maxRowHeight.clear();
        columnWidths.clear();
    }
    
    private Component renderEvaluation(JTable table, Evaluation evaluation, boolean isSelected, boolean hasFocus, int row, int column) {
        Component ret = null;
        if (palettes != null) {
            Image img = palettes.getImage(evaluation);
            if (img != null) {
                JLabel icon = new JLabel(new ImageIcon(img));
                if (icon != null) {
                    adjustRowHeightIfNeeded(table, row, img.getHeight(null) + MARGIN * 2);
                    adjustColWidthIfNeeded(table, column, img.getWidth(null) + MARGIN * 2);
                }
                ret = icon;
            }
        }
        if (ret == null) {
            String value = evaluation != Evaluation.NONE ? evaluation.displayString() : "";
            ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        return ret;
    }
    
    private Component renderStudy(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component ret = null;
        if (!isNull(value)) {
            FontMetrics fm = getFontMetrics(table.getFont());
            adjustRowHeightIfNeeded(table, row, fm.getHeight());
            adjustColWidthIfNeeded(table, column, fm.stringWidth(value.toString()));
            setAlignment(Integer.valueOf(Settings.instance().properties().getProperty(Settings.STUDY_CELL_ALIGNMENT)));
        }
        ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        return ret;
    }
    
    private Component renderCriteria(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component ret = null;
        int angle = Integer.valueOf(Settings.instance().properties().getProperty(Settings.FONT_ROTATE));
        int alignment = Integer.valueOf(Settings.instance().properties().getProperty(Settings.CRITERIA_CELL_ALIGNMENT));
        if (angle > 0 && angle < 360 && MainTableModel.translateRowIndex(table.getModel(), row) == 0 && value != null && !value.toString().isEmpty()) {
            ret = new RotatedLabel(table, row, value.toString(), angle, alignment);
        }
        if (ret == null) {
            if (!isNull(value)) {
                FontMetrics fm = getFontMetrics(table.getFont());
                adjustRowHeightIfNeeded(table, row, fm.getHeight());
                setAlignment(alignment);
            }
            ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        return ret;
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
    
    private void setAlignment(int alignment) {
        switch (alignment) {
            case (JLabel.CENTER):
                setHorizontalAlignment(JLabel.CENTER);
                setVerticalAlignment(JLabel.CENTER);
            break;
            case (JLabel.TOP):
                setHorizontalAlignment(JLabel.CENTER);
                setVerticalAlignment(JLabel.TOP);
            break;
            case (JLabel.LEFT):
                setHorizontalAlignment(JLabel.LEFT);
                setVerticalAlignment(JLabel.CENTER);
            break;
            case (JLabel.BOTTOM):
                setHorizontalAlignment(JLabel.CENTER);
                setVerticalAlignment(JLabel.BOTTOM);
            break;
            case (JLabel.RIGHT):
                setHorizontalAlignment(JLabel.RIGHT);
                setVerticalAlignment(JLabel.CENTER);
            break;
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
    
    private static boolean isNull(Object value) {
        return value == null || value.toString().isEmpty();
    }
    
    private class RotatedLabel extends JPanel {
        
        // members
        private final JTable table;
        private final int row;
        private final String str;
        private final int angle;
        private final int alignment;
        
        private RotatedLabel(JTable table, int row, String str, int angle, int alignment) {
            this.table = table;
            this.row = row;
            this.str = str;
            this.angle = angle;
            this.alignment = alignment;
        }
        
        @Override public void paintComponent(Graphics g) {
            final Graphics2D g2d = (Graphics2D)g;
            final Font font = table.getFont();
            final FontRenderContext frc = g2d.getFontRenderContext();
            final TextLayout layout = new TextLayout(str, font, frc);
            final int sw = (int)layout.getBounds().getWidth();
            final int sh = (int)layout.getBounds().getHeight();
            final Rectangle rect = translateRectange(getBounds(), sw, sh);
            final AffineTransform at = new AffineTransform();

            at.translate(rect.width, rect.height);
            at.rotate(Math.toRadians(angle), 0, 0);
            
            g2d.setFont(table.getFont());
            g2d.setColor(table.getForeground());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.transform(at);
            g2d.drawString(str, -sw / 2, sh / 2);
            
            adjustRowHeightIfNeeded(table, row, textHeight(sw, sh) + MARGIN * 2);
        }
    
        private Rectangle translateRectange(Rectangle rect, int sw, int sh) {
            Rectangle transRect;
            int width;
            int height;
            switch (alignment) {
                case JLabel.CENTER:
                    width = rect.width / 2;
                    height = rect.height / 2;
                    transRect = new Rectangle(width, height);
                break;
                case JLabel.TOP:
                    width = rect.width / 2;
                    height = textHeight(sw, sh) / 2 + MARGIN;
                    transRect = new Rectangle(width, height);
                break;
                case JLabel.LEFT:
                    width = textWidth(sw, sh) / 2 + MARGIN;
                    height = rect.height / 2;
                    transRect = new Rectangle(width, height);
                break;
                case JLabel.BOTTOM:
                    width = rect.width / 2;
                    height = rect.height - (textHeight(sw, sh) / 2 + MARGIN);
                    transRect = new Rectangle(width, height);
                break;
                case JLabel.RIGHT:
                    width = rect.width - (textWidth(sw, sh) / 2 + MARGIN);
                    height = rect.height / 2;
                    transRect = new Rectangle(width, height);
                break;
                default:
                    transRect = rect;
            }
            return transRect;
        }
        
        private int textHeight(int sw, int sh) {
            if (angle == 180) {
                return sh;
            }
            else if (angle == 90 || angle == 270) {
                return sw;
            }
            else if (angle < 90) {
                return (int) Math.round(sw * Math.sin(Math.toRadians(angle))) + (int) Math.round(sh * Math.cos(Math.toRadians(angle)));
            }
            else if (angle < 180) {
                return (int) Math.round(sh * Math.sin(Math.toRadians(angle - 90))) + (int) Math.round(sw * Math.cos(Math.toRadians(angle - 90)));
            }
            else if (angle < 270) {
                return (int) Math.round(sw * Math.sin(Math.toRadians(angle - 180))) + (int) Math.round(sh * Math.cos(Math.toRadians(angle - 180)));
            }
            else {
                return (int) Math.round(sh * Math.sin(Math.toRadians(angle - 270))) + (int) Math.round(sw * Math.cos(Math.toRadians(angle - 270)));
            }
        }
        
        private int textWidth(int sw, int sh) {
            if (angle == 180) {
                return sw;
            }
            else if (angle == 90 || angle == 270) {
                return sh;
            }
            else if (angle < 90) {
                return (int) Math.round(sw * Math.cos(Math.toRadians(angle))) + (int) Math.round(sh * Math.sin(Math.toRadians(angle)));
            }
            else if (angle < 180) {
                return (int) Math.round(sh * Math.cos(Math.toRadians(angle - 90))) + (int) Math.round(sw * Math.sin(Math.toRadians(angle - 90)));
            }
            else if (angle < 270) {
                return (int) Math.round(sw * Math.cos(Math.toRadians(angle - 180))) + (int) Math.round(sh * Math.sin(Math.toRadians(angle - 180)));
            }
            else {
                return (int) Math.round(sh * Math.cos(Math.toRadians(angle - 270))) + (int) Math.round(sw * Math.sin(Math.toRadians(angle - 270)));
            }
        }
    }
}
