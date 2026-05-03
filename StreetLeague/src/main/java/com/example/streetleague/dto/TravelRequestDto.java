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
public class TravelRequestDto {
    private Long coachId;
    private Long teamId;
    private Long tournamentId;
    private Long transportId; // Nullable if same city
    private Long accommodationId;
    private List<Long> selectedMemberIds;

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====
    public Long getCoachId() { return this.coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getTeamId() { return this.teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public Long getTournamentId() { return this.tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getTransportId() { return this.transportId; }
    public void setTransportId(Long transportId) { this.transportId = transportId; }

    public Long getAccommodationId() { return this.accommodationId; }
    public void setAccommodationId(Long accommodationId) { this.accommodationId = accommodationId; }

    public List<Long> getSelectedMemberIds() { return this.selectedMemberIds; }
    public void setSelectedMemberIds(List<Long> selectedMemberIds) { this.selectedMemberIds = selectedMemberIds; }

    // ===== STATIC BUILDER HELPER =====
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

        public TravelRequestDtoBuilder coachId(Long coachId) {
            this.coachId = coachId;
            return this;
        }

        public TravelRequestDtoBuilder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public TravelRequestDtoBuilder tournamentId(Long tournamentId) {
            this.tournamentId = tournamentId;
            return this;
        }

        public TravelRequestDtoBuilder transportId(Long transportId) {
            this.transportId = transportId;
            return this;
        }

        public TravelRequestDtoBuilder accommodationId(Long accommodationId) {
            this.accommodationId = accommodationId;
            return this;
        }

        public TravelRequestDtoBuilder selectedMemberIds(List<Long> selectedMemberIds) {
            this.selectedMemberIds = selectedMemberIds;
            return this;
        }

        public TravelRequestDto build() {
            TravelRequestDto dto = new TravelRequestDto();
            dto.setCoachId(coachId);
            dto.setTeamId(teamId);
            dto.setTournamentId(tournamentId);
            dto.setTransportId(transportId);
            dto.setAccommodationId(accommodationId);
            dto.setSelectedMemberIds(selectedMemberIds);
            return dto;
        }
    }
}
