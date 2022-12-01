package com.dj.config;

import com.dj.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SpringBootSecurityConfig {

    @Value("${spring.security.oauth2.resource-server.jwt.jwk-set-uri}")
    private String jwkUri;

    @Bean
    public SecurityFilterChain securityFilterChainForForOAuthResourceServer(HttpSecurity http, UserAuthenticationProvider authenticationProvider) throws Exception {
        final String finalIgnoredUriList = "/actuator/**";
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new SecurityScopeExtractor());


        http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new UsernamePasswordAuthFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new CookieAuthenticationFilter(), UsernamePasswordAuthFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().logout().deleteCookies(Constants.AUTH_COOKIE_NAME)
                .and()
                .authenticationProvider(authenticationProvider)
                .authorizeRequests(authorize -> authorize
                        .antMatchers(finalIgnoredUriList).permitAll()
                        .anyRequest().authenticated().and())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

    @Bean
    public JwtDecoder nimbusDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkUri).build();
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>();
        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }
}
