package ru.venidiktov.spring.security.detail.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Фильтр нужен для реализации своего способа аутентификации
 * Свой способ аутентификации, наш способ как и стандартный будет использовать логин и пароль но кодировать их будет не в base64 а в base16
 * <p>
 * !!! 'dbuser:password' в XXAsci (base16) =  '6462757365723a70617373776f7264' !!!
 * curl -H "Authorization: Hex6462757365723a70617373776f7264" http://localhost:8080/api/http_servlet_request/greetings
 * curl нормально работает в bash!
 */
public class HexAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Для аутентификации нам нужен AuthenticaitonManager, точка входа и еще кое что
     */
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();
    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public HexAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Фильр написан максимально просто, для того чтобы понять как правильно писать фильтр можно зайти в готовый фильтр от spirng secyrity
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Hex")) {
            final var rowToken = authorization.replaceAll("^Hex", ""); // Получаем сырой токен из заголовка, логин и пароль в base16 без всего лишнего
            final var token = new String(Hex.decode(rowToken), StandardCharsets.UTF_8); // Раскодируем сырой токен из base16 в utf8
            final var tokenParts = token.split(":"); // Делим токен на логин и пароль
            final var authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(tokenParts[0], tokenParts[1]); // Запрос на аутентификацию не аутентифицированного пользователя
            try {
                final var authenticationResult = authenticationManager.authenticate(authenticationRequest); // Пытаемся аутентифицировать пользователя, по ранее составленному запросу
                final var securityContext = securityContextHolderStrategy.createEmptyContext(); // Создаем пустой контекст безопасноти
                securityContext.setAuthentication(authenticationResult);
                securityContextHolderStrategy.setContext(securityContext);
                securityContextRepository.saveContext(securityContext, request, response); // Сохраняем контекст в репозитории
            } catch (AuthenticationException e) {
                securityContextHolderStrategy.clearContext(); // Чистим контекст
                authenticationEntryPoint.commence(request, response, e); // Отправляем пользователя на повторную аутентификацию
                return; // Выходим чтоб цепочька фильтров дальше не продолжалась
            }
        }
        filterChain.doFilter(request, response);
    }
}
