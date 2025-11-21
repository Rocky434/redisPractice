package com.example.demo.config;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.security.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        Claims claims = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            claims = jwtUtil.extractAllClaims(jwt);
            username = claims.getSubject();
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (claims.getExpiration().after(new Date())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
        // final String authHeader = request.getHeader("Authorization");
        // String username = null;
        // String jwt = null;

        // // Bearer token 格式
        // if (authHeader != null && authHeader.startsWith("Bearer ")) {
        // jwt = authHeader.substring(7);
        // try {
        // username = jwtUtil.extractUsername(jwt);
        // } catch (Exception e) {
        // // token 解析失敗
        // }
        // }

        // // 如果 SecurityContext 沒有認證過，且 jwt 有效，就建立認證物件
        // if (username != null &&
        // SecurityContextHolder.getContext().getAuthentication() == null) {
        // UserDetails userDetails =
        // this.userDetailsService.loadUserByUsername(username);
        // if (jwtUtil.validateToken(jwt, userDetails)) {
        // UsernamePasswordAuthenticationToken authToken = new
        // UsernamePasswordAuthenticationToken(userDetails,
        // null, userDetails.getAuthorities());
        // SecurityContextHolder.getContext().setAuthentication(authToken);
        // }
        // }
        // filterChain.doFilter(request, response);
    }
}
