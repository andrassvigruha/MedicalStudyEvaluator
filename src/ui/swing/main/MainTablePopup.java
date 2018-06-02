package ui.swing.main;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ui.settings.Settings;

public class MainTablePopup extends JPopupMenu {

    // members
    private final MainTable table;
    private final JMenuItem mAddRowAbove = new JMenuItem("Add Row Above");
    private final JMenuItem mAddRowBelow = new JMenuItem("Add Row Below");
    private final JMenuItem mDeleteRow = new JMenuItem("Delete Row");
    private final JMenuItem mAddColumnBefore = new JMenuItem("Add Column Before");
    private final JMenuItem mAddColumnAfter = new JMenuItem("Add Column After");
    private final JMenuItem mDeleteColumn = new JMenuItem("Delete Column");

    // location
    private int x;
    private int y;

    public MainTablePopup(MainTable table) {
        this.table = table;
        buildMenu();
        setupMenu();
    }
    
    @Override public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        this.x = x;
        this.y = y;
    }
    
    private void buildMenu() {
        add(mAddRowAbove);
        add(mAddRowBelow);
        add(mDeleteRow);
        addSeparator();
        add(mAddColumnBefore);
        add(mAddColumnAfter);
        add(mDeleteColumn);
    }

    private void setupMenu() {
        mAddRowAbove.addActionListener(e -> {
            int alignment = Integer.valueOf(Settings.instance().properties().getProperty(Settings.CRITERIA_ROW_ALIGNMENT));
            int index = rowAtPoint(true);
            if (alignment == JLabel.TOP && !verifyRow(index, "You cannot add row above criterias row!")) {
                return;
            }
            if (alignment == JLabel.BOTTOM && index == 0) {
                table.addRow(rowAtPoint(false) + 1);
            } else {
                table.addRow(index);
            }
        });
        mAddRowBelow.addActionListener(e -> {
            int alignment = Integer.valueOf(Settings.instance().properties().getProperty(Settings.CRITERIA_ROW_ALIGNMENT));
            int index = rowAtPoint(true);
            if (alignment == JLabel.BOTTOM && !verifyRow(index, "You cannot add row below criterias row!")) {
                return;
            }
            table.addRow(index + 1);
        });
        mDeleteRow.addActionListener(e -> {
            int index = rowAtPoint(true);
            if (verifyRow(index, "You cannot delete criterias row!")) {
                table.deleteRow(index);
            }
        });
        mAddColumnBefore.addActionListener(e -> {
            int index = columnAtPoint();
            if (verifyColumn(index, "You cannot add column before studies column!")) {
                table.addColumn(index);
            }
        });
        mAddColumnAfter.addActionListener(e -> {
            int index = columnAtPoint();
            table.addColumn(index + 1);
        });
        mDeleteColumn.addActionListener(e -> {
            int index = columnAtPoint();
            if (verifyColumn(index, "You cannot delete studies column!")) {
                table.deleteColumn(index);
            }
        });
    }
    
    private boolean verifyRow(int index, String message) {
        if (index == 0) {
            JOptionPane.showMessageDialog(table, message);
            return false;
        }
        if (table.getModel().getRowCount() <= 2) {
            JOptionPane.showMessageDialog(table, "At lease 2 rows should be left in matrix!");
            return false;
        }
        return true;
    }
    
    private boolean verifyColumn(int index, String message) {
        if (index == 0) {
            JOptionPane.showMessageDialog(table, message);
            return false;
        }
        if (table.getModel().getColumnCount() <= 2) {
            JOptionPane.showMessageDialog(table, "At lease 2 columns should be left in matrix!");
            return false;
        }
        return true;
    }
    
    private int rowAtPoint(boolean translate) {
        return translate
            ? MainTableModel.translateRowIndex(table.getModel(), table.rowAtPoint(new Point(x, y)))
            : table.rowAtPoint(new Point(x, y));
    }
    
    private int columnAtPoint() {
        return table.columnAtPoint(new Point(x, y));
    }
}
