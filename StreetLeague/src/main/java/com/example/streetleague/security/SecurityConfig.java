package com.example.streetleague.security;

import com.example.streetleague.security.jwt.JwtAuthFilter;
import com.example.streetleague.security.jwt.JwtService;
import com.example.streetleague.Repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.http.HttpMethod;
import java.util.List;
import java.util.List;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final UserRepository userRepository;
    private final JwtService jwtService;

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
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
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
                        // AUTH
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // USERS
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/by-email").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/team/**").authenticated()

                        // TOURNAMENTS
                        .requestMatchers(HttpMethod.GET,
                                "/api/tournaments").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/tournaments/**").permitAll()

                        // COACH TRAVEL
                        .requestMatchers(HttpMethod.GET,
                                "/api/coach/travel/**").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/coach/travel/**").authenticated()

                        // ADMIN TRAVEL
                        .requestMatchers(
                                "/api/admin/travel/**").authenticated()

                        // TEAMS
                        .requestMatchers("/team/add",
                                "/team/update/**").hasAnyRole("PLAYER", "COACH")
                        .requestMatchers("/team/delete/**")
                        .hasAnyRole("PLAYER", "COACH", "ADMIN")
                        .requestMatchers("/team/showTeams",
                                "/team/showTeamById/**",
                                "/team/myTeams").permitAll()
                        .requestMatchers("/team/*/join",
                                "/team/*/leave").hasRole("PLAYER")

                        // MATCHES
                        .requestMatchers("/match/add",
                                "/match/update").hasAnyRole("PLAYER", "COACH")
                        .requestMatchers("/match/delete/**")
                        .hasAnyRole("PLAYER", "COACH", "ADMIN")
                        .requestMatchers("/match/showMatchs",
                                "/match/showMatchById/**").permitAll()

                        // TRAININGS
                        .requestMatchers("/training/add",
                                "/training/update").hasRole("COACH")
                        .requestMatchers("/training/delete/**")
                        .hasAnyRole("COACH", "ADMIN")
                        .requestMatchers("/training/showTrainings",
                                "/training/showTrainingById/**").permitAll()
                        .requestMatchers("/training/*/join",
                                "/training/*/leave").hasRole("PLAYER")

                        // STUDENT / TEACHER (from main)
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/teacher/**").hasRole("TEACHER")

                        // TOUT LE RESTE
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(
                                new OAuth2AuthSuccessHandler(userRepository, jwtService))
                );

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
