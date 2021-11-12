package com.noamzaks.chess.utilities;

public class Random {
    private static final String GAME_CODE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String GAME_CODE_NUMBERS = "0123456789";
    private static final int GAME_CODE_LENGTH = 6;

    public static String generateGameCode() {
        StringBuilder builder = new StringBuilder();
        int firstNumber = (int) (Math.random() * 6);
        int secondNumber = (int) (Math.random() * 5);
        if (secondNumber == firstNumber) {
            secondNumber += 1;
            secondNumber %= 6;
        }
        for (int i = 0; i < GAME_CODE_LENGTH; i++) {
            if (i == firstNumber || i == secondNumber) {
                builder.append(GAME_CODE_NUMBERS.charAt((int) (Math.random() * GAME_CODE_NUMBERS.length())));
            } else {
                builder.append(GAME_CODE_LETTERS.charAt((int) (Math.random() * GAME_CODE_LETTERS.length())));
            }
        }
        return builder.toString();
    }
}
