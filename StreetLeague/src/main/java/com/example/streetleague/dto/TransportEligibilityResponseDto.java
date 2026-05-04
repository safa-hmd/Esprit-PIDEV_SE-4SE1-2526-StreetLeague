package com.example.streetleague.dto;

import java.util.List;
import java.util.Objects;

public class TransportEligibilityResponseDto {
    private boolean eligible;
    private String reason;
    private List<TransportDto> transports;

    public TransportEligibilityResponseDto() {
    }

    public TransportEligibilityResponseDto(boolean eligible, String reason, List<TransportDto> transports) {
        this.eligible = eligible;
        this.reason = reason;
        this.transports = transports;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<TransportDto> getTransports() {
        return transports;
    }

    public void setTransports(List<TransportDto> transports) {
        this.transports = transports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportEligibilityResponseDto that = (TransportEligibilityResponseDto) o;
        return eligible == that.eligible && Objects.equals(reason, that.reason) && Objects.equals(transports, that.transports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eligible, reason, transports);
    }

    @Override
    public String toString() {
        return "TransportEligibilityResponseDto{" +
                "eligible=" + eligible +
                ", reason='" + reason + '\'' +
                ", transportsSize=" + (transports != null ? transports.size() : 0) +
                '}';
    }

    public static TransportEligibilityResponseDtoBuilder builder() {
        return new TransportEligibilityResponseDtoBuilder();
    }

    public static class TransportEligibilityResponseDtoBuilder {
        private boolean eligible;
        private String reason;
        private List<TransportDto> transports;

        public TransportEligibilityResponseDtoBuilder eligible(boolean eligible) { this.eligible = eligible; return this; }
        public TransportEligibilityResponseDtoBuilder reason(String reason) { this.reason = reason; return this; }
        public TransportEligibilityResponseDtoBuilder transports(List<TransportDto> transports) { this.transports = transports; return this; }

        public TransportEligibilityResponseDto build() {
            return new TransportEligibilityResponseDto(eligible, reason, transports);
        }
    }
}
