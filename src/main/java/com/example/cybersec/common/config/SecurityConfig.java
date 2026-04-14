package com.example.cybersec.common.config;

import com.example.cybersec.auth.security.JwtFilter;
import com.example.cybersec.auth.service.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Cấu hình bảo mật chính — Spring Security filter chain.
 * Định nghĩa quyền truy cập URL, JWT filter, form login và logout.
 */
@Configuration
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, JwtFilter jwtFilter) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .userDetailsService(jpaUserDetailsService)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/search").permitAll()
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/compete-static/**").permitAll()
                        .requestMatchers("/", "/login", "/register", "/learn-guest/**", "/auth/forgot-password", "/auth/change-password").permitAll()
                        .requestMatchers("/api/dashboard/**").hasAnyAuthority("USER", "LEARNER", "ADMIN")
                        .requestMatchers("/api/compete/**", "/api/labs/**", "/api/quizzes/**", "/api/progress/**")
                                .hasAnyAuthority("USER", "LEARNER", "ADMIN")
                        .requestMatchers("/dashboard/**", "/learn/**", "/lab/**", "/member/**", "/compete/**")
                                .authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/learn", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
