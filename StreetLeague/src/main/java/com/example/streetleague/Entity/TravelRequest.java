package com.example.streetleague.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class TravelRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id")
    private Transport transport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    private TravelRequestStatus status;

    private Boolean sameCity;
    private Boolean accommodationRequired;

    @Column(length = 500)
    private String adminComment;

    private Double totalAmount;

    @Column(name = "selected_member_ids_str", length = 1000)
    private String selectedMemberIdsStr;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TravelRequest() {
    }

    public TravelRequest(Long id, Team team, Tournament tournament, Transport transport, Accommodation accommodation, TravelRequestStatus status, Boolean sameCity, Boolean accommodationRequired, String adminComment, Double totalAmount, String selectedMemberIdsStr, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.team = team;
        this.tournament = tournament;
        this.transport = transport;
        this.accommodation = accommodation;
        this.status = status;
        this.sameCity = sameCity;
        this.accommodationRequired = accommodationRequired;
        this.adminComment = adminComment;
        this.totalAmount = totalAmount;
        this.selectedMemberIdsStr = selectedMemberIdsStr;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelRequest that = (TravelRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(team, that.team) && Objects.equals(tournament, that.tournament) && Objects.equals(transport, that.transport) && Objects.equals(accommodation, that.accommodation) && status == that.status && Objects.equals(sameCity, that.sameCity) && Objects.equals(accommodationRequired, that.accommodationRequired) && Objects.equals(adminComment, that.adminComment) && Objects.equals(totalAmount, that.totalAmount) && Objects.equals(selectedMemberIdsStr, that.selectedMemberIdsStr) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, team, tournament, transport, accommodation, status, sameCity, accommodationRequired, adminComment, totalAmount, selectedMemberIdsStr, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "TravelRequest{" +
                "id=" + id +
                ", team=" + team +
                ", tournament=" + tournament +
                ", transport=" + transport +
                ", accommodation=" + accommodation +
                ", status=" + status +
                ", sameCity=" + sameCity +
                ", accommodationRequired=" + accommodationRequired +
                ", adminComment='" + adminComment + '\'' +
                ", totalAmount=" + totalAmount +
                ", selectedMemberIdsStr='" + selectedMemberIdsStr + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TravelRequestStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public Tournament getTournament() { return this.tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public Transport getTransport() { return this.transport; }
    public void setTransport(Transport transport) { this.transport = transport; }

    public Accommodation getAccommodation() { return this.accommodation; }
    public void setAccommodation(Accommodation accommodation) { this.accommodation = accommodation; }

    public TravelRequestStatus getStatus() { return this.status; }
    public void setStatus(TravelRequestStatus status) { this.status = status; }

    public Boolean getSameCity() { return this.sameCity; }
    public void setSameCity(Boolean sameCity) { this.sameCity = sameCity; }

    public Boolean getAccommodationRequired() { return this.accommodationRequired; }
    public void setAccommodationRequired(Boolean accommodationRequired) { this.accommodationRequired = accommodationRequired; }

    public String getAdminComment() { return this.adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public Double getTotalAmount() { return this.totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public List<Long> getSelectedMemberIds() {
        if (this.selectedMemberIdsStr == null || this.selectedMemberIdsStr.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return java.util.Arrays.stream(this.selectedMemberIdsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(java.util.stream.Collectors.toList());
    }
    public void setSelectedMemberIds(List<Long> selectedMemberIds) {
        if (selectedMemberIds == null || selectedMemberIds.isEmpty()) {
            this.selectedMemberIdsStr = "";
        } else {
            this.selectedMemberIdsStr = selectedMemberIds.stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
        }
    }

    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ===== STATIC BUILDER HELPER =====
    public static TravelRequestBuilder builder() {
        return new TravelRequestBuilder();
    }

    public static class TravelRequestBuilder {
        private Long id;
        private Team team;
        private Tournament tournament;
        private Transport transport;
        private Accommodation accommodation;
        private TravelRequestStatus status;
        private Boolean sameCity;
        private Boolean accommodationRequired;
        private String adminComment;
        private Double totalAmount;
        private List<Long> selectedMemberIds;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TravelRequestBuilder id(Long id) { this.id = id; return this; }
        public TravelRequestBuilder team(Team team) { this.team = team; return this; }
        public TravelRequestBuilder tournament(Tournament tournament) { this.tournament = tournament; return this; }
        public TravelRequestBuilder transport(Transport transport) { this.transport = transport; return this; }
        public TravelRequestBuilder accommodation(Accommodation accommodation) { this.accommodation = accommodation; return this; }
        public TravelRequestBuilder status(TravelRequestStatus status) { this.status = status; return this; }
        public TravelRequestBuilder sameCity(Boolean sameCity) { this.sameCity = sameCity; return this; }
        public TravelRequestBuilder accommodationRequired(Boolean accommodationRequired) { this.accommodationRequired = accommodationRequired; return this; }
        public TravelRequestBuilder adminComment(String adminComment) { this.adminComment = adminComment; return this; }
        public TravelRequestBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }
        public TravelRequestBuilder selectedMemberIds(List<Long> selectedMemberIds) { this.selectedMemberIds = selectedMemberIds; return this; }
        public TravelRequestBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TravelRequestBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public TravelRequest build() {
            TravelRequest req = new TravelRequest();
            req.setId(id);
            req.setTeam(team);
            req.setTournament(tournament);
            req.setTransport(transport);
            req.setAccommodation(accommodation);
            req.setStatus(status);
            req.setSameCity(sameCity);
            req.setAccommodationRequired(accommodationRequired);
            req.setAdminComment(adminComment);
            req.setTotalAmount(totalAmount);
            req.setSelectedMemberIds(selectedMemberIds);
            req.setCreatedAt(createdAt);
            req.setUpdatedAt(updatedAt);
            return req;
        }
    }
}
