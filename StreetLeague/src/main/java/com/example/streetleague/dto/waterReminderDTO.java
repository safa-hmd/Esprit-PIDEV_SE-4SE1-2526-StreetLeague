package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import java.util.Objects;

public class waterReminderDTO {
    private Long id;

    @Min(value = 1)
    @Max(value = 120)
    private int frequency;

    @Min(value = 100, message = "Quantity must be at least 100ml")
    @Max(value = 2000, message = "Quantity must be at most 2000ml")
    private int quantity;
    private boolean active;

    private Long userId;

    public waterReminderDTO() {
    }

    public waterReminderDTO(Long id, int frequency, int quantity, boolean active, Long userId) {
        this.id = id;
        this.frequency = frequency;
        this.quantity = quantity;
        this.active = active;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        waterReminderDTO that = (waterReminderDTO) o;
        return frequency == that.frequency && quantity == that.quantity && active == that.active && Objects.equals(id, that.id) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frequency, quantity, active, userId);
    }

    @Override
    public String toString() {
        return "waterReminderDTO{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", quantity=" + quantity +
                ", active=" + active +
                '}';
    }

    public static waterReminderDTOBuilder builder() {
        return new waterReminderDTOBuilder();
    }

    public static class waterReminderDTOBuilder {
        private Long id;
        private int frequency;
        private int quantity;
        private boolean active;
        private Long userId;

        public waterReminderDTOBuilder id(Long id) { this.id = id; return this; }
        public waterReminderDTOBuilder frequency(int frequency) { this.frequency = frequency; return this; }
        public waterReminderDTOBuilder quantity(int quantity) { this.quantity = quantity; return this; }
        public waterReminderDTOBuilder active(boolean active) { this.active = active; return this; }
        public waterReminderDTOBuilder userId(Long userId) { this.userId = userId; return this; }

        public waterReminderDTO build() {
            return new waterReminderDTO(id, frequency, quantity, active, userId);
        }
    }
}
