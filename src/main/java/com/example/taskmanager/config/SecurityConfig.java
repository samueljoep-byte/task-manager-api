package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import com.example.taskmanager.security.JwtAuthenticationFilter;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                		.requestMatchers(
                		        "/api/auth/register",
                		        "/api/auth/login",
                		        "/swagger-ui/**",
                		        "/v3/api-docs/**"
                		).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                	    .authenticationEntryPoint((request, response, authException) -> {
                	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                	        response.setContentType("application/json");
                	        response.getWriter().write(
                	            "{\"message\":\"Authentication required\"}"
                	        );
                	    })
                	)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    
    }  
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}