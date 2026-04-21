package com.example.streetleague.Repository;

import com.example.streetleague.Entity.TravelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelRequestRepository extends JpaRepository<TravelRequest, Long> {
    List<TravelRequest> findByTeam_IdTeam(Long teamId);
}
