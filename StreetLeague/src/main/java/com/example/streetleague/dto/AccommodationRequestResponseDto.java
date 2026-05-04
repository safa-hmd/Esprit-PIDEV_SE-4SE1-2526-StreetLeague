package com.example.streetleague.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    public AccommodationRequestResponseDto() {
    }

    public AccommodationRequestResponseDto(Long id, AccommodationDto accommodation, List<Long> memberIds, Long tournamentId, Long coachId, String coachName, String status, String adminComment, LocalDateTime createdAt, Double totalAmount) {
        this.id = id;
        this.accommodation = accommodation;
        this.memberIds = memberIds;
        this.tournamentId = tournamentId;
        this.coachId = coachId;
        this.coachName = coachName;
        this.status = status;
        this.adminComment = adminComment;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AccommodationDto getAccommodation() { return accommodation; }
    public void setAccommodation(AccommodationDto accommodation) { this.accommodation = accommodation; }

    public List<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminComment() { return adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccommodationRequestResponseDto that = (AccommodationRequestResponseDto) o;
        return Objects.equals(id, that.id) && Objects.equals(accommodation, that.accommodation) && Objects.equals(memberIds, that.memberIds) && Objects.equals(tournamentId, that.tournamentId) && Objects.equals(coachId, that.coachId) && Objects.equals(coachName, that.coachName) && Objects.equals(status, that.status) && Objects.equals(adminComment, that.adminComment) && Objects.equals(createdAt, that.createdAt) && Objects.equals(totalAmount, that.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accommodation, memberIds, tournamentId, coachId, coachName, status, adminComment, createdAt, totalAmount);
    }

    @Override
    public String toString() {
        return "AccommodationRequestResponseDto{" +
                "id=" + id +
                ", accommodation=" + accommodation +
                ", memberIds=" + memberIds +
                ", tournamentId=" + tournamentId +
                ", coachId=" + coachId +
                ", coachName='" + coachName + '\'' +
                ", status='" + status + '\'' +
                ", adminComment='" + adminComment + '\'' +
                ", createdAt=" + createdAt +
                ", totalAmount=" + totalAmount +
                '}';
    }

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

        public AccommodationRequestResponseDtoBuilder id(Long id) { this.id = id; return this; }
        public AccommodationRequestResponseDtoBuilder accommodation(AccommodationDto accommodation) { this.accommodation = accommodation; return this; }
        public AccommodationRequestResponseDtoBuilder memberIds(List<Long> memberIds) { this.memberIds = memberIds; return this; }
        public AccommodationRequestResponseDtoBuilder tournamentId(Long tournamentId) { this.tournamentId = tournamentId; return this; }
        public AccommodationRequestResponseDtoBuilder coachId(Long coachId) { this.coachId = coachId; return this; }
        public AccommodationRequestResponseDtoBuilder coachName(String coachName) { this.coachName = coachName; return this; }
        public AccommodationRequestResponseDtoBuilder status(String status) { this.status = status; return this; }
        public AccommodationRequestResponseDtoBuilder adminComment(String adminComment) { this.adminComment = adminComment; return this; }
        public AccommodationRequestResponseDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AccommodationRequestResponseDtoBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }

        public AccommodationRequestResponseDto build() {
            return new AccommodationRequestResponseDto(id, accommodation, memberIds, tournamentId, coachId, coachName, status, adminComment, createdAt, totalAmount);
        }
    }
}
