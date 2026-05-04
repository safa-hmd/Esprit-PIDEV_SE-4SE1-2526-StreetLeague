package com.example.streetleague.dto;

import java.util.Objects;

public class RevenueByFieldDto {
    private String fieldName;
    private Double revenue;
    private Long reservationCount;

    public RevenueByFieldDto() {
    }

    public RevenueByFieldDto(String fieldName, Double revenue, Long reservationCount) {
        this.fieldName = fieldName;
        this.revenue = revenue;
        this.reservationCount = reservationCount;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Long getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(Long reservationCount) {
        this.reservationCount = reservationCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevenueByFieldDto that = (RevenueByFieldDto) o;
        return Objects.equals(fieldName, that.fieldName) && Objects.equals(revenue, that.revenue) && Objects.equals(reservationCount, that.reservationCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, revenue, reservationCount);
    }

    @Override
    public String toString() {
        return "RevenueByFieldDto{" +
                "fieldName='" + fieldName + '\'' +
                ", revenue=" + revenue +
                ", reservationCount=" + reservationCount +
                '}';
    }

    public static RevenueByFieldDtoBuilder builder() {
        return new RevenueByFieldDtoBuilder();
    }

    public static class RevenueByFieldDtoBuilder {
        private String fieldName;
        private Double revenue;
        private Long reservationCount;

        public RevenueByFieldDtoBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public RevenueByFieldDtoBuilder revenue(Double revenue) { this.revenue = revenue; return this; }
        public RevenueByFieldDtoBuilder reservationCount(Long reservationCount) { this.reservationCount = reservationCount; return this; }

        public RevenueByFieldDto build() {
            return new RevenueByFieldDto(fieldName, revenue, reservationCount);
        }
    }
}