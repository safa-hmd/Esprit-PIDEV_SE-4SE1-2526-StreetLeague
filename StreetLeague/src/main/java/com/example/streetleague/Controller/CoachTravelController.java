package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.ServiceInterface.CoachTravelService;
import com.example.streetleague.dto.*;
import com.example.streetleague.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach/travel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CoachTravelController {

    private final CoachTravelService coachTravelService;

    @GetMapping("/transports/tournament/{tournamentId}")
    public ResponseEntity<List<Transport>> getAvailableTransports(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(coachTravelService.getAvailableTransports());
    }

    @GetMapping("/accommodations/tournament/{tournamentId}")
    public ResponseEntity<List<Accommodation>> getAvailableAccommodations(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(coachTravelService.getAvailableAccommodations());
    }

    @GetMapping("/accommodations")
    public ResponseEntity<List<Accommodation>> getApprovedAccommodations() {
        return ResponseEntity.ok(coachTravelService.getApprovedAccommodations());
    }

    @GetMapping("/team-members")
    public ResponseEntity<List<User>> getMyTeamMembers(
            @RequestParam(name = "coachId", required = false) Long coachId) {
        if (coachId == null) {
            coachId = getAuthenticatedUserId();
        }
        System.out.println("==> getMyTeamMembers appelé avec coachId: " + coachId);
        return ResponseEntity.ok(coachTravelService.getMyTeamMembers(coachId));
    }

    @PostMapping("/accommodation-request")
    public ResponseEntity<AccommodationRequestResponseDto> submitAccommodationRequest(
            @RequestBody AccommodationRequestDto requestDto) {
        if (requestDto.getCoachId() == null) {
            requestDto.setCoachId(getAuthenticatedUserId());
        }
        return ResponseEntity.ok(
                coachTravelService.submitAccommodationRequest(requestDto));
    }

    @GetMapping("/accommodation-requests/my")
    public ResponseEntity<List<AccommodationRequestResponseDto>> getMyAccommodationRequests(
            @RequestParam(name = "coachId", required = false) Long coachId) {
        if (coachId == null) {
            coachId = getAuthenticatedUserId();
        }
        return ResponseEntity.ok(
                coachTravelService.getMyAccommodationRequests(coachId));
    }

    @PostMapping("/requests")
    public ResponseEntity<TravelRequestResponseDto> submitTravelRequest(
            @RequestBody TravelRequestDto requestDto) {
        return ResponseEntity.ok(
                coachTravelService.submitTravelRequest(requestDto));
    }

    @GetMapping("/transports/check")
    public ResponseEntity<TransportEligibilityResponseDto> checkEligibilityAndGetTransports(
            @RequestParam(name = "coachId", required = false) Long coachId,
            @RequestParam(name = "tournamentId") Long tournamentId) {
        if (coachId == null) {
            coachId = getAuthenticatedUserId();
        }
        return ResponseEntity.ok(coachTravelService.checkEligibilityAndGetTransports(coachId, tournamentId));
    }

    @PostMapping("/transport/personal-car")
    public ResponseEntity<TransportDto> submitPersonalCar(@RequestBody TransportDto carDto) {
        return ResponseEntity.ok(coachTravelService.submitPersonalCar(carDto));
    }

    @GetMapping("/requests/my")
    public ResponseEntity<List<TravelRequestResponseDto>> getMyTravelRequests(
            @RequestParam(name = "coachId", required = false) Long coachId) {
        if (coachId == null) {
            coachId = getAuthenticatedUserId();
        }
        
        // Find team by coach ID to query requests
        Long teamId = getTeamIdByCoachId(coachId); // Helper to implement or inject user service if needed
        if (teamId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(coachTravelService.getRequestsByTeam(teamId));
    }

    private Long getTeamIdByCoachId(Long coachId) {
        try {
            // Ideally we get User and check Team ID. For this implementation we might just rely on passing teamId explicitly instead 
            // of my requests, but since requested, here is a mock retrieval:
            // This would realistically call userService or teamService here.
            com.example.streetleague.domain.User coach = getAuthenticatedUser();
            if (coach != null) {
                return coach.getTeamId();
            }
        } catch (Exception e) {}
        return null;
    }

    private com.example.streetleague.domain.User getAuthenticatedUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                return (User) auth.getPrincipal();
            }
        } catch (Exception e) {}
        return null;
    }

    @GetMapping("/requests/team/{teamId}")
    public ResponseEntity<List<TravelRequestResponseDto>> getRequestsByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(coachTravelService.getRequestsByTeam(teamId));
    }

    private Long getAuthenticatedUserId() {
        try {
            Authentication auth = SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                return ((User) auth.getPrincipal()).getIdUser();
            }
        } catch (Exception e) {
            System.err.println("Impossible de récupérer l'utilisateur auth: "
                    + e.getMessage());
        }
        return null;
    }
}