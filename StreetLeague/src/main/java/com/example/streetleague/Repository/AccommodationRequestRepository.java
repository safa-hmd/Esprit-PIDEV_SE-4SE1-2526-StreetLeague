package com.example.streetleague.Repository;

import com.example.streetleague.Entity.AccommodationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationRequestRepository extends JpaRepository<AccommodationRequest, Long> {
    List<AccommodationRequest> findByCoachId(Long coachId);
    List<AccommodationRequest> findByStatus(String status);
    List<AccommodationRequest> findByAccommodationId(Long accommodationId);
}
