package br.com.frotasyncro.core.security;

import br.com.frotasyncro.infrastructure.authentication.provider.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends GenericFilter {

    private static final String EXECUTED_FILTER = "executedFilter";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final transient TokenProvider tokenProvider;

    private static boolean isLoginRequest(HttpServletRequest httpRequest) {
        return httpRequest.getRequestURI().equals("/auth") && httpRequest.getMethod().equals("POST");
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) servletRequest;

        if (filterAlreadyExecuted(httpRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            final String authorizationHeaderContent = httpRequest.getHeader(AUTHORIZATION);

            if (isNotBlank(authorizationHeaderContent) && authorizationHeaderContent.startsWith(BEARER)) {
                setAuthenticationContext(authorizationHeaderContent);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (JwtException | IllegalArgumentException jwtException) {
            // Deixar o Spring Security tratar como erro de autenticação (401)
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid JWT token", jwtException);
        } catch (Exception exception) {
            // Qualquer outra exceção não deve ser encapsulada como erro de autenticação
            // Deixar propagarem para o ApiExceptionHandler
            throw exception;
        }
    }

    @SuppressWarnings("unchecked")
    private void setAuthenticationContext(String authorizationHeaderContent) {
        String token;
        Claims claims;

        token = authorizationHeaderContent.substring(7);

        tokenProvider.validateToken(token);

        claims = tokenProvider.getClaims(token);

        var roles = fetchSubRoles((ArrayList<LinkedHashMap<String, String>>) claims.get("authorities"));

        var authentication = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                roles
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Set<SimpleGrantedAuthority> fetchSubRoles(ArrayList<LinkedHashMap<String, String>> authorities) {
        return authorities.stream()
                .map(authority -> authority.get("authority"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private boolean filterAlreadyExecuted(HttpServletRequest httpRequest) {
        if (httpRequest.getAttribute(EXECUTED_FILTER) != null
                || isLoginRequest(httpRequest)) {
            return true;
        } else {
            httpRequest.setAttribute(EXECUTED_FILTER, true);
            return false;
        }
    }

}

