package com.example.streetleague.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduleEventDto {
    private Long id;
    private String type;           // "TRAINING" | "MATCH" | "TOURNAMENT"
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String status;
    private String teamName;
    private String coachName;
    private String opponentTeamName;
    private Integer scoreTeamA;
    private Integer scoreTeamB;
    private boolean hasConflict;
    private String conflictReason;
    private String color;
    private Double aiScore;          // score du modèle Flask (0-1)
    private String recommendation;   // EXCELLENT / ACCEPTABLE / DÉCONSEILLÉ
    private String    tournamentName;
    private String    tournamentType;
    private Double    prizePool;
    private String    sportType;
    private LocalDate tournamentEndDate;
    private Long   recommendedFieldId;
    private String recommendedFieldName;
    private String recommendedFieldLocation;
    private Double recommendedFieldLat;
    private Double recommendedFieldLng;
    private Double recommendedFieldDist;
    private Double recommendedFieldScore;
    private String recommendedFieldRec;

    public ScheduleEventDto() {
    }

    public ScheduleEventDto(Long id, String type, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location, String status, String teamName, String coachName, String opponentTeamName, Integer scoreTeamA, Integer scoreTeamB, boolean hasConflict, String conflictReason, String color, Double aiScore, String recommendation, String tournamentName, String tournamentType, Double prizePool, String sportType, LocalDate tournamentEndDate, Long recommendedFieldId, String recommendedFieldName, String recommendedFieldLocation, Double recommendedFieldLat, Double recommendedFieldLng, Double recommendedFieldDist, Double recommendedFieldScore, String recommendedFieldRec) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.status = status;
        this.teamName = teamName;
        this.coachName = coachName;
        this.opponentTeamName = opponentTeamName;
        this.scoreTeamA = scoreTeamA;
        this.scoreTeamB = scoreTeamB;
        this.hasConflict = hasConflict;
        this.conflictReason = conflictReason;
        this.color = color;
        this.aiScore = aiScore;
        this.recommendation = recommendation;
        this.tournamentName = tournamentName;
        this.tournamentType = tournamentType;
        this.prizePool = prizePool;
        this.sportType = sportType;
        this.tournamentEndDate = tournamentEndDate;
        this.recommendedFieldId = recommendedFieldId;
        this.recommendedFieldName = recommendedFieldName;
        this.recommendedFieldLocation = recommendedFieldLocation;
        this.recommendedFieldLat = recommendedFieldLat;
        this.recommendedFieldLng = recommendedFieldLng;
        this.recommendedFieldDist = recommendedFieldDist;
        this.recommendedFieldScore = recommendedFieldScore;
        this.recommendedFieldRec = recommendedFieldRec;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getOpponentTeamName() { return opponentTeamName; }
    public void setOpponentTeamName(String opponentTeamName) { this.opponentTeamName = opponentTeamName; }

    public Integer getScoreTeamA() { return scoreTeamA; }
    public void setScoreTeamA(Integer scoreTeamA) { this.scoreTeamA = scoreTeamA; }

    public Integer getScoreTeamB() { return scoreTeamB; }
    public void setScoreTeamB(Integer scoreTeamB) { this.scoreTeamB = scoreTeamB; }

    public boolean isHasConflict() { return hasConflict; }
    public void setHasConflict(boolean hasConflict) { this.hasConflict = hasConflict; }

    public String getConflictReason() { return conflictReason; }
    public void setConflictReason(String conflictReason) { this.conflictReason = conflictReason; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Double getAiScore() { return aiScore; }
    public void setAiScore(Double aiScore) { this.aiScore = aiScore; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getTournamentType() { return tournamentType; }
    public void setTournamentType(String tournamentType) { this.tournamentType = tournamentType; }

    public Double getPrizePool() { return prizePool; }
    public void setPrizePool(Double prizePool) { this.prizePool = prizePool; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public LocalDate getTournamentEndDate() { return tournamentEndDate; }
    public void setTournamentEndDate(LocalDate tournamentEndDate) { this.tournamentEndDate = tournamentEndDate; }

    public Long getRecommendedFieldId() { return recommendedFieldId; }
    public void setRecommendedFieldId(Long recommendedFieldId) { this.recommendedFieldId = recommendedFieldId; }

    public String getRecommendedFieldName() { return recommendedFieldName; }
    public void setRecommendedFieldName(String recommendedFieldName) { this.recommendedFieldName = recommendedFieldName; }

    public String getRecommendedFieldLocation() { return recommendedFieldLocation; }
    public void setRecommendedFieldLocation(String recommendedFieldLocation) { this.recommendedFieldLocation = recommendedFieldLocation; }

    public Double getRecommendedFieldLat() { return recommendedFieldLat; }
    public void setRecommendedFieldLat(Double recommendedFieldLat) { this.recommendedFieldLat = recommendedFieldLat; }

    public Double getRecommendedFieldLng() { return recommendedFieldLng; }
    public void setRecommendedFieldLng(Double recommendedFieldLng) { this.recommendedFieldLng = recommendedFieldLng; }

    public Double getRecommendedFieldDist() { return recommendedFieldDist; }
    public void setRecommendedFieldDist(Double recommendedFieldDist) { this.recommendedFieldDist = recommendedFieldDist; }

    public Double getRecommendedFieldScore() { return recommendedFieldScore; }
    public void setRecommendedFieldScore(Double recommendedFieldScore) { this.recommendedFieldScore = recommendedFieldScore; }

    public String getRecommendedFieldRec() { return recommendedFieldRec; }
    public void setRecommendedFieldRec(String recommendedFieldRec) { this.recommendedFieldRec = recommendedFieldRec; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleEventDto that = (ScheduleEventDto) o;
        return hasConflict == that.hasConflict && Objects.equals(id, that.id) && Objects.equals(type, that.type) && Objects.equals(title, that.title) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, startTime, endTime, hasConflict);
    }

    @Override
    public String toString() {
        return "ScheduleEventDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static ScheduleEventDtoBuilder builder() {
        return new ScheduleEventDtoBuilder();
    }

    public static class ScheduleEventDtoBuilder {
        private Long id;
        private String type;
        private String title;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location;
        private String status;
        private String teamName;
        private String coachName;
        private String opponentTeamName;
        private Integer scoreTeamA;
        private Integer scoreTeamB;
        private boolean hasConflict;
        private String conflictReason;
        private String color;
        private Double aiScore;
        private String recommendation;
        private String    tournamentName;
        private String    tournamentType;
        private Double    prizePool;
        private String    sportType;
        private LocalDate tournamentEndDate;
        private Long   recommendedFieldId;
        private String recommendedFieldName;
        private String recommendedFieldLocation;
        private Double recommendedFieldLat;
        private Double recommendedFieldLng;
        private Double recommendedFieldDist;
        private Double recommendedFieldScore;
        private String recommendedFieldRec;

        public ScheduleEventDtoBuilder id(Long id) { this.id = id; return this; }
        public ScheduleEventDtoBuilder type(String type) { this.type = type; return this; }
        public ScheduleEventDtoBuilder title(String title) { this.title = title; return this; }
        public ScheduleEventDtoBuilder description(String description) { this.description = description; return this; }
        public ScheduleEventDtoBuilder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public ScheduleEventDtoBuilder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public ScheduleEventDtoBuilder location(String location) { this.location = location; return this; }
        public ScheduleEventDtoBuilder status(String status) { this.status = status; return this; }
        public ScheduleEventDtoBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public ScheduleEventDtoBuilder coachName(String coachName) { this.coachName = coachName; return this; }
        public ScheduleEventDtoBuilder opponentTeamName(String opponentTeamName) { this.opponentTeamName = opponentTeamName; return this; }
        public ScheduleEventDtoBuilder scoreTeamA(Integer scoreTeamA) { this.scoreTeamA = scoreTeamA; return this; }
        public ScheduleEventDtoBuilder scoreTeamB(Integer scoreTeamB) { this.scoreTeamB = scoreTeamB; return this; }
        public ScheduleEventDtoBuilder hasConflict(boolean hasConflict) { this.hasConflict = hasConflict; return this; }
        public ScheduleEventDtoBuilder conflictReason(String conflictReason) { this.conflictReason = conflictReason; return this; }
        public ScheduleEventDtoBuilder color(String color) { this.color = color; return this; }
        public ScheduleEventDtoBuilder aiScore(Double aiScore) { this.aiScore = aiScore; return this; }
        public ScheduleEventDtoBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public ScheduleEventDtoBuilder tournamentName(String tournamentName) { this.tournamentName = tournamentName; return this; }
        public ScheduleEventDtoBuilder tournamentType(String tournamentType) { this.tournamentType = tournamentType; return this; }
        public ScheduleEventDtoBuilder prizePool(Double prizePool) { this.prizePool = prizePool; return this; }
        public ScheduleEventDtoBuilder sportType(String sportType) { this.sportType = sportType; return this; }
        public ScheduleEventDtoBuilder tournamentEndDate(LocalDate tournamentEndDate) { this.tournamentEndDate = tournamentEndDate; return this; }
        public ScheduleEventDtoBuilder recommendedFieldId(Long recommendedFieldId) { this.recommendedFieldId = recommendedFieldId; return this; }
        public ScheduleEventDtoBuilder recommendedFieldName(String recommendedFieldName) { this.recommendedFieldName = recommendedFieldName; return this; }
        public ScheduleEventDtoBuilder recommendedFieldLocation(String recommendedFieldLocation) { this.recommendedFieldLocation = recommendedFieldLocation; return this; }
        public ScheduleEventDtoBuilder recommendedFieldLat(Double recommendedFieldLat) { this.recommendedFieldLat = recommendedFieldLat; return this; }
        public ScheduleEventDtoBuilder recommendedFieldLng(Double recommendedFieldLng) { this.recommendedFieldLng = recommendedFieldLng; return this; }
        public ScheduleEventDtoBuilder recommendedFieldDist(Double recommendedFieldDist) { this.recommendedFieldDist = recommendedFieldDist; return this; }
        public ScheduleEventDtoBuilder recommendedFieldScore(Double recommendedFieldScore) { this.recommendedFieldScore = recommendedFieldScore; return this; }
        public ScheduleEventDtoBuilder recommendedFieldRec(String recommendedFieldRec) { this.recommendedFieldRec = recommendedFieldRec; return this; }

        public ScheduleEventDto build() {
            return new ScheduleEventDto(id, type, title, description, startTime, endTime, location, status, teamName, coachName, opponentTeamName, scoreTeamA, scoreTeamB, hasConflict, conflictReason, color, aiScore, recommendation, tournamentName, tournamentType, prizePool, sportType, tournamentEndDate, recommendedFieldId, recommendedFieldName, recommendedFieldLocation, recommendedFieldLat, recommendedFieldLng, recommendedFieldDist, recommendedFieldScore, recommendedFieldRec);
        }
    }
}