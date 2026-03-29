package com.example.streetleague.security;

import com.example.streetleague.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // Désactiver CSRF (application stateless, JWT protège les requêtes)
                .csrf(AbstractHttpConfigurer::disable)

                // Activer CORS pour le frontend Angular (localhost:4200)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Session STATELESS : chaque requête doit contenir un JWT valide
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Provider d'authentification
                .authenticationProvider(authProvider())

                // Règles d'autorisation des endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics (login, register, forgot/reset password)
                        .requestMatchers("/auth/**").permitAll()
                        // Endpoints protégés par rôle
                        // Posts
                        .requestMatchers(HttpMethod.GET,    "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts/like/**").hasRole("PLAYER")
                        .requestMatchers(HttpMethod.POST,   "/posts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/posts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/posts/**").hasRole("ADMIN")


                        // Comments - PLAYER
                        .requestMatchers(HttpMethod.GET,    "/comments/**").hasAnyRole("ADMIN", "PLAYER") //
                        .requestMatchers(HttpMethod.POST,   "/comments/**").hasRole("PLAYER")
                        .requestMatchers(HttpMethod.PUT,    "/comments/**").hasRole("PLAYER")
                        .requestMatchers(HttpMethod.DELETE, "/comments/**").hasRole("PLAYER")


                        .requestMatchers("/water-reminders/**").hasRole("PLAYER")


                        // Tout autre endpoint nécessite une authentification
                        .anyRequest().authenticated()
                )

                // Insérer le filtre JWT AVANT UsernamePasswordAuthenticationFilter
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
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:59619"));

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
