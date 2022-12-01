package com.dj.config;

import com.dj.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class SecurityScopeExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<?> authorities = CommonUtils.getData(jwt.getClaims().get("roles"));
        Collection<GrantedAuthority> authorityCollection;
        if (authorities.isEmpty()) {
            authorities = CommonUtils.getData(jwt.getClaims().get("scope"));
            authorityCollection = authorities.stream()
                    .map(o -> "SCOPE_" + o.toString())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorityCollection = authorities.stream()
                    .map(o -> "ROLE_" + o.toString())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        log.info("Validated Authorization token successfully");
        return authorityCollection;
    }
}