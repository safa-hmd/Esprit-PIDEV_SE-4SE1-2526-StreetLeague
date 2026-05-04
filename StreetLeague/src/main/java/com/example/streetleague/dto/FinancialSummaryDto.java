package com.example.streetleague.dto;

import java.util.Objects;

public class FinancialSummaryDto {
    private Double totalRevenue;
    private Long totalPayments;
    private Long totalRefunds;
    private Long pendingCount;

    public FinancialSummaryDto() {
    }

    public FinancialSummaryDto(Double totalRevenue, Long totalPayments, Long totalRefunds, Long pendingCount) {
        this.totalRevenue = totalRevenue;
        this.totalPayments = totalPayments;
        this.totalRefunds = totalRefunds;
        this.pendingCount = pendingCount;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(Long totalPayments) {
        this.totalPayments = totalPayments;
    }

    public Long getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(Long totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public Long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Long pendingCount) {
        this.pendingCount = pendingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialSummaryDto that = (FinancialSummaryDto) o;
        return Objects.equals(totalRevenue, that.totalRevenue) && Objects.equals(totalPayments, that.totalPayments) && Objects.equals(totalRefunds, that.totalRefunds) && Objects.equals(pendingCount, that.pendingCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalRevenue, totalPayments, totalRefunds, pendingCount);
    }

    @Override
    public String toString() {
        return "FinancialSummaryDto{" +
                "totalRevenue=" + totalRevenue +
                ", totalPayments=" + totalPayments +
                ", totalRefunds=" + totalRefunds +
                ", pendingCount=" + pendingCount +
                '}';
    }

    public static FinancialSummaryDtoBuilder builder() {
        return new FinancialSummaryDtoBuilder();
    }

    public static class FinancialSummaryDtoBuilder {
        private Double totalRevenue;
        private Long totalPayments;
        private Long totalRefunds;
        private Long pendingCount;

        public FinancialSummaryDtoBuilder totalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public FinancialSummaryDtoBuilder totalPayments(Long totalPayments) { this.totalPayments = totalPayments; return this; }
        public FinancialSummaryDtoBuilder totalRefunds(Long totalRefunds) { this.totalRefunds = totalRefunds; return this; }
        public FinancialSummaryDtoBuilder pendingCount(Long pendingCount) { this.pendingCount = pendingCount; return this; }

        public FinancialSummaryDto build() {
            return new FinancialSummaryDto(totalRevenue, totalPayments, totalRefunds, pendingCount);
        }
    }
}