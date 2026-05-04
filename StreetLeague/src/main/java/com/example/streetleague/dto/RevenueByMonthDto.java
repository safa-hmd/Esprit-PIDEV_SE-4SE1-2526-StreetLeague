package com.example.streetleague.dto;

import java.util.Objects;

public class RevenueByMonthDto {
    private String month; // format "2025-03"
    private Double revenue;

    public RevenueByMonthDto() {
    }

    public RevenueByMonthDto(String month, Double revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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
        RevenueByMonthDto that = (RevenueByMonthDto) o;
        return Objects.equals(month, that.month) && Objects.equals(revenue, that.revenue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, revenue);
    }

    @Override
    public String toString() {
        return "RevenueByMonthDto{" +
                "month='" + month + '\'' +
                ", revenue=" + revenue +
                '}';
    }

    public static RevenueByMonthDtoBuilder builder() {
        return new RevenueByMonthDtoBuilder();
    }

    public static class RevenueByMonthDtoBuilder {
        private String month;
        private Double revenue;

        public RevenueByMonthDtoBuilder month(String month) { this.month = month; return this; }
        public RevenueByMonthDtoBuilder revenue(Double revenue) { this.revenue = revenue; return this; }

        public RevenueByMonthDto build() {
            return new RevenueByMonthDto(month, revenue);
        }
    }
}