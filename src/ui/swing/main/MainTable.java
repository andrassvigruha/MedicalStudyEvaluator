package ui.swing.main;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import core.matrix.Evaluation;
import core.matrix.Matrix;
import ui.settings.Settings;
import ui.swing.misc.SingleClickTableCellEditor;

public class MainTable extends JTable {

    // constants
    private static final MainTableCellRenderer CELL_RENDERER = new MainTableCellRenderer();
    private static final MainTableCellEditor CELL_EDITOR = new MainTableCellEditor();
    private static final SingleClickTableCellEditor SINGLE_CLICK_EDITOR = new SingleClickTableCellEditor();

    // members
    private final MainTableModel model = new MainTableModel();

    public MainTable() {
        setModel(model);
        setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(true);
        createMatrix(5, 5);
    }

    public Matrix getMatrix() {
        return model.matrix();
    }

    public void setMatrix(Matrix matrix) {
        model.matrix(matrix);
        redraw();
    }

    public void createMatrix(int rowNum, int colNum) {
        model.createMatrix(rowNum, colNum);
        redraw();
    }

    public void editMatrix(int rowNum, int colNum) {
        model.editMatrix(rowNum, colNum);
        redraw();
    }

    public void redraw() {
        CELL_RENDERER.reset();
        model.fireTableStructureChanged();
    }

    public void setFontSize(int size) {
        Font font = getFont();
        setFont(new Font(font.getName(), font.getStyle(), size));
    }

    public void setFontType(String type) {
        Font font = getFont();
        setFont(new Font(type, font.getStyle(), font.getSize()));
    }

    public BufferedImage snapshot() {
        setShowGrid(!Boolean.valueOf((String)Settings.instance().properties().get(Settings.SNAPSHOT_BORDERS)));
        Rectangle rect = getBounds();
        BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        paint(graphics);
        setShowGrid(true);
        return image;
    }

    @Override public TableCellRenderer getCellRenderer(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellRenderer renderer = tableColumn.getCellRenderer();
        if (renderer == null) {
            renderer = getDefaultRenderer(model.getCellClass(row, column));
        }
        return renderer;
    }

    @Override public TableCellEditor getCellEditor(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellEditor editor = tableColumn.getCellEditor();
        if (editor == null) {
            editor = getDefaultEditor(model.getCellClass(row, column));
        }
        return editor;
    }

    @Override protected void createDefaultRenderers() {
        super.createDefaultRenderers();
        defaultRenderersByColumnClass.put(Object.class, CELL_RENDERER);
        defaultRenderersByColumnClass.put(String.class, CELL_RENDERER);
        defaultRenderersByColumnClass.put(Evaluation.class, CELL_RENDERER);
    }

    @Override protected void createDefaultEditors() {
        super.createDefaultEditors();
        defaultEditorsByColumnClass.put(Object.class, SINGLE_CLICK_EDITOR);
        defaultEditorsByColumnClass.put(String.class, SINGLE_CLICK_EDITOR);
        defaultEditorsByColumnClass.put(Evaluation.class, CELL_EDITOR);
    }
}
