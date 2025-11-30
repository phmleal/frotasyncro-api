package br.com.frotasyncro.domain.report.exception;

/**
 * Exceção lançada durante a geração de um relatório.
 * Pode ser causada por erro em query, geração de arquivo, ou upload em S3.
 */
public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportGenerationException(Throwable cause) {
        super("Error when generate report", cause);
    }
}
