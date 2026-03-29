package com.example.streetleague.dto;

import com.example.streetleague.Entity.TravelRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelRequestResponseDto {
    private Long id;
    private Long teamId;
    private String teamName;
    private String teamCity;
    private TournamentDto tournament;
    private TransportDto transport;
    private AccommodationDto accommodation;
    private TravelRequestStatus status;
    private Boolean sameCity;
    private Boolean accommodationRequired;
    private String adminComment;
    private Double individualPrice;
    private Double totalAmount;
    private LocalDateTime createdAt;

    // ===== EXPLICIT GETTERS/SETTERS =====
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeamId() { return this.teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return this.teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamCity() { return this.teamCity; }
    public void setTeamCity(String teamCity) { this.teamCity = teamCity; }

    public TournamentDto getTournament() { return this.tournament; }
    public void setTournament(TournamentDto tournament) { this.tournament = tournament; }

    public TransportDto getTransport() { return this.transport; }
    public void setTransport(TransportDto transport) { this.transport = transport; }

    public AccommodationDto getAccommodation() { return this.accommodation; }
    public void setAccommodation(AccommodationDto accommodation) { this.accommodation = accommodation; }

    public TravelRequestStatus getStatus() { return this.status; }
    public void setStatus(TravelRequestStatus status) { this.status = status; }

    public Boolean getSameCity() { return this.sameCity; }
    public void setSameCity(Boolean sameCity) { this.sameCity = sameCity; }

    public Boolean getAccommodationRequired() { return this.accommodationRequired; }
    public void setAccommodationRequired(Boolean accommodationRequired) { this.accommodationRequired = accommodationRequired; }

    public String getAdminComment() { return this.adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public Double getIndividualPrice() { return this.individualPrice; }
    public void setIndividualPrice(Double individualPrice) { this.individualPrice = individualPrice; }

    public Double getTotalAmount() { return this.totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ===== STATIC BUILDER HELPER =====
    public static TravelRequestResponseDtoBuilder builder() {
        return new TravelRequestResponseDtoBuilder();
    }

    public static class TravelRequestResponseDtoBuilder {
        private Long id;
        private Long teamId;
        private String teamName;
        private String teamCity;
        private TournamentDto tournament;
        private TransportDto transport;
        private AccommodationDto accommodation;
        private TravelRequestStatus status;
        private Boolean sameCity;
        private Boolean accommodationRequired;
        private String adminComment;
        private Double individualPrice;
        private Double totalAmount;
        private LocalDateTime createdAt;

        public TravelRequestResponseDtoBuilder id(Long id) { this.id = id; return this; }
        public TravelRequestResponseDtoBuilder teamId(Long teamId) { this.teamId = teamId; return this; }
        public TravelRequestResponseDtoBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public TravelRequestResponseDtoBuilder teamCity(String teamCity) { this.teamCity = teamCity; return this; }
        public TravelRequestResponseDtoBuilder tournament(TournamentDto tournament) { this.tournament = tournament; return this; }
        public TravelRequestResponseDtoBuilder transport(TransportDto transport) { this.transport = transport; return this; }
        public TravelRequestResponseDtoBuilder accommodation(AccommodationDto accommodation) { this.accommodation = accommodation; return this; }
        public TravelRequestResponseDtoBuilder status(TravelRequestStatus status) { this.status = status; return this; }
        public TravelRequestResponseDtoBuilder sameCity(Boolean sameCity) { this.sameCity = sameCity; return this; }
        public TravelRequestResponseDtoBuilder accommodationRequired(Boolean accommodationRequired) { this.accommodationRequired = accommodationRequired; return this; }
        public TravelRequestResponseDtoBuilder adminComment(String adminComment) { this.adminComment = adminComment; return this; }
        public TravelRequestResponseDtoBuilder individualPrice(Double individualPrice) { this.individualPrice = individualPrice; return this; }
        public TravelRequestResponseDtoBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }
        public TravelRequestResponseDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TravelRequestResponseDto build() {
            TravelRequestResponseDto dto = new TravelRequestResponseDto();
            dto.setId(id);
            dto.setTeamId(teamId);
            dto.setTeamName(teamName);
            dto.setTeamCity(teamCity);
            dto.setTournament(tournament);
            dto.setTransport(transport);
            dto.setAccommodation(accommodation);
            dto.setStatus(status);
            dto.setSameCity(sameCity);
            dto.setAccommodationRequired(accommodationRequired);
            dto.setAdminComment(adminComment);
            dto.setIndividualPrice(individualPrice);
            dto.setTotalAmount(totalAmount);
            dto.setCreatedAt(createdAt);
            return dto;
        }
    }
}
