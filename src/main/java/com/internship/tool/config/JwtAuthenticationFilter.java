package com.internship.tool.config;

import com.internship.tool.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // ✅ IMPORTANT
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ Skip auth endpoints
        if (path.startsWith("/api/compliance")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // ❌ No token → continue (will become 403 later)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);

            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            // 🔍 DEBUG
            System.out.println("USER: " + username);
            System.out.println("ROLE FROM TOKEN: " + role);

            // ✅ Set authentication
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                var authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)); // 🔥 MUST HAVE ROLE_

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("AUTH SET: " + auth);
            }

        } catch (Exception e) {
            System.out.println("❌ Invalid Token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}