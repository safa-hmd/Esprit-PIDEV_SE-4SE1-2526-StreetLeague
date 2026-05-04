package com.example.streetleague.dto;

import java.util.List;
import java.util.Objects;

public class AccommodationRequestDto {
    private Long accommodationId;
    private List<Long> memberIds;
    private Long tournamentId;
    private Long coachId;
    private String coachName;
    private Double totalAmount;

    public AccommodationRequestDto() {
    }

    public AccommodationRequestDto(Long accommodationId, List<Long> memberIds, Long tournamentId, Long coachId, String coachName, Double totalAmount) {
        this.accommodationId = accommodationId;
        this.memberIds = memberIds;
        this.tournamentId = tournamentId;
        this.coachId = coachId;
        this.coachName = coachName;
        this.totalAmount = totalAmount;
    }

    public Long getAccommodationId() { return accommodationId; }
    public void setAccommodationId(Long accommodationId) { this.accommodationId = accommodationId; }

    public List<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccommodationRequestDto that = (AccommodationRequestDto) o;
        return Objects.equals(accommodationId, that.accommodationId) && Objects.equals(memberIds, that.memberIds) && Objects.equals(tournamentId, that.tournamentId) && Objects.equals(coachId, that.coachId) && Objects.equals(coachName, that.coachName) && Objects.equals(totalAmount, that.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accommodationId, memberIds, tournamentId, coachId, coachName, totalAmount);
    }

    @Override
    public String toString() {
        return "AccommodationRequestDto{" +
                "accommodationId=" + accommodationId +
                ", memberIds=" + memberIds +
                ", tournamentId=" + tournamentId +
                ", coachId=" + coachId +
                ", coachName='" + coachName + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }

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

        public AccommodationRequestDtoBuilder accommodationId(Long accommodationId) { this.accommodationId = accommodationId; return this; }
        public AccommodationRequestDtoBuilder memberIds(List<Long> memberIds) { this.memberIds = memberIds; return this; }
        public AccommodationRequestDtoBuilder tournamentId(Long tournamentId) { this.tournamentId = tournamentId; return this; }
        public AccommodationRequestDtoBuilder coachId(Long coachId) { this.coachId = coachId; return this; }
        public AccommodationRequestDtoBuilder coachName(String coachName) { this.coachName = coachName; return this; }
        public AccommodationRequestDtoBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }

        public AccommodationRequestDto build() {
            return new AccommodationRequestDto(accommodationId, memberIds, tournamentId, coachId, coachName, totalAmount);
        }
    }
}