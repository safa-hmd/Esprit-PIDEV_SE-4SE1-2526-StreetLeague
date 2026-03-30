package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestResponseDto {
    private Long id;
    private AccommodationDto accommodation;
    private List<Long> memberIds;
    private Long tournamentId;
    private Long coachId;
    private String coachName;
    private String status;
    private String adminComment;
    private LocalDateTime createdAt;
    private Double totalAmount;

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public AccommodationDto getAccommodation() { return this.accommodation; }
    public void setAccommodation(AccommodationDto accommodation) { this.accommodation = accommodation; }

    public List<Long> getMemberIds() { return this.memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }

    public Long getTournamentId() { return this.tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getCoachId() { return this.coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachName() { return this.coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminComment() { return this.adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Double getTotalAmount() { return this.totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    // ===== STATIC BUILDER HELPER =====
    public static AccommodationRequestResponseDtoBuilder builder() {
        return new AccommodationRequestResponseDtoBuilder();
    }

    public static class AccommodationRequestResponseDtoBuilder {
        private Long id;
        private AccommodationDto accommodation;
        private List<Long> memberIds;
        private Long tournamentId;
        private Long coachId;
        private String coachName;
        private String status;
        private String adminComment;
        private LocalDateTime createdAt;
        private Double totalAmount;

        public AccommodationRequestResponseDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder accommodation(AccommodationDto accommodation) {
            this.accommodation = accommodation;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder memberIds(List<Long> memberIds) {
            this.memberIds = memberIds;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder tournamentId(Long tournamentId) {
            this.tournamentId = tournamentId;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder coachId(Long coachId) {
            this.coachId = coachId;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder coachName(String coachName) {
            this.coachName = coachName;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder adminComment(String adminComment) {
            this.adminComment = adminComment;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AccommodationRequestResponseDtoBuilder totalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public AccommodationRequestResponseDto build() {
            AccommodationRequestResponseDto dto = new AccommodationRequestResponseDto();
            dto.setId(id);
            dto.setAccommodation(accommodation);
            dto.setMemberIds(memberIds);
            dto.setTournamentId(tournamentId);
            dto.setCoachId(coachId);
            dto.setCoachName(coachName);
            dto.setStatus(status);
            dto.setAdminComment(adminComment);
            dto.setCreatedAt(createdAt);
            dto.setTotalAmount(totalAmount);
            return dto;
        }
    }
}
