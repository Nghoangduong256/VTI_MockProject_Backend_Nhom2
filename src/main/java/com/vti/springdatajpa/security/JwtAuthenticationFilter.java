package com.vti.springdatajpa.security;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.exception.AuthException;
import com.vti.springdatajpa.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token, continue filter chain
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token
            String token = authHeader.substring(7);

            // Validate and extract claims
            Claims claims = jwtUtil.extractClaims(token);
            Integer userId = Integer.parseInt(claims.getSubject());
            String role = claims.get("role", String.class);
            String username = claims.get("username", String.class);

            log.debug("JWT token validated for user: {} with role: {}", username, role);

            // Load user from database
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException("User not found"));

            // Check if user is still active
            if (!user.isActive()) {
                log.warn("Inactive user attempted to access: {}", username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"User account is inactive\"}");
                return;
            }

            // Create authentication token with authorities
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authentication set for user: {}", username);

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\"}");
            return;

        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;

        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication failed\"}");
            return;
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
