package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Field;
import com.example.streetleague.Entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    List<Field> findBySportType(SportType sportType);

    List<Field> findByAvailable(boolean available);

    List<Field> findBySportTypeAndAvailable(SportType sportType, boolean available);
}
