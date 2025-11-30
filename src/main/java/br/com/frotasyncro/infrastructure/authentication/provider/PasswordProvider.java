package br.com.frotasyncro.infrastructure.authentication.provider;

public interface PasswordProvider {

    String generateTempPassword();

    String encodePassword(String password);

}
