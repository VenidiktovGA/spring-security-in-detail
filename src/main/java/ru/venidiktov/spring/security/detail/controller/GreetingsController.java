package ru.venidiktov.spring.security.detail.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Варианты получения аутентифицированного пользователя,
 * при Basic аутентификации через форму
 */
@RestController
@RequestMapping("/api")
public class GreetingsController {

    /**
     * Достаем данные о пользователе из контекста безопасности
     */
    @GetMapping("/security_context_holder/greetings")
    public ResponseEntity<Map<String, String>> getGreeting() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting", "Hello, %s! User from SecurityContextHolder".formatted(userDetails.getUsername())));
    }

    /**
     * Достаем из запроса, UserPrincipal который возвращает Principal, Principal расширяет Authentication
     */
    @GetMapping("/http_servlet_request/greetings")
    public ResponseEntity<Map<String, String>> getGreeting(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) ((Authentication) request.getUserPrincipal()).getPrincipal();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting", "Hello, %s! User from HttpServletRequest".formatted(userDetails.getUsername())));
    }

    /**
     * Тут автоматически из контекста безопасности в метод внедряется Principal и кастуется к (UserDetails)
     */
    @GetMapping("/authentication_principal/greetings")
    public ResponseEntity<Map<String, String>> getGreeting(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting", "Hello, %s! User from @AuthenticationPrincipal".formatted(userDetails.getUsername())));
    }

    /**
     * В любой метод контроллера можно внедрить аргумент класса который реализует Principal,
     * и так как Authentication расширяет Principal то можно использовать любой тип аутентификации
     * например UsernamePasswordAuthenticationToken
     */
    @GetMapping("/principal/greetings")
    public ResponseEntity<Map<String, String>> getGreeting(UsernamePasswordAuthenticationToken principal) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting", "Hello, %s! User from Principal".formatted(principal.getName())));
    }

    /**
     * Функциональный обработчика, кто то использует их как методы контроллера!
     */
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/api/router_function/greetings", request -> {
                    UserDetails userDetails = request.principal().map(Authentication.class::cast)
                            .map(Authentication::getPrincipal)
                            .map(UserDetails.class::cast)
                            .orElseThrow();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Map.of("greeting", "Hello, %s! User from RouterFunction".formatted(userDetails.getUsername())));
                }).build();
    }

}
