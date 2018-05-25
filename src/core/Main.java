package core;

import ui.swing.main.MainFrame;

public class Main {

    public static void main(String args[]) {
        try {
            MainFrame.instance().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
