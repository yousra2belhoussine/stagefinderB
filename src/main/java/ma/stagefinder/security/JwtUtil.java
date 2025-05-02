package ma.stagefinder.security;

import ma.stagefinder.entities.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private long jwtExpiration;

  @Value("${jwt.refresh-expiration}")
  private long refreshExpiration;

  // ✅ Générer un access token avec rôle
  public String generateToken(String email, Role role) {
    return Jwts.builder()
      .setSubject(email)
      .claim("role", role.name())  // 🔥 Sauvegarde rôle (STAGIAIRE, ADMINISTRATEUR, etc.)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  // ✅ Générer un refresh token sans rôle
  public String generateRefreshToken(String email) {
    return Jwts.builder()
      .setSubject(email)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  // ✅ Extraire l'email depuis un token
  public String extractEmail(String token) {
    return extractAllClaims(token).getSubject();
  }

  // ✅ Vérifier si token valide
  public boolean isTokenValid(String token, String email) {
    final String extractedEmail = extractEmail(token);
    return extractedEmail.equals(email) && !isTokenExpired(token);
  }

  // ✅ Vérifier expiration du token
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // ✅ Extraire expiration
  public Date extractExpiration(String token) {
    return extractAllClaims(token).getExpiration();
  }

  // ✅ Extraire tous les claims
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  // ✅ Clé de signature
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }
}
