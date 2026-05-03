package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Field;
import com.example.streetleague.Entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    List<Field> findByAvailable(boolean available);

    List<Field> findBySportType(SportType sportType);

    @Query("SELECT f FROM Field f WHERE f.available = true")
    List<Field> findAvailableFields();

    @Query("SELECT f FROM Field f WHERE f.location = :location AND f.available = true")
    List<Field> findAvailableFieldsByLocation(@Param("location") String location);

    @Query("SELECT f FROM Field f WHERE f.location = :location AND cast(f.sportType as string) = :type AND f.available = true")
    List<Field> findAvailableFieldsByLocationAndType(@Param("location") String location, @Param("type") String type);

    @Query("SELECT f FROM Field f WHERE f.id NOT IN " +
           "(SELECT r.field.id FROM FieldReservation r WHERE " +
           "r.startTime < :endTime AND r.endTime > :startTime AND r.status = 'CONFIRMED')")
    List<Field> findAvailableFieldsForTimeSlot(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);
}
