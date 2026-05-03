package com.example.streetleague.Repository;

import com.example.streetleague.Entity.SpinResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpinResultRepository extends JpaRepository<SpinResult, Long> {
    @Query("SELECT s FROM SpinResult s WHERE s.user.idUser = :userId")
    Optional<SpinResult> findByUserId(@Param("userId") Long userId);}
