package com.example.streetleague.dto;

import java.util.Objects;

public class WaterLogDTO {
    private int amount; // ml bus

    public WaterLogDTO() {
    }

    public WaterLogDTO(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterLogDTO that = (WaterLogDTO) o;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return "WaterLogDTO{" +
                "amount=" + amount +
                '}';
    }

    public static WaterLogDTOBuilder builder() {
        return new WaterLogDTOBuilder();
    }

    public static class WaterLogDTOBuilder {
        private int amount;

        public WaterLogDTOBuilder amount(int amount) { this.amount = amount; return this; }

        public WaterLogDTO build() {
            return new WaterLogDTO(amount);
        }
    }
}
