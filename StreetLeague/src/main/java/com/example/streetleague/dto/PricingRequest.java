package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class PricingRequest {

    @JsonProperty("sport_type")
    private String sportType;

    @JsonProperty("location")
    private String location;

    @JsonProperty("capacity")
    private int capacity;

    @JsonProperty("base_price_per_hour")
    private double basePricePerHour;

    @JsonProperty("duration_hours")
    private int durationHours;

    @JsonProperty("day_of_week")
    private int dayOfWeek;

    @JsonProperty("hour_of_day")
    private int hourOfDay;

    @JsonProperty("is_weekend")
    private int isWeekend;

    @JsonProperty("is_peak_hour")
    private int isPeakHour;

    @JsonProperty("month")
    private int month;

    @JsonProperty("occupation_rate")
    private double occupationRate;

    @JsonProperty("cancellation_rate")
    private double cancellationRate;

    public PricingRequest() {
    }

    public PricingRequest(String sportType, String location, int capacity, double basePricePerHour, int durationHours, int dayOfWeek, int hourOfDay, int isWeekend, int isPeakHour, int month, double occupationRate, double cancellationRate) {
        this.sportType = sportType;
        this.location = location;
        this.capacity = capacity;
        this.basePricePerHour = basePricePerHour;
        this.durationHours = durationHours;
        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay;
        this.isWeekend = isWeekend;
        this.isPeakHour = isPeakHour;
        this.month = month;
        this.occupationRate = occupationRate;
        this.cancellationRate = cancellationRate;
    }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getBasePricePerHour() { return basePricePerHour; }
    public void setBasePricePerHour(double basePricePerHour) { this.basePricePerHour = basePricePerHour; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getHourOfDay() { return hourOfDay; }
    public void setHourOfDay(int hourOfDay) { this.hourOfDay = hourOfDay; }

    public int getIsWeekend() { return isWeekend; }
    public void setIsWeekend(int isWeekend) { this.isWeekend = isWeekend; }

    public int getIsPeakHour() { return isPeakHour; }
    public void setIsPeakHour(int isPeakHour) { this.isPeakHour = isPeakHour; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public double getOccupationRate() { return occupationRate; }
    public void setOccupationRate(double occupationRate) { this.occupationRate = occupationRate; }

    public double getCancellationRate() { return cancellationRate; }
    public void setCancellationRate(double cancellationRate) { this.cancellationRate = cancellationRate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricingRequest that = (PricingRequest) o;
        return capacity == that.capacity && Double.compare(that.basePricePerHour, basePricePerHour) == 0 && durationHours == that.durationHours && dayOfWeek == that.dayOfWeek && hourOfDay == that.hourOfDay && isWeekend == that.isWeekend && isPeakHour == that.isPeakHour && month == that.month && Double.compare(that.occupationRate, occupationRate) == 0 && Double.compare(that.cancellationRate, cancellationRate) == 0 && Objects.equals(sportType, that.sportType) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sportType, location, capacity, basePricePerHour, durationHours, dayOfWeek, hourOfDay, isWeekend, isPeakHour, month, occupationRate, cancellationRate);
    }

    @Override
    public String toString() {
        return "PricingRequest{" +
                "sportType='" + sportType + '\'' +
                ", location='" + location + '\'' +
                ", basePricePerHour=" + basePricePerHour +
                '}';
    }

    public static PricingRequestBuilder builder() {
        return new PricingRequestBuilder();
    }

    public static class PricingRequestBuilder {
        private String sportType;
        private String location;
        private int capacity;
        private double basePricePerHour;
        private int durationHours;
        private int dayOfWeek;
        private int hourOfDay;
        private int isWeekend;
        private int isPeakHour;
        private int month;
        private double occupationRate;
        private double cancellationRate;

        public PricingRequestBuilder sportType(String sportType) { this.sportType = sportType; return this; }
        public PricingRequestBuilder location(String location) { this.location = location; return this; }
        public PricingRequestBuilder capacity(int capacity) { this.capacity = capacity; return this; }
        public PricingRequestBuilder basePricePerHour(double basePricePerHour) { this.basePricePerHour = basePricePerHour; return this; }
        public PricingRequestBuilder durationHours(int durationHours) { this.durationHours = durationHours; return this; }
        public PricingRequestBuilder dayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
        public PricingRequestBuilder hourOfDay(int hourOfDay) { this.hourOfDay = hourOfDay; return this; }
        public PricingRequestBuilder isWeekend(int isWeekend) { this.isWeekend = isWeekend; return this; }
        public PricingRequestBuilder isPeakHour(int isPeakHour) { this.isPeakHour = isPeakHour; return this; }
        public PricingRequestBuilder month(int month) { this.month = month; return this; }
        public PricingRequestBuilder occupationRate(double occupationRate) { this.occupationRate = occupationRate; return this; }
        public PricingRequestBuilder cancellationRate(double cancellationRate) { this.cancellationRate = cancellationRate; return this; }

        public PricingRequest build() {
            return new PricingRequest(sportType, location, capacity, basePricePerHour, durationHours, dayOfWeek, hourOfDay, isWeekend, isPeakHour, month, occupationRate, cancellationRate);
        }
    }
}