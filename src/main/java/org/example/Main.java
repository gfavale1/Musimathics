package org.example;

import javax.sound.midi.*;

public class Main {
    public static void main(String[] args) {
        String midiFile1 = "MIDI/Movie_Themes_-_Conan_The_Barbarian.mid";
        String midiFile2 = "MIDI/12.mid";
        // Converto i due file MIDI in stringhe
        String sequence1 = convertMidiToSequenceString(midiFile1);
        String sequence2 = convertMidiToSequenceString(midiFile2);

        // int editDistance = computeED(sequence1, sequence2) -> produce lo stesso output della versione ottimizzata
        int editDistance = computeEDOptimized(sequence1, sequence2);
        // Calcolo la similarità tra le due sequenze
        float similarity = calculateSimilarity(sequence1, sequence2);

        System.out.println("Distanza di Edit tra le sequenze: " + editDistance);
        System.out.println("Similarità tra le sequenze: " + similarity + "%");
    }

    /*
        Questo metodo serve ad estrarre i dati delle note musicali da file MIDI ed a restituirli sottoforma di
        stringa di numeri, i quali rappresentano ognuno una nota musicale contenuta nel file.
     */
    public static String convertMidiToSequenceString(String midiFilePath) {
        try {
            // Carico il content musicale dal file MIDI specificato in input
            Sequence sequence = MidiSystem.getSequence(new java.io.File(midiFilePath));
            // Ottengo tutte le eventuali tracce nella sequenza
            Track[] tracks = sequence.getTracks();

            StringBuilder sequenceBuilder = new StringBuilder();
            // Per ogni traccia, attraverso gli eventi MIDI dentro le tracce; come sappiamo ogni evento MIDI
            // rappresenta o una nota, o un'operazione di qualche tipo. Verifico se è un messaggio MIDI breve.
            for (Track track : tracks) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    // Se lo è, la aggiungo alla stringa sequenceBuilder, con uno spazio dopo
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        int data1 = sm.getData1();
                        sequenceBuilder.append(data1).append(" ");
                    }
                }
            }

            // La stringa è restituita come rappresentazione delle note musicali nel MIDI
            return sequenceBuilder.toString().trim();
        } catch (InvalidMidiDataException | java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
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

        return similarity;
    }

    /*
        Algoritmo di calcolo della editDistance, rifacendoci al problema della editDistance tra 2 stringhe studiato durante
        Progettazione di Algoritmi, e presentatosi in Musimatica. Il problema consiste nel calcolare il minimo numero di op. di
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

    /*
        Algoritmo editDistance del prof.
     */
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

