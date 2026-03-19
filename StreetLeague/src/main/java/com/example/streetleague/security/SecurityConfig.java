package com.example.streetleague.security;

import com.example.streetleague.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * DaoAuthenticationProvider : définit COMMENT les utilisateurs sont authentifiés
     * - Utilise CustomUserDetailsService pour charger l'utilisateur
     * - Utilise PasswordEncoder pour vérifier le mot de passe
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    /**
     * AuthenticationManager : requis pour l'authentification manuelle lors du login
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain : définit toutes les règles de sécurité HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ← AJOUTE CETTE LIGNE
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Auth publique
                        .requestMatchers("/auth/**").permitAll()

                        // ✅ CRUD équipe : PLAYER ou COACH
                        .requestMatchers("/team/add", "/team/update", "/team/delete/**").hasAnyRole("PLAYER", "COACH")
                        .requestMatchers("/team/showTeams", "/team/showTeamById/**").permitAll()
                        .requestMatchers("/team/*/join", "/team/*/leave").hasRole("PLAYER")

                        // ✅ CRUD match : PLAYER ou COACH (captain)
                        .requestMatchers("/match/add", "/match/update", "/match/delete/**").hasAnyRole("PLAYER", "COACH")
                        .requestMatchers("/match/showMatchs", "/match/showMatchById/**").permitAll()

                        // ✅ CRUD training : COACH seulement
                        .requestMatchers("/training/add", "/training/update", "/training/delete/**").hasRole("COACH")
                        .requestMatchers("/training/showTrainings", "/training/showTrainingById/**").permitAll()
                        .requestMatchers("/training/*/join", "/training/*/leave").hasRole("PLAYER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS : permet au frontend Angular (localhost:4200)
     * d'accéder aux APIs Spring Boot
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Autoriser uniquement le frontend Angular
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Méthodes HTTP autorisées (OPTIONS obligatoire pour les requêtes CORS preflight)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Autoriser tous les headers (requis pour Authorization: Bearer <token>)
        config.setAllowedHeaders(List.of("*"));

        // Autoriser l'envoi des credentials (headers d'autorisation)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Appliquer cette configuration à tous les endpoints
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
