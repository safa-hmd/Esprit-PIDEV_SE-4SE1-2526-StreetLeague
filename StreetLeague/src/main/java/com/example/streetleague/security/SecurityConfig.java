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

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

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

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // setAllowedOriginPatterns requis pour SockJS (remplace setAllowedOrigins)
        config.setAllowedOriginPatterns(List.of("http://localhost:*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}