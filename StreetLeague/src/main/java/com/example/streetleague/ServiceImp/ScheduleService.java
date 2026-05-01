//package com.example.streetleague.ServiceImp;
//
//import com.example.streetleague.Entity.*;
//import com.example.streetleague.Repository.*;
//import com.example.streetleague.domain.Role;
//import com.example.streetleague.domain.User;
//import com.example.streetleague.dto.FieldRecommendationDto;
//import com.example.streetleague.dto.ScheduleEventDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//import org.springframework.transaction.annotation.Transactional;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ScheduleService {
//
//    private final TrainingRepository              trainingRepository;
//    private final MatchRepository                 matchRepository;
//    private final UserRepository                  userRepository;
//    private final TournamentRegistrationRepository tournamentRegistrationRepository;
//    private final RecommendationService            recommendationService;
//    private final RestTemplate restTemplate;
//
//    private static final String FLASK_URL = "http://localhost:5001/predict";
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    // ─────────────────────────────────────────────────────────────────────
//    // POINT D'ENTRÉE PRINCIPAL
//    // ─────────────────────────────────────────────────────────────────────
//    @Transactional
//    public List<ScheduleEventDto> getSchedule(Long userId, LocalDateTime from, LocalDateTime to) {
//        return getScheduleWithGps(userId, from, to, null, null);
//    }
//
//    @Transactional
//    public List<ScheduleEventDto> getScheduleWithGps(
//            Long userId, LocalDateTime from, LocalDateTime to,
//            Double userLat, Double userLng) {
//
//        entityManager.clear();
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<ScheduleEventDto> events = new ArrayList<>();
//        events.addAll(buildTrainingEvents(user, from, to));
//
//        if (user.getRole() != Role.COACH) {
//            events.addAll(buildMatchEvents(user, from, to));
//            events.addAll(buildTournamentEvents(user, from, to));
//        }
//
//        events.sort(Comparator.comparing(ScheduleEventDto::getStartTime));
//        detectConflicts(events);
//        enrichWithAiScores(events, user);
//
//        // Enrichir chaque événement avec le terrain IA le plus proche
//        if (userLat != null && userLng != null) {
//            enrichWithRecommendedField(events, userId, userLat, userLng);
//        }
//
//        return events;
//    }
//
//    // ─────────────────────────────────────────────────────────────────────
//    // TRAININGS
//    // ─────────────────────────────────────────────────────────────────────
//    private List<ScheduleEventDto> buildTrainingEvents(User user, LocalDateTime from, LocalDateTime to) {
//        List<Training> trainings = user.getRole() == Role.COACH
//                ? trainingRepository.findByCoachAndTrainingDateBetween(user, from, to)
//                : trainingRepository.findByParticipantAndDateBetween(user, from, to);
//
//        return trainings.stream()
//                .filter(t -> t.getStatus() != TrainingStatus.CANCELLED)
//                .map(this::toTrainingDto)
//                .collect(Collectors.toList());
//    }
//
//    // ─────────────────────────────────────────────────────────────────────
//    // MATCHES
//    // ─────────────────────────────────────────────────────────────────────
//    private List<ScheduleEventDto> buildMatchEvents(User user, LocalDateTime from, LocalDateTime to) {
//        if (user.getTeams() == null || user.getTeams().isEmpty()) return List.of();
//
//        return user.getTeams().stream()
//                .flatMap(team -> matchRepository.findByTeamAndDateBetween(team, from, to).stream())
//                .filter(m -> m.getStatus() != MatchStatus.CANCELLED
//                          && m.getStatus() != MatchStatus.REJECTED)
//                .distinct()
//                .map(m -> toMatchDto(m, user))
//                .collect(Collectors.toList());
//    }
//
//    // ─────────────────────────────────────────────────────────────────────
//    // TOURNOIS  (inscriptions du joueur ou de son équipe)
//    // ─────────────────────────────────────────────────────────────────────
//    private List<ScheduleEventDto> buildTournamentEvents(User user, LocalDateTime from, LocalDateTime to) {
//        Set<Tournament> tournaments = new HashSet<>();
//
//        // Registrations directes en tant que joueur
//        List<TournamentRegistration> playerRegs =
//                tournamentRegistrationRepository.findByPlayerIdUser(user.getIdUser());
//        playerRegs.stream()
//                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED
//                        || r.getStatus() == RegistrationStatus.PENDING)
//                .map(TournamentRegistration::getTournament)
//                .filter(Objects::nonNull)
//                .forEach(tournaments::add);
//
//        // Registrations via les équipes du joueur
//        if (user.getTeams() != null) {
//            user.getTeams().forEach(team -> {
//                List<TournamentRegistration> teamRegs =
//                        tournamentRegistrationRepository.findByTeamIdTeam(team.getIdTeam());
//                teamRegs.stream()
//                        .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED
//                                || r.getStatus() == RegistrationStatus.PENDING)
//                        .map(TournamentRegistration::getTournament)
//                        .filter(Objects::nonNull)
//                        .forEach(tournaments::add);
//            });
//        }
//
//        LocalDate fromDate = from.toLocalDate();
//        LocalDate toDate   = to.toLocalDate();
//
//        return tournaments.stream()
//                .filter(t -> t.getStatus() != TournamentStatus.CANCELLED)
//                .filter(t -> t.getStartDate() != null
//                          && !t.getStartDate().isAfter(toDate)
//                          && (t.getEndDate() == null || !t.getEndDate().isBefore(fromDate)))
//                .map(this::toTournamentDto)
//                .collect(Collectors.toList());
//    }
//
//    // ─────────────────────────────────────────────────────────────────────
//    // CONFLITS HORAIRES
//    // ─────────────────────────────────────────────────────────────────────
//    private void detectConflicts(List<ScheduleEventDto> events) {
//        for (int i = 0; i < events.size(); i++) {
//            for (int j = i + 1; j < events.size(); j++) {
//                ScheduleEventDto a = events.get(i), b = events.get(j);
//                if (a.getStartTime().isBefore(b.getEndTime())
//                        && b.getStartTime().isBefore(a.getEndTime())) {
//                    a.setHasConflict(true); b.setHasConflict(true);
//                    a.setConflictReason("⚠️ Conflit avec : " + b.getTitle());
//                    b.setConflictReason("⚠️ Conflit avec : " + a.getTitle());
//                    // Ne pas écraser la couleur si conflit sur tournoi
//                    if (!"TOURNAMENT".equals(a.getType())) a.setColor("#e67e22");
//                    if (!"TOURNAMENT".equals(b.getType())) b.setColor("#e67e22");
//                }
//            }
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────
//    // AI SCORES via Flask /predict
//    // ─────────────────────────────────────────────────────────────────────
//    private void enrichWithAiScores(List<ScheduleEventDto> events, User user) {
//        int teamSize = user.getTeams() != null
//                ? user.getTeams().stream()
//                        .mapToInt(t -> t.getPlayers() != null ? t.getPlayers().size() : 0)
//                        .max().orElse(10)
//                : 10;
//
//        long matchesWeek   = events.stream().filter(e -> "MATCH".equals(e.getType())).count();
//        long trainingsWeek = events.stream().filter(e -> "TRAINING".equals(e.getType())).count();
//        boolean hasTournament = events.stream().anyMatch(e -> "TOURNAMENT".equals(e.getType()));
//
//        for (ScheduleEventDto ev : events) {
//            try {
//                Map<String, Object> payload = new HashMap<>();
//                payload.put("elo",            user.getTeams() != null && !user.getTeams().isEmpty()
//                        ? user.getTeams().get(0).getEloScore() : 1000);
//                payload.put("playerCount",    teamSize);
//                payload.put("level",          2);
//                payload.put("matchesWeek",    matchesWeek);
//                payload.put("trainingsWeek",  trainingsWeek);
//                payload.put("hour",           ev.getStartTime().getHour());
//                payload.put("day",            ev.getStartTime().getDayOfWeek().getValue() - 1);
//                payload.put("duration",       90);
//                payload.put("fieldAvailable", 1);
//                payload.put("fieldPressure",  0.4);
//                payload.put("fieldCapacity",  teamSize + 5);
//                payload.put("hasTournament",  hasTournament ? 1 : 0);
//                payload.put("tournamentPriority", hasTournament ? 2 : 0);
//
//                Map<?, ?> response = restTemplate.postForObject(FLASK_URL, payload, Map.class);
//                if (response != null) {
//                    ev.setAiScore(((Number) response.get("score")).doubleValue());
//                    ev.setRecommendation((String) response.get("recommendation"));
//                }
//            } catch (Exception ignored) {
//                // Flask pas encore lancé → pas de crash
//            }
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────
//    // ENRICHISSEMENT TERRAIN IA (appel recommendFields pour tout le batch)
//    // ─────────────────────────────────────────────────────────────────────
//    private void enrichWithRecommendedField(
//            List<ScheduleEventDto> events, Long userId, double userLat, double userLng) {
//        try {
//            List<FieldRecommendationDto> fields =
//                    recommendationService.recommendFields(userId, userLat, userLng);
//            if (fields == null || fields.isEmpty()) return;
//
//            // Prend le meilleur terrain (#1) et l'accroche à chaque event
//            FieldRecommendationDto best = fields.get(0);
//            for (ScheduleEventDto ev : events) {
//                ev.setRecommendedFieldId(best.getFieldId());
//                ev.setRecommendedFieldName(best.getFieldName());
//                ev.setRecommendedFieldLocation(best.getFieldLocation());
//                ev.setRecommendedFieldLat(best.getFieldLat());
//                ev.setRecommendedFieldLng(best.getFieldLng());
//                ev.setRecommendedFieldDist(best.getDistanceKm());
//                ev.setRecommendedFieldScore(best.getAiScore());
//                ev.setRecommendedFieldRec(best.getRecommendation());
//            }
//        } catch (Exception e) {
//            log.warn("enrichWithRecommendedField: {}", e.getMessage());
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────
//    // BUILDERS DTO
//    // ─────────────────────────────────────────────────────────────────────
//    private ScheduleEventDto toTrainingDto(Training t) {
//        int dur = t.getDurationInMinutes() != null ? t.getDurationInMinutes() : 90;
//        return ScheduleEventDto.builder()
//                .id(t.getIdTraining()).type("TRAINING")
//                .title("🏋️ " + (t.getTitle() != null ? t.getTitle() : "Training"))
//                .description(t.getDescription())
//                .startTime(t.getTrainingDate())
//                .endTime(t.getTrainingDate().plusMinutes(dur))
//                .location(t.getLocation())
//                .status(t.getStatus().name())
//                .teamName(t.getTeam() != null ? t.getTeam().getName() : "")
//                .coachName(t.getCoach() != null ? t.getCoach().getFullName() : "Non assigné")
//                .color("#3498db").build();
//    }
//
//    private ScheduleEventDto toMatchDto(Match m, User user) {
//        boolean isTeamA = user.getTeams() != null && user.getTeams().contains(m.getTeamA());
//        String opponent = isTeamA ? m.getTeamB().getName() : m.getTeamA().getName();
//        String myTeam   = isTeamA ? m.getTeamA().getName() : m.getTeamB().getName();
//        return ScheduleEventDto.builder()
//                .id(m.getIdMatch()).type("MATCH")
//                .title("⚽ vs " + opponent)
//                .description(myTeam + " affronte " + opponent)
//                .startTime(m.getMatchDate())
//                .endTime(m.getMatchDate().plusMinutes(90))
//                .location(m.getLocation())
//                .status(m.getStatus().name())
//                .teamName(myTeam).opponentTeamName(opponent)
//                .scoreTeamA(m.getScoreTeamA()).scoreTeamB(m.getScoreTeamB())
//                .color("#e74c3c").build();
//    }
//
//    private ScheduleEventDto toTournamentDto(Tournament t) {
//        LocalDateTime start = t.getStartDate().atTime(9, 0);
//        LocalDateTime end   = t.getEndDate() != null
//                ? t.getEndDate().atTime(21, 0)
//                : start.plusHours(8);
//        return ScheduleEventDto.builder()
//                .id(t.getId()).type("TOURNAMENT")
//                .title("🏆 " + t.getName())
//                .description(t.getDescription())
//                .startTime(start)
//                .endTime(end)
//                .location(t.getLocation())
//                .status(t.getStatus().name())
//                .tournamentName(t.getName())
//                .tournamentType(t.getTournamentType() != null ? t.getTournamentType().name() : "")
//                .prizePool(t.getPrizePool())
//                .sportType(t.getSportType() != null ? t.getSportType().name() : "")
//                .tournamentEndDate(t.getEndDate())
//                .color("#8e44ad").build();
//    }
//}
