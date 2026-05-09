package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.LivraisonRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.Livraison;
import com.example.streetleague.domain.LivraisonStatus;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TourneeDTO;
import com.example.streetleague.dto.TourneeDTO.StopDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TspService {

    private final LivraisonRepository livraisonRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final double VITESSE_MOYENNE_KMH = 30.0;
    private static final List<LivraisonStatus> STATUTS_ACTIFS = List.of(
            LivraisonStatus.ASSIGNEE,
            LivraisonStatus.EXPEDIEE,
            LivraisonStatus.OUT_FOR_DELIVERY
    );
    private static final String OSRM_URL = "http://router.project-osrm.org/route/v1/driving/";

    // ==============================
    // CALL OSRM ROUTING API
    // ==============================
    private Map<String, Object> getRoute(double lon1, double lat1, double lon2, double lat2) {
        // ✅ Suppression des espaces et concaténation stricte
        String url = OSRM_URL + lon1 + "," + lat1 + ";" + lon2 + "," + lat2 + "?overview=full&geometries=polyline";

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            if (routes == null || routes.isEmpty())
                throw new RuntimeException("OSRM no routes");

            Map<String, Object> route = routes.get(0);

            double distanceKm = ((Number) route.get("distance")).doubleValue() / 1000.0;
            String polyline = (String) route.get("geometry");

            return Map.of("distance", distanceKm, "polyline", polyline);
        } catch (Exception e) {
            log.error("OSRM ERROR → fallback haversine", e);
            return Map.of("distance", haversine(lat1, lon1, lat2, lon2), "polyline", "");
        }
    }

    // ==============================
    // HAVERSINE DISTANCE
    // ==============================
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                        Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    // ==============================
    // MAIN TSP FUNCTION
    // ==============================
    public TourneeDTO calculerTournee(Long livreurId) {
        User livreur = userRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur introuvable"));

        List<Livraison> livraisons = livraisonRepository
                .findByLivreur_IdUserAndStatutIn(livreurId, STATUTS_ACTIFS)
                .stream()
                .filter(l -> l.getLatitudeClient() != null && l.getLongitudeClient() != null)
                .collect(Collectors.toList());

        if (livraisons.isEmpty())
            return TourneeDTO.builder()
                    .livreurId(livreurId)
                    .stops(List.of())
                    .distanceTotaleKm(0.0)
                    .etaTotalMinutes(0)
                    .polylines(List.of())
                    .build();

        double latCourante = livreur.getLatitude();
        double lonCourante = livreur.getLongitude();

        List<Livraison> nonVisitees = new ArrayList<>(livraisons);
        List<StopDTO> stops = new ArrayList<>();
        List<String> polylines = new ArrayList<>();

        double totalKm = 0;
        double totalMin = 0;
        int ordre = 1;

        while (!nonVisitees.isEmpty()) {
            Livraison best = null;
            double bestDist = Double.MAX_VALUE;
            Map<String, Object> bestRoute = null;

            for (Livraison l : nonVisitees) {
                var route = getRoute(lonCourante, latCourante,
                        l.getLongitudeClient(), l.getLatitudeClient());

                double dist = (double) route.get("distance");

                if (dist < bestDist) {
                    bestDist = dist;
                    best = l;
                    bestRoute = route;
                }
            }

            polylines.add((String) bestRoute.get("polyline"));

            totalKm += bestDist;
            totalMin += (bestDist / VITESSE_MOYENNE_KMH) * 60;

            stops.add(StopDTO.builder()
                    .ordre(ordre++)
                    .livraisonId(best.getId())
                    .adresse(best.getAdresse())
                    .latitude(best.getLatitudeClient())
                    .longitude(best.getLongitudeClient())
                    .etaMinutesDepuisDepart((int) totalMin)
                    .distanceDepuisPrecedentKm(bestDist)
                    .build());

            latCourante = best.getLatitudeClient();
            lonCourante = best.getLongitudeClient();
            nonVisitees.remove(best);
        }

        return TourneeDTO.builder()
                .livreurId(livreurId)
                .stops(stops)
                .distanceTotaleKm(totalKm)
                .etaTotalMinutes((int) totalMin)
                .polylines(polylines)
                .build();
    }
}