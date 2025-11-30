package br.com.frotasyncro.infrastructure.authentication.provider;

import java.security.NoSuchAlgorithmException;

public interface OtpProvider {

    String generateOtpCode();

    String generateSalt();

    String hashOtpCode(String code, String salt) throws NoSuchAlgorithmException;

    Boolean verifyOtpCode(String otpCode, String hashedOtpCode, String salt) throws NoSuchAlgorithmException;

}
