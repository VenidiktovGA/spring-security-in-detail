package ru.venidiktov.spring.security.detail.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Свой фильтр, тут мы не стали расширять интерфейс Filter, просто расширили уже существующий фильтр
 * OncePerRequestFilter удобнее обычного интерфейса Filter тем что в аргументах метода у него HttpServletRequest, HttpServletResponse,
 * а не просто ServletRequest, ServletResponse
 */
public class DeniedClientFilter extends OncePerRequestFilter {

    /**
     * Чтобы USER_AGENT имел в начале 'curl' выполнить запрос через Bash оболочку, только там был curl, через консоль или Shell нет такого в заголовке
     * curl http://localhost:8080/api/http_servlet_request/greetings
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (userAgent != null && userAgent.startsWith("curl")) {
            response.sendError(HttpStatus.FORBIDDEN.value());
            return; // Выходим чтобы цепочка фильтров далее не выполнялась, то есть запрос дальше не пойдет мы вернем ответ FORBIDDEN
        }
        filterChain.doFilter(request, response); // Если запрос делался не с помощью curl, выполняем цепочку фильтров далее
    }
}
