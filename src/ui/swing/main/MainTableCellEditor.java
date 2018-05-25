package ui.swing.main;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import core.matrix.Evaluation;

public class MainTableCellEditor extends DefaultCellEditor {

    public MainTableCellEditor() {
        super(new JComboBox<Object>(Evaluation.values()));
        ((JComboBox<Object>)editorComponent).setRenderer(new MainComboRenderer());
    }
}
