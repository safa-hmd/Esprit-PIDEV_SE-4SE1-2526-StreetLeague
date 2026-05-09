package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.*;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.DashboardStatsDTO;
import com.example.streetleague.dto.DashboardStatsDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ══════════════════════════════════════════════════════════════
 *  StatsService — Dashboard admin
 *
 *  Toutes les requêtes stats agrégées sur une période glissante.
 *  Par défaut : 30 derniers jours.
 *
 *  Jointures impliquées :
 *   Commande ←→ LigneCommande ←→ Materiel  (top produits)
 *   Commande ←→ Livraison ←→ User          (perf livreurs)
 *   User                                    (livreurs actifs)
 *   Materiel                                (stock critique)
 * ══════════════════════════════════════════════════════════════
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final CommandeRepository     commandeRepository;
    private final LivraisonRepository    livraisonRepository;
    private final UserRepository         userRepository;
    private final MaterielRepository     materielRepository;
    private final LigneCommandeRepository ligneCommandeRepository;

    private static final int SEUIL_STOCK_CRITIQUE = 5;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Point d'entrée principal — retourne tout le dashboard en un appel.
     * @param jours  nombre de jours à analyser (défaut : 30)
     */
    public DashboardStatsDTO getDashboard(int jours) {
        LocalDateTime debut  = LocalDateTime.now().minusDays(jours);
        LocalDateTime debutP = LocalDateTime.now().minusDays(jours * 2L); // période précédente

        log.info("Calcul dashboard : {} jours depuis {}", jours, debut.toLocalDate());

        // ── 1. KPI Cards ──────────────────────────────────
        double caPeriode   = calculCA(debut, LocalDateTime.now());
        double caPrecedent = calculCA(debutP, debut);
        double evolutionPct = caPrecedent > 0
                ? ((caPeriode - caPrecedent) / caPrecedent) * 100.0
                : 0.0;

        long nbCommandes         = commandeRepository.countByDateCreationAfter(debut);
        int nbLivreursActifs = userRepository.countLivreursActifs(
                Role.DELIVERY,
                List.of(LivreurStatus.DISPONIBLE, LivreurStatus.OCCUPE)
        );
        long nbStockCritique     = materielRepository.countByQuantiteStockLessThan(SEUIL_STOCK_CRITIQUE);

        // ── 2. Répartition statuts livraisons ─────────────
        List<Livraison> livraisons = livraisonRepository.findByDateCreationAfter(debut);
        Map<String, Long> statutsMap = livraisons.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getStatut() != null ? l.getStatut().name() : "INCONNU",
                        Collectors.counting()
                ));

        // ── 3. Évolution CA jour par jour ─────────────────
        List<CaJourDTO> evolutionCA = calculEvolutionCA(debut, jours);

        // ── 4. Top produits ───────────────────────────────
        List<TopProduitDTO> topProduits = calculTopProduits(debut, 10);

        // ── 5. Performance livreurs ───────────────────────
        List<LivreurPerfDTO> perfLivreurs = calculPerfLivreurs(debut);

        return DashboardStatsDTO.builder()
                .caTotalTND(Math.round(caPeriode * 100.0) / 100.0)
                .nbCommandes(nbCommandes)
                .nbLivreursActifs(nbLivreursActifs)
                .nbProduitsStockCritique(nbStockCritique)
                .caEvolutionPct(Math.round(evolutionPct * 10.0) / 10.0)
                .evolutionCA(evolutionCA)
                .livraisonsPreparees(statutsMap.getOrDefault("PREPAREE", 0L).intValue())
                .livraisonsAssignees(statutsMap.getOrDefault("ASSIGNEE", 0L).intValue())
                .livraisonsExpediees(
                        statutsMap.getOrDefault("EXPEDIEE", 0L).intValue() +
                                statutsMap.getOrDefault("OUT_FOR_DELIVERY", 0L).intValue())
                .livraisonsLivrees(statutsMap.getOrDefault("LIVREE", 0L).intValue())
                .livraisonsEchecs(statutsMap.getOrDefault("ECHEC", 0L).intValue())
                .topProduits(topProduits)
                .performanceLivreurs(perfLivreurs)
                .build();
    }

    // ─────────────────────────────────────────────────────
    //  CA total sur une période
    // ─────────────────────────────────────────────────────
    private double calculCA(LocalDateTime debut, LocalDateTime fin) {
        Double ca = commandeRepository.sumCaByPeriode(debut, fin);
        return ca != null ? ca : 0.0;
    }

    // ─────────────────────────────────────────────────────
    //  Évolution CA jour par jour (pour LineChart)
    //  Jointure : Commande GROUP BY DATE(date_creation)
    // ─────────────────────────────────────────────────────
    private List<CaJourDTO> calculEvolutionCA(LocalDateTime debut, int jours) {
        List<Object[]> rows = commandeRepository.caParJour(debut);

        // Construire un map date → données
        Map<String, CaJourDTO> byDate = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String date  = row[0].toString().substring(0, 10);
            double ca    = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            int    count = row[2] != null ? ((Number) row[2]).intValue() : 0;
            byDate.put(date, CaJourDTO.builder().date(date).ca(ca).nbCommandes(count).build());
        }

        // Compléter les jours sans commandes avec 0
        List<CaJourDTO> result = new ArrayList<>();
        LocalDate d = debut.toLocalDate();
        for (int i = 0; i <= jours; i++) {
            String key = d.plusDays(i).format(DATE_FMT);
            result.add(byDate.getOrDefault(key,
                    CaJourDTO.builder().date(key).ca(0).nbCommandes(0).build()));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────
    //  Top N produits vendus (pour BarChart + cercles)
    //  Jointure : LigneCommande ←→ Materiel ←→ Commande
    // ─────────────────────────────────────────────────────
    private List<TopProduitDTO> calculTopProduits(LocalDateTime debut, int topN) {
        List<Object[]> rows = ligneCommandeRepository.topProduits(debut, topN);
        double caTotal = rows.stream()
                .mapToDouble(r -> r[3] != null ? ((Number) r[3]).doubleValue() : 0.0)
                .sum();

        return rows.stream().map(row -> {
            Long   id      = ((Number) row[0]).longValue();
            String nom     = (String) row[1];
            int    qte     = row[2] != null ? ((Number) row[2]).intValue() : 0;
            double ca      = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
            int    stock   = row[4] != null ? ((Number) row[4]).intValue() : 0;
            double pct     = caTotal > 0 ? (ca / caTotal) * 100.0 : 0.0;

            return TopProduitDTO.builder()
                    .materielId(id)
                    .nom(nom)
                    .quantiteVendue(qte)
                    .caGenere(Math.round(ca * 100.0) / 100.0)
                    .stockActuel(stock)
                    .pctDuTotal(Math.round(pct * 10.0) / 10.0)
                    .build();
        }).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────
    //  Performance livreurs (pour tableau + barres)
    //  Jointure : Livraison ←→ User(livreur)
    //  Algo : score = tauxReussite*0.7 + (1/tempsLivraison)*0.3
    // ─────────────────────────────────────────────────────
    private List<LivreurPerfDTO> calculPerfLivreurs(LocalDateTime debut) {
        List<Object[]> rows = livraisonRepository.perfParLivreur(debut);

        return rows.stream().map(row -> {
            Long   livreurId = ((Number) row[0]).longValue();
            String nom       = (String) row[1];
            int    total     = row[2] != null ? ((Number) row[2]).intValue() : 0;
            int    reussies  = row[3] != null ? ((Number) row[3]).intValue() : 0;
            double tempsH    = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
            double scoreAvg  = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;

            double taux = total > 0 ? (reussies * 100.0 / total) : 0.0;
            String niveau = taux >= 90 ? "excellent" : taux >= 70 ? "bon" : "faible";

            return LivreurPerfDTO.builder()
                    .livreurId(livreurId)
                    .nom(nom)
                    .totalLivraisons(total)
                    .livraisonsReussies(reussies)
                    .tauxReussite(Math.round(taux * 10.0) / 10.0)
                    .tempsLivraisonMoyen(Math.round(tempsH * 10.0) / 10.0)
                    .scoreAffectationMoyen(Math.round(scoreAvg * 100.0) / 100.0)
                    .niveau(niveau)
                    .build();
        }).collect(Collectors.toList());
    }
}