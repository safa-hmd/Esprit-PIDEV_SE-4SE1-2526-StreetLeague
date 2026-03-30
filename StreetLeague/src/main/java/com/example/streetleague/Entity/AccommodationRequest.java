package com.example.streetleague.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccommodationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    Accommodation accommodation;

    Long tournamentId;
    Long coachId;
    
    @Column(name = "coach_name")
    String coachName;

    @ElementCollection
    @CollectionTable(name = "accommodation_request_members", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "member_id")
    List<Long> memberIds;

    @Builder.Default
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status = "PENDING";

    @Column(name = "admin_comment")
    private String adminComment;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "total_amount")
    private Double totalAmount;

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
            AccommodationRequest req = new AccommodationRequest();
            req.setId(id);
            req.setAccommodation(accommodation);
            req.setTournamentId(tournamentId);
            req.setCoachId(coachId);
            req.setCoachName(coachName);
            req.setMemberIds(memberIds);
            req.setStatus(status);
            req.setAdminComment(adminComment);
            req.setCreatedAt(createdAt);
            req.setTotalAmount(totalAmount);
            return req;
        }
    }
}
