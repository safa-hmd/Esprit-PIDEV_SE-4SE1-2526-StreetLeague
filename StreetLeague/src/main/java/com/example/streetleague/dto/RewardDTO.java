package com.example.streetleague.dto;

import java.util.Objects;

public class RewardDTO {
    private String result;    // "BADGE", "FREE_DELIVERY", "COUPON_5", "COUPON_10", "TRY_AGAIN"
    private String message;
    private boolean canRetry;
    private int segmentIndex;

    public RewardDTO() {
    }

    public RewardDTO(String result, String message, boolean canRetry, int segmentIndex) {
        this.result = result;
        this.message = message;
        this.canRetry = canRetry;
        this.segmentIndex = segmentIndex;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCanRetry() {
        return canRetry;
    }

    public void setCanRetry(boolean canRetry) {
        this.canRetry = canRetry;
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RewardDTO rewardDTO = (RewardDTO) o;
        return canRetry == rewardDTO.canRetry && segmentIndex == rewardDTO.segmentIndex && Objects.equals(result, rewardDTO.result) && Objects.equals(message, rewardDTO.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, message, canRetry, segmentIndex);
    }

    @Override
    public String toString() {
        return "RewardDTO{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", canRetry=" + canRetry +
                '}';
    }

    public static RewardDTOBuilder builder() {
        return new RewardDTOBuilder();
    }

    public static class RewardDTOBuilder {
        private String result;
        private String message;
        private boolean canRetry;
        private int segmentIndex;

        public RewardDTOBuilder result(String result) { this.result = result; return this; }
        public RewardDTOBuilder message(String message) { this.message = message; return this; }
        public RewardDTOBuilder canRetry(boolean canRetry) { this.canRetry = canRetry; return this; }
        public RewardDTOBuilder segmentIndex(int segmentIndex) { this.segmentIndex = segmentIndex; return this; }

        public RewardDTO build() {
            return new RewardDTO(result, message, canRetry, segmentIndex);
        }
    }
}
