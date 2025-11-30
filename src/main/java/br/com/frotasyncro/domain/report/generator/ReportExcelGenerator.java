package br.com.frotasyncro.domain.report.generator;

import br.com.frotasyncro.domain.report.model.ExcelColumnConfig;

import java.util.List;
import java.util.Map;

/**
 * Interface para geração de relatórios em Excel usando Apache POI.
 * Define o contrato para criar arquivos Excel com dados estruturados.
 */
public interface ReportExcelGenerator {

    /**
     * Gera um arquivo Excel em bytes a partir dos dados fornecidos.
     *
     * @param sheetName     Nome da aba do Excel
     * @param columnConfigs Configuração das colunas (headers, largura, formato, etc)
     * @param data          Lista de maps contendo os dados (chave = attributeName da coluna, valor = dado)
     * @return byte array do arquivo Excel gerado
     * @throws RuntimeException se houver erro na geração
     */
    byte[] generate(String sheetName, List<ExcelColumnConfig> columnConfigs, List<Map<String, Object>> data);

    /**
     * Gera um arquivo Excel com múltiplas abas.
     *
     * @param sheetsData Map contendo [nomeDaAba -> (colunas, dados)]
     * @return byte array do arquivo Excel gerado
     * @throws RuntimeException se houver erro na geração
     */
    byte[] generateMultiSheet(Map<String, SheetData> sheetsData);

    /**
     * Modelo interno para dados de uma aba
     */
    record SheetData(List<ExcelColumnConfig> columnConfigs,
                     List<Map<String, Object>> data) {
    }
}

