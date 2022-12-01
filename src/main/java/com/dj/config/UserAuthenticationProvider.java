package com.dj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            // authentication by username and password

        } else if (authentication instanceof BearerTokenAuthenticationToken) {
            // authentication by cookie
            BearerTokenAuthenticationToken bearerTokenAuthentication = (BearerTokenAuthenticationToken) authentication;
            String token = bearerTokenAuthentication.getToken();
            Jwt jwt = jwtDecoder.decode(token);
            Map<String, Object> claims = jwt.getClaims();
            List<String> authorities = (List<String>) claims.get("scope");
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.get("sub"), "", authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            auth.setDetails(claims);
            return auth;
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
