package ui.swing.misc;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class ImageChooser extends LastSelectionChooser {

    public ImageChooser() {
        addChoosableFileFilter(new ImageFilter());
        setAccessory(new ImagePreview(this));
    }

    @Override public final void approveSelection() {
        if (getDialogType() == OPEN_DIALOG) {
            if (!canOpenFile()) {
                return;
            }
        }
        if (getDialogType() == SAVE_DIALOG) {
            if (!saveFile()) {
                return;
            }
        }
        super.approveSelection();
    }

    private boolean canOpenFile() {
        File file = getSelectedFile();
        if (!isImage(file)) {
            JOptionPane.showMessageDialog(this, "Cannot use this type of file as an icon!");
            return false;
        }
        return true;
    }

    private boolean saveFile() {
        File file = getSelectedFile();
        if (file.exists()) {
            if (!FrameUtilities.showConfirmationDlg(this, "Do you really want to overwrite this file?")) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isImage(File file) {
        String ext = getExtension(file);
        return ext != null && (
            "jpeg".equals(ext) ||
            "jpg".equals(ext) ||
            "gif".equals(ext) ||
            "png".equals(ext)
        );
    }
    
    private static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    private static class ImageFilter extends FileFilter {
        @Override public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            return isImage(file);
        }
        @Override public String getDescription() {
            return "Images";
        }
    }
    
    private static class ImagePreview extends JComponent implements PropertyChangeListener {
        
        // members
        ImageIcon thumbnail = null;
        File file = null;

        public ImagePreview(JFileChooser fc) {
            setPreferredSize(new Dimension(100, 50));
            fc.addPropertyChangeListener(this);
        }

        public void loadImage() {
            if (file == null) {
                thumbnail = null;
                return;
            }

            ImageIcon tmpIcon = new ImageIcon(file.getPath());
            if (tmpIcon != null) {
                if (tmpIcon.getIconWidth() > 90) {
                    thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
                } else {
                    thumbnail = tmpIcon;
                }
            }
        }

        @Override public void propertyChange(PropertyChangeEvent e) {
            boolean update = false;
            String prop = e.getPropertyName();

            if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
                file = null;
                update = true;
            } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
                file = (File) e.getNewValue();
                update = true;
            }

            if (update) {
                thumbnail = null;
                if (isShowing()) {
                    loadImage();
                    repaint();
                }
            }
        }

        @Override protected void paintComponent(Graphics g) {
            if (thumbnail == null) {
                loadImage();
            }
            if (thumbnail != null) {
                int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
                int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

                if (y < 0) {
                    y = 0;
                }

                if (x < 5) {
                    x = 5;
                }
                thumbnail.paintIcon(this, g, x, y);
            }
        }
    }
}
