package br.com.frotasyncro.infrastructure.authentication.provider.impl;

import br.com.frotasyncro.core.configuration.AppConfiguration;
import br.com.frotasyncro.domain.authentication.model.AccessToken;
import br.com.frotasyncro.infrastructure.authentication.provider.TokenProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenProviderImpl implements TokenProvider {

    private final AppConfiguration appConfiguration;
    private final ObjectMapper objectMapper;

    @Override
    public AccessToken generateToken(User user) {
        var claims = objectMapper.convertValue(user, new TypeReference<Map<String, Object>>() {
        });

        return createToken(claims, user.getUsername());
    }

    @Override
    public void validateToken(String token) {
        Jwts.parser().verifyWith(secretKey()).build().parse(token).getPayload();
    }

    @Override
    public Claims getClaims(String token) {
        return (Claims) Jwts.parser().json(new JacksonDeserializer<>(objectMapper)).verifyWith(secretKey()).build().parse(token).getPayload();
    }

    private AccessToken createToken(Map<String, Object> claims, String subject) {
        long expirationTimeMillis = Instant.now().plus(Duration.ofHours(12)).toEpochMilli();

        String accessToken = Jwts.builder()
                .claims(claims)
                .json(new JacksonSerializer<>(objectMapper))
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(expirationTimeMillis))
                .signWith(secretKey()).compact();

        return new AccessToken(accessToken, expirationTimeMillis);
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(appConfiguration.getSecretKey()));
    }

}
