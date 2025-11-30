package br.com.frotasyncro.infrastructure.authentication.provider;

import br.com.frotasyncro.domain.authentication.model.AccessToken;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.User;

public interface TokenProvider {

    AccessToken generateToken(User user);

    void validateToken(String token);

    Claims getClaims(String token);

}
