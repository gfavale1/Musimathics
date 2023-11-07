package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileUpload extends JFrame {
    private JLabel label = new JLabel("Inserisci due track MIDI:");
    private JButton button = new JButton("Upload");
    private JPanel panel = new JPanel();
    public FileUpload () {
        panel.add(label);
        panel.add(button);
        this.add(panel, BorderLayout.CENTER);

        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                try {
                    if (selectedFiles.length >= 2) {
                        String path1 = selectedFiles[0].getAbsolutePath();
                        String path2 = selectedFiles[1].getAbsolutePath();
                        MIDIAnalysis midiAnalysis = new MIDIAnalysis(path1, path2);
                        midiAnalysis.setDefaultCloseOperation(EXIT_ON_CLOSE);
                        midiAnalysis.setTitle("Carica");
                        midiAnalysis.setSize(400, 400);
                        midiAnalysis.setResizable(false);
                        midiAnalysis.setVisible(true);
                        this.setVisible(false);
                    }
                } catch (Exception exception) {

                }
            }
        });




    }
}
