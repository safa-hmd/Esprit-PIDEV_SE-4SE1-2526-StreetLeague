package com.example.streetleague.Repository;

import com.example.streetleague.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNom(String nom);

    boolean existsByNom(String nom);
}