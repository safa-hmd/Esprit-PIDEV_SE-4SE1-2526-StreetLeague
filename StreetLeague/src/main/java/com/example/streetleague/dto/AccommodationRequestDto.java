package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestDto {
    private Long accommodationId;
    private List<Long> memberIds;
    private Long tournamentId;
    private Long coachId;
    private String coachName;
    private Double totalAmount;

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====
    public Long getAccommodationId() { return this.accommodationId; }
    public void setAccommodationId(Long accommodationId) { this.accommodationId = accommodationId; }

    public List<Long> getMemberIds() { return this.memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }

    public Long getTournamentId() { return this.tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getCoachId() { return this.coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachName() { return this.coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public Double getTotalAmount() { return this.totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    // ===== STATIC BUILDER HELPER =====
    public static AccommodationRequestDtoBuilder builder() {
        return new AccommodationRequestDtoBuilder();
    }

    public static class AccommodationRequestDtoBuilder {
        private Long accommodationId;
        private List<Long> memberIds;
        private Long tournamentId;
        private Long coachId;
        private String coachName;
        private Double totalAmount;

        public AccommodationRequestDtoBuilder accommodationId(Long accommodationId) {
            this.accommodationId = accommodationId;
            return this;
        }

        public AccommodationRequestDtoBuilder memberIds(List<Long> memberIds) {
            this.memberIds = memberIds;
            return this;
        }

        public AccommodationRequestDtoBuilder tournamentId(Long tournamentId) {
            this.tournamentId = tournamentId;
            return this;
        }

        public AccommodationRequestDtoBuilder coachId(Long coachId) {
            this.coachId = coachId;
            return this;
        }

        public AccommodationRequestDtoBuilder coachName(String coachName) {
            this.coachName = coachName;
            return this;
        }

        public AccommodationRequestDtoBuilder totalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public AccommodationRequestDto build() {
            AccommodationRequestDto dto = new AccommodationRequestDto();
            dto.setAccommodationId(accommodationId);
            dto.setMemberIds(memberIds);
            dto.setTournamentId(tournamentId);
            dto.setCoachId(coachId);
            dto.setCoachName(coachName);
            dto.setTotalAmount(totalAmount);
            return dto;
        }
    }
}