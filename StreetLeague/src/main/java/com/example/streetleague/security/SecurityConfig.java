package com.example.streetleague.security;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.security.jwt.JwtAuthFilter;
import com.example.streetleague.security.jwt.JwtService;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // AUTH
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // USERS
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/by-email").permitAll()  // ← AJOUTÉ
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}