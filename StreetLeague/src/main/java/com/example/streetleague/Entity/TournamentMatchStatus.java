package com.example.streetleague.Entity;

public enum TournamentMatchStatus {
    PENDING,      // pas encore joué
    IN_PROGRESS,  // en cours
    COMPLETED,    // résultat saisi
    BYE           // passage automatique (impair)
}