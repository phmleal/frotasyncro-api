package br.com.frotasyncro.infrastructure.authentication.provider.impl;

import br.com.frotasyncro.core.configuration.AppConfiguration;
import br.com.frotasyncro.infrastructure.authentication.provider.PasswordProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public record PasswordProviderImpl(PasswordEncoder passwordEncoder,
                                   AppConfiguration appConfiguration) implements PasswordProvider {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generateTempPassword() {
        StringBuilder sb = new StringBuilder(appConfiguration.getApiPasswordLength());
        for (int i = 0; i < appConfiguration.getApiPasswordLength(); i++) {
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
