package ui.swing.misc;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class NumberTextField extends JTextField {

    public NumberTextField(int min, int max) {
        super("", String.valueOf(max).length());
        addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                int num = getIntValue();
                if (num < min) {
                    setText(String.valueOf(min));
                }
                if (num > max) {
                    setText(String.valueOf(max));
                }
            }
        });
    }

    @Override public void processKeyEvent(KeyEvent ev) {
        if (Character.isDigit(ev.getKeyChar()) || ev.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            super.processKeyEvent(ev);
        }
        ev.consume();
        return;
    }
    
    public int getIntValue() {
        String text = getText();
        if (text != null && !"".equals(text)) {
            return Integer.valueOf(text);
        }
        return 0;
    }
}
