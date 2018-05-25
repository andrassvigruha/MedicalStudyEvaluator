package ui.swing.misc;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

public class SingleClickTableCellEditor extends DefaultCellEditor {

    public SingleClickTableCellEditor() {
        super(new JTextField());
        setClickCountToStart(1);
    }
}
