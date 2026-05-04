package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class PricingResponse {

    @JsonProperty("suggested_price")
    private double suggestedPrice;

    @JsonProperty("base_price")
    private double basePrice;

    @JsonProperty("delta_percent")
    private double deltaPercent;

    @JsonProperty("model")
    private String model;

    public PricingResponse() {
    }

    public PricingResponse(double suggestedPrice, double basePrice, double deltaPercent, String model) {
        this.suggestedPrice = suggestedPrice;
        this.basePrice = basePrice;
        this.deltaPercent = deltaPercent;
        this.model = model;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricingResponse that = (PricingResponse) o;
        return Double.compare(that.suggestedPrice, suggestedPrice) == 0 && Double.compare(that.basePrice, basePrice) == 0 && Double.compare(that.deltaPercent, deltaPercent) == 0 && Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suggestedPrice, basePrice, deltaPercent, model);
    }

    @Override
    public String toString() {
        return "PricingResponse{" +
                "suggestedPrice=" + suggestedPrice +
                ", basePrice=" + basePrice +
                ", deltaPercent=" + deltaPercent +
                ", model='" + model + '\'' +
                '}';
    }

    public static PricingResponseBuilder builder() {
        return new PricingResponseBuilder();
    }

    public static class PricingResponseBuilder {
        private double suggestedPrice;
        private double basePrice;
        private double deltaPercent;
        private String model;

        public PricingResponseBuilder suggestedPrice(double suggestedPrice) { this.suggestedPrice = suggestedPrice; return this; }
        public PricingResponseBuilder basePrice(double basePrice) { this.basePrice = basePrice; return this; }
        public PricingResponseBuilder deltaPercent(double deltaPercent) { this.deltaPercent = deltaPercent; return this; }
        public PricingResponseBuilder model(String model) { this.model = model; return this; }

        public PricingResponse build() {
            return new PricingResponse(suggestedPrice, basePrice, deltaPercent, model);
        }
    }
}
