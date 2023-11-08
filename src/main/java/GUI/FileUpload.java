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
        this.add(panel, BorderLayout.NORTH);

        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                    if (selectedFiles.length == 2) {
                        String path1 = selectedFiles[0].getAbsolutePath();
                        String path2 = selectedFiles[1].getAbsolutePath();
                        if (path1.contains(".mid") && path2.contains(".mid")) {
                            MIDIAnalysis midiAnalysis = new MIDIAnalysis(path1, path2);
                            midiAnalysis.setDefaultCloseOperation(EXIT_ON_CLOSE);
                            midiAnalysis.setTitle("");
                            midiAnalysis.setSize(600, 550);
                            midiAnalysis.setResizable(false);
                            midiAnalysis.setLocationRelativeTo(null);
                            midiAnalysis.setVisible(true);
                            this.setVisible(false);
                        } else {
                            JOptionPane.showMessageDialog(null, "I due file non sono di tipo .mid", "Errore", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (selectedFiles.length >= 3){
                        JOptionPane.showMessageDialog(null, "Non selezionare più di due file", "Errore", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Seleziona due file", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
            }
        });
    }
}
