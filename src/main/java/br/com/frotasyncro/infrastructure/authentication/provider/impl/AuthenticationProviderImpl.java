package br.com.frotasyncro.infrastructure.authentication.provider.impl;

import br.com.frotasyncro.infrastructure.authentication.provider.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public record AuthenticationProviderImpl(
        AuthenticationManager authenticationManager) implements AuthenticationProvider {

    @Override
    public Authentication authenticateByUsernameAndPassword(String username, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    @Override
    public Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
