package com.chapterconnect.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

               .authorizeHttpRequests(auth -> auth

        .requestMatchers("/api/auth/**")
        .authenticated()

        .requestMatchers(HttpMethod.POST, "/api/members")
        .hasRole("ADMIN")

        .requestMatchers("/api/members/**")
        .authenticated()

        .requestMatchers("/api/events/**")
        .authenticated()

        .requestMatchers("/api/announcements/pending")
        .hasRole("ADMIN")

        .requestMatchers("/api/announcements/*/review")
        .hasRole("ADMIN")

        .requestMatchers("/api/announcements/*/publish")
        .hasRole("ADMIN")

        .requestMatchers("/api/announcements/**")
        .authenticated()

        .requestMatchers("/api/**")
        .permitAll()

        .anyRequest()
        .permitAll()
)

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}