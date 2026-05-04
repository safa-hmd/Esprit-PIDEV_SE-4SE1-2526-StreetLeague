package com.example.streetleague.dto;

import com.example.streetleague.Entity.TravelRequestStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @JsonProperty("selectedMemberIds")
    private List<Long> selectedMemberIds;

    public TravelRequestResponseDto() {
    }

    public TravelRequestResponseDto(Long id, Long teamId, String teamName, String teamCity, TournamentDto tournament, TransportDto transport, AccommodationDto accommodation, TravelRequestStatus status, Boolean sameCity, Boolean accommodationRequired, String adminComment, Double individualPrice, Double totalAmount, LocalDateTime createdAt, List<Long> selectedMemberIds) {
        this.id = id;
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamCity = teamCity;
        this.tournament = tournament;
        this.transport = transport;
        this.accommodation = accommodation;
        this.status = status;
        this.sameCity = sameCity;
        this.accommodationRequired = accommodationRequired;
        this.adminComment = adminComment;
        this.individualPrice = individualPrice;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.selectedMemberIds = selectedMemberIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamCity() { return teamCity; }
    public void setTeamCity(String teamCity) { this.teamCity = teamCity; }

    public TournamentDto getTournament() { return tournament; }
    public void setTournament(TournamentDto tournament) { this.tournament = tournament; }

    public TransportDto getTransport() { return transport; }
    public void setTransport(TransportDto transport) { this.transport = transport; }

    public AccommodationDto getAccommodation() { return accommodation; }
    public void setAccommodation(AccommodationDto accommodation) { this.accommodation = accommodation; }

    public TravelRequestStatus getStatus() { return status; }
    public void setStatus(TravelRequestStatus status) { this.status = status; }

    public Boolean getSameCity() { return sameCity; }
    public void setSameCity(Boolean sameCity) { this.sameCity = sameCity; }

    public Boolean getAccommodationRequired() { return accommodationRequired; }
    public void setAccommodationRequired(Boolean accommodationRequired) { this.accommodationRequired = accommodationRequired; }

    public String getAdminComment() { return adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public Double getIndividualPrice() { return individualPrice; }
    public void setIndividualPrice(Double individualPrice) { this.individualPrice = individualPrice; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Long> getSelectedMemberIds() { return selectedMemberIds; }
    public void setSelectedMemberIds(List<Long> selectedMemberIds) { this.selectedMemberIds = selectedMemberIds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelRequestResponseDto that = (TravelRequestResponseDto) o;
        return Objects.equals(id, that.id) && Objects.equals(teamId, that.teamId) && Objects.equals(teamName, that.teamName) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamId, teamName, status);
    }

    @Override
    public String toString() {
        return "TravelRequestResponseDto{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                ", status=" + status +
                '}';
    }

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
        private List<Long> selectedMemberIds;

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
        public TravelRequestResponseDtoBuilder selectedMemberIds(List<Long> selectedMemberIds) { this.selectedMemberIds = selectedMemberIds; return this; }

        public TravelRequestResponseDto build() {
            return new TravelRequestResponseDto(id, teamId, teamName, teamCity, tournament, transport, accommodation, status, sameCity, accommodationRequired, adminComment, individualPrice, totalAmount, createdAt, selectedMemberIds);
        }
    }
}
