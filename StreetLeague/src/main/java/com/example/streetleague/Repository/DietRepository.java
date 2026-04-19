package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
    Optional<Diet> findTopByUserIdOrderByCreatedDateDesc(Long userId);
}