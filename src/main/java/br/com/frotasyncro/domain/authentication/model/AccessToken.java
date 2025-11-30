package br.com.frotasyncro.domain.authentication.model;

public record AccessToken(
        String accessToken,
        long expiresIn
) {
}
