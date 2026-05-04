package com.example.streetleague.dto;

import java.util.Objects;

public class SuggestedPriceResponse {

    private double suggestedPrice;
    private double basePrice;
    private double deltaPercent;
    private String sport;
    private String fieldName;

    public SuggestedPriceResponse() {
    }

    public SuggestedPriceResponse(double suggestedPrice, double basePrice, double deltaPercent, String sport, String fieldName) {
        this.suggestedPrice = suggestedPrice;
        this.basePrice = basePrice;
        this.deltaPercent = deltaPercent;
        this.sport = sport;
        this.fieldName = fieldName;
    }

    public double getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(double suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getDeltaPercent() {
        return deltaPercent;
    }

    public void setDeltaPercent(double deltaPercent) {
        this.deltaPercent = deltaPercent;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestedPriceResponse that = (SuggestedPriceResponse) o;
        return Double.compare(that.suggestedPrice, suggestedPrice) == 0 && Double.compare(that.basePrice, basePrice) == 0 && Double.compare(that.deltaPercent, deltaPercent) == 0 && Objects.equals(sport, that.sport) && Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suggestedPrice, basePrice, deltaPercent, sport, fieldName);
    }

    @Override
    public String toString() {
        return "SuggestedPriceResponse{" +
                "suggestedPrice=" + suggestedPrice +
                ", basePrice=" + basePrice +
                ", sport='" + sport + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }

    public static SuggestedPriceResponseBuilder builder() {
        return new SuggestedPriceResponseBuilder();
    }

    public static class SuggestedPriceResponseBuilder {
        private double suggestedPrice;
        private double basePrice;
        private double deltaPercent;
        private String sport;
        private String fieldName;

        public SuggestedPriceResponseBuilder suggestedPrice(double suggestedPrice) { this.suggestedPrice = suggestedPrice; return this; }
        public SuggestedPriceResponseBuilder basePrice(double basePrice) { this.basePrice = basePrice; return this; }
        public SuggestedPriceResponseBuilder deltaPercent(double deltaPercent) { this.deltaPercent = deltaPercent; return this; }
        public SuggestedPriceResponseBuilder sport(String sport) { this.sport = sport; return this; }
        public SuggestedPriceResponseBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }

        public SuggestedPriceResponse build() {
            return new SuggestedPriceResponse(suggestedPrice, basePrice, deltaPercent, sport, fieldName);
        }
    }
}
