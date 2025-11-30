package br.com.frotasyncro.domain.authentication.exception;

public class OtpMaxAttemptsExceededException extends RuntimeException {

    public OtpMaxAttemptsExceededException(String message) {
        super(message);
    }

}
