package br.com.frotasyncro.domain.authentication.exception;

public class OtpErrorException extends RuntimeException {

    public OtpErrorException(String message) {
        super(message);
    }

}
