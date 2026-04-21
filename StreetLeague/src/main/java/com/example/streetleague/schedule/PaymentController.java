package com.example.streetleague.schedule;

import com.example.streetleague.Entity.PaymentMethod;
import com.example.streetleague.ServiceInterface.IPaymentService;
import com.example.streetleague.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final IPaymentService paymentService;

    // ── ADMIN ─────────────────────────────────────────────────────────────────

    // Appelé automatiquement par FieldReservationServiceImp — mais exposé aussi manuellement
    @PostMapping("/initiate/{reservationId}")
    public ResponseEntity<PaymentDto> initiate(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.initiatePayment(reservationId));
    }

    // Admin rembourse après annulation
    @PostMapping("/refund/{reservationId}")
    public ResponseEntity<PaymentDto> refund(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.refundPayment(reservationId));
    }

    // Vue globale admin
    @GetMapping("/all")
    public ResponseEntity<List<PaymentDto>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // ── JOUEUR ────────────────────────────────────────────────────────────────

    // Joueur paie → POST /api/payments/pay/3?method=CARD
    @PostMapping("/pay/{reservationId}")
    public ResponseEntity<PaymentDto> pay(
            @PathVariable Long reservationId,
            @RequestParam PaymentMethod method
    ) {
        return ResponseEntity.ok(paymentService.processPayment(reservationId, method));
    }

    // Consulter le paiement d'une réservation
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<PaymentDto> getByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.getPaymentByReservation(reservationId));
    }

    // Historique paiements joueur
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<PaymentDto>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPlayer(playerId));
    }
}