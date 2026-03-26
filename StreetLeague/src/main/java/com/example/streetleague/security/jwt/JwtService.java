/*package com.example.streetleague.security.jwt;

import com.example.streetleague.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMs;
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        // Convertir la clé secrète en clé HMAC sécurisée
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    //
     * Génère un JWT token après une authentification réussie.
     * Le token contient : subject (email), claim (role), issuedAt, expiration, signature
     //
    public String generateToken(UserDetails userDetails) {
        // Extraire le rôle depuis les authorities Spring Security
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_STUDENT");

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        // **** Ajouter l'id et le fullName si UserDetails est une instance de User
        if (userDetails instanceof User user) {
            claims.put("id", user.getIdUser());
            claims.put("fullName", user.getFullName());
        }

        return Jwts.builder()
                .subject(userDetails.getUsername()) // email
                .claims(claims)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

        /*return Jwts.builder()
                .subject(userDetails.getUsername()) // username = email
                .claims(Map.of("role", role))
                .issuedAt(now)
                .expiration(exp)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }/*

    //
     * Extrait l'email (subject) depuis le token
     //
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    //
     * Vérifie si le token est valide :
     * - Signature correcte
     * - Appartient à l'utilisateur
     * - Non expiré
     //
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Vérifie si le token est expiré
    private boolean isTokenExpired(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp.before(new Date());
    }

    //Parse et retourne les claims du token (vérifie la signature)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}*/
package com.example.streetleague.security.jwt;

import com.example.streetleague.Repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;
    private final UserRepository userRepository;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs,
            UserRepository userRepository
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.userRepository = userRepository;
    }

    /**
     * Génère un JWT token après une authentification réussie.
     *
     * ✅ FIX 1 : .claims(map) doit être appelé AVANT .subject()
     *    car dans JJWT, .claims() réinitialise tous les claims précédents
     *    (y compris "sub" défini par .subject()).
     *
     * ✅ FIX 2 : ajoute les claims "id" et "fullName" depuis la DB
     *    via UserRepository (puisque UserDetails Spring ≠ notre entité User)
     */
    public String generateToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_PLAYER");

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        // Construire les claims EXTRA (sans "sub" — il sera mis par .subject())
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);

        // Récupérer id et fullName depuis la BD
        userRepository.findByEmail(userDetails.getUsername()).ifPresent(user -> {
            extraClaims.put("id", user.getIdUser());
            extraClaims.put("fullName", user.getFullName());
        });

        return Jwts.builder()
                // ✅ ORDRE CORRECT : claims() EN PREMIER, puis subject()
                // Si on met subject() avant claims(), claims() écrase "sub"
                .claims(extraClaims)
                .subject(userDetails.getUsername())   // ← écrit "sub" APRÈS les extra claims
                .issuedAt(now)
                .expiration(exp)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extrait l'email (subject) depuis le token
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Vérifie si le token est valide :
     * - Signature correcte
     * - Appartient à l'utilisateur
     * - Non expiré
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email != null
                    && email.equals(userDetails.getUsername())
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}