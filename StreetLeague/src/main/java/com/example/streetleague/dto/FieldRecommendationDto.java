package com.example.streetleague.dto;

import java.util.List;
import java.util.Objects;

public class FieldRecommendationDto {

    private Long   fieldId;
    private String fieldName;
    private String fieldLocation;
    private int    fieldCapacity;
    private Double pricePerHour;
    private boolean fieldAvailable;
    private Double  aiScore;           // 0.0 – 1.0
    private String  recommendation;   // EXCELLENT / ACCEPTABLE / DECONSEILLE
    private Double  distanceKm;
    private String  weather;
    private Double  weatherScore;
    private Double  fieldLat;
    private Double  fieldLng;
    private List<SlotDto> bestSlots;

    public FieldRecommendationDto() {
    }

    public FieldRecommendationDto(Long fieldId, String fieldName, String fieldLocation, int fieldCapacity, Double pricePerHour, boolean fieldAvailable, Double aiScore, String recommendation, Double distanceKm, String weather, Double weatherScore, Double fieldLat, Double fieldLng, List<SlotDto> bestSlots) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.fieldCapacity = fieldCapacity;
        this.pricePerHour = pricePerHour;
        this.fieldAvailable = fieldAvailable;
        this.aiScore = aiScore;
        this.recommendation = recommendation;
        this.distanceKm = distanceKm;
        this.weather = weather;
        this.weatherScore = weatherScore;
        this.fieldLat = fieldLat;
        this.fieldLng = fieldLng;
        this.bestSlots = bestSlots;
    }

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldLocation() { return fieldLocation; }
    public void setFieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; }

    public int getFieldCapacity() { return fieldCapacity; }
    public void setFieldCapacity(int fieldCapacity) { this.fieldCapacity = fieldCapacity; }

    public Double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }

    public boolean isFieldAvailable() { return fieldAvailable; }
    public void setFieldAvailable(boolean fieldAvailable) { this.fieldAvailable = fieldAvailable; }

    public Double getAiScore() { return aiScore; }
    public void setAiScore(Double aiScore) { this.aiScore = aiScore; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public Double getWeatherScore() { return weatherScore; }
    public void setWeatherScore(Double weatherScore) { this.weatherScore = weatherScore; }

    public Double getFieldLat() { return fieldLat; }
    public void setFieldLat(Double fieldLat) { this.fieldLat = fieldLat; }

    public Double getFieldLng() { return fieldLng; }
    public void setFieldLng(Double fieldLng) { this.fieldLng = fieldLng; }

    public List<SlotDto> getBestSlots() { return bestSlots; }
    public void setBestSlots(List<SlotDto> bestSlots) { this.bestSlots = bestSlots; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldRecommendationDto that = (FieldRecommendationDto) o;
        return fieldCapacity == that.fieldCapacity && fieldAvailable == that.fieldAvailable && Objects.equals(fieldId, that.fieldId) && Objects.equals(fieldName, that.fieldName) && Objects.equals(fieldLocation, that.fieldLocation) && Objects.equals(pricePerHour, that.pricePerHour) && Objects.equals(aiScore, that.aiScore) && Objects.equals(recommendation, that.recommendation) && Objects.equals(distanceKm, that.distanceKm) && Objects.equals(weather, that.weather) && Objects.equals(weatherScore, that.weatherScore) && Objects.equals(fieldLat, that.fieldLat) && Objects.equals(fieldLng, that.fieldLng) && Objects.equals(bestSlots, that.bestSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldId, fieldName, fieldLocation, fieldCapacity, pricePerHour, fieldAvailable, aiScore, recommendation, distanceKm, weather, weatherScore, fieldLat, fieldLng, bestSlots);
    }

    @Override
    public String toString() {
        return "FieldRecommendationDto{" +
                "fieldId=" + fieldId +
                ", fieldName='" + fieldName + '\'' +
                ", aiScore=" + aiScore +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }

    public static FieldRecommendationDtoBuilder builder() {
        return new FieldRecommendationDtoBuilder();
    }

    public static class FieldRecommendationDtoBuilder {
        private Long   fieldId;
        private String fieldName;
        private String fieldLocation;
        private int    fieldCapacity;
        private Double pricePerHour;
        private boolean fieldAvailable;
        private Double  aiScore;
        private String  recommendation;
        private Double  distanceKm;
        private String  weather;
        private Double  weatherScore;
        private Double  fieldLat;
        private Double  fieldLng;
        private List<SlotDto> bestSlots;

        public FieldRecommendationDtoBuilder fieldId(Long fieldId) { this.fieldId = fieldId; return this; }
        public FieldRecommendationDtoBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public FieldRecommendationDtoBuilder fieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; return this; }
        public FieldRecommendationDtoBuilder fieldCapacity(int fieldCapacity) { this.fieldCapacity = fieldCapacity; return this; }
        public FieldRecommendationDtoBuilder pricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; return this; }
        public FieldRecommendationDtoBuilder fieldAvailable(boolean fieldAvailable) { this.fieldAvailable = fieldAvailable; return this; }
        public FieldRecommendationDtoBuilder aiScore(Double aiScore) { this.aiScore = aiScore; return this; }
        public FieldRecommendationDtoBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public FieldRecommendationDtoBuilder distanceKm(Double distanceKm) { this.distanceKm = distanceKm; return this; }
        public FieldRecommendationDtoBuilder weather(String weather) { this.weather = weather; return this; }
        public FieldRecommendationDtoBuilder weatherScore(Double weatherScore) { this.weatherScore = weatherScore; return this; }
        public FieldRecommendationDtoBuilder fieldLat(Double fieldLat) { this.fieldLat = fieldLat; return this; }
        public FieldRecommendationDtoBuilder fieldLng(Double fieldLng) { this.fieldLng = fieldLng; return this; }
        public FieldRecommendationDtoBuilder bestSlots(List<SlotDto> bestSlots) { this.bestSlots = bestSlots; return this; }

        public FieldRecommendationDto build() {
            return new FieldRecommendationDto(fieldId, fieldName, fieldLocation, fieldCapacity, pricePerHour, fieldAvailable, aiScore, recommendation, distanceKm, weather, weatherScore, fieldLat, fieldLng, bestSlots);
        }
    }

    public static class SlotDto {
        private String  dayName;
        private int     hour;
        private String  label;
        private Double  score;
        private String  rec;

        public SlotDto() {
        }

        public SlotDto(String dayName, int hour, String label, Double score, String rec) {
            this.dayName = dayName;
            this.hour = hour;
            this.label = label;
            this.score = score;
            this.rec = rec;
        }

        public String getDayName() { return dayName; }
        public void setDayName(String dayName) { this.dayName = dayName; }

        public int getHour() { return hour; }
        public void setHour(int hour) { this.hour = hour; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        public String getRec() { return rec; }
        public void setRec(String rec) { this.rec = rec; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SlotDto slotDto = (SlotDto) o;
            return hour == slotDto.hour && Objects.equals(dayName, slotDto.dayName) && Objects.equals(label, slotDto.label) && Objects.equals(score, slotDto.score) && Objects.equals(rec, slotDto.rec);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dayName, hour, label, score, rec);
        }

        @Override
        public String toString() {
            return "SlotDto{" +
                    "label='" + label + '\'' +
                    ", score=" + score +
                    '}';
        }

        public static SlotDtoBuilder builder() {
            return new SlotDtoBuilder();
        }

        public static class SlotDtoBuilder {
            private String  dayName;
            private int     hour;
            private String  label;
            private Double  score;
            private String  rec;

            public SlotDtoBuilder dayName(String dayName) { this.dayName = dayName; return this; }
            public SlotDtoBuilder hour(int hour) { this.hour = hour; return this; }
            public SlotDtoBuilder label(String label) { this.label = label; return this; }
            public SlotDtoBuilder score(Double score) { this.score = score; return this; }
            public SlotDtoBuilder rec(String rec) { this.rec = rec; return this; }

            public SlotDto build() {
                return new SlotDto(dayName, hour, label, score, rec);
            }
        }
    }
}