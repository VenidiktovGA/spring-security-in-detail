package ru.venidiktov.spring.security.detail.converter;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * Данный конвертер реализован по принципу AuthenticationConverter из AuthenticationFilter,
 * Данный конвертор достает данные аутентификации из запроса и формирует запрос аутентификации
 */
public class HexAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        final var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Hex")) {
            final var rowToken = authorization.replaceAll("^Hex", ""); // Получаем сырой токен из заголовка, логин и пароль в base16 без всего лишнего
            final var token = new String(Hex.decode(rowToken), StandardCharsets.UTF_8); // Раскодируем сырой токен из base16 в utf8
            final var tokenParts = token.split(":"); // Делим токен на логин и пароль
            return UsernamePasswordAuthenticationToken.unauthenticated(tokenParts[0], tokenParts[1]); // Запрос на аутентификацию не аутентифицированного пользователя
        }
        return null;
    }
}
