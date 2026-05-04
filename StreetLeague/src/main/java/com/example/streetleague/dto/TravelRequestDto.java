package com.example.streetleague.dto;

import java.util.List;
import java.util.Objects;

public class TravelRequestDto {
    private Long coachId;
    private Long teamId;
    private Long tournamentId;
    private Long transportId; // Nullable if same city
    private Long accommodationId;
    private List<Long> selectedMemberIds;

    public TravelRequestDto() {
    }

    public TravelRequestDto(Long coachId, Long teamId, Long tournamentId, Long transportId, Long accommodationId, List<Long> selectedMemberIds) {
        this.coachId = coachId;
        this.teamId = teamId;
        this.tournamentId = tournamentId;
        this.transportId = transportId;
        this.accommodationId = accommodationId;
        this.selectedMemberIds = selectedMemberIds;
    }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getTransportId() { return transportId; }
    public void setTransportId(Long transportId) { this.transportId = transportId; }

    public Long getAccommodationId() { return accommodationId; }
    public void setAccommodationId(Long accommodationId) { this.accommodationId = accommodationId; }

    public List<Long> getSelectedMemberIds() { return selectedMemberIds; }
    public void setSelectedMemberIds(List<Long> selectedMemberIds) { this.selectedMemberIds = selectedMemberIds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelRequestDto that = (TravelRequestDto) o;
        return Objects.equals(coachId, that.coachId) && Objects.equals(teamId, that.teamId) && Objects.equals(tournamentId, that.tournamentId) && Objects.equals(transportId, that.transportId) && Objects.equals(accommodationId, that.accommodationId) && Objects.equals(selectedMemberIds, that.selectedMemberIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coachId, teamId, tournamentId, transportId, accommodationId, selectedMemberIds);
    }

    @Override
    public String toString() {
        return "TravelRequestDto{" +
                "coachId=" + coachId +
                ", teamId=" + teamId +
                ", tournamentId=" + tournamentId +
                '}';
    }

    public static TravelRequestDtoBuilder builder() {
        return new TravelRequestDtoBuilder();
    }

    public static class TravelRequestDtoBuilder {
        private Long coachId;
        private Long teamId;
        private Long tournamentId;
        private Long transportId;
        private Long accommodationId;
        private List<Long> selectedMemberIds;

        public TravelRequestDtoBuilder coachId(Long coachId) { this.coachId = coachId; return this; }
        public TravelRequestDtoBuilder teamId(Long teamId) { this.teamId = teamId; return this; }
        public TravelRequestDtoBuilder tournamentId(Long tournamentId) { this.tournamentId = tournamentId; return this; }
        public TravelRequestDtoBuilder transportId(Long transportId) { this.transportId = transportId; return this; }
        public TravelRequestDtoBuilder accommodationId(Long accommodationId) { this.accommodationId = accommodationId; return this; }
        public TravelRequestDtoBuilder selectedMemberIds(List<Long> selectedMemberIds) { this.selectedMemberIds = selectedMemberIds; return this; }

        public TravelRequestDto build() {
            return new TravelRequestDto(coachId, teamId, tournamentId, transportId, accommodationId, selectedMemberIds);
        }
    }
}
