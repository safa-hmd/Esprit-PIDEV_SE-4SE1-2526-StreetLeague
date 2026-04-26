//package com.example.streetleague.ServiceImp;
//
//import com.example.streetleague.Entity.Field;
//import com.example.streetleague.Entity.Match;
//import com.example.streetleague.Entity.Tournament;
//import com.example.streetleague.Entity.Training;
//import com.example.streetleague.Repository.FieldRepository;
//import com.example.streetleague.Repository.MatchRepository;
//import com.example.streetleague.Repository.TournamentRepository;
//import com.example.streetleague.Repository.TrainingRepository;
//import com.example.streetleague.Repository.UserRepository;
//import com.example.streetleague.domain.User;
//import com.example.streetleague.dto.FieldRecommendationDto;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@AllArgsConstructor
//@Slf4j
//public class RecommendationService {
//
//    private final FieldRepository      fieldRepository;
//    private final MatchRepository      matchRepository;
//    private final TrainingRepository   trainingRepository;
//    private final TournamentRepository tournamentRepository;
//    private final UserRepository       userRepository;
//    private final RestTemplate         restTemplate;
//
//    private static final String FLASK_URL      = "http://localhost:5001";
//    private static final double MAX_DISTANCE_KM = 30.0;
//
//    // ─────────────────────────────────────────────────────────────
//    // Haversine : distance en km entre deux points GPS
//    // ─────────────────────────────────────────────────────────────
//    private double haversine(double lat1, double lng1, double lat2, double lng2) {
//        final double R = 6371;
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLng = Math.toRadians(lng2 - lng1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
//        return Math.round(R * 2 * Math.asin(Math.sqrt(a)) * 100.0) / 100.0;
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    // Contexte équipe (fatigue, matchs semaine, etc.)
//    // ─────────────────────────────────────────────────────────────
//    private Map<String, Object> buildTeamContext(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
//
//        // Matchs cette semaine pour les équipes du joueur
//        LocalDate weekStart = LocalDate.now().with(
//                java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
//        LocalDate weekEnd   = weekStart.plusDays(7);
//
//        long matchesWeek = matchRepository.findAll().stream()
//                .filter(m -> m.getMatchDate() != null
//                        && !m.getMatchDate().toLocalDate().isBefore(weekStart)
//                        && !m.getMatchDate().toLocalDate().isAfter(weekEnd))
//                .count();
//
//        long trainingsWeek = trainingRepository.findAll().stream()
//                .filter(t -> t.getTrainingDate() != null
//                        && !t.getTrainingDate().toLocalDate().isBefore(weekStart)
//                        && !t.getTrainingDate().toLocalDate().isAfter(weekEnd))
//                .count();
//
//        // Tournament actif cette semaine ?
//        boolean hasTournament = tournamentRepository.findAll().stream()
//                .anyMatch(t -> t.getStartDate() != null
//                        && !t.getStartDate().isBefore(weekStart)
//                        && !t.getStartDate().isAfter(weekEnd));
//
//        Map<String, Object> ctx = new HashMap<>();
//        ctx.put("matchesWeek",       (int) Math.min(matchesWeek, 5));
//        ctx.put("trainingsWeek",     (int) Math.min(trainingsWeek, 7));
//        ctx.put("hasTournament",     hasTournament ? 1 : 0);
//        ctx.put("tournamentPriority",hasTournament ? 2 : 0);
//        ctx.put("elo",               1000);
//        ctx.put("playerCount",       10);
//        ctx.put("level",             2);
//        ctx.put("victories",         5);
//        ctx.put("defeats",           5);
//        ctx.put("duration",          90);
//        ctx.put("hour",              17);
//        ctx.put("day",               LocalDate.now().getDayOfWeek().getValue() - 1);
//        return ctx;
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    // ENDPOINT PRINCIPAL : /api/recommend/fields/{userId}
//    // Reçoit lat/lng de l'utilisateur, retourne Top 5 terrains scorés
//    // ─────────────────────────────────────────────────────────────
//    public List<FieldRecommendationDto> recommendFields(
//            Long userId, double userLat, double userLng) {
//
//        Map<String, Object> ctx = buildTeamContext(userId);
//
//        // 1. Filtrer les terrains dans un rayon de MAX_DISTANCE_KM
//        List<Field> allFields = fieldRepository.findAll();
//        List<Map<String, Object>> fieldsPayload = new ArrayList<>();
//
//        for (Field f : allFields) {
//            if (f.getLatitude() == null || f.getLongitude() == null) continue;
//
//            double dist = haversine(userLat, userLng,
//                    f.getLatitude(), f.getLongitude());
//            if (dist > MAX_DISTANCE_KM) continue;
//
//            // Pression terrain = réservations actives / capacité
//            long activeReservations = f.getReservations() == null ? 0 :
//                    f.getReservations().stream()
//                            .filter(r -> r.getStatus() != null &&
//                                    r.getStatus().name().equals("CONFIRMED"))
//                            .count();
//            double pressure = f.getCapacity() > 0
//                    ? Math.min(1.0, (double) activeReservations / f.getCapacity())
//                    : 0.3;
//
//            Map<String, Object> fp = new HashMap<>();
//            fp.put("id",          f.getId());
//            fp.put("name",        f.getName());
//            fp.put("lat",         f.getLatitude());
//            fp.put("lng",         f.getLongitude());
//            fp.put("capacity",    f.getCapacity());
//            fp.put("isAvailable", f.isAvailable());
//            fp.put("pressure",    Math.round(pressure * 100.0) / 100.0);
//            fieldsPayload.add(fp);
//        }
//
//        if (fieldsPayload.isEmpty()) {
//            log.warn("Aucun terrain trouvé dans un rayon de {}km", MAX_DISTANCE_KM);
//            return Collections.emptyList();
//        }
//
//        // 2. Construire le body Flask
//        Map<String, Object> body = new HashMap<>(ctx);
//        body.put("userLat", userLat);
//        body.put("userLng", userLng);
//        body.put("fields",  fieldsPayload);
//
//        // 3. Appel Flask /suggest-fields
//        try {
//            @SuppressWarnings("unchecked")
//            Map<String, Object> flaskResp = restTemplate.postForObject(
//                    FLASK_URL + "/suggest-fields", body, Map.class);
//
//            if (flaskResp == null) return Collections.emptyList();
//
//            @SuppressWarnings("unchecked")
//            List<Map<String, Object>> bestFields =
//                    (List<Map<String, Object>>) flaskResp.get("bestFields");
//
//            if (bestFields == null || bestFields.isEmpty())
//                return fallbackScore(allFields, fieldsPayload, userLat, userLng);
//
//            // 4. Mapper vers FieldRecommendationDto
//            return bestFields.stream().map(bf -> {
//                Long fid = ((Number) bf.get("fieldId")).longValue();
//                Field field = allFields.stream()
//                        .filter(f -> f.getId().equals(fid))
//                        .findFirst().orElse(null);
//
//                return FieldRecommendationDto.builder()
//                        .fieldId(fid)
//                        .fieldName((String) bf.get("fieldName"))
//                        .fieldLocation(field != null ? field.getLocation() : "")
//                        .fieldCapacity(field != null ? field.getCapacity() : 0)
//                        .pricePerHour(field != null ? field.getPricePerHour() : null)
//                        .fieldAvailable(field != null && field.isAvailable())
//                        .fieldLat(field != null ? field.getLatitude() : null)
//                        .fieldLng(field != null ? field.getLongitude() : null)
//                        .aiScore(((Number) bf.get("score")).doubleValue())
//                        .recommendation((String) bf.get("recommendation"))
//                        .distanceKm(((Number) bf.get("distanceKm")).doubleValue())
//                        .weather((String) bf.get("weather"))
//                        .weatherScore(bf.get("weatherScore") != null
//                                ? ((Number) bf.get("weatherScore")).doubleValue() : null)
//                        .build();
//            }).collect(Collectors.toList());
//
//        } catch (Exception e) {
//            log.warn("Flask indisponible, fallback scoring Java: {}", e.getMessage());
//            return fallbackScore(allFields, fieldsPayload, userLat, userLng);  // ← avant: return emptyList()
//        }
//    }
//
//    private List<FieldRecommendationDto> fallbackScore(
//            List<Field> allFields,
//            List<Map<String, Object>> fieldsPayload,
//            double userLat, double userLng) {
//
//        return fieldsPayload.stream()
//                .sorted(Comparator.comparingDouble(fp ->
//                        haversine(userLat, userLng,
//                                (Double) fp.get("lat"),
//                                (Double) fp.get("lng"))))
//                .limit(5)
//                .map(fp -> {
//                    Long fid = ((Number) fp.get("id")).longValue();
//                    Field field = allFields.stream()
//                            .filter(f -> f.getId().equals(fid))
//                            .findFirst().orElse(null);
//                    if (field == null) return null;
//
//                    double dist    = haversine(userLat, userLng, field.getLatitude(), field.getLongitude());
//                    double pressure = (Double) fp.get("pressure");
//                    // Score simple : disponibilité + proximité + faible pression
//                    double score = (field.isAvailable() ? 0.5 : 0.1)
//                            + Math.max(0, (30 - dist) / 30) * 0.3
//                            + (1 - pressure) * 0.2;
//                    score = Math.min(1.0, score);
//
//                    String rec = score >= 0.7 ? "EXCELLENT"
//                            : score >= 0.4 ? "ACCEPTABLE"
//                            : "DECONSEILLE";
//
//                    return FieldRecommendationDto.builder()
//                            .fieldId(field.getId())
//                            .fieldName(field.getName())
//                            .fieldLocation(field.getLocation())
//                            .fieldCapacity(field.getCapacity())
//                            .pricePerHour(field.getPricePerHour())
//                            .fieldAvailable(field.isAvailable())
//                            .fieldLat(field.getLatitude())
//                            .fieldLng(field.getLongitude())
//                            .aiScore(Math.round(score * 100.0) / 100.0)
//                            .recommendation(rec)
//                            .distanceKm(dist)
//                            .weather("Clear")
//                            .weatherScore(0.8)
//                            .build();
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    // ENDPOINT : /api/recommend/slot/{userId}/{fieldId}
//    // Retourne le meilleur créneau de la semaine pour un terrain
//    // ─────────────────────────────────────────────────────────────
//    public List<FieldRecommendationDto.SlotDto> recommendSlot(
//            Long userId, Long fieldId, double userLat, double userLng) {
//
//        Field field = fieldRepository.findById(fieldId)
//                .orElseThrow(() -> new RuntimeException("Field not found: " + fieldId));
//
//        if (field.getLatitude() == null || field.getLongitude() == null) {
//            throw new RuntimeException("Field has no GPS coordinates");
//        }
//
//        Map<String, Object> ctx = buildTeamContext(userId);
//
//        long activeRes = field.getReservations() == null ? 0 :
//                field.getReservations().stream()
//                        .filter(r -> r.getStatus() != null &&
//                                r.getStatus().name().equals("CONFIRMED"))
//                        .count();
//        double pressure = field.getCapacity() > 0
//                ? Math.min(1.0, (double) activeRes / field.getCapacity()) : 0.3;
//
//        Map<String, Object> body = new HashMap<>(ctx);
//        body.put("userLat",        userLat);
//        body.put("userLng",        userLng);
//        body.put("fieldLat",       field.getLatitude());
//        body.put("fieldLng",       field.getLongitude());
//        body.put("fieldCapacity",  field.getCapacity());
//        body.put("fieldPressure",  Math.round(pressure * 100.0) / 100.0);
//        body.put("fieldAvailable", field.isAvailable() ? 1 : 0);
//
//        try {
//            @SuppressWarnings("unchecked")
//            Map<String, Object> flaskResp = restTemplate.postForObject(
//                    FLASK_URL + "/suggest-slot", body, Map.class);
//
//            if (flaskResp == null) return Collections.emptyList();
//
//            @SuppressWarnings("unchecked")
//            List<Map<String, Object>> bestSlots =
//                    (List<Map<String, Object>>) flaskResp.get("bestSlots");
//
//            return bestSlots.stream().map(s -> FieldRecommendationDto.SlotDto.builder()
//                    .dayName((String) s.get("dayName"))
//                    .hour(((Number) s.get("hour")).intValue())
//                    .label((String) s.get("label"))
//                    .score(((Number) s.get("score")).doubleValue())
//                    .rec((String) s.get("rec"))
//                    .build()
//            ).collect(Collectors.toList());
//
//        } catch (Exception e) {
//            log.error("Flask /suggest-slot erreur: {}", e.getMessage());
//            return Collections.emptyList();
//        }
//    }
//}