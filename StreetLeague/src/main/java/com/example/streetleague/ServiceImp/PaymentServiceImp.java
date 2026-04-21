package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.FieldReservationRepository;
import com.example.streetleague.Repository.PaymentRepository;
import com.example.streetleague.ServiceInterface.IPaymentService;
import com.example.streetleague.dto.PaymentDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements IPaymentService {

    private final PaymentRepository          paymentRepository;
    private final FieldReservationRepository reservationRepository;

    // ── INITIATE ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PaymentDto initiatePayment(Long reservationId) {

        // Éviter les doublons
        if (paymentRepository.existsByReservationId(reservationId)) {
            return getPaymentByReservation(reservationId);
        }

        FieldReservation reservation = findReservationById(reservationId);

        Payment payment = Payment.builder()
                .reservation(reservation)
                .player(reservation.getPlayer())
                .amount(reservation.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .method(null)   // pas encore choisie
                .build();

        return mapToDto(paymentRepository.save(payment));
    }

    // ── PROCESS (joueur paie) ─────────────────────────────────────────────────

    @Override
    @Transactional
    public PaymentDto processPayment(Long reservationId, PaymentMethod method) {

        Payment payment = findPaymentByReservationId(reservationId);

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Payment already completed");
        }
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Payment has been refunded");
        }

        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionRef(generateTransactionRef());

        return mapToDto(paymentRepository.save(payment));
    }

    // ── REFUND (admin annule après paiement) ──────────────────────────────────

    @Override
    @Transactional
    public PaymentDto refundPayment(Long reservationId) {

        Payment payment = findPaymentByReservationId(reservationId);

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Only PAID payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());

        return mapToDto(paymentRepository.save(payment));
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByReservation(Long reservationId) {
        return mapToDto(findPaymentByReservationId(reservationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByPlayer(Long playerId) {
        return paymentRepository.findByPlayerId(playerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAllWithDetails()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private FieldReservation findReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    private Payment findPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for reservation id: " + reservationId));
    }

    private String generateTransactionRef() {
        return "TXN-" + LocalDateTime.now().toLocalDate()
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentDto mapToDto(Payment p) {
        FieldReservation r = p.getReservation();
        Field            f = r.getField();

        return PaymentDto.builder()
                .id(p.getId())
                .amount(p.getAmount())
                .method(p.getMethod())
                .status(p.getStatus())
                .transactionRef(p.getTransactionRef())
                .createdAt(p.getCreatedAt())
                .paidAt(p.getPaidAt())
                .refundedAt(p.getRefundedAt())
                .reservationId(r.getId())
                .slotStart(r.getStartTime())
                .slotEnd(r.getEndTime())
                .fieldId(f.getId())
                .fieldName(f.getName())
                .fieldLocation(f.getLocation())
                .playerId(p.getPlayer().getIdUser())
                .playerName(p.getPlayer().getFullName())
                .build();
    }
}