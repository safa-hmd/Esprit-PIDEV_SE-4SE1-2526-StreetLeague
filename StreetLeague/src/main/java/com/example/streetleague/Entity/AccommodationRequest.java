package com.example.streetleague.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class AccommodationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    private Long tournamentId;
    private Long coachId;
    
    @Column(name = "coach_name")
    private String coachName;

    @ElementCollection
    @CollectionTable(name = "accommodation_request_members", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "member_id")
    private List<Long> memberIds;

    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status = "PENDING";

    @Column(name = "admin_comment")
    private String adminComment;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "total_amount")
    private Double totalAmount;

    public AccommodationRequest() {
    }

    public AccommodationRequest(Long id, Accommodation accommodation, Long tournamentId, Long coachId, String coachName, List<Long> memberIds, String status, String adminComment, LocalDateTime createdAt, Double totalAmount) {
        this.id = id;
        this.accommodation = accommodation;
        this.tournamentId = tournamentId;
        this.coachId = coachId;
        this.coachName = coachName;
        this.memberIds = memberIds;
        this.status = status;
        this.adminComment = adminComment;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccommodationRequest that = (AccommodationRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(accommodation, that.accommodation) && Objects.equals(tournamentId, that.tournamentId) && Objects.equals(coachId, that.coachId) && Objects.equals(coachName, that.coachName) && Objects.equals(memberIds, that.memberIds) && Objects.equals(status, that.status) && Objects.equals(adminComment, that.adminComment) && Objects.equals(createdAt, that.createdAt) && Objects.equals(totalAmount, that.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accommodation, tournamentId, coachId, coachName, memberIds, status, adminComment, createdAt, totalAmount);
    }

    @Override
    public String toString() {
        return "AccommodationRequest{" +
                "id=" + id +
                ", accommodation=" + accommodation +
                ", tournamentId=" + tournamentId +
                ", coachId=" + coachId +
                ", coachName='" + coachName + '\'' +
                ", memberIds=" + memberIds +
                ", status='" + status + '\'' +
                ", adminComment='" + adminComment + '\'' +
                ", createdAt=" + createdAt +
                ", totalAmount=" + totalAmount +
                '}';
    }

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public Accommodation getAccommodation() { return this.accommodation; }
    public void setAccommodation(Accommodation accommodation) { this.accommodation = accommodation; }

    public Long getTournamentId() { return this.tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getCoachId() { return this.coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachName() { return this.coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public List<Long> getMemberIds() { return this.memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminComment() { return this.adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Double getTotalAmount() { return this.totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    // ===== STATIC BUILDER HELPER =====
    public static AccommodationRequestBuilder builder() {
        return new AccommodationRequestBuilder();
    }

    public static class AccommodationRequestBuilder {
        private Long id;
        private Accommodation accommodation;
        private Long tournamentId;
        private Long coachId;
        private String coachName;
        private List<Long> memberIds;
        private String status = "PENDING";
        private String adminComment;
        private LocalDateTime createdAt;
        private Double totalAmount;

        public AccommodationRequestBuilder id(Long id) { this.id = id; return this; }
        public AccommodationRequestBuilder accommodation(Accommodation accommodation) { this.accommodation = accommodation; return this; }
        public AccommodationRequestBuilder tournamentId(Long tournamentId) { this.tournamentId = tournamentId; return this; }
        public AccommodationRequestBuilder coachId(Long coachId) { this.coachId = coachId; return this; }
        public AccommodationRequestBuilder coachName(String coachName) { this.coachName = coachName; return this; }
        public AccommodationRequestBuilder memberIds(List<Long> memberIds) { this.memberIds = memberIds; return this; }
        public AccommodationRequestBuilder status(String status) { this.status = status; return this; }
        public AccommodationRequestBuilder adminComment(String adminComment) { this.adminComment = adminComment; return this; }
        public AccommodationRequestBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AccommodationRequestBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }

        public AccommodationRequest build() {
            return new AccommodationRequest(id, accommodation, tournamentId, coachId, coachName, memberIds, status, adminComment, createdAt != null ? createdAt : LocalDateTime.now(), totalAmount);
        }
    }
}

