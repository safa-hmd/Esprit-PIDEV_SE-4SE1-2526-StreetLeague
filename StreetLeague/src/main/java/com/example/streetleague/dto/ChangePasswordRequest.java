package com.example.streetleague.dto;

import java.util.Objects;

public class ChangePasswordRequest {

    private String currentPassword;

    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangePasswordRequest that = (ChangePasswordRequest) o;
        return Objects.equals(currentPassword, that.currentPassword) && Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPassword, newPassword);
    }

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "currentPassword='***', newPassword='***'}";
    }

    public static ChangePasswordRequestBuilder builder() {
        return new ChangePasswordRequestBuilder();
    }

    public static class ChangePasswordRequestBuilder {
        private String currentPassword;
        private String newPassword;

        public ChangePasswordRequestBuilder currentPassword(String currentPassword) { this.currentPassword = currentPassword; return this; }
        public ChangePasswordRequestBuilder newPassword(String newPassword) { this.newPassword = newPassword; return this; }

        public ChangePasswordRequest build() {
            return new ChangePasswordRequest(currentPassword, newPassword);
        }
    }
}
