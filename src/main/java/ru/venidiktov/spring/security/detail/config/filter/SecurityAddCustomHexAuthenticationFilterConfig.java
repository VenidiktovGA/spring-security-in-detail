package ru.venidiktov.spring.security.detail.config.filter;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import ru.venidiktov.spring.security.detail.configurator.HexConfigurer;
import ru.venidiktov.spring.security.detail.service.JdbcUserDetailService;

/**
 * При использовании spring-boot конфигурировать DelegatingFilterProxy не нужно, так же не нужна аннотация @EnableWebSecurity
 */
@Configuration
public class SecurityAddCustomHexAuthenticationFilterConfig {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailService(dataSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorizationManagerRequestMatcher ->
                        authorizationManagerRequestMatcher.requestMatchers("/error").permitAll()
                                .anyRequest().authenticated())
                .apply(new HexConfigurer());
        return httpSecurity.build();
    }
}
