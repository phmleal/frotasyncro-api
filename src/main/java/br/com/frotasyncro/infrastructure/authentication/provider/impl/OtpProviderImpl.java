package br.com.frotasyncro.infrastructure.authentication.provider.impl;

import br.com.frotasyncro.infrastructure.authentication.provider.OtpProvider;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class OtpProviderImpl implements OtpProvider {

    public static final String SHA_256 = "SHA-256";
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generateOtpCode() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int digit = RANDOM.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    @Override
    public String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    @Override
    public String hashOtpCode(String code, String salt) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance(SHA_256);

        sha256.reset();
        sha256.update(code.getBytes(StandardCharsets.UTF_8));
        sha256.update((byte) ':');
        sha256.update(Base64.getDecoder().decode(salt));

        byte[] digest = sha256.digest();
        return Base64.getEncoder().encodeToString(digest);
    }

    @Override
    public Boolean verifyOtpCode(String otpCode, String hashedOtpCode, String salt) throws NoSuchAlgorithmException {
        return MessageDigest.isEqual(
                hashedOtpCode.getBytes(StandardCharsets.UTF_8),
                hashOtpCode(otpCode, salt).getBytes(StandardCharsets.UTF_8)
        );
    }
}
