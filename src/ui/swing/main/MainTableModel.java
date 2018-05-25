package ui.swing.main;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import core.matrix.Matrix;

public class MainTableModel extends AbstractTableModel {

    // members
    private Matrix matrix = new Matrix(0, 0);

    // getters
    public Matrix matrix() { return matrix; }
    
    // setters
    public void matrix(Matrix matrix) { this.matrix = matrix; }
    
    public void createMatrix(int rowNum, int colNum) {
        matrix = new Matrix(rowNum, colNum);
    }

    public void editMatrix(int rowNum, int colNum) {
        if (matrix != null) {
            Matrix oldMatrix = matrix;
            matrix = new Matrix(rowNum, colNum);
            Matrix.copy(oldMatrix, matrix);
        } else {
            createMatrix(rowNum, colNum);
        }
    }

    @Override public int getRowCount() {
        return matrix.rows();
    }

    @Override public int getColumnCount() {
        return matrix.cols();
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        return matrix.getValueAt(translateRowIndex(this, rowIndex), columnIndex);
    }

    @Override public void setValueAt(Object value, int rowIndex, int columnIndex) {
        matrix.setValueAt(translateRowIndex(this, rowIndex), columnIndex, value);
    }

    @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (translateRowIndex(this, rowIndex) == 0 && columnIndex == 0) {
            return false;
        }
        return true;
    }

    public Class<?> getCellClass(int rowIndex, int colIndex) {
        Object value = getValueAt(rowIndex, colIndex);
        return value != null ? value.getClass() : Object.class;
    }
    
    public static int translateRowIndex(TableModel model, int row) {
        return row == model.getRowCount() - 1
            ? 0
            : row + 1;
    }
}
