package com.negocio.controlventas.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey jwtSecretKey() {

        byte[] claveBytes = jwtSecret.getBytes(
            StandardCharsets.UTF_8
        );

        return new SecretKeySpec(
            claveBytes,
            "HmacSHA256"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(
        SecretKey jwtSecretKey
    ) {

        return NimbusJwtEncoder
            .withSecretKey(jwtSecretKey)
            .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
        SecretKey jwtSecretKey
    ) {

        return NimbusJwtDecoder
            .withSecretKey(jwtSecretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http
    ) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .cors(cors -> cors.configurationSource(
                corsConfigurationSource()
            ))

            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/login"
                ).permitAll()

                .requestMatchers(
                    "/",
                    "/index.html",
                    "/*.html",
                    "/css/**",
                    "/js/**",
                    "/favicon.ico"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource
        corsConfigurationSource() {

        CorsConfiguration configuracion =
            new CorsConfiguration();

        configuracion.setAllowedOrigins(
            List.of(
                "http://localhost:5173",
                "http://localhost:4173",
                 "https://controlventas-mobile.vercel.app"
            )
        );

        configuracion.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
            )
        );

        configuracion.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource origen =
            new UrlBasedCorsConfigurationSource();

        origen.registerCorsConfiguration(
            "/**",
            configuracion
        );

        return origen;
    }

}