package com.dcc.actividades.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para la Tarea 5.
 * 
 * Define dos usuarios en memoria:
 *  - cc5002 / examen    → acceso a /admin-fotos y /mensajes-log
 *  - auditor / log-auditor → acceso solo a /mensajes-log
 * 
 * Las demás rutas (/buscador, /api/**, /estadistica-fotos, etc.) son públicas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas protegidas: solo usuarios con rol ADMIN
                .requestMatchers("/admin-fotos/**").hasRole("ADMIN")
                // Rutas protegidas: usuarios con rol ADMIN o AUDITOR
                .requestMatchers("/mensajes-log/**").hasAnyRole("ADMIN", "AUDITOR")
                // Todo lo demás es público
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Usuario admin: acceso a /admin-fotos y /mensajes-log
        UserDetails admin = User.builder()
                .username("cc5002")
                .password("{noop}examen")
                .roles("ADMIN")
                .build();

        // Usuario auditor: acceso solo a /mensajes-log
        UserDetails auditor = User.builder()
                .username("auditor")
                .password("{noop}log-auditor")
                .roles("AUDITOR")
                .build();

        return new InMemoryUserDetailsManager(admin, auditor);
    }
}
