package com.example.streetleague.algorithm;

import com.example.streetleague.Entity.Level;

public final class EloCalculator {

    private EloCalculator() {}

    //probabilité que une equipe gagne
    public static double expectedScore(int eloA, int eloB) {
        return 1.0 / (1 + Math.pow(10, (eloB - eloA) / 400.0));
    }

    public static int newElo(int currentElo, double expected,
                             double actual, Level level) {
        int k = kFactor(level);
        int result = (int) Math.round(currentElo + k * (actual - expected));
        return Math.max(100, result);
    }

    public static int kFactor(Level level) {
        if (level == null) return 32;
        return switch (level) {
            case BEGINNER      -> 32;
            case INTERMEDIATE  -> 24;
            case ADVANCED      -> 16;
            case PROFESSIONAL  -> 12;
            default            -> 32;
        };
    }

    public static Level computeLevel(int elo) {
        if (elo >= 1600) return Level.PROFESSIONAL;
        if (elo >= 1300) return Level.ADVANCED;
        if (elo >= 1000) return Level.INTERMEDIATE;
        return Level.BEGINNER;
    }
}