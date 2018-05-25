package core.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class EvaluationMatrixOld {

    // constants
    private static final String DELIMITER = ";";
    private static final String NULL = "null";

    // members
    private final int stdsCnt;
    private final int critCnt;
    private final String[] studies;
    private final String[] criterias;
    private final Evaluation[][] matrix;

    // getters
    public int rowCount() { return stdsCnt + 1; }
    public int colCount() { return critCnt + 1; }

    public EvaluationMatrixOld(int stdsCnt, int critCnt) {
        this.stdsCnt = stdsCnt;
        this.critCnt = critCnt;
        this.studies = new String[stdsCnt];
        this.criterias = new String[critCnt];
        this.matrix = new Evaluation[stdsCnt][critCnt];
        for (int i = 0; i < stdsCnt; i++) {
            for (int j = 0; j < critCnt; j++) {
                matrix[i][j] = Evaluation.NONE;
            }
        }
    }

    public static void copy(EvaluationMatrixOld from, EvaluationMatrixOld to) {
        for (int i = 0; i < to.stdsCnt; i++) {
            if (from.studies.length > i) {
                to.studies[i] = from.studies[i];
            }
        }
        for (int i = 0; i < to.critCnt; i++) {
            if (from.criterias.length > i) {
                to.criterias[i] = from.criterias[i];
            }
        }
        for (int i = 0; i < to.stdsCnt; i++) {
            for (int j = 0; j < to.critCnt; j++) {
                if (from.matrix.length > i && from.matrix[i].length > j) {
                    to.matrix[i][j] = from.matrix[i][j];
                }
            }
        }
    }

    public Object getValueAt(int row, int col) {
        Object val;
        if (row == 0 && col == 0) {
            val = null;
        } else if (row == 0) {
            val = criterias[col - 1];
        } else if (col == 0) {
            val = studies[row - 1];
        } else {
            val = matrix[row - 1][col - 1];
        }
        return val;
    }

    public void setValueAt(int row, int col, Object value) {
        if (row == 0 && col == 0) {
            // NOOP
        } else if (row == 0) {
            criterias[col - 1] = String.valueOf(value);
        } else if (col == 0) {
            studies[row - 1] = String.valueOf(value);
        } else {
            matrix[row - 1][col - 1] = Evaluation.valueOf(String.valueOf(value));
        }
    }
    
    public static EvaluationMatrixOld fromLines(List<String> lines) {
        String studies[] = lines.get(0).split(DELIMITER);
        String criterias[] = lines.get(1).split(DELIMITER);
        String matrix[][] = new String[studies.length][criterias.length];
        for (int i = 2; i < lines.size(); i++) {
            matrix[i - 2] = lines.get(i).split(DELIMITER);
        }
        
        EvaluationMatrixOld evMatrix = new EvaluationMatrixOld(studies.length, criterias.length);
        
        for (int i = 0; i < studies.length; i++) {
            evMatrix.studies[i] = NULL.equals(studies[i]) ? null : studies[i];
        }
        for (int i = 0; i < criterias.length; i++) {
            evMatrix.criterias[i] = NULL.equals(criterias[i]) ? null : criterias[i];
        }
        for (int i = 0; i < studies.length; i++) {
            for (int j = 0; j < criterias.length; j++) {
                evMatrix.matrix[i][j] = Evaluation.valueOf(matrix[i][j]);
            }
        }
        return evMatrix;
    }
    
    public static List<String> toLines(EvaluationMatrixOld evMatrix) {
        List<String> lines = new ArrayList<>();
        lines.add(Arrays.stream(evMatrix.studies).collect(Collectors.joining(DELIMITER)));
        lines.add(Arrays.stream(evMatrix.criterias).collect(Collectors.joining(DELIMITER)));
        Arrays.stream(evMatrix.matrix).
            forEach(evMatrixLine -> lines.add(Arrays.stream(evMatrixLine).
                map(Object::toString).
                collect(Collectors.joining(DELIMITER)))
            );
        return lines;
    }
}
