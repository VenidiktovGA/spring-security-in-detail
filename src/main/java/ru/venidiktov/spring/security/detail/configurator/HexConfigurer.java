package ru.venidiktov.spring.security.detail.configurator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.venidiktov.spring.security.detail.converter.HexAuthenticationConverter;

/**
 * Нужен для того чтобы добавить кастомный фильтр в список фильтров,
 * делаем в конфигураторе чтоб использовать AuthenticationManager который тут уже не null,
 * в отличии от SecurityConfig бина
 */
public class HexConfigurer extends AbstractHttpConfigurer<HexConfigurer, HttpSecurity> {

    private AuthenticationEntryPoint authenticationEntryPoint = (request, response, authException) -> {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Hex");
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    };

    public HexConfigurer setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    @Override
    public void init(HttpSecurity builder) throws Exception {
        //Регистрируем нашу точку входа как точку для аутентификации для приложения по умолчанию
        builder.exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint));
    }

    /**
     * Использование своего фильтра аутентификации
     */
//    @Override
//    public void configure(HttpSecurity builder) throws Exception {
//        final var authenticationManager = builder.getSharedObject(AuthenticationManager.class);
//        builder.addFilterBefore(new HexAuthenticationFilter(authenticationManager, authenticationEntryPoint), BasicAuthenticationFilter.class);
//    }

    /**
     * Используем стандартный фильтр от spring security передав в него свой converter
     * Стандартный фильтр аутентификации от spring security рассчитан на работу в web приложении поэтому настроим его
     */
    @Override
    public void configure(HttpSecurity builder) throws Exception {
        final var authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        final var authenticationFilter = new AuthenticationFilter(authenticationManager, new HexAuthenticationConverter());
        // При успешной аутентификации мы не перенаправляем пользователя ни куда, мы просто продолжаем выполнять цепочку фильтров
        authenticationFilter.setSuccessHandler((request, response, authentication) -> {
        });
        // При неудачной аутентификации запрос уйдет на точку входа, и точка входа дальше отправит ответ пользователю
        authenticationFilter.setFailureHandler(new AuthenticationEntryPointFailureHandler(authenticationEntryPoint));
        builder.addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class);
    }
}
