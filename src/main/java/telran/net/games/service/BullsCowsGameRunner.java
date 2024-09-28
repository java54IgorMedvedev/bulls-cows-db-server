package telran.net.games.service;

import java.util.Random;
import java.util.stream.Collectors;
import telran.net.games.model.MoveData;

public class BullsCowsGameRunner {
    private static final int DEFAULT_N_DIGITS = 4; 
    int nDigits;

    public BullsCowsGameRunner(int nDigits) {
        this.nDigits = nDigits > 0 ? nDigits : DEFAULT_N_DIGITS; 
    }

    public BullsCowsGameRunner() {
        this.nDigits = DEFAULT_N_DIGITS;
    }

    public String getRandomSequence() {
        String toBeGuessed = new Random().ints(0, 10).distinct()
                .limit(nDigits).mapToObj(Integer::toString)
                .collect(Collectors.joining());
        return toBeGuessed;
    }

    public MoveData moveProcessing(String guess, String toBeGuessed) {
        int[] bullsCows = {0, 0};
        char chars[] = guess.toCharArray();
        for (int i = 0; i < nDigits; i++) {
            int index = toBeGuessed.indexOf(chars[i]);
            if (index >= 0) {
                int j = index == i ? 0 : 1; 
                bullsCows[j]++;
            }
        }
        return new MoveData(guess, bullsCows[0], bullsCows[1]);
    }

    boolean checkGuess(String guess) {
        return guess.chars().distinct().count() == nDigits;
    }

    boolean checkGameFinished(MoveData moveData) {
        return moveData.bulls() == nDigits;
    }
}
