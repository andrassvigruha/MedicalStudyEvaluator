package ui.swing.edit;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ui.swing.main.MainFrame;
import ui.swing.misc.FrameUtilities;
import ui.swing.misc.NumberTextField;

public abstract class EditFrame extends JFrame {

    // instance
    private static EditFrame s_newInstance;
    private static EditFrame s_editInstance;

    // members
    private final JTextField rowsField = new NumberTextField(0, 99);
    private final JTextField colsField = new NumberTextField(0, 99);
    private final JButton createButton = createActionButton();
    private final JButton cancelButton = new JButton("Cancel");

    // abstract
    protected abstract JButton createActionButton();

    // factory
    public static EditFrame newInstance() {
        if (s_newInstance == null) {
            s_newInstance = new EditFrame() {
                @Override protected JButton createActionButton() {
                    final JButton actionButton = new JButton("Create");
                    actionButton.addActionListener(e -> {
                        try {
                            int rowNum = Integer.parseInt(s_newInstance.rowsField.getText()) + 1;
                            int colNum = Integer.parseInt(s_newInstance.colsField.getText()) + 1;
                            MainFrame.instance().createMatrix(rowNum, colNum);
                            setVisible(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                    return actionButton;
                }
            };
        }
        return s_newInstance;
    }

    public static EditFrame editInstance() {
        if (s_editInstance == null) {
            s_editInstance = new EditFrame() {
                @Override protected JButton createActionButton() {
                    final JButton actionButton = new JButton("Edit");
                    actionButton.addActionListener(e -> {
                        try {
                            int rowNum = Integer.parseInt(s_editInstance.rowsField.getText()) + 1;
                            int colNum = Integer.parseInt(s_editInstance.colsField.getText()) + 1;
                            MainFrame.instance().editMatrix(rowNum, colNum);
                            setVisible(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                    return actionButton;
                }
            };
        }
        return s_editInstance;
    }

    {
        rowsField.setMaximumSize(rowsField.getPreferredSize());
        colsField.setMaximumSize(colsField.getPreferredSize());
        cancelButton.addActionListener(e -> setVisible(false));
    }

    private EditFrame() {
        build();
        FrameUtilities.centerOnScreen(this);
    }

    private void build() {
        JPanel rowsPanel = new JPanel();
        JLabel rowsLabel = new JLabel("Studies count");
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.LINE_AXIS));
        rowsPanel.add(rowsLabel);
        rowsPanel.add(Box.createHorizontalGlue());
        rowsPanel.add(rowsField);

        JPanel colsPanel = new JPanel();
        JLabel colsLabel = new JLabel("Criterias count");
        colsPanel.setLayout(new BoxLayout(colsPanel, BoxLayout.LINE_AXIS));
        colsPanel.add(colsLabel);
        colsPanel.add(Box.createHorizontalGlue());
        colsPanel.add(colsField);

        JPanel btnsPanel = new JPanel();
        btnsPanel.setLayout(new BoxLayout(btnsPanel, BoxLayout.LINE_AXIS));
        btnsPanel.add(Box.createHorizontalGlue());
        btnsPanel.add(createButton);
        btnsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnsPanel.add(cancelButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(rowsPanel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(colsPanel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(btnsPanel);
        add(mainPanel);
        pack();
    }
}
