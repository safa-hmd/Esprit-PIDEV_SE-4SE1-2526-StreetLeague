package com.example.streetleague.Entity;

public enum PaymentStatus {

    PENDING,    // paiement créé, en attente du joueur
    PAID,       // joueur a payé
    REFUNDED,   // remboursé suite à annulation admin
    FAILED      // échec du paiement
}