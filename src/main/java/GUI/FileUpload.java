package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileUpload extends JFrame {
    private JLabel label = new JLabel("Inserisci due track MIDI:");
    private JButton button = new JButton("Upload");
    private JTextField filePathTextField = new JTextField();
    private JPanel panel = new JPanel();
    public FileUpload () {
        button.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    filePathTextField.setText(path);
                }
        });

        panel.add(label);
        panel.add(button);
        panel.add(filePathTextField);
        this.add(panel, BorderLayout.CENTER);
    }
}
