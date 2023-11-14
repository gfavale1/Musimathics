package org.example;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

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
                                .addComponent(similarityLabel)  // Aggiunto il JLabel alla parte orizzontale
                                .addComponent(timeLabel)  // Aggiunto il JLabel per il tempo trascorso
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
                        .addComponent(similarityLabel)  // Aggiunto il JLabel alla parte verticale
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timeLabel)  // Aggiunto il JLabel per il tempo trascorso
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calculateButton)
                        .addContainerGap())
        );

        calculateButton.addActionListener(e -> execution(path1, path2));
    }

    public void execution(String path1, String path2) {
        long startTime = System.currentTimeMillis();
        String sequence1 = convertMidiToSequenceString(path1);
        String sequence2 = convertMidiToSequenceString(path2);
        int editDistance = computeEDOptimized(sequence1, sequence2);
        float similarity = calculateSimilarity(sequence1, sequence2);

        similarityLabel.setText("Similarità pari a: " + similarity + "%");

        progressBar.setValue((int)similarity);
        Color color = getColorForSimilarity(similarity);
        progressBar.setForeground(color);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        timeLabel.setText("Tempo trascorso: " + (double) elapsedTime/1000.0 + " secondi.");
    }

    /*
        Calcolo la similarità dei due brani basandomi sulla editDistance tra i due, ed effettuare un'op. di
        conversione in virgola mobile float, piuttosto che double.
     */
    public static float calculateSimilarity(String m1, String m2) {
        int editDistance = computeEDOptimized(m1, m2);

        // Calcola la lunghezza massima delle due sequenze
        int maxLength = (m1.length() > m2.length()) ? m1.length() : m2.length();

        // Calcola la similarità basata sull'inverso della distanza di edit normalizzata
        // (Partendo da sinistra a destra) Converto l'inverso in un valoce perc., moltiplicando per 100.0f
        // Il contenuto nella parentesi calcola l'inverso della similarità -> un valore più alto rappresenta una maggiore similarità
        // La frazione rappresenta un valore continuo, che va da 0 a 1 -> [0 - nessuna modifica | 1 - tutti i caratteri]
        float similarity = 100.0f * (1.0f - (float) editDistance / maxLength);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSimilarity = decimalFormat.format(similarity);
        try {
            Number number = decimalFormat.parse(formattedSimilarity);
            similarity = number.floatValue();
        } catch (ParseException e) {
            e.printStackTrace();  // Gestisci l'eccezione in base alle tue esigenze
            similarity = 0.0f;  // Assegnare un valore di default in caso di errore
        }

        return similarity;
    }

    public static String convertMidiToSequenceString(String midiFilePath) {
        try {
            // Carica la sequenza MIDI dal file specificato
            Sequence sequence = MidiSystem.getSequence(new java.io.File(midiFilePath));

            // Ottieni tutte le tracce nella sequenza MIDI
            Track[] tracks = sequence.getTracks();

            // StringBuilder per costruire la rappresentazione della sequenza musicale
            StringBuilder sequenceBuilder = new StringBuilder();

            // Itera su tutte le tracce
            for (Track track : tracks) {
                long noteOnTick = -1;  // Memorizza il tick dell'evento di inizio nota
                int lastNote = -1;     // Memorizza l'ultimo pitch della nota

                // Itera su tutti gli eventi nella traccia corrente
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();

                    // Verifica se l'evento MIDI è di tipo ShortMessage (nota)
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        int data1 = sm.getData1();  // Ottiene il pitch della nota

                        // Se è un evento di inizio nota (NOTE_ON) con velocità maggiore di 0
                        if (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() > 0) {
                            noteOnTick = event.getTick();  // Memorizza il tick dell'evento di inizio nota
                            lastNote = data1;              // Memorizza il pitch della nota corrente
                        } else if (sm.getCommand() == ShortMessage.NOTE_OFF || (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() == 0)) {
                            // Se è un evento di fine nota o di rilascio
                            if (noteOnTick >= 0) {
                                // Calcola la durata della nota sottraendo il tick di inizio nota dal tick corrente
                                int duration = (int) (event.getTick() - noteOnTick);
                                // Aggiunge la rappresentazione della nota al StringBuilder
                                sequenceBuilder.append(lastNote).append(":").append(duration).append(" ");
                                noteOnTick = -1;  // Reimposta il tick dell'evento di inizio nota
                            }
                        }
                    }
                }
            }

            // Restituisce la rappresentazione della sequenza musicale come stringa
            return sequenceBuilder.toString().trim();
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();  // Gestisce le eccezioni
            return "";  // Restituisce una stringa vuota in caso di errore
        }
    }

    /*
        Algoritmo di calcolo della editDistance, rifacendoci al problema della editDistance tra 2 stringhe studiato durante
        Progettazione di Algoritmi, e ripresentatosi in Musimatica. Il problema consiste nel calcolare il minimo numero di op. di
        inserimento, cancellazione e sostituzione, effettuate per trasformare una delle stringhe nell'altra. Informazioni sul funzionamento
        dell'algoritmo contenute nel codice stesso.
     */
    public static int computeEDOptimized(String m1, String m2) {
        int len1 = m1.length();
        int len2 = m2.length();
        int maxLen = (len1 > len2) ? len1 : len2;
        // Creo una matrice, ma con una dimensione spaziale molto minore (uso solo 2 righe)
        // Vado quindi a creare una matrice 2xN: N è identificata come la lunghezza massima tra le due sequenze
        int[][] d = new int[2][maxLen + 1];

        // Inizializzazione della prima riga: la prima riga rappresenta il costo delle operazioni di inserimento
        // perchè stiamo confrontando la prima stringa (m1) con una "stringa vuota".
        for (int j = 0; j <= len2; j++) {
            d[0][j] = j;
        }

        // In questa nuova versione "ottimizzata", utilizziamo solo due righe anzichè len1 + 1 (riduciamo l'uso della memoria)
        for (int i = 1; i <= len1; i++) {
            // L'introduzione delle variabili currentRow e previousRow serve per tenere traccia delle due righe considerate attualmente
            // nella matrice. Possiamo alternare tra le 2 righe durante l'iterazione, risparmiando memoria
            int currentRow = i % 2;
            int previousRow = 1 - currentRow;

            d[currentRow][0] = i;

            for (int j = 1; j <= len2; j++) {
                if (m1.charAt(i - 1) == m2.charAt(j - 1)) {
                    // Stiamo assegnando solo se i caratteri corrispondenti nelle posizioni i ed j sono uguali.
                    // Consideriamo quindi il caso in cui non è necessaria alcuna op. di modifica (contenute nell'else)
                    d[currentRow][j] = d[previousRow][j - 1];
                } else {
                    // Insert è il costo dell'op. di inserimento.
                    // È uguale al costo di trasformare la sotto-sequenza m1 di lunghezza i, in una di m2 di lunghezza j-1. (Rimuovo un carattere da m2)
                    int insert = d[currentRow][j - 1] + 1;
                    // Delete è il costo dell'op. di eliminazione.
                    // È pari al costo di trasformare la sotto-sequenza m1, lunghezza i-1, in una di m2, lunghezza j-1 (Aggiungo un carat. di m1 con uno di m2)
                    int delete = d[previousRow][j] + 1;
                    // Substitute è il costo dell'op di sostituzione.
                    // È uguale al costo di trasformare la sotto-sequenza m1 di lunghezza i-1, in una di m2, lunghezza j-1 (Sostituisco un carattere di m1 con uno di m2)
                    int substitute = d[previousRow][j - 1] + 1;

                    // Calcolo il costo minimo tra le 3 op. ed assegno; il costo minimo rappresenta la distanza di edit minima
                    // tra le sotto-sequenze m1 di lunghezza i e m2 di lunghezza j
                    d[currentRow][j] = Math.min(Math.min(insert, delete), substitute);
                }
            }
        }
        // La distanza di edit si trova nell'ultima cella.
        // Dato che uso solo 2 righe invece di 'len1 + 1' righe, uso le due variabili currentRow e previousRow.
        // Uso quindi 'len1 % 2' per accedere alla editDistance finale (Restituirà sempre 0 o 1) -> Seleziono la riga corrente o precedente in base all'iterazione corrente
        return d[len1 % 2][len2];
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

