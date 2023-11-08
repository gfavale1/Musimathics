package GUI;

import javax.swing.*;
import java.awt.*;

public class MIDIAnalysis extends JFrame {
    private static final int MAX_PERCENT = 100;
    private static int currentPercent = 0;
    public MIDIAnalysis(String path1, String path2) {
        JPanel panel = new JPanel();
        String[] token1 = path1.split("\\\\");
        String[] token2 = path2.split("\\\\");
        JTextField textField1 = new JTextField(token1[token1.length - 1], 20);
        JTextField textField2 = new JTextField(token2[token2.length - 1], 20);

        CurvedRectangleProgressPanel progressPanel = new CurvedRectangleProgressPanel();
        progressPanel.setPreferredSize(new Dimension(250, 25));

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
                // Simula un calcolo (ad esempio, una simulazione di progresso)
                simulateCalculation(progressPanel);
        });

        panel.add(textField1, BorderLayout.NORTH);
        panel.add(textField2, BorderLayout.NORTH);
        panel.add(progressPanel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        this.add(panel);
    }
    private static void simulateCalculation(final CurvedRectangleProgressPanel progressPanel) {
        Timer timer = new Timer(500, e-> {
            if (currentPercent < MAX_PERCENT) {
                currentPercent += 5; // Incrementa la percentuale (simulazione)
                progressPanel.setPercentage(currentPercent);
                progressPanel.repaint();
            }
        });
        timer.start();
    }
    class CurvedRectangleProgressPanel extends JPanel {
        private int percentage = 0;

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int x = 10;
            int y = 10;
            int width = getWidth() - 20;
            int height = getHeight() - 20;
            int arcWidth = 20;
            int arcHeight = 20;

            int filledWidth = (percentage * width) / 100;

            g.setColor(Color.LIGHT_GRAY);
            g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
            g.setColor(Color.BLUE);
            g.fillRoundRect(x, y, filledWidth, height, arcWidth, arcHeight);
        }
    }
}

