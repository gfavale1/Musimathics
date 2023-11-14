package GraphicUserInterface;

import javax.swing.*;
import java.io.File;

public class FileUpload extends JFrame {
    private JLabel label = new JLabel("Inserisci due track MIDI:");
    private JButton uploadButton = new JButton("Carica File");
    private JPanel panel = new JPanel();

    public FileUpload () {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(uploadButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uploadButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
        );

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();

                if (selectedFiles.length == 2) {
                    String path1 = selectedFiles[0].getAbsolutePath();
                    String path2 = selectedFiles[1].getAbsolutePath();

                    if (path1.contains(".mid") && path2.contains(".mid")) {
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        MIDIAnalysis midiAnalysis = new MIDIAnalysis(path1, path2);

                        midiAnalysis.setDefaultCloseOperation(EXIT_ON_CLOSE);
                        midiAnalysis.setTitle("Similarity calculator");
                        midiAnalysis.setSize(450, 200);
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
