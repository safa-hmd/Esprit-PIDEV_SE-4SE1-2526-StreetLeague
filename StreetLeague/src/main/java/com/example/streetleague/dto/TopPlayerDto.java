package com.example.streetleague.dto;

import java.util.Objects;

public class TopPlayerDto {
    private String playerName;
    private String email;
    private Double totalSpent;
    private Long paymentCount;

    public TopPlayerDto() {
    }

    public TopPlayerDto(String playerName, String email, Double totalSpent, Long paymentCount) {
        this.playerName = playerName;
        this.email = email;
        this.totalSpent = totalSpent;
        this.paymentCount = paymentCount;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Long getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(Long paymentCount) {
        this.paymentCount = paymentCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopPlayerDto that = (TopPlayerDto) o;
        return Objects.equals(playerName, that.playerName) && Objects.equals(email, that.email) && Objects.equals(totalSpent, that.totalSpent) && Objects.equals(paymentCount, that.paymentCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, email, totalSpent, paymentCount);
    }

    @Override
    public String toString() {
        return "TopPlayerDto{" +
                "playerName='" + playerName + '\'' +
                ", email='" + email + '\'' +
                ", totalSpent=" + totalSpent +
                '}';
    }

    public static TopPlayerDtoBuilder builder() {
        return new TopPlayerDtoBuilder();
    }

    public static class TopPlayerDtoBuilder {
        private String playerName;
        private String email;
        private Double totalSpent;
        private Long paymentCount;

        public TopPlayerDtoBuilder playerName(String playerName) { this.playerName = playerName; return this; }
        public TopPlayerDtoBuilder email(String email) { this.email = email; return this; }
        public TopPlayerDtoBuilder totalSpent(Double totalSpent) { this.totalSpent = totalSpent; return this; }
        public TopPlayerDtoBuilder paymentCount(Long paymentCount) { this.paymentCount = paymentCount; return this; }

        public TopPlayerDto build() {
            return new TopPlayerDto(playerName, email, totalSpent, paymentCount);
        }
    }
}