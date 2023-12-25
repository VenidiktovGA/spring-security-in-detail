package ru.venidiktov.spring.security.detail.config.filter;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import ru.venidiktov.spring.security.detail.filter.DeniedClientFilter;
import ru.venidiktov.spring.security.detail.service.JdbcUserDetailService;

/**
 * При использовании spring-boot конфигурировать DelegatingFilterProxy не нужно, так же не нужна аннотация @EnableWebSecurity
 */
@Slf4j
//@Configuration
public class SecurityAddCustomDeclineFilterConfig {

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
        httpSecurity.addFilterBefore(new DeniedClientFilter(), DisableEncodeUrlFilter.class) // Добавили свой фильтр перед DisableEncodeUrlFilter
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorizationManagerRequestMatcher ->
                        authorizationManagerRequestMatcher.requestMatchers("/error").permitAll()
                                .anyRequest().authenticated());
        return httpSecurity.build();
    }
}
