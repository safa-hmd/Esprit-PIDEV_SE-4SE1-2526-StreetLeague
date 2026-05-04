package com.example.streetleague.dto;

import java.util.Objects;

public class WaterReminderResponseDTO {
    private Long id;
    private int frequency;
    private int quantity;
    private boolean active;
    private String userName;
    private String userEmail;

    public WaterReminderResponseDTO() {
    }

    public WaterReminderResponseDTO(Long id, int frequency, int quantity, boolean active, String userName, String userEmail) {
        this.id = id;
        this.frequency = frequency;
        this.quantity = quantity;
        this.active = active;
        this.userName = userName;
        this.userEmail = userEmail;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterReminderResponseDTO that = (WaterReminderResponseDTO) o;
        return frequency == that.frequency && quantity == that.quantity && active == that.active && Objects.equals(id, that.id) && Objects.equals(userName, that.userName) && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frequency, quantity, active, userName, userEmail);
    }

    @Override
    public String toString() {
        return "WaterReminderResponseDTO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", active=" + active +
                '}';
    }

    public static WaterReminderResponseDTOBuilder builder() {
        return new WaterReminderResponseDTOBuilder();
    }

    public static class WaterReminderResponseDTOBuilder {
        private Long id;
        private int frequency;
        private int quantity;
        private boolean active;
        private String userName;
        private String userEmail;

        public WaterReminderResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public WaterReminderResponseDTOBuilder frequency(int frequency) { this.frequency = frequency; return this; }
        public WaterReminderResponseDTOBuilder quantity(int quantity) { this.quantity = quantity; return this; }
        public WaterReminderResponseDTOBuilder active(boolean active) { this.active = active; return this; }
        public WaterReminderResponseDTOBuilder userName(String userName) { this.userName = userName; return this; }
        public WaterReminderResponseDTOBuilder userEmail(String userEmail) { this.userEmail = userEmail; return this; }

        public WaterReminderResponseDTO build() {
            return new WaterReminderResponseDTO(id, frequency, quantity, active, userName, userEmail);
        }
    }
}