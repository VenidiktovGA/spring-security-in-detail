package ru.venidiktov.spring.security.detail.configurator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Класс AbstractHttpConfigurer расширяет SecurityConfigurerAdapter, AbstractHttpConfigurer требует указать два класса в generic
 * первый это класс текущего конфигуратора, а второй это построитель контекста безопасности
 * <p>
 * Одна из основных причин использования свого конфигуратора а не делать это в классе конфигурации,
 * это наличие объекта AuthenticationManager в методе configurer,
 * до этого в классе SecurityConfig AuthenticationManager есть null
 */
@Slf4j
public class MyConfigurer extends AbstractHttpConfigurer<MyConfigurer, HttpSecurity> {

    private String realmName = "My configurer Realm";

    /**
     * Вызывается на этапе инициализации контекста безопасности
     */
    @Override
    public void init(HttpSecurity builder) throws Exception {
        log.info("AM in init {}", builder.getSharedObject(AuthenticationManager.class));
        builder.httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.realmName(this.realmName))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry.anyRequest().authenticated());
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        log.info("AM in configure {}", builder.getSharedObject(AuthenticationManager.class));
        super.configure(builder);
    }

    public MyConfigurer realmName(String realmName) {
        this.realmName = realmName;
        return this;
    }
}
