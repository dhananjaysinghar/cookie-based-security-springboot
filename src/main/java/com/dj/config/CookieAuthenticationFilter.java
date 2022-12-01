package com.dj.config;

import com.dj.model.CookieModel;
import com.dj.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CookieAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(httpServletRequest.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> Constants.AUTH_COOKIE_NAME.equals(cookie.getName()))
                .findFirst();

        if (cookieAuth.isPresent()) {
            String cookieValue = cookieAuth.get().getValue();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CookieModel cookieModel = objectMapper.readValue(Base64.getDecoder().decode(cookieValue), CookieModel.class);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("maerskRestAPI", "",
                    cookieModel.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            auth.setDetails(Constants.AUTH_COOKIE_NAME);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
