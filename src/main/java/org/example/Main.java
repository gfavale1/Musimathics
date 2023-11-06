package org.example;

import javax.sound.midi.*;

public class Main {
    public static void main(String[] args) {
        // Sostituisci questi percorsi con i file MIDI che vuoi convertire
        String midiFile1 = "MIDI/Michael_Jackson_-_Beat_It.mid";
        String midiFile2 = "MIDI/Movie_Themes_-_Conan_The_Barbarian.mid";
        String sequence1 = convertMidiToSequenceString(midiFile1);
        String sequence2 = convertMidiToSequenceString(midiFile2);

        System.out.println(sequence1);
        System.out.println(sequence2);

        int editDistance = computeEDOptimized(sequence1, sequence2);
        float similarity = calculateSimilarity(sequence1, sequence2);

        System.out.println("Distanza di Edit tra le sequenze: " + editDistance);
        System.out.println("Similarità tra le sequenze: " + similarity + "%");
    }

    public static String convertMidiToSequenceString(String midiFilePath) {
        try {
            Sequence sequence = MidiSystem.getSequence(new java.io.File(midiFilePath));
            Track[] tracks = sequence.getTracks();

            StringBuilder sequenceBuilder = new StringBuilder();
            for (Track track : tracks) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        int data1 = sm.getData1();
                        sequenceBuilder.append(data1).append(" ");
                    }
                }
            }

            return sequenceBuilder.toString().trim();
        } catch (InvalidMidiDataException | java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static float calculateSimilarity(String m1, String m2) {
        int editDistance = computeED(m1, m2);

        // Calcola la lunghezza massima delle due sequenze
        int maxLength = Math.max(m1.length(), m2.length());

        // Calcola la similarità basata sull'inverso della distanza di edit normalizzata
        float similarity = 100.0f * (1.0f - (float) editDistance / maxLength);

        return similarity;
    }

    public static int computeEDOptimized(String m1, String m2) {
        int len1 = m1.length();
        int len2 = m2.length();

        // Riduci la dimensione della matrice
        int[][] d = new int[2][len2 + 1];

        // Inizializzazione della prima riga
        for (int j = 0; j <= len2; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            int currentRow = i % 2;
            int previousRow = 1 - currentRow;

            d[currentRow][0] = i;

            for (int j = 1; j <= len2; j++) {
                if (m1.charAt(i - 1) == m2.charAt(j - 1)) {
                    d[currentRow][j] = d[previousRow][j - 1];
                } else {
                    int insert = d[currentRow][j - 1] + 1;
                    int delete = d[previousRow][j] + 1;
                    int substitute = d[previousRow][j - 1] + 1;

                    d[currentRow][j] = Math.min(Math.min(insert, delete), substitute);
                }
            }
        }

        // La distanza di edit si trova nell'ultima cella
        return d[len1 % 2][len2];
    }

    public static int computeED(String m1, String m2) {
        int len1 = m1.length();
        int len2 = m2.length();
        int[][] d = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (m1.charAt(i - 1) == m2.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    int insert = d[i][j - 1] + 1;
                    int delete = d[i - 1][j] + 1;
                    int substitute = d[i - 1][j - 1] + 1;

                    d[i][j] = Math.min(Math.min(insert, delete), substitute);
                }
            }
        }
        return d[len1][len2];
    }
}

