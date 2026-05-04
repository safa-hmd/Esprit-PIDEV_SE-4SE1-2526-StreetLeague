package com.example.streetleague.config;

import com.example.streetleague.domain.Role;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Entity.Level;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only run if we don't have enough players
        if (userRepository.count() < 3) {
            System.out.println("=== INSERTING TEST DATA FOR COACH DEMO ===");

            // 1. Ensure we have a Coach
            User coach = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.COACH)
                    .findFirst()
                    .orElseGet(() -> {
                        User newCoach = User.builder()
                                .email("coach@streetleague.com")
                                .fullName("Demo Coach")
                                .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lXWC")
                                .role(Role.COACH)
                                .enabled(true)
                                .build();
                        return userRepository.save(newCoach);
                    });

            // 2. Insert test players
            User player1 = User.builder()
                    .email("player1@streetleague.com")
                    .fullName("Ahmed Ben Salah")
                    .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lXWC")
                    .role(Role.PLAYER)
                    .enabled(true)
                    .build();

            User player2 = User.builder()
                    .email("player2@streetleague.com")
                    .fullName("Sami Trabelsi")
                    .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lXWC")
                    .role(Role.PLAYER)
                    .enabled(true)
                    .build();

            User player3 = User.builder()
                    .email("player3@streetleague.com")
                    .fullName("Karim Mansour")
                    .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lXWC")
                    .role(Role.PLAYER)
                    .enabled(true)
                    .build();

            userRepository.save(player1);
            userRepository.save(player2);
            userRepository.save(player3);

            // 3. Create the team
            Team team = Team.builder()
                    .name("FC Tunis")
                    .description("Street League Team")
                    .sport("FOOTBALL")
                    .city("Tunis")
                    .level(Level.INTERMEDIATE)
                    .creationDate(LocalDate.now())
                    .captain(coach)
                    .players(new HashSet<>())
                    .build();

            // Link players
            team.getPlayers().add(player1);
            team.getPlayers().add(player2);
            team.getPlayers().add(player3);

            teamRepository.save(team);
            
            System.out.println("=== TEST DATA INSERTED SUCCESSFULLY ===");
        }
    }
}
