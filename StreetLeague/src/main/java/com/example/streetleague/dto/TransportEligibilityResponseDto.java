package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportEligibilityResponseDto {
    private boolean eligible;
    private String reason;
    private List<TransportDto> transports;

    // ===== EXPLICIT GETTERS/SETTERS =====
    public boolean isEligible() { return this.eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public String getReason() { return this.reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<TransportDto> getTransports() { return this.transports; }
    public void setTransports(List<TransportDto> transports) { this.transports = transports; }

    // ===== STATIC BUILDER HELPER =====
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
            TransportEligibilityResponseDto dto = new TransportEligibilityResponseDto();
            dto.setEligible(eligible);
            dto.setReason(reason);
            dto.setTransports(transports);
            return dto;
        }
    }
}
