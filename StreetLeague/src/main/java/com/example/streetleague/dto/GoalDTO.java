package com.example.streetleague.dto;

import java.util.Objects;

public class GoalDTO {
    private String goalType;    // "WATER" ou "BMI"
    private double targetValue;

    public GoalDTO() {
    }

    public GoalDTO(String goalType, double targetValue) {
        this.goalType = goalType;
        this.targetValue = targetValue;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoalDTO goalDTO = (GoalDTO) o;
        return Double.compare(goalDTO.targetValue, targetValue) == 0 && Objects.equals(goalType, goalDTO.goalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goalType, targetValue);
    }

    @Override
    public String toString() {
        return "GoalDTO{" +
                "goalType='" + goalType + '\'' +
                ", targetValue=" + targetValue +
                '}';
    }

    public static GoalDTOBuilder builder() {
        return new GoalDTOBuilder();
    }

    public static class GoalDTOBuilder {
        private String goalType;
        private double targetValue;

        public GoalDTOBuilder goalType(String goalType) { this.goalType = goalType; return this; }
        public GoalDTOBuilder targetValue(double targetValue) { this.targetValue = targetValue; return this; }

        public GoalDTO build() {
            return new GoalDTO(goalType, targetValue);
        }
    }
}
