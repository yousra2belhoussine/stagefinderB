package ma.stagefinder.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private CustomUserDetailsService userDetailsService; // ✅ Corrigé : pour User

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
    throws ServletException, IOException {

    String path = request.getServletPath();
    System.out.println("🔎 Requête interceptée : " + path);

    if (path.equals("/auth/login") || path.equals("/auth/register") || path.equals("/auth/refresh-token")) {
      System.out.println("⛔ Endpoint public détecté, passage sans filtre");
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");
    String email = null;
    String jwt = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      jwt = authHeader.substring(7);
      System.out.println("✅ JWT extrait : " + jwt);
      try {
        email = jwtUtil.extractEmail(jwt);
        System.out.println("✅ Email extrait du token : " + email);
      } catch (Exception e) {
        System.out.println("❌ Erreur lors de l'extraction de l'email depuis le token : " + e.getMessage());
      }
    } else {
      System.out.println("❌ Header Authorization absent ou invalide.");
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      System.out.println("✅ UserDetails chargé depuis la DB pour : " + email);

      if (jwtUtil.isTokenValid(jwt, email)) {
        System.out.println("🔐 Token valide, injection de l'authentification dans SecurityContext");

        UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
          );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } else {
        System.out.println("❌ Token invalide !");
      }
    } else {
      if (email == null) {
        System.out.println("❌ Email introuvable dans le token.");
      } else {
        System.out.println("ℹ️ SecurityContext déjà authentifié.");
      }
    }

    filterChain.doFilter(request, response);
  }}
