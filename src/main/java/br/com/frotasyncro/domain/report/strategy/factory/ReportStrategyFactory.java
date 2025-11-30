package br.com.frotasyncro.domain.report.strategy.factory;

import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import br.com.frotasyncro.domain.report.exception.InvalidReportTypeException;
import br.com.frotasyncro.domain.report.strategy.ReportStrategy;
import br.com.frotasyncro.domain.report.strategy.impl.CouplingHistoryReportStrategy;
import br.com.frotasyncro.domain.report.strategy.impl.DriversReportStrategy;
import br.com.frotasyncro.domain.report.strategy.impl.TiresReportStrategy;
import br.com.frotasyncro.domain.report.strategy.impl.WorkOrdersReportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory para seleção da estratégia de geração de relatório.
 * Responsável por mapear tipos de relatórios para suas respectivas implementações.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportStrategyFactory {

    private final CouplingHistoryReportStrategy couplingHistoryReportStrategy;
    private final WorkOrdersReportStrategy workOrdersReportStrategy;
    private final TiresReportStrategy tiresReportStrategy;
    private final DriversReportStrategy driversReportStrategy;

    /**
     * Retorna a estratégia de relatório correspondente ao tipo fornecido.
     *
     * @param reportName Tipo de relatório desejado
     * @return Implementação de ReportStrategy
     * @throws InvalidReportTypeException se o tipo não for suportado
     */
    public ReportStrategy getStrategy(ReportNameEnum reportName) {
        log.debug("Obtendo estratégia para tipo de relatório: {}", reportName);

        ReportStrategy strategy = switch (reportName) {
            case COUPLING_HISTORY -> {
                log.debug("Estratégia selecionada: CouplingHistoryReportStrategy");
                yield couplingHistoryReportStrategy;
            }
            case WORK_ORDERS -> {
                log.debug("Estratégia selecionada: WorkOrdersReportStrategy");
                yield workOrdersReportStrategy;
            }
            case TIRES -> {
                log.debug("Estratégia selecionada: TiresReportStrategy");
                yield tiresReportStrategy;
            }
            case DRIVERS -> {
                log.debug("Estratégia selecionada: DriversReportStrategy");
                yield driversReportStrategy;
            }
            default -> null;
        };

        if (strategy == null) {
            log.error("Tipo de relatório não suportado: {}", reportName);
            throw new InvalidReportTypeException("Tipo de relatório não suportado: " + reportName);
        }

        return strategy;
    }

    /**
     * Verifica se um tipo de relatório é suportado.
     *
     * @param reportName Tipo de relatório
     * @return true se suportado, false caso contrário
     */
    public boolean isSupported(ReportNameEnum reportName) {
        try {
            getStrategy(reportName);
            return true;
        } catch (InvalidReportTypeException e) {
            return false;
        }
    }
}
