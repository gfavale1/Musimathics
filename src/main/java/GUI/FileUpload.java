package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileUpload extends JFrame {
    private JLabel label = new JLabel("Inserisci due track MIDI:");
    private JButton button = new JButton("Upload");
    private JPanel panel = new JPanel();
    JTextField textField = new JTextField(10);

    public FileUpload () {
        panel.add(label);
        panel.add(button);
        this.add(panel, BorderLayout.NORTH);
        this.add(textField, BorderLayout.SOUTH);

        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                    if (selectedFiles.length >= 2) {
                        String path1 = selectedFiles[0].getAbsolutePath();
                        String path2 = selectedFiles[1].getAbsolutePath();
                        if (path1.contains(".mid") && path2.contains(".mid")) {
                            MIDIAnalysis midiAnalysis = new MIDIAnalysis(path1, path2);
                            midiAnalysis.setDefaultCloseOperation(EXIT_ON_CLOSE);
                            midiAnalysis.setTitle("Carica");
                            midiAnalysis.setSize(400, 400);
                            midiAnalysis.setResizable(false);
                            midiAnalysis.setVisible(true);
                            this.setVisible(false);
                        } else {
                            textField.setText("Seleziona due file MIDI. (.mid)");
                            panel.add(textField);
                            this.add(panel);
                        }
                    } else {
                        textField.setText("Non hai selezionato abbastanza file.");
                        panel.add(textField);
                        this.add(panel);
                    }
            }
        });
    }
}
