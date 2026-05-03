package com.example.streetleague.Controller;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TrainingRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Value("${flask.api.url}")
    private String flaskUrl;

    private final RestTemplate      restTemplate;
    private final MatchRepository   matchRepository;
    private final TrainingRepository trainingRepository;
    private final FieldRepository   fieldRepository;
    private final UserRepository    userRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ════════════════════════════════════════════════════════════════
    // GET /api/schedule/{userId}/week?lat=36.8&lng=10.1
    // ════════════════════════════════════════════════════════════════
    @GetMapping("/{userId}/week")
    public ResponseEntity<Map<String, Object>> week(
            @PathVariable Long userId,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {

        LocalDateTime from = LocalDateTime.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
        LocalDateTime to = from.plusDays(7);

        return getSchedule(userId, from, to, lat, lng);
    }

    // ════════════════════════════════════════════════════════════════
    // GET /api/schedule/{userId}/month?lat=36.8&lng=10.1
    // ════════════════════════════════════════════════════════════════
    @GetMapping("/{userId}/month")
    public ResponseEntity<Map<String, Object>> month(
            @PathVariable Long userId,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {

        LocalDateTime from = LocalDateTime.now()
                .withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime to = LocalDateTime.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59);

        return getSchedule(userId, from, to, lat, lng);
    }

    // ════════════════════════════════════════════════════════════════
    // CORE LOGIC
    // ════════════════════════════════════════════════════════════════
    private ResponseEntity<Map<String, Object>> getSchedule(
            Long userId, LocalDateTime from, LocalDateTime to,
            Double userLat, Double userLng) {

        // 1. Load real events from DB
        List<Map<String, Object>> events = getEventsFromDb(userId, from, to);

        // 2. Load all fields from DB for AI scoring
        List<Map<String, Object>> allFields = getAllFieldsFromDb();

        // 3. Detect conflicts (overlapping events)
        detectConflicts(events);

        // 4. Enrich each event with Flask AI field recommendation
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Map<String, Object> event : events) {
            Double eventLat = (Double) event.remove("fieldLat");
            Double eventLng = (Double) event.remove("fieldLng");

            if (!allFields.isEmpty()) {
                // Use event coordinates if available, fallback to user GPS, fallback to null
                Double refLat = (eventLat != null) ? eventLat : userLat;
                Double refLng = (eventLng != null) ? eventLng : userLng;

                Map<String, Object> body = new HashMap<>();
                body.put("fields", allFields);
                if (refLat != null && refLng != null) {
                    body.put("eventLat", refLat);
                    body.put("eventLng", refLng);
                    body.put("userLat", userLat);
                    body.put("userLng", userLng);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

                try {
                    // Cast response body safely
                    ResponseEntity<List> resp = restTemplate.postForEntity(
                            flaskUrl + "/api/recommend/fields", req, List.class);

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> recs = (List<Map<String, Object>>) resp.getBody();

                    if (recs != null && !recs.isEmpty()) {
                        Map<String, Object> top = recs.get(0);
                        // Ajoute les recommandations à l'événement
                        event.put("recommendedFieldId", top.get("fieldId"));
                        event.put("recommendedFieldName", top.get("fieldName"));
                        event.put("recommendedFieldLocation", top.get("fieldLocation"));
                        event.put("recommendedFieldLat", refLat);
                        event.put("recommendedFieldLng", refLng);
                        event.put("recommendedFieldScore", top.get("aiScore"));
                        event.put("recommendedFieldRec", top.get("recommendation"));
                        event.put("recommendedFieldDist", top.get("distanceKm"));

                        // Met à jour le score AI si pas déjà défini
                        if (event.get("aiScore") == null) {
                            event.put("aiScore", top.get("aiScore"));
                            event.put("recommendation", top.get("recommendation"));
                        }
                    }
                } catch (Exception e) {
                    // Si Flask est indisponible, on garde le score existant
                    event.put("flaskWarning", "IA indisponible");
                }
            }
            enriched.add(event);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("events", enriched);
        result.put("topFields", allFields.subList(0, Math.min(3, allFields.size())));
        return ResponseEntity.ok(result);
    }

    // ════════════════════════════════════════════════════════════════
    // LOAD REAL EVENTS FROM DB
    // ════════════════════════════════════════════════════════════════
    private List<Map<String, Object>> getEventsFromDb(
            Long userId, LocalDateTime from, LocalDateTime to) {

        List<Map<String, Object>> events = new ArrayList<>();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return events;

        // ── MATCHES ──────────────────────────────────────────────────
        try {
            List<Match> matches = matchRepository.findAll().stream()
                    .filter(m -> m.getMatchDate() != null
                            && !m.getMatchDate().isBefore(from)
                            && !m.getMatchDate().isAfter(to))
                    .filter(m -> isUserInMatch(m, user))
                    .collect(Collectors.toList());

            for (Match m : matches) {
                Map<String, Object> ev = new HashMap<>();
                ev.put("id",               m.getIdMatch());
                ev.put("type",             "MATCH");
                ev.put("title",            buildMatchTitle(m));
                ev.put("startTime",        m.getMatchDate().format(FMT));
                ev.put("endTime",          m.getMatchDate().plusHours(2).format(FMT));
                ev.put("location",         m.getLocation() != null ? m.getLocation() : "");
                ev.put("status",           m.getStatus() != null ? m.getStatus().name() : "SCHEDULED");
                ev.put("color",            "#e74c3c");
                ev.put("hasConflict",      false);
                ev.put("teamName",         m.getTeamA() != null ? m.getTeamA().getName() : "");
                ev.put("opponentTeamName", m.getTeamB() != null ? m.getTeamB().getName() : "");
                ev.put("scoreTeamA",       m.getScoreTeamA());
                ev.put("scoreTeamB",       m.getScoreTeamB());

                // Find field coords by location name for AI distance scoring
                Double[] coords = findFieldCoordsByName(m.getLocation());
                ev.put("fieldLat", coords[0]);
                ev.put("fieldLng", coords[1]);

                events.add(ev);
            }
        } catch (Exception e) {
            // Match loading failed — continue with trainings
        }

        // ── TRAININGS ────────────────────────────────────────────────
        try {
            List<Training> allTrainings = trainingRepository.findVisibleTrainingsForUserBetween(user, from, to);

            for (Training t : allTrainings) {
                int duration = t.getDurationInMinutes() != null ? t.getDurationInMinutes() : 90;

                Map<String, Object> ev = new HashMap<>();
                ev.put("id",          t.getIdTraining());
                ev.put("type",        "TRAINING");
                ev.put("title",       t.getTitle() != null ? t.getTitle() : "Training");
                ev.put("description", t.getDescription() != null ? t.getDescription() : "");
                ev.put("startTime",   t.getTrainingDate().format(FMT));
                ev.put("endTime",     t.getTrainingDate().plusMinutes(duration).format(FMT));
                ev.put("location",    t.getLocation() != null ? t.getLocation() : "");
                ev.put("status",      t.getStatus() != null ? t.getStatus().name() : "PLANNED");
                ev.put("color",       "#3498db");
                ev.put("hasConflict", false);
                ev.put("teamName",    t.getTeam() != null ? t.getTeam().getName() : "");
                ev.put("coachName",   t.getCoach() != null ? t.getCoach().getFullName() : "");

                Double[] coords = findFieldCoordsByName(t.getLocation());
                ev.put("fieldLat", coords[0]);
                ev.put("fieldLng", coords[1]);

                events.add(ev);
            }
        } catch (Exception e) {
            // Training loading failed
        }

        // Sort by start time
        events.sort(Comparator.comparing(e -> (String) e.get("startTime")));
        return events;
    }

    // ════════════════════════════════════════════════════════════════
    // CONFLICT DETECTION
    // ════════════════════════════════════════════════════════════════
    private void detectConflicts(List<Map<String, Object>> events) {
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                Map<String, Object> a = events.get(i);
                Map<String, Object> b = events.get(j);
                try {
                    LocalDateTime aStart = LocalDateTime.parse((String) a.get("startTime"), FMT);
                    LocalDateTime aEnd   = LocalDateTime.parse((String) a.get("endTime"),   FMT);
                    LocalDateTime bStart = LocalDateTime.parse((String) b.get("startTime"), FMT);
                    LocalDateTime bEnd   = LocalDateTime.parse((String) b.get("endTime"),   FMT);

                    boolean overlap = aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
                    if (overlap) {
                        a.put("hasConflict",    true);
                        a.put("conflictReason", "Overlaps with: " + b.get("title"));
                        a.put("color",          "#e67e22");
                        b.put("hasConflict",    true);
                        b.put("conflictReason", "Overlaps with: " + a.get("title"));
                        b.put("color",          "#e67e22");
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════
    private boolean isUserInMatch(Match m, User user) {
        if (m.getCreatedBy() != null && m.getCreatedBy().equals(user)) return true;
        if (m.getTeamA() != null && m.getTeamA().getPlayers() != null
                && m.getTeamA().getPlayers().contains(user)) return true;
        if (m.getTeamB() != null && m.getTeamB().getPlayers() != null
                && m.getTeamB().getPlayers().contains(user)) return true;
        return false;
    }

    private String buildMatchTitle(Match m) {
        String a = m.getTeamA() != null ? m.getTeamA().getName() : "?";
        String b = m.getTeamB() != null ? m.getTeamB().getName() : "?";
        return a + " vs " + b;
    }

    /**
     * Find field coordinates by matching location text against field names/locations in DB.
     *
     * Strategy (best-match wins):
     *  1. Exact substring match (original behaviour)
     *  2. Any significant word (≥ 3 chars) from locationText found in field name/location
     *  3. Any significant word from field name/location found in locationText
     *
     * This handles cases like locationText="Sousse Park" matching a field named "Stade de Sousse".
     */
    private Double[] findFieldCoordsByName(String locationText) {
        if (locationText == null || locationText.isBlank())
            return new Double[]{null, null};

        String lower = locationText.toLowerCase().trim();
        // Significant words: length >= 3, skip common stop-words
        Set<String> stopWords = Set.of("de", "du", "la", "le", "les", "des", "au", "aux",
                "el", "al", "the", "and", "et", "en", "sur", "park", "stade", "terrain",
                "complexe", "municipal", "centre", "club");

        String[] queryWords = lower.split("[\\s,/\\-]+");
        List<String> sigWords = Arrays.stream(queryWords)
                .filter(w -> w.length() >= 3 && !stopWords.contains(w))
                .collect(Collectors.toList());

        List<com.example.streetleague.Entity.Field> allFields =
                fieldRepository.findAll().stream()
                        .filter(f -> f.getLatitude() != null && f.getLongitude() != null)
                        .collect(Collectors.toList());

        // Pass 1 — exact substring (original logic)
        for (var f : allFields) {
            String name = f.getName() != null ? f.getName().toLowerCase() : "";
            String loc  = f.getLocation() != null ? f.getLocation().toLowerCase() : "";
            if (name.contains(lower) || lower.contains(name)
                    || loc.contains(lower) || lower.contains(loc)) {
                return new Double[]{f.getLatitude(), f.getLongitude()};
            }
        }

        // Pass 2 — word-level match: any significant query word appears in field name/location
        if (!sigWords.isEmpty()) {
            for (var f : allFields) {
                String name = f.getName() != null ? f.getName().toLowerCase() : "";
                String loc  = f.getLocation() != null ? f.getLocation().toLowerCase() : "";
                boolean matched = sigWords.stream()
                        .anyMatch(w -> name.contains(w) || loc.contains(w));
                if (matched) return new Double[]{f.getLatitude(), f.getLongitude()};
            }

            // Pass 3 — word-level match: any significant field word appears in query
            for (var f : allFields) {
                String combined = ((f.getName() != null ? f.getName() : "")
                        + " " + (f.getLocation() != null ? f.getLocation() : "")).toLowerCase();
                String[] fieldWords = combined.split("[\\s,/\\-]+");
                boolean matched = Arrays.stream(fieldWords)
                        .filter(w -> w.length() >= 3 && !stopWords.contains(w))
                        .anyMatch(lower::contains);
                if (matched) return new Double[]{f.getLatitude(), f.getLongitude()};
            }
        }

        return new Double[]{null, null};
    }

    private List<Map<String, Object>> getAllFieldsFromDb() {
        return fieldRepository.findAll().stream()
                .filter(f -> f.getLatitude() != null && f.getLongitude() != null)
                .map(f -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",          f.getId());
                    m.put("name",        f.getName());
                    m.put("lat",         f.getLatitude());
                    m.put("lng",         f.getLongitude());
                    m.put("capacity",    f.getCapacity());
                    m.put("isAvailable", f.isAvailable());
                    m.put("pressure",    0.3);
                    m.put("pricePerHour", f.getPricePerHour());
                    m.put("location",    f.getLocation() != null ? f.getLocation() : "");
                    return m;
                })
                .collect(Collectors.toList());
    }
}
