package br.com.frotasyncro.domain.report;

import br.com.frotasyncro.core.configuration.AppConfiguration;
import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import br.com.frotasyncro.domain.report.exception.ReportGenerationException;
import br.com.frotasyncro.domain.report.model.ReportGenerationResult;
import br.com.frotasyncro.domain.report.strategy.ReportStrategy;
import br.com.frotasyncro.domain.report.strategy.factory.ReportStrategyFactory;
import br.com.frotasyncro.infrastructure.attachment.provider.AttachmentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço de domínio responsável pela geração e armazenamento de relatórios.
 * Orquestra as estratégias de geração de relatórios e o provider de anexos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGenerationService {

    private static final String REPORTS_BASE_PATH = "reports";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            // Avoid characters like ':' in S3 object keys to prevent signing/canonicalization issues
            .ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));

    private final ReportStrategyFactory strategyFactory;
    private final AttachmentProvider<String> attachmentProvider;
    private final AppConfiguration appConfiguration;

    public ReportGenerationResult generate(ReportNameEnum reportName, Map<String, Object> filters) {
        log.info("Iniciando geração de relatório: {}", reportName);

        try {
            // 1. Obter timestamp da geração
            Instant generationTimestamp = Instant.now();
            String formattedTimestamp = TIMESTAMP_FORMATTER.format(generationTimestamp);

            // 2. Selecionar estratégia de relatório
            ReportStrategy strategy = strategyFactory.getStrategy(reportName);
            log.info("Estratégia selecionada: {}", strategy.getClass().getSimpleName());

            // 3. Gerar arquivo Excel
            Map<String, Object> reportFilters = filters != null ? filters : new HashMap<>();
            byte[] excelContent = strategy.generateReport(reportFilters);
            log.info("Arquivo Excel gerado com sucesso: {} bytes", excelContent.length);

            // 4. Construir caminho no S3
            String s3Key = buildS3Key(strategy, formattedTimestamp);
            log.info("Chave S3 construída: {}", s3Key);

            // 5. Upload no S3 com metadados
            Map<String, String> metadata = buildMetadata(reportName, formattedTimestamp, reportFilters);
            String presignedUrl = attachmentProvider.uploadWithMetadata(
                    excelContent,
                    appConfiguration.getApiBucketName(),
                    s3Key,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    metadata
            );
            log.info("Arquivo uploaded com sucesso no S3: {}", s3Key);

            // 6. Construir resultado
            String fileName = buildFileName(strategy, formattedTimestamp);
            ReportGenerationResult result = ReportGenerationResult.builder()
                    .reportName(reportName)
                    .fileName(fileName)
                    .presignedUrl(presignedUrl)
                    .build();

            log.info("Relatório {} gerado com sucesso. URL: {}", reportName, presignedUrl);
            return result;

        } catch (Exception e) {
            log.error("Erro ao gerar relatório {}: {}", reportName, e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar relatório " + reportName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Constrói a chave (path) do arquivo no S3.
     * Formato: reports/{reportName}/{reportName}_{timestamp}.xlsx
     *
     * @param strategy  Estratégia do relatório
     * @param timestamp Timestamp formatado
     * @return Chave do S3
     */
    private String buildS3Key(ReportStrategy strategy, String timestamp) {
        String reportFileName = strategy.getReportFileName();
        return String.format(
                "%s/%s/%s_%s.xlsx",
                REPORTS_BASE_PATH,
                reportFileName,
                reportFileName,
                timestamp
        );
    }

    /**
     * Constrói o nome do arquivo para retorno na resposta.
     * Formato: {reportName}_{timestamp}.xlsx
     *
     * @param strategy  Estratégia do relatório
     * @param timestamp Timestamp formatado
     * @return Nome do arquivo
     */
    private String buildFileName(ReportStrategy strategy, String timestamp) {
        return String.format("%s_%s.xlsx", strategy.getReportFileName(), timestamp);
    }

    /**
     * Constrói metadados customizados para o arquivo no S3.
     *
     * @param reportName Tipo de relatório
     * @param timestamp  Timestamp de geração
     * @param filters    Filtros aplicados
     * @return Map com metadados
     */
    private Map<String, String> buildMetadata(ReportNameEnum reportName, String timestamp, Map<String, Object> filters) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("report-name", reportName.getCode());
        metadata.put("report-description", reportName.getDescription());
        metadata.put("generated-at", timestamp);
        metadata.put("filter-count", String.valueOf(filters.size()));

        // Adicionar filtros específicos aos metadados
        filters.forEach((key, value) -> {
            if (value != null) {
                metadata.put("filter-" + key, value.toString());
            }
        });

        return metadata;
    }

    /**
     * Valida se um tipo de relatório é suportado.
     *
     * @param reportName Tipo de relatório
     * @return true se suportado
     */
    public boolean isReportSupported(ReportNameEnum reportName) {
        return strategyFactory.isSupported(reportName);
    }
}
