package GraphicUserInterface;

import Backend.Comparison;

import javax.swing.*;
import java.awt.*;

public class MIDIAnalysis extends JFrame {
    private JTextField textField1;
    private JTextField textField2;
    private JProgressBar progressBar;
    private JButton calculateButton = new JButton("Calcola Similarità");
    private JLabel similarityLabel;
    private JLabel timeLabel;


    public MIDIAnalysis(String path1, String path2) {
        String[] token1 = path1.split("\\\\");
        String[] token2 = path2.split("\\\\");

        JTextField textField1 = new JTextField(token1[token1.length - 1], 20);
        textField1.setEditable(false);
        textField1.setFocusable(false);

        JTextField textField2 = new JTextField(token2[token2.length - 1], 20);
        textField2.setEditable(false);
        textField2.setFocusable(false);

        progressBar = new JProgressBar(0, 100);
        similarityLabel = new JLabel("Similarità: ");
        timeLabel = new JLabel("Tempo trascorso: ");


        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(textField1)
                                .addComponent(textField2)
                                .addComponent(progressBar)
                                .addComponent(similarityLabel)
                                .addComponent(timeLabel)
                                .addComponent(calculateButton, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(similarityLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timeLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calculateButton)
                        .addContainerGap())
        );

        calculateButton.addActionListener(e -> execution(path1, path2));
    }

    public void execution(String path1, String path2) {
        long startTime = System.currentTimeMillis();
        Comparison comparison = new Comparison(path1, path2);
        String sequence1 = comparison.convertMidiToSequenceString(path1);
        String sequence2 = comparison.convertMidiToSequenceString(path2);
        int editDistance = comparison.computeEDOptimized(sequence1, sequence2);
        float similarity = comparison.calculateSimilarity(sequence1, sequence2);

        similarityLabel.setText("Similarità pari a: " + similarity + "%");

        progressBar.setValue((int)similarity);
        Color color = getColorForSimilarity(similarity);
        progressBar.setForeground(color);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        timeLabel.setText("Tempo trascorso: " + (double) elapsedTime/1000.0 + " secondi.");
    }

    private Color getColorForSimilarity(double similarity) {
        if (similarity >= 70) {
            return Color.GREEN;
        } else if (similarity >= 40) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }
}

