package ru.venidiktov.spring.security.detail.config.entry.point;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * При использовании spring-boot конфигурировать DelegatingFilterProxy не нужно, так же не нужна аннотация @EnableWebSecurity
 */
//@Configuration
public class SecurityExceptionHandlingPointConfig {

    /**
     * Точки входа и Entry Point
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/public/**").permitAll() // Путь доступен всем
                                .anyRequest().authenticated() // Запрос по любому пути должен делаться только аутентифицированными пользователями
                )
                /**
                 * Тут мы можем создать точку входя когда не аутентифицированный пользователь хочет получить доступ к
                 * защищенному ресурсу
                 */
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint((request, response, authException) -> {
                        authException.printStackTrace();
                        response.setHeader("Bad", "bad");
                        response.sendRedirect("http://localhost:8080/public/403.html"); // Перенаправляем не аутентифицированного пользователя
//                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    });
                }).build();
    }
}
