package com.example.streetleague.dto;

import com.example.streetleague.Entity.TravelRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDto {
    private TravelRequestStatus status;
    private String adminComment;

    // ===== EXPLICIT GETTERS/SETTERS =====
    public TravelRequestStatus getStatus() { return this.status; }
    public void setStatus(TravelRequestStatus status) { this.status = status; }

    public String getAdminComment() { return this.adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }
}
