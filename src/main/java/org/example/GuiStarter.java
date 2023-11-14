package org.example;
import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GuiStarter {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        FileUpload fileUpload = new FileUpload();
        fileUpload.setDefaultCloseOperation(EXIT_ON_CLOSE);
        fileUpload.setTitle("Carica");
        fileUpload.setSize(300, 100);
        fileUpload.setResizable(false);
        fileUpload.setLocationRelativeTo(null);
        fileUpload.setVisible(true);
    }
}


