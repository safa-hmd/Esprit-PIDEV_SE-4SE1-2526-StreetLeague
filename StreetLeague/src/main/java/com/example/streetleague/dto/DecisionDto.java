package com.example.streetleague.dto;

import com.example.streetleague.Entity.TravelRequestStatus;
import java.util.Objects;

public class DecisionDto {
    private TravelRequestStatus status;
    private String adminComment;

    public DecisionDto() {
    }

    public DecisionDto(TravelRequestStatus status, String adminComment) {
        this.status = status;
        this.adminComment = adminComment;
    }

    public TravelRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TravelRequestStatus status) {
        this.status = status;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecisionDto that = (DecisionDto) o;
        return status == that.status && Objects.equals(adminComment, that.adminComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, adminComment);
    }

    @Override
    public String toString() {
        return "DecisionDto{" +
                "status=" + status +
                ", adminComment='" + adminComment + '\'' +
                '}';
    }

    public static DecisionDtoBuilder builder() {
        return new DecisionDtoBuilder();
    }

    public static class DecisionDtoBuilder {
        private TravelRequestStatus status;
        private String adminComment;

        public DecisionDtoBuilder status(TravelRequestStatus status) { this.status = status; return this; }
        public DecisionDtoBuilder adminComment(String adminComment) { this.adminComment = adminComment; return this; }

        public DecisionDto build() {
            return new DecisionDto(status, adminComment);
        }
    }
}
