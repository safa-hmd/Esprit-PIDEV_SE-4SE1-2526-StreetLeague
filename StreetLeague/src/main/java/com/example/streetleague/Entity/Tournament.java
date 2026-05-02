package com.example.streetleague.Entity;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Entity.TournamentRegistration;
import com.example.streetleague.Entity.TournamentStatus;
import com.example.streetleague.Entity.TournamentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments") // from main
//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
    // city kept for Logistique branch
    private String city;

    private Double prizePool;

    // ---- Relations ----

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentRegistration> registrations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = true)
    private Field field;

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
