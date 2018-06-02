package core.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Matrix {

    // constants
    private static final String DELIMITER = ";";
    private static final String NULL = "null";

    // members
    private int rows;
    private int cols;
    private Object[][] data;

    public int rows() { return rows; }
    public int cols() { return cols; }

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new Object[rows][cols];
        if (rows > 0 && cols > 0) {
            for (int i = 1; i < rows; i++) {
                for (int j = 1; j < cols; j++) {
                    data[i][j] = Evaluation.NONE;
                }
            }
        }
    }

    public static void copy(Matrix from, Matrix to) {
        for (int i = 0; i < to.rows; i++) {
            for (int j = 0; j < to.cols; j++) {
                if (from.rows > i && from.cols > j) {
                    to.data[i][j] = from.data[i][j];
                }
            }
        }
    }

    public void addRow(int index) {
        Object[][] newData = new Object[++rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i < index) {
                    newData[i][j] = data[i][j];
                } else if (i == index) {
                    if (i > 0) {
                        newData[i][j] = Evaluation.NONE;
                    } else {
                        newData[i][j] = null;
                    }
                } else {
                    newData[i][j] = data[i - 1][j];
                }
            }
        }
        data = newData;
    }

    public void deleteRow(int index) {
        Object[][] newData = new Object[--rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i < index) {
                    newData[i][j] = data[i][j];
                } else {
                    newData[i][j] = data[i + 1][j];
                }
            }
        }
        data = newData;
    }

    public void addColumn(int index) {
        Object[][] newData = new Object[rows][++cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j < index) {
                    newData[i][j] = data[i][j];
                } else if (j == index) {
                    if (j > 0) {
                        newData[i][j] = Evaluation.NONE;
                    } else {
                        newData[i][j] = null;
                    }
                } else {
                    newData[i][j] = data[i][j - 1];
                }
            }
        }
        data = newData;
    }
    
    public void deleteColumn(int index) {
        Object[][] newData = new Object[rows][--cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j < index) {
                    newData[i][j] = data[i][j];
                } else {
                    newData[i][j] = data[i][j + 1];
                }
            }
        }
        data = newData;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public void setValueAt(int row, int col, Object value) {
        if (row == 0 && col == 0) {
            data[row][col] = null;
        } else if (row == 0 || col == 0) {
            data[row][col] = !NULL.equals(value) ? String.valueOf(value) : null;
        } else {
            data[row][col] = Evaluation.valueOf(String.valueOf(value));
        }
    }
    
    public static Matrix fromLines(List<String> lines) {
        final int rows = lines.size();
        final int cols = lines.get(0).split(DELIMITER).length;
        final Matrix matrix = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            final String rowValues[] = lines.get(i).split(DELIMITER);
            for (int j = 0; j < cols; j++) {
                matrix.setValueAt(i, j, rowValues[j]);
            }
        }
        return matrix;
    }
    
    public static List<String> toLines(Matrix evMatrix) {
        final List<String> lines = new ArrayList<>();
        Arrays.stream(evMatrix.data).
            forEach(rowValues -> {
                lines.add(Arrays.stream(rowValues).
                    map(rowValue -> rowValue != null && !rowValue.toString().isEmpty() ? rowValue.toString() : NULL).
                    collect(Collectors.
                        joining(DELIMITER)));
            });
        return lines;
    }
}
