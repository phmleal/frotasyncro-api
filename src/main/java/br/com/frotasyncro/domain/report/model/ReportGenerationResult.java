package br.com.frotasyncro.domain.report.model;

import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Modelo que representa o resultado da geração de um relatório.
 * Contém informações sobre o arquivo gerado e URL assinada para download.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationResult {

    /**
     * Tipo de relatório gerado
     */
    private ReportNameEnum reportName;

    /**
     * Nome do arquivo gerado
     */
    private String fileName;

    /**
     * URL assinada (presigned) para download do arquivo
     */
    private String presignedUrl;

    /**
     * Timestamp ISO 8601 da geração do relatório
     */
    private Instant timestamp;

    /**
     * Tamanho do arquivo em bytes
     */
    private Long fileSize;

    /**
     * Bucket S3 onde o arquivo foi armazenado
     */
    private String bucket;

    /**
     * Chave (path) do objeto no S3
     */
    private String s3Key;
}

