package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findByStatus(String status);
    List<Accommodation> findByLogisticsId(Long logisticsId);
}
