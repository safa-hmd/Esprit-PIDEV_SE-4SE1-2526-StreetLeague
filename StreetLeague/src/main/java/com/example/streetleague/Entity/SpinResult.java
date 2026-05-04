package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class SpinResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String result;      // "BADGE", "FREE_DELIVERY", "COUPON_5", "COUPON_10", "TRY_AGAIN"
    private boolean canRetry;   // true si résultat = TRY_AGAIN
    private boolean hasSpun;    // a-t-il déjà tourné ?
    private LocalDate spinDate;

    @OneToOne
    private User user;

    public SpinResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isCanRetry() {
        return canRetry;
    }

    public void setCanRetry(boolean canRetry) {
        this.canRetry = canRetry;
    }

    public boolean isHasSpun() {
        return hasSpun;
    }

    public void setHasSpun(boolean hasSpun) {
        this.hasSpun = hasSpun;
    }

    public LocalDate getSpinDate() {
        return spinDate;
    }

    public void setSpinDate(LocalDate spinDate) {
        this.spinDate = spinDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpinResult that = (SpinResult) o;
        return canRetry == that.canRetry && hasSpun == that.hasSpun && Objects.equals(id, that.id) && Objects.equals(result, that.result) && Objects.equals(spinDate, that.spinDate) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, result, canRetry, hasSpun, spinDate, user);
    }

    @Override
    public String toString() {
        return "SpinResult{" +
                "id=" + id +
                ", result='" + result + '\'' +
                ", canRetry=" + canRetry +
                ", hasSpun=" + hasSpun +
                ", spinDate=" + spinDate +
                ", user=" + user +
                '}';
    }
}

