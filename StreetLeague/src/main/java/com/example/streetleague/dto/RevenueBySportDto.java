package com.example.streetleague.dto;

import java.util.Objects;

public class RevenueBySportDto {
    private String sportType;
    private Double revenue;

    public RevenueBySportDto() {
    }

    public RevenueBySportDto(String sportType, Double revenue) {
        this.sportType = sportType;
        this.revenue = revenue;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevenueBySportDto that = (RevenueBySportDto) o;
        return Objects.equals(sportType, that.sportType) && Objects.equals(revenue, that.revenue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sportType, revenue);
    }

    @Override
    public String toString() {
        return "RevenueBySportDto{" +
                "sportType='" + sportType + '\'' +
                ", revenue=" + revenue +
                '}';
    }

    public static RevenueBySportDtoBuilder builder() {
        return new RevenueBySportDtoBuilder();
    }

    public static class RevenueBySportDtoBuilder {
        private String sportType;
        private Double revenue;

        public RevenueBySportDtoBuilder sportType(String sportType) { this.sportType = sportType; return this; }
        public RevenueBySportDtoBuilder revenue(Double revenue) { this.revenue = revenue; return this; }

        public RevenueBySportDto build() {
            return new RevenueBySportDto(sportType, revenue);
        }
    }
}