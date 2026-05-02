//package com.example.streetleague.dto;
//
//import lombok.*;
//import java.util.List;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class FieldRecommendationDto {
//
//    // ─── Field info ───────────────────────────────────────────
//    private Long   fieldId;
//    private String fieldName;
//    private String fieldLocation;
//    private int    fieldCapacity;
//    private Double pricePerHour;
//    private boolean fieldAvailable;
//
//    // ─── AI scores ────────────────────────────────────────────
//    private Double  aiScore;           // 0.0 – 1.0
//    private String  recommendation;   // EXCELLENT / ACCEPTABLE / DECONSEILLE
//    private Double  distanceKm;
//    private String  weather;
//    private Double  weatherScore;
//    private Double  fieldLat;          // GPS latitude du terrain
//    private Double  fieldLng;          // GPS longitude du terrain
//
//    // ─── Inner class : créneau recommandé ────────────────────
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class SlotDto {
//        private String  dayName;   // "Sam", "Dim"…
//        private int     hour;
//        private String  label;    // "Sam 17h"
//        private Double  score;
//        private String  rec;
//    }
//
//    private List<SlotDto> bestSlots;   // top 3 créneaux pour ce terrain
//}