package com.example.streetleague.security.jwt;

import com.example.streetleague.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lire le header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Si pas de token → continuer sans authentification (endpoints publics)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (supprimer "Bearer ")
        String token = authHeader.substring(7);
        String email;

        // 4. Extraire l'email depuis le token
        try {
            email = jwtService.extractEmail(token);
        } catch (Exception e) {
            // Token malformé → continuer sans authentification
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Si email valide et pas encore authentifié
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Charger les infos utilisateur depuis la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Valider le token (signature + expiration)
            if (jwtService.isTokenValid(token, userDetails)) {

                // Créer l'objet d'authentification
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Stocker dans le SecurityContext → requête considérée comme authentifiée
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Passer au filtre suivant ou au controller
        filterChain.doFilter(request, response);
    }
}
