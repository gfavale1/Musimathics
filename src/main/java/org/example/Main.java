package org.example;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String midiFile1 = "MIDI/Michael_Jackson_-_Billie_Jean.mid";
        String midiFile2 = "MIDI/Movie_Themes_-_Beetlejuice_-_by_Danny_Elfman.mid";

        try {
            Sequence sequence1 = MidiSystem.getSequence(new File(midiFile1));
            Sequence sequence2 = MidiSystem.getSequence(new File(midiFile2));

            float duration1 = calculateTotalNoteDuration(sequence1);
            float duration2 = calculateTotalNoteDuration(sequence2);

            float similarity = calculateSimilarity(duration1, duration2);

            System.out.println("Percentuale di plagio: " + similarity + "%");
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
    }

    public static float calculateTotalNoteDuration(Sequence sequence) {
        long totalNoteDuration = 0;

        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) message;
                    int command = shortMessage.getCommand();

                    if (command == ShortMessage.NOTE_ON || command == ShortMessage.NOTE_OFF) {
                        long tick = event.getTick();
                        totalNoteDuration += tick;
                    }
                }
            }
        }

        return (float) totalNoteDuration / sequence.getResolution();
    }

    public static float calculateSimilarity(float duration1, float duration2) {
        // Calcola la percentuale di plagio basata sulla lunghezza totale delle note
        float similarity;
        if (duration1 > duration2) {
            similarity = (duration2 / duration1) * 100;
        } else {
            similarity = (duration1 / duration2) * 100;
        }

        return similarity;
    }
}
