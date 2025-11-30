package br.com.frotasyncro.domain.report.strategy;

import br.com.frotasyncro.domain.report.enums.ReportNameEnum;

import java.util.Map;

/**
 * Interface que define o contrato para estratégias de geração de relatórios.
 * Implementa o padrão Strategy para permitir diferentes tipos de relatórios.
 */
public interface ReportStrategy {

    /**
     * Gera um relatório em formato de byte array (Excel).
     *
     * @param filters Map com os filtros a serem aplicados
     *                Chave: nome do filtro (ex: "truckId", "trailerId")
     *                Valor: valor do filtro
     * @return byte array do arquivo Excel gerado
     * @throws RuntimeException se houver erro na geração
     */
    byte[] generateReport(Map<String, Object> filters);

    /**
     * Retorna o tipo de relatório que esta estratégia implementa.
     *
     * @return ReportNameEnum do tipo de relatório
     */
    ReportNameEnum getReportName();

    /**
     * Retorna o nome amigável do relatório para usar no nome do arquivo.
     *
     * @return String com nome amigável (ex: "couplingHistory")
     */
    String getReportFileName();
}

