package com.example.streetleague.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentType tournamentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate registrationDeadline;

    @Column(nullable = false)
    private int maxParticipants;

    private String location;
    
    private String city;

    private Double prizePool;

    // ---- Relations ----

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentRegistration> registrations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = true)
    private Field field;

    public Tournament() {
    }

    public Tournament(Long id, String name, String description, SportType sportType, TournamentType tournamentType, TournamentStatus status, LocalDate startDate, LocalDate endDate, LocalDate registrationDeadline, int maxParticipants, String location, String city, Double prizePool, List<TournamentRegistration> registrations, Field field) {
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
        this.location = location;
        this.city = city;
        this.prizePool = prizePool;
        this.registrations = registrations != null ? registrations : new ArrayList<>();
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return maxParticipants == that.maxParticipants && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && sportType == that.sportType && tournamentType == that.tournamentType && status == that.status && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(registrationDeadline, that.registrationDeadline) && Objects.equals(location, that.location) && Objects.equals(city, that.city) && Objects.equals(prizePool, that.prizePool) && Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, sportType, tournamentType, status, startDate, endDate, registrationDeadline, maxParticipants, location, city, prizePool, field);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sportType=" + sportType +
                ", tournamentType=" + tournamentType +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", registrationDeadline=" + registrationDeadline +
                ", maxParticipants=" + maxParticipants +
                ", location='" + location + '\'' +
                ", city='" + city + '\'' +
                ", prizePool=" + prizePool +
                ", field=" + field +
                '}';
    }

    public static TournamentBuilder builder() {
        return new TournamentBuilder();
    }

    public static class TournamentBuilder {
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
        private String location;
        private String city;
        private Double prizePool;
        private List<TournamentRegistration> registrations = new ArrayList<>();
        private Field field;

        public TournamentBuilder id(Long id) { this.id = id; return this; }
        public TournamentBuilder name(String name) { this.name = name; return this; }
        public TournamentBuilder description(String description) { this.description = description; return this; }
        public TournamentBuilder sportType(SportType sportType) { this.sportType = sportType; return this; }
        public TournamentBuilder tournamentType(TournamentType tournamentType) { this.tournamentType = tournamentType; return this; }
        public TournamentBuilder status(TournamentStatus status) { this.status = status; return this; }
        public TournamentBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public TournamentBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public TournamentBuilder registrationDeadline(LocalDate registrationDeadline) { this.registrationDeadline = registrationDeadline; return this; }
        public TournamentBuilder maxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; return this; }
        public TournamentBuilder location(String location) { this.location = location; return this; }
        public TournamentBuilder city(String city) { this.city = city; return this; }
        public TournamentBuilder prizePool(Double prizePool) { this.prizePool = prizePool; return this; }
        public TournamentBuilder registrations(List<TournamentRegistration> registrations) { this.registrations = registrations; return this; }
        public TournamentBuilder field(Field field) { this.field = field; return this; }

        public Tournament build() {
            return new Tournament(id, name, description, sportType, tournamentType, status, startDate, endDate, registrationDeadline, maxParticipants, location, city, prizePool, registrations, field);
        }
    }

// Ajouter ces getters explicites dans Tournament.java

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public SportType getSportType() { return this.sportType; }
    public void setSportType(SportType sportType) { this.sportType = sportType; }

    public TournamentType getTournamentType() { return this.tournamentType; }
    public void setTournamentType(TournamentType tournamentType) { this.tournamentType = tournamentType; }

    public TournamentStatus getStatus() { return this.status; }
    public void setStatus(TournamentStatus status) { this.status = status; }

    public LocalDate getStartDate() { return this.startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return this.endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getRegistrationDeadline() { return this.registrationDeadline; }
    public void setRegistrationDeadline(LocalDate d) { this.registrationDeadline = d; }

    public int getMaxParticipants() { return this.maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }

    public String getCity() { return this.city; }        // ← MANQUAIT
    public void setCity(String city) { this.city = city; }

    public Double getPrizePool() { return this.prizePool; }
    public void setPrizePool(Double prizePool) { this.prizePool = prizePool; }

    public List<TournamentRegistration> getRegistrations() { return this.registrations; }
    public void setRegistrations(List<TournamentRegistration> r) { this.registrations = r; }

    public Field getField() { return this.field; }
    public void setField(Field field) { this.field = field; }
}
