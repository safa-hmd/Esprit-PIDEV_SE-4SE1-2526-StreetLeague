package com.example.streetleague.dto;

import java.util.Objects;

public class UserProfileResponse {
    private Long idUser;
    private String fullName;
    private String email;
    private String role;
    // Statistiques
    private int teamCount;
    private int matchCount;
    private int trainingCount;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long idUser, String fullName, String email, String role, int teamCount, int matchCount, int trainingCount) {
        this.idUser = idUser;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.teamCount = teamCount;
        this.matchCount = matchCount;
        this.trainingCount = trainingCount;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public int getTrainingCount() {
        return trainingCount;
    }

    public void setTrainingCount(int trainingCount) {
        this.trainingCount = trainingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileResponse that = (UserProfileResponse) o;
        return teamCount == that.teamCount && matchCount == that.matchCount && trainingCount == that.trainingCount && Objects.equals(idUser, that.idUser) && Objects.equals(fullName, that.fullName) && Objects.equals(email, that.email) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, fullName, email, role, teamCount, matchCount, trainingCount);
    }

    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "idUser=" + idUser +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
    }

    public static class UserProfileResponseBuilder {
        private Long idUser;
        private String fullName;
        private String email;
        private String role;
        private int teamCount;
        private int matchCount;
        private int trainingCount;

        public UserProfileResponseBuilder idUser(Long idUser) { this.idUser = idUser; return this; }
        public UserProfileResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserProfileResponseBuilder email(String email) { this.email = email; return this; }
        public UserProfileResponseBuilder role(String role) { this.role = role; return this; }
        public UserProfileResponseBuilder teamCount(int teamCount) { this.teamCount = teamCount; return this; }
        public UserProfileResponseBuilder matchCount(int matchCount) { this.matchCount = matchCount; return this; }
        public UserProfileResponseBuilder trainingCount(int trainingCount) { this.trainingCount = trainingCount; return this; }

        public UserProfileResponse build() {
            return new UserProfileResponse(idUser, fullName, email, role, teamCount, matchCount, trainingCount);
        }
    }
}