package com.example.streetleague.security;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.security.jwt.JwtAuthFilter;
import com.example.streetleague.security.jwt.JwtService;
import com.example.streetleague.Repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("authProvider") DaoAuthenticationProvider daoAuthProvider
    ) throws Exception {
        http

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(daoAuthProvider)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()          // ✅ couvre /auth/complete-google-register
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/ws-chat/**").permitAll()        // ✅ WebSocket
                        .requestMatchers("/api/chat/**").permitAll()      // ✅ Chat History
                        // Lecture publique (listes / détails) — écriture reste soumise à authenticated() plus bas
                        .requestMatchers(HttpMethod.GET,
                                "/api/communaute", "/api/communaute/**",
                                "/api/evenement", "/api/evenement/**",
                                "/api/contrat", "/api/contrat/**",
                                "/api/contrat-sponsor", "/api/contrat-sponsor/**"
                        ).permitAll()
                        // Ajout des endpoints de test pour sponsoring stats et recherche
                        .requestMatchers("/api/sponsoring/test/**").permitAll()
                        // Aligné sur StreetLeagueApp (demo RBAC + APIs sponsor)
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        // Front /client (PLAYER, COACH, etc.) : CRUD API métier avec JWT valide
                        .requestMatchers(HttpMethod.PATCH, "/api/sponsor/*/status").hasRole("ADMIN")
                        .requestMatchers("/api/sponsor/**").authenticated()
                        .requestMatchers("/api/sponsoring/**").authenticated()
                        .requestMatchers("/api/communaute/**").authenticated()
                        .requestMatchers("/api/contrat/**").authenticated()
                        .requestMatchers("/api/contrat-sponsor/**").authenticated()
                        .requestMatchers("/user/profile").authenticated()
                        .requestMatchers("/api/schedule/**").permitAll()
                        .requestMatchers("/api/recommend/**", "/notification/**").permitAll()
                        //.requestMatchers("/matchmaking/**").permitAll()
                        .requestMatchers("/api/matchmaking/**").permitAll()
                        .requestMatchers("/api/performance/**").permitAll()
                        .requestMatchers("/api/fields/**").permitAll()
                        .requestMatchers("/api/registrations/**").permitAll()
                        .requestMatchers("/api/tournaments/**").permitAll()



                        .requestMatchers("/team/add", "/team/update/**").hasAnyRole("PLAYER", "COACH")
                        .requestMatchers("/team/delete/**").hasAnyRole("PLAYER", "COACH", "ADMIN")
                        .requestMatchers("/team/showTeams", "/team/showTeamById/**", "/team/myTeams").permitAll()
                        .requestMatchers("/team/*/join", "/team/*/leave").hasRole("PLAYER")
                        .requestMatchers("/match/add", "/match/update").hasAnyRole("PLAYER", "COACH")
                        .requestMatchers("/match/delete/**").hasAnyRole("PLAYER", "COACH", "ADMIN")
                        .requestMatchers("/match/showMatchs", "/match/showMatchById/**").permitAll()
                        .requestMatchers("/matches-history/**").permitAll()
                        .requestMatchers("/match/*/respond").permitAll()
                        .requestMatchers("/training/add", "/training/update").hasRole("COACH")
                        .requestMatchers("/training/delete/**").hasAnyRole("COACH", "ADMIN")
                        .requestMatchers("/training/showTrainings", "/training/showTrainingById/**").permitAll()
                        .requestMatchers("/training/*/join", "/training/*/leave").hasRole("PLAYER")
                        .requestMatchers("/api/pricing/**").permitAll()

                        // Endpoints publics (login, register, forgot/reset password)

                        //houssem

                        .requestMatchers("/api/transporteurs/**").permitAll()
                        .requestMatchers("/livraisons/**").permitAll()



                        // Auth publique

                        .requestMatchers("/auth/**").permitAll()

                        // WebSocket — DOIT être avant tout autre règle
                        .requestMatchers("/ws/**").permitAll()

                        // Health
                        .requestMatchers("/health/**").permitAll()

                        // Posts
                        .requestMatchers(HttpMethod.GET,    "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/posts/like/**").hasRole("PLAYER")
                        .requestMatchers(HttpMethod.POST,   "/posts/dislike/**").hasRole("PLAYER")
                        .requestMatchers(HttpMethod.POST,   "/posts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/posts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/posts/**").hasRole("ADMIN")

                        // Comments
                        .requestMatchers(HttpMethod.GET,    "/comments/**").hasAnyRole("ADMIN", "PLAYER")
                        .requestMatchers(HttpMethod.POST,   "/comments/**").hasRole("PLAYER")
                        .requestMatchers(HttpMethod.PUT,    "/comments/**").hasRole("PLAYER")
                        .requestMatchers(HttpMethod.DELETE, "/comments/**").hasRole("PLAYER")

                        // Water reminders
                        .requestMatchers("/water-reminders/**").hasRole("PLAYER")



                        // Tout autre endpoint nécessite une authentification

                        .anyRequest().authenticated()
                )
                // ✅ FIX PRINCIPAL : empêche Spring de rediriger les appels REST vers OAuth2/login
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new OAuth2AuthSuccessHandler(userRepository, jwtService))
                );


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();


        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:4201", "http://localhost:59619"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}