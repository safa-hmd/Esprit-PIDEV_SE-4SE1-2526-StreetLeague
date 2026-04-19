package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Entity.Training;
import com.example.streetleague.Entity.TrainingStatus;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TrainingRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.ScheduleEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor

public class ScheduleService {

    private final TrainingRepository trainingRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;  // à déclarer en @Bean

    private static final String FLASK_URL = "http://localhost:5001/predict";

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<ScheduleEventDto> getSchedule(Long userId, LocalDateTime from, LocalDateTime to) {
        entityManager.clear();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ScheduleEventDto> events = new ArrayList<>();
        events.addAll(buildTrainingEvents(user, from, to));

        if (user.getRole() != Role.COACH) {
            events.addAll(buildMatchEvents(user, from, to));
        }

        events.sort(Comparator.comparing(ScheduleEventDto::getStartTime));
        detectConflicts(events);
        enrichWithAiScores(events, user); // ← appel Flask
        return events;
    }

    // ── Trainings ────────────────────────────────────────────────
    private List<ScheduleEventDto> buildTrainingEvents(User user, LocalDateTime from, LocalDateTime to) {
        List<Training> trainings = user.getRole() == Role.COACH
                ? trainingRepository.findByCoachAndTrainingDateBetween(user, from, to)
                : trainingRepository.findByParticipantAndDateBetween(user, from, to);

        return trainings.stream()
                .filter(t -> t.getStatus() != TrainingStatus.CANCELLED)
                .map(this::toTrainingDto)
                .collect(Collectors.toList());
    }

    // ── Matchs ───────────────────────────────────────────────────
    private List<ScheduleEventDto> buildMatchEvents(User user, LocalDateTime from, LocalDateTime to) {
        if (user.getTeams() == null || user.getTeams().isEmpty()) return List.of();

        return user.getTeams().stream()
                .flatMap(team -> matchRepository.findByTeamAndDateBetween(team, from, to).stream())
                .filter(m -> m.getStatus() != MatchStatus.CANCELLED
                        && m.getStatus() != MatchStatus.REJECTED)
                .distinct()
                .map(m -> toMatchDto(m, user))
                .collect(Collectors.toList());
    }

    // ── Détection conflits horaires ───────────────────────────────
    private void detectConflicts(List<ScheduleEventDto> events) {
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                ScheduleEventDto a = events.get(i), b = events.get(j);
                if (a.getStartTime().isBefore(b.getEndTime())
                        && b.getStartTime().isBefore(a.getEndTime())) {
                    a.setHasConflict(true); b.setHasConflict(true);
                    a.setConflictReason("⚠️ Conflit avec : " + b.getTitle());
                    b.setConflictReason("⚠️ Conflit avec : " + a.getTitle());
                    a.setColor("#e67e22"); b.setColor("#e67e22");
                }
            }
        }
    }

    // ── Appel Flask pour enrichir avec le score AI ────────────────
    private void enrichWithAiScores(List<ScheduleEventDto> events, User user) {
        int teamSize = user.getTeams() != null
                ? user.getTeams().stream()
                .mapToInt(t -> t.getPlayers() != null ? t.getPlayers().size() : 0)
                .max().orElse(10)
                : 10;

        long matchesWeek   = events.stream().filter(e -> "MATCH".equals(e.getType())).count();
        long trainingsWeek = events.stream().filter(e -> "TRAINING".equals(e.getType())).count();

        for (ScheduleEventDto ev : events) {
            try {
                Map<String, Object> payload = new HashMap<>();
                payload.put("elo",            user.getTeams() != null && !user.getTeams().isEmpty()
                        ? user.getTeams().get(0).getEloScore() : 1000);
                payload.put("playerCount",    teamSize);
                payload.put("level",          2);
                payload.put("matchesWeek",    matchesWeek);
                payload.put("trainingsWeek",  trainingsWeek);
                payload.put("hour",           ev.getStartTime().getHour());
                payload.put("day",            ev.getStartTime().getDayOfWeek().getValue() - 1);
                payload.put("duration",       90);
                payload.put("fieldAvailable", 1);
                payload.put("fieldPressure",  0.4);
                payload.put("fieldCapacity",  teamSize + 5);
                payload.put("winRatio",       0.5);

                Map<?, ?> response = restTemplate.postForObject(FLASK_URL, payload, Map.class);
                if (response != null) {
                    ev.setAiScore(((Number) response.get("score")).doubleValue());
                    ev.setRecommendation((String) response.get("recommendation"));
                }
            } catch (Exception ignored) {
                // Flask pas encore lancé → pas de crash
            }
        }
    }

    // ── Builders DTO ─────────────────────────────────────────────
    private ScheduleEventDto toTrainingDto(Training t) {
        int dur = t.getDurationInMinutes() != null ? t.getDurationInMinutes() : 90;
        return ScheduleEventDto.builder()
                .id(t.getIdTraining()).type("TRAINING")
                .title("🏋️ " + (t.getTitle() != null ? t.getTitle() : "Training"))
                .description(t.getDescription())
                .startTime(t.getTrainingDate())
                .endTime(t.getTrainingDate().plusMinutes(dur))
                .location(t.getLocation())
                .status(t.getStatus().name())
                .teamName(t.getTeam().getName())
                .coachName(t.getCoach() != null ? t.getCoach().getFullName() : "Non assigné")
                .color("#3498db").build();
    }

    private ScheduleEventDto toMatchDto(Match m, User user) {
        boolean isTeamA = user.getTeams() != null && user.getTeams().contains(m.getTeamA());
        String opponent = isTeamA ? m.getTeamB().getName() : m.getTeamA().getName();
        String myTeam   = isTeamA ? m.getTeamA().getName() : m.getTeamB().getName();
        return ScheduleEventDto.builder()
                .id(m.getIdMatch()).type("MATCH")
                .title("⚽ vs " + opponent)
                .description(myTeam + " affronte " + opponent)
                .startTime(m.getMatchDate())
                .endTime(m.getMatchDate().plusMinutes(90))
                .location(m.getLocation())
                .status(m.getStatus().name())
                .teamName(myTeam).opponentTeamName(opponent)
                .scoreTeamA(m.getScoreTeamA()).scoreTeamB(m.getScoreTeamB())
                .color("#e74c3c").build();
    }
}
