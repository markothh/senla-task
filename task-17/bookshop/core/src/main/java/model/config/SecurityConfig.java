package model.config;

import model.entity.DTO.MessageDTO;
import model.utils.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private String UNAUTHORIZED_ERROR_MSG = "Авторизация не удалась";
    private String FORBIDDEN_ERROR_MSG = "Нет прав на выполнение данной операции";

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/books/import", "/requests/import", "/orders/import").hasRole("ADMIN")
                        .requestMatchers("/books/export", "/requests/export", "/orders/export").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/statistics/**").hasRole("ADMIN")
                        .requestMatchers("/requests/**").hasRole("ADMIN")
                        .requestMatchers("/books/stock").hasRole("ADMIN")
                        .requestMatchers("/orders/*/status").hasRole("ADMIN")
                        .requestMatchers("/orders/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.getWriter().write(new MessageDTO("UNAUTHORIZED", UNAUTHORIZED_ERROR_MSG).toString());
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.getWriter().write(new MessageDTO("FORBIDDEN", FORBIDDEN_ERROR_MSG).toString());
                        })
                );

        return http.build();
    }
}