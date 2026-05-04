package com.example.streetleague.dto;

import lombok.*;
import java.util.List;

/**
 * DTO principal du dashboard admin.
 * Un seul endpoint GET /api/stats/dashboard retourne tout.
 * Toutes les stats sont calculées sur les 30 derniers jours par défaut.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsDTO {

    // ── 1. KPI Cards ──────────────────────────────────────
    private double  caTotalTND;           // CA total période
    private long     nbCommandes;          // Nombre commandes
    private int     nbLivreursActifs;     // Livreurs DISPONIBLE ou OCCUPE
    private long     nbProduitsStockCritique; // Stock < seuil (ex: 5 unités)
    private double  caEvolutionPct;       // % évolution vs période précédente

    // ── 2. Évolution CA jour par jour ─────────────────────
    private List<CaJourDTO> evolutionCA;  // 30 points pour le LineChart

    // ── 3. Répartition statuts livraisons ─────────────────
    private int livraisonsPreparees;
    private int livraisonsAssignees;
    private int livraisonsExpediees;
    private int livraisonsLivrees;
    private int livraisonsEchecs;

    // ── 4. Top produits vendus ────────────────────────────
    private List<TopProduitDTO> topProduits;    // Pour BarChart + cercles

    // ── 5. Performance livreurs ───────────────────────────
    private List<LivreurPerfDTO> performanceLivreurs;

    // ══════════════════════════════════════════════════════
    //  DTOs imbriqués
    // ══════════════════════════════════════════════════════

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CaJourDTO {
        private String date;       // "2026-04-01"
        private double ca;         // CA du jour
        private int    nbCommandes;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopProduitDTO {
        private Long   materielId;
        private String nom;
        private int    quantiteVendue;
        private double caGenere;
        private int    stockActuel;
        /** Rayon du cercle = sqrt(quantiteVendue) * facteur, calculé côté frontend */
        private double pctDuTotal;   // % du CA total pour les cercles proportionnels
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LivreurPerfDTO {
        private Long   livreurId;
        private String nom;
        private int    totalLivraisons;
        private int    livraisonsReussies;
        private double tauxReussite;       // % 0-100
        private double tempsLivraisonMoyen; // en heures
        private double scoreAffectationMoyen;
        /** Niveau : "excellent" ≥90%, "bon" ≥70%, "faible" <70% */
        private String niveau;
    }
}