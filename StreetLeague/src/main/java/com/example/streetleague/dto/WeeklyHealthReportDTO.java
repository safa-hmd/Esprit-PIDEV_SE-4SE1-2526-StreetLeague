package com.example.streetleague.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class WeeklyHealthReportDTO {

    private String fullName;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private double currentWeight;
    private double currentHeight;
    private double currentBmi;
    private double weightChangeThisWeek;
    private int totalWaterConsumedMl;
    private int daysGoalReached;
    private double avgDailyWaterMl;
    private List<GoalSummary> goals;
    private List<BmiEntry> bmiHistory;
    private int currentWaterStreak;
    private int longestWaterStreak;

    public WeeklyHealthReportDTO() {
    }

    public WeeklyHealthReportDTO(String fullName, LocalDate weekStart, LocalDate weekEnd, double currentWeight, double currentHeight, double currentBmi, double weightChangeThisWeek, int totalWaterConsumedMl, int daysGoalReached, double avgDailyWaterMl, List<GoalSummary> goals, List<BmiEntry> bmiHistory, int currentWaterStreak, int longestWaterStreak) {
        this.fullName = fullName;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.currentWeight = currentWeight;
        this.currentHeight = currentHeight;
        this.currentBmi = currentBmi;
        this.weightChangeThisWeek = weightChangeThisWeek;
        this.totalWaterConsumedMl = totalWaterConsumedMl;
        this.daysGoalReached = daysGoalReached;
        this.avgDailyWaterMl = avgDailyWaterMl;
        this.goals = goals;
        this.bmiHistory = bmiHistory;
        this.currentWaterStreak = currentWaterStreak;
        this.longestWaterStreak = longestWaterStreak;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }

    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }

    public double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(double currentWeight) { this.currentWeight = currentWeight; }

    public double getCurrentHeight() { return currentHeight; }
    public void setCurrentHeight(double currentHeight) { this.currentHeight = currentHeight; }

    public double getCurrentBmi() { return currentBmi; }
    public void setCurrentBmi(double currentBmi) { this.currentBmi = currentBmi; }

    public double getWeightChangeThisWeek() { return weightChangeThisWeek; }
    public void setWeightChangeThisWeek(double weightChangeThisWeek) { this.weightChangeThisWeek = weightChangeThisWeek; }

    public int getTotalWaterConsumedMl() { return totalWaterConsumedMl; }
    public void setTotalWaterConsumedMl(int totalWaterConsumedMl) { this.totalWaterConsumedMl = totalWaterConsumedMl; }

    public int getDaysGoalReached() { return daysGoalReached; }
    public void setDaysGoalReached(int daysGoalReached) { this.daysGoalReached = daysGoalReached; }

    public double getAvgDailyWaterMl() { return avgDailyWaterMl; }
    public void setAvgDailyWaterMl(double avgDailyWaterMl) { this.avgDailyWaterMl = avgDailyWaterMl; }

    public List<GoalSummary> getGoals() { return goals; }
    public void setGoals(List<GoalSummary> goals) { this.goals = goals; }

    public List<BmiEntry> getBmiHistory() { return bmiHistory; }
    public void setBmiHistory(List<BmiEntry> bmiHistory) { this.bmiHistory = bmiHistory; }

    public int getCurrentWaterStreak() { return currentWaterStreak; }
    public void setCurrentWaterStreak(int currentWaterStreak) { this.currentWaterStreak = currentWaterStreak; }

    public int getLongestWaterStreak() { return longestWaterStreak; }
    public void setLongestWaterStreak(int longestWaterStreak) { this.longestWaterStreak = longestWaterStreak; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyHealthReportDTO that = (WeeklyHealthReportDTO) o;
        return Double.compare(that.currentBmi, currentBmi) == 0 && Objects.equals(fullName, that.fullName) && Objects.equals(weekStart, that.weekStart) && Objects.equals(weekEnd, that.weekEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, weekStart, weekEnd, currentBmi);
    }

    @Override
    public String toString() {
        return "WeeklyHealthReportDTO{" +
                "fullName='" + fullName + '\'' +
                ", weekStart=" + weekStart +
                ", weekEnd=" + weekEnd +
                ", currentBmi=" + currentBmi +
                '}';
    }

    public static WeeklyHealthReportDTOBuilder builder() {
        return new WeeklyHealthReportDTOBuilder();
    }

    public static class WeeklyHealthReportDTOBuilder {
        private String fullName;
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private double currentWeight;
        private double currentHeight;
        private double currentBmi;
        private double weightChangeThisWeek;
        private int totalWaterConsumedMl;
        private int daysGoalReached;
        private double avgDailyWaterMl;
        private List<GoalSummary> goals;
        private List<BmiEntry> bmiHistory;
        private int currentWaterStreak;
        private int longestWaterStreak;

        public WeeklyHealthReportDTOBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public WeeklyHealthReportDTOBuilder weekStart(LocalDate weekStart) { this.weekStart = weekStart; return this; }
        public WeeklyHealthReportDTOBuilder weekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; return this; }
        public WeeklyHealthReportDTOBuilder currentWeight(double currentWeight) { this.currentWeight = currentWeight; return this; }
        public WeeklyHealthReportDTOBuilder currentHeight(double currentHeight) { this.currentHeight = currentHeight; return this; }
        public WeeklyHealthReportDTOBuilder currentBmi(double currentBmi) { this.currentBmi = currentBmi; return this; }
        public WeeklyHealthReportDTOBuilder weightChangeThisWeek(double weightChangeThisWeek) { this.weightChangeThisWeek = weightChangeThisWeek; return this; }
        public WeeklyHealthReportDTOBuilder totalWaterConsumedMl(int totalWaterConsumedMl) { this.totalWaterConsumedMl = totalWaterConsumedMl; return this; }
        public WeeklyHealthReportDTOBuilder daysGoalReached(int daysGoalReached) { this.daysGoalReached = daysGoalReached; return this; }
        public WeeklyHealthReportDTOBuilder avgDailyWaterMl(double avgDailyWaterMl) { this.avgDailyWaterMl = avgDailyWaterMl; return this; }
        public WeeklyHealthReportDTOBuilder goals(List<GoalSummary> goals) { this.goals = goals; return this; }
        public WeeklyHealthReportDTOBuilder bmiHistory(List<BmiEntry> bmiHistory) { this.bmiHistory = bmiHistory; return this; }
        public WeeklyHealthReportDTOBuilder currentWaterStreak(int currentWaterStreak) { this.currentWaterStreak = currentWaterStreak; return this; }
        public WeeklyHealthReportDTOBuilder longestWaterStreak(int longestWaterStreak) { this.longestWaterStreak = longestWaterStreak; return this; }

        public WeeklyHealthReportDTO build() {
            return new WeeklyHealthReportDTO(fullName, weekStart, weekEnd, currentWeight, currentHeight, currentBmi, weightChangeThisWeek, totalWaterConsumedMl, daysGoalReached, avgDailyWaterMl, goals, bmiHistory, currentWaterStreak, longestWaterStreak);
        }
    }

    public static class GoalSummary {
        private String goalType;
        private double targetValue;
        private boolean achieved;

        public GoalSummary() {
        }

        public GoalSummary(String goalType, double targetValue, boolean achieved) {
            this.goalType = goalType;
            this.targetValue = targetValue;
            this.achieved = achieved;
        }

        public String getGoalType() { return goalType; }
        public void setGoalType(String goalType) { this.goalType = goalType; }

        public double getTargetValue() { return targetValue; }
        public void setTargetValue(double targetValue) { this.targetValue = targetValue; }

        public boolean isAchieved() { return achieved; }
        public void setAchieved(boolean achieved) { this.achieved = achieved; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GoalSummary that = (GoalSummary) o;
            return Double.compare(that.targetValue, targetValue) == 0 && achieved == that.achieved && Objects.equals(goalType, that.goalType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(goalType, targetValue, achieved);
        }

        public static GoalSummaryBuilder builder() {
            return new GoalSummaryBuilder();
        }

        public static class GoalSummaryBuilder {
            private String goalType;
            private double targetValue;
            private boolean achieved;

            public GoalSummaryBuilder goalType(String goalType) { this.goalType = goalType; return this; }
            public GoalSummaryBuilder targetValue(double targetValue) { this.targetValue = targetValue; return this; }
            public GoalSummaryBuilder achieved(boolean achieved) { this.achieved = achieved; return this; }

            public GoalSummary build() {
                return new GoalSummary(goalType, targetValue, achieved);
            }
        }
    }

    public static class BmiEntry {
        private LocalDate date;
        private double bmi;
        private double weight;

        public BmiEntry() {
        }

        public BmiEntry(LocalDate date, double bmi, double weight) {
            this.date = date;
            this.bmi = bmi;
            this.weight = weight;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public double getBmi() { return bmi; }
        public void setBmi(double bmi) { this.bmi = bmi; }

        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BmiEntry bmiEntry = (BmiEntry) o;
            return Double.compare(bmiEntry.bmi, bmi) == 0 && Double.compare(bmiEntry.weight, weight) == 0 && Objects.equals(date, bmiEntry.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, bmi, weight);
        }

        public static BmiEntryBuilder builder() {
            return new BmiEntryBuilder();
        }

        public static class BmiEntryBuilder {
            private LocalDate date;
            private double bmi;
            private double weight;

            public BmiEntryBuilder date(LocalDate date) { this.date = date; return this; }
            public BmiEntryBuilder bmi(double bmi) { this.bmi = bmi; return this; }
            public BmiEntryBuilder weight(double weight) { this.weight = weight; return this; }

            public BmiEntry build() {
                return new BmiEntry(date, bmi, weight);
            }
        }
    }
}