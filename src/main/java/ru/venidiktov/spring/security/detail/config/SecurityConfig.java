package ru.venidiktov.spring.security.detail.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import ru.venidiktov.spring.security.detail.service.JdbcUserDetailService;

/**
 * При использовании spring-boot конфигурировать DelegatingFilterProxy не нужно, так же не нужна аннотация @EnableWebSecurity
 */
@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailService(dataSource);
    }

    /**
     * !!! Для настройки цепочки фильтров регистрируем бин SecurityFilterChain (создать его можно из экземпляра HttpSecurity, который зарегистрирован в контексте приложения после включения поддержки Spring Security)!!!
     * По умолчанию при подключении security включена базовая аутентификация
     * При использовании источников пользователей по умолчанию в логах будет
     * автоматически сгенерированный пароль (Using generated security password: 32c14a00-8fe5-4056-99f0-e446f3a94b03)
     * логи пользователя по умолчанию "user"
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                .anyRequest().authenticated() // Запрос по любому пути должен делаться только аутентифицированными пользователями
                )
                /**
                 * !!! Если включить форму от Spring Security то при первой аутентификации постоянно перебрасывает на /error?continue,
                 * решил только обходным путем через перенаправление на / то-есть на index.html
                 * Таска на проблему https://github.com/spring-projects/spring-security/issues/12635
                 */
                .formLogin(
                        httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.defaultSuccessUrl("/", true) // При удачной аутентификации всегда перенаправляем на главную страницу
                ) // Форма входа будет использоваться по умолчанию от Spring Security
                .httpBasic(Customizer.withDefaults()) // Включаем Basic аутентификацию
                .build();
    }
}
