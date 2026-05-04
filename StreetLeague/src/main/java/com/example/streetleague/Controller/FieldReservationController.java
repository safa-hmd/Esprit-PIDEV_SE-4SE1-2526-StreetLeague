package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.IFieldReservationService;
import com.example.streetleague.dto.FieldReservationDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class FieldReservationController {

    private final IFieldReservationService reservationService;

    public FieldReservationController(IFieldReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // ===== PLAYER =====

    @PostMapping
    public ResponseEntity<FieldReservationDto> create(@Valid @RequestBody FieldReservationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<FieldReservationDto> cancel(@PathVariable Long id,
                                                      @RequestParam Long playerId) {
        return ResponseEntity.ok(reservationService.cancelReservation(id, playerId));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<FieldReservationDto>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(reservationService.getReservationsByPlayer(playerId));
    }

    // ===== ADMIN =====

    @GetMapping
    public ResponseEntity<List<FieldReservationDto>> getAll() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FieldReservationDto>> getPending() {
        return ResponseEntity.ok(reservationService.getPendingReservations());
    }

    @GetMapping("/field/{fieldId}")
    public ResponseEntity<List<FieldReservationDto>> getByField(@PathVariable Long fieldId) {
        return ResponseEntity.ok(reservationService.getReservationsByField(fieldId));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<FieldReservationDto> approve(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("adminNote") : null;
        return ResponseEntity.ok(reservationService.approveReservation(id, note));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<FieldReservationDto> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("adminNote") : null;
        return ResponseEntity.ok(reservationService.rejectReservation(id, note));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    // ===== ADMIN + PLAYER =====

    @GetMapping("/{id}")
    public ResponseEntity<FieldReservationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }
}