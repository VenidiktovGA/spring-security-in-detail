package ru.venidiktov.spring.security.detail.config.entry.point;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * Тут мы делаем нехорошую вещь,
 * при первой неудачной аутентификации мы больше не возвращаем пользователю ответ с возможностью
 * повторной аутентификации
 */
//@Configuration
public class SecurityAuthenticationEntryPointConfig {

    /**
     * Точки входа и Entry Point
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        BasicAuthenticationEntryPoint basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("Realm"); // Источник Realm обязательно нужно указать
        return httpSecurity
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                .anyRequest().authenticated() // Запрос по любому пути должен делаться только аутентифицированными пользователями
                )
                .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer
                        /**
                         * Из-за такой точки входа (обработки неудачной аутентификации) при неудачной аутентификации пользователь
                         * получит ответ 200 с путатой, аутенртификация не будет предоставлена повторно
                         * Будет возвращен дефолтный http ответ 200
                         */
                        .authenticationEntryPoint((request, response, authException) -> {
                            //Тут можно логировать что пошло не так
                            authException.printStackTrace();
                            /**
                             * Если добавить Entry Pint он обработает ошибку и все будет ок
                             * В результате обработки неудачной аутентификации точкой входа basicAuthenticationEntryPoint
                             * форма ввода логина и пароля будет показываться снова
                             */
//                            basicAuthenticationEntryPoint.commence(request, response, authException);
                        }))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(basicAuthenticationEntryPoint); // Нисего не далем
                }).build();
    }
}
