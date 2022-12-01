package com.dj.controller;

import com.dj.model.CookieModel;
import com.dj.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class HelloController {


    @GetMapping("/hello")
    @SneakyThrows
    public String hello(Principal principal, HttpServletResponse servletResponse) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!Constants.AUTH_COOKIE_NAME.equals(authentication.getDetails())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Cookie authCookie = new Cookie(Constants.AUTH_COOKIE_NAME, Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(CookieModel.builder()
                    .details((Map<String, Object>) authentication.getDetails())
                    .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())).build()).getBytes()));
            authCookie.setHttpOnly(true);
            //authCookie.setSecure(true);
            authCookie.setMaxAge((int) Duration.of(1, ChronoUnit.MINUTES).toSeconds());
            authCookie.setPath("/");
            servletResponse.addCookie(authCookie);
        }
        return "RequestID is " + UUID.randomUUID().toString();
    }
}
