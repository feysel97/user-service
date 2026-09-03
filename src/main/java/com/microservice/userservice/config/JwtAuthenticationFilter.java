package com.microservice.userservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = extractJwtFromRequest(request);

        if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken)) {
            String username = tokenProvider.getUsernameFromToken(jwtToken);
            List<String> roles = tokenProvider.getRolesFromToken(jwtToken);

            // Convert raw string roles from JWT into GrantedAuthority objects Spring Security understands
            List<SimpleGrantedAuthority> authorities = roles != null ? roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()) : List.of();

            // Constructing authentication object without querying a DB—stateless microservice style!
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Authenticate the user for the lifetime of this individual request
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Pass control smoothly to the next filter in the security chain
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}