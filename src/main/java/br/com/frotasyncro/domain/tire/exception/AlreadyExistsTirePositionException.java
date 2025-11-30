package br.com.frotasyncro.domain.tire.exception;

public class AlreadyExistsTirePositionException extends RuntimeException {

    public AlreadyExistsTirePositionException(String errorMessage) {
        super(errorMessage);
    }

}
