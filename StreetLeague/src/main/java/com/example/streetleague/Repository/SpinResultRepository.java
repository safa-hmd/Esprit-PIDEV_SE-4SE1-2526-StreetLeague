package com.example.streetleague.Repository;

import com.example.streetleague.Entity.SpinResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpinResultRepository extends JpaRepository<SpinResult, Long> {
    Optional<SpinResult> findByUserId(Long userId);
}
