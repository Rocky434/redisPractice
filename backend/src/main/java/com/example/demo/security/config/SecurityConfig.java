package com.example.demo.security.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import com.example.demo.security.jwt.JwtFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/auth/register").permitAll()
                        .requestMatchers("/api/public/**").hasAnyRole("USER")
                        .requestMatchers("/graphql").hasAnyRole("USER")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(((request, response, authException) -> {
                            String exceptionType = (String) request.getAttribute("jwt_exception");
                            int status = HttpServletResponse.SC_UNAUTHORIZED;
                            writeErrorResponse(request, response, status, exceptionType, exceptionType);
                        }))
                        .accessDeniedHandler((request, response, ex1) -> writeErrorResponse(request, response,
                                HttpServletResponse.SC_FORBIDDEN, "Forbidden", "Forbidden")))
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeErrorResponse(HttpServletRequest request, HttpServletResponse response, int status,
            String graphqlMessage, String restMessage) throws IOException {

        String path = request.getRequestURI();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        if (path.startsWith("/graphql")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"errors\":[{\"message\":\"" + graphqlMessage + "\"}]}");
        } else {
            response.setStatus(status);
            response.getWriter().write("{\"error\":\"" + restMessage + "\"}");
        }
        response.getWriter().flush();
    }

}
