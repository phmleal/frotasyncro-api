package br.com.frotasyncro.application.report;

import br.com.frotasyncro.controller.report.model.GenerateReportRequestDTO;
import br.com.frotasyncro.controller.report.model.GenerateReportResponseDTO;
import br.com.frotasyncro.domain.report.ReportGenerationService;
import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import br.com.frotasyncro.domain.report.exception.InvalidReportTypeException;
import br.com.frotasyncro.domain.report.model.ReportGenerationResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportApplicationService {

    private final ReportGenerationService reportGenerationService;

    public GenerateReportResponseDTO generateReport(@Valid GenerateReportRequestDTO request) {

        log.info("Processando requisição de geração de relatório: {}", request.getReportName());

        try {
            ReportNameEnum reportName = ReportNameEnum.fromCode(request.getReportName());

            if (!reportGenerationService.isReportSupported(reportName)) {
                log.error("Tipo de relatório não suportado: {}", reportName);
                throw new InvalidReportTypeException("Tipo de relatório não suportado: " + reportName.getCode());
            }

            Map<String, Object> filters = request.getFilters();

            ReportGenerationResult result = reportGenerationService.generate(reportName, filters);
            log.info("Relatório gerado com sucesso: {} bytes", result.getFileSize());

            GenerateReportResponseDTO response = GenerateReportResponseDTO.builder()
                    .presignedUrl(result.getPresignedUrl())
                    .fileName(result.getFileName())
                    .build();

            log.info("Response de relatório construída com sucesso");
            return response;

        } catch (InvalidReportTypeException e) {
            log.error("Tipo de relatório inválido: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar requisição de relatório: {}", e.getMessage(), e);
            throw e;
        }
    }
}
