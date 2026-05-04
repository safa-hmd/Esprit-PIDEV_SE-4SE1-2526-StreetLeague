package com.example.streetleague.dto;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Entity.TournamentStatus;
import com.example.streetleague.Entity.TournamentType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

public class TournamentDto {
    private Long id;

    @NotBlank(message = "Tournament name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Sport type is required")
    private SportType sportType;

    @NotNull(message = "Tournament type is required")
    private TournamentType tournamentType;

    private TournamentStatus status;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Registration deadline is required")
    private LocalDate registrationDeadline;

    @Min(value = 2, message = "At least 2 participants are required")
    @Max(value = 256, message = "Cannot exceed 256 participants")
    private int maxParticipants;

    private Long fieldId;
    private String fieldName;      // pour affichage frontend (optionnel)
    private String fieldLocation;  // pour affichage frontend (optionnel)

    // city kept from LOGISTIQUE branch for compatibility
    private String city;

    @DecimalMin(value = "0.0", message = "Prize pool cannot be negative")
    private Double prizePool;

    private int registeredCount;

    public TournamentDto() {
    }

    public TournamentDto(Long id, String name, String description, SportType sportType, TournamentType tournamentType, TournamentStatus status, LocalDate startDate, LocalDate endDate, LocalDate registrationDeadline, int maxParticipants, Long fieldId, String fieldName, String fieldLocation, String city, Double prizePool, int registeredCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sportType = sportType;
        this.tournamentType = tournamentType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationDeadline = registrationDeadline;
        this.maxParticipants = maxParticipants;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.city = city;
        this.prizePool = prizePool;
        this.registeredCount = registeredCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SportType getSportType() { return sportType; }
    public void setSportType(SportType sportType) { this.sportType = sportType; }

    public TournamentType getTournamentType() { return tournamentType; }
    public void setTournamentType(TournamentType tournamentType) { this.tournamentType = tournamentType; }

    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDate registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldLocation() { return fieldLocation; }
    public void setFieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getPrizePool() { return prizePool; }
    public void setPrizePool(Double prizePool) { this.prizePool = prizePool; }

    public int getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentDto that = (TournamentDto) o;
        return maxParticipants == that.maxParticipants && registeredCount == that.registeredCount && Objects.equals(id, that.id) && Objects.equals(name, that.name) && sportType == that.sportType && tournamentType == that.tournamentType && status == that.status && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sportType, tournamentType, status, startDate, endDate, maxParticipants, registeredCount);
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sportType=" + sportType +
                ", tournamentType=" + tournamentType +
                ", status=" + status +
                '}';
    }

    public static TournamentDtoBuilder builder() {
        return new TournamentDtoBuilder();
    }

    public static class TournamentDtoBuilder {
        private Long id;
        private String name;
        private String description;
        private SportType sportType;
        private TournamentType tournamentType;
        private TournamentStatus status;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate registrationDeadline;
        private int maxParticipants;
        private Long fieldId;
        private String fieldName;
        private String fieldLocation;
        private String city;
        private Double prizePool;
        private int registeredCount;

        public TournamentDtoBuilder id(Long id) { this.id = id; return this; }
        public TournamentDtoBuilder name(String name) { this.name = name; return this; }
        public TournamentDtoBuilder description(String description) { this.description = description; return this; }
        public TournamentDtoBuilder sportType(SportType sportType) { this.sportType = sportType; return this; }
        public TournamentDtoBuilder tournamentType(TournamentType tournamentType) { this.tournamentType = tournamentType; return this; }
        public TournamentDtoBuilder status(TournamentStatus status) { this.status = status; return this; }
        public TournamentDtoBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public TournamentDtoBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public TournamentDtoBuilder registrationDeadline(LocalDate registrationDeadline) { this.registrationDeadline = registrationDeadline; return this; }
        public TournamentDtoBuilder maxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; return this; }
        public TournamentDtoBuilder fieldId(Long fieldId) { this.fieldId = fieldId; return this; }
        public TournamentDtoBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public TournamentDtoBuilder fieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; return this; }
        public TournamentDtoBuilder city(String city) { this.city = city; return this; }
        public TournamentDtoBuilder prizePool(Double prizePool) { this.prizePool = prizePool; return this; }
        public TournamentDtoBuilder registeredCount(int registeredCount) { this.registeredCount = registeredCount; return this; }

        public TournamentDto build() {
            return new TournamentDto(id, name, description, sportType, tournamentType, status, startDate, endDate, registrationDeadline, maxParticipants, fieldId, fieldName, fieldLocation, city, prizePool, registeredCount);
        }
    }
}
