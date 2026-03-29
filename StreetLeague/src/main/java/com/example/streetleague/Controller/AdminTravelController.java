package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.ServiceInterface.AdminTravelService;
import com.example.streetleague.dto.AccommodationDto;
import com.example.streetleague.dto.AccommodationRequestResponseDto;
import com.example.streetleague.dto.DecisionDto;
import com.example.streetleague.dto.TransportDto;
import com.example.streetleague.dto.TravelRequestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/travel")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminTravelController {

    private final AdminTravelService adminTravelService;

    @PostMapping("/transport")
    public ResponseEntity<Transport> addTransport(@RequestBody TransportDto transportDto) {
        return ResponseEntity.ok(adminTravelService.addTransport(transportDto));
    }

    @GetMapping("/transport")
    public ResponseEntity<List<TransportDto>> getAllTransports() {
        return ResponseEntity.ok(adminTravelService.getAllTransports());
    }

    @PutMapping("/transport/{id}")
    public ResponseEntity<Transport> updateTransport(
            @PathVariable Long id,
            @RequestBody TransportDto dto) {
        return ResponseEntity.ok(adminTravelService.updateTransport(id, dto));
    }

    @DeleteMapping("/transport/{id}")
    public ResponseEntity<Void> deleteTransport(@PathVariable Long id) {
        adminTravelService.deleteTransport(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accommodation")
    public ResponseEntity<Accommodation> addAccommodation(@RequestBody AccommodationDto accommodationDto) {
        return ResponseEntity.ok(adminTravelService.addAccommodation(accommodationDto));
    }

    @PutMapping("/accommodation/{id}")
    public ResponseEntity<Accommodation> updateAccommodation(
            @PathVariable Long id,
            @RequestBody AccommodationDto dto) {
        return ResponseEntity.ok(adminTravelService.updateAccommodation(id, dto));
    }

    @DeleteMapping("/accommodation/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {
        adminTravelService.deleteAccommodation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accommodation/{id}/approve")
    public ResponseEntity<Accommodation> approveAccommodation(@PathVariable Long id) {
        return ResponseEntity.ok(adminTravelService.approveAccommodation(id));
    }

    @PutMapping("/accommodation/{id}/reject")
    public ResponseEntity<Accommodation> rejectAccommodation(@PathVariable Long id) {
        return ResponseEntity.ok(adminTravelService.rejectAccommodation(id));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<TravelRequestResponseDto>> getAllRequests() {
        return ResponseEntity.ok(adminTravelService.getAllRequests());
    }

    @PutMapping("/requests/{id}/decision")
    public ResponseEntity<TravelRequestResponseDto> decideRequest(
            @PathVariable Long id,
            @RequestBody DecisionDto decisionDto) {
        return ResponseEntity.ok(adminTravelService.decideRequest(id, decisionDto));
    }

    @GetMapping("/accommodation")
    public ResponseEntity<List<AccommodationDto>> getAllAccommodations() {
        return ResponseEntity.ok(adminTravelService.getAllAccommodations());
    }

    // ==================== ACCOMMODATION REQUEST ENDPOINTS ====================

    @GetMapping("/accommodation-requests")
    public ResponseEntity<List<AccommodationRequestResponseDto>> 
        getAllAccommodationRequests() {
        return ResponseEntity.ok(
            adminTravelService.getAllAccommodationRequests());
    }

    @PutMapping("/accommodation-requests/{id}/approve")
    public ResponseEntity<AccommodationRequestResponseDto> 
        approveRequest(
            @PathVariable Long id,
            @RequestBody(required = false) DecisionDto decision) {
        return ResponseEntity.ok(
            adminTravelService.approveAccommodationRequest(id, decision));
    }

    @PutMapping("/accommodation-requests/{id}/reject")
    public ResponseEntity<AccommodationRequestResponseDto> 
        rejectRequest(
            @PathVariable Long id,
            @RequestBody DecisionDto decision) {
        return ResponseEntity.ok(
            adminTravelService.rejectAccommodationRequest(id, decision));
    }

    @GetMapping("/accommodation-requests/{id}/pdf")
    public ResponseEntity<byte[]> getRequestPdf(
        @PathVariable Long id) {
        byte[] pdf = adminTravelService.generateRequestPdf(id);
        return ResponseEntity.ok()
            .header("Content-Disposition",
                "attachment; filename=request-" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
