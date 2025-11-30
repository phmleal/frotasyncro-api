package br.com.frotasyncro.domain.report.exception;

/**
 * Exceção lançada quando um tipo de relatório não é suportado.
 */
public class InvalidReportTypeException extends RuntimeException {

    public InvalidReportTypeException(String message) {
        super(message);
    }

    public InvalidReportTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
