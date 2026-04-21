package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.PaymentMethod;
import com.example.streetleague.dto.PaymentDto;

import java.util.List;

public interface IPaymentService {

    // ── Admin ─────────────────────────────────────────────────────

    // Appelé automatiquement quand admin approuve une réservation
    PaymentDto initiatePayment(Long reservationId);

    // Appelé quand admin annule une réservation déjà payée
    PaymentDto refundPayment(Long reservationId);

    // Vue globale admin
    List<PaymentDto> getAllPayments();

    // ── Joueur ────────────────────────────────────────────────────

    // Joueur clique Pay Now → choisit sa méthode
    PaymentDto processPayment(Long reservationId, PaymentMethod method);

    // Consulter le paiement d'une réservation
    PaymentDto getPaymentByReservation(Long reservationId);

    // Historique complet des paiements d'un joueur
    List<PaymentDto> getPaymentsByPlayer(Long playerId);
}