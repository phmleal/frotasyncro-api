package br.com.frotasyncro.domain.report.strategy.impl;

import br.com.frotasyncro.core.specification.FilterCriteriaSpecification;
import br.com.frotasyncro.core.specification.enums.FilterCriteriaOperator;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import br.com.frotasyncro.domain.report.exception.ReportGenerationException;
import br.com.frotasyncro.domain.report.generator.ReportExcelGenerator;
import br.com.frotasyncro.domain.report.model.ExcelColumnConfig;
import br.com.frotasyncro.domain.report.strategy.ReportStrategy;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.TruckTrailerCombinationRepository;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckTrailerCombinationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estratégia de geração de relatório de Histórico de Acoplamento.
 * Gera um relatório em Excel com os dados de acoplamentos (Truck, Trailer, Employer).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouplingHistoryReportStrategy implements ReportStrategy {

    private static final String SHEET_NAME = "Histórico de Acoplamento";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String TRUCK_ID_FILTER = "truckId";
    private static final String TRAILER_ID_FILTER = "trailerId";
    private static final String EMPLOYER_ID_FILTER = "employerId";
    private static final String STRING_TYPE = "STRING";
    private static final String NUMBER_TYPE = "NUMBER";
    private static final String NUMBER_FORMAT = "#,##0";
    private final TruckTrailerCombinationRepository combinationRepository;
    private final ReportExcelGenerator excelGenerator;

    @Override
    public byte[] generateReport(Map<String, Object> filters) {
        log.info("Iniciando geração do relatório de Histórico de Acoplamento");

        try {
            // 1. Buscar dados com filtros dinâmicos
            List<TruckTrailerCombinationEntity> combinations = fetchDataWithFilters(filters);

            // 2. Mapear dados para formato de Excel
            List<Map<String, Object>> excelData = mapToExcelData(combinations);

            // 3. Definir configuração das colunas
            List<ExcelColumnConfig> columnConfigs = defineColumnConfigs();

            // 4. Gerar Excel
            byte[] excelBytes = excelGenerator.generate(SHEET_NAME, columnConfigs, excelData);
            log.info("Relatório de Histórico de Acoplamento gerado com sucesso: {} bytes", excelBytes.length);

            return excelBytes;

        } catch (Exception e) {
            log.error("Erro ao gerar relatório de Histórico de Acoplamento: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar relatório de Histórico de Acoplamento: " + e.getMessage(), e);
        }
    }

    @Override
    public ReportNameEnum getReportName() {
        return ReportNameEnum.COUPLING_HISTORY;
    }

    @Override
    public String getReportFileName() {
        return "couplingHistory";
    }

    /**
     * Busca dados com filtros dinâmicos usando Specifications.
     * Filtros suportados: truckId, trailerId, employerId
     */
    private List<TruckTrailerCombinationEntity> fetchDataWithFilters(Map<String, Object> filters) {
        List<FilterCriteria> criteriaList = new ArrayList<>();

        // Construir predicates baseado nos filtros fornecidos
        if (filters.containsKey(TRUCK_ID_FILTER) && filters.get(TRUCK_ID_FILTER) != null) {
            criteriaList.add(new FilterCriteria("truck.id", FilterCriteriaOperator.EQUAL, filters.get(TRUCK_ID_FILTER)));
        }

        if (filters.containsKey(TRAILER_ID_FILTER) && filters.get(TRAILER_ID_FILTER) != null) {
            criteriaList.add(new FilterCriteria("trailer.id", FilterCriteriaOperator.EQUAL, filters.get(TRAILER_ID_FILTER)));
        }

        if (filters.containsKey(EMPLOYER_ID_FILTER) && filters.get(EMPLOYER_ID_FILTER) != null) {
            criteriaList.add(new FilterCriteria("employer.id", FilterCriteriaOperator.EQUAL, filters.get(EMPLOYER_ID_FILTER)));
        }

        return criteriaList.isEmpty()
                ? combinationRepository.findAll()
                : combinationRepository.findAll(new FilterCriteriaSpecification<>(criteriaList));
    }

    /**
     * Mapeia entidades para formato de dados para o Excel.
     */
    private List<Map<String, Object>> mapToExcelData(List<TruckTrailerCombinationEntity> combinations) {
        return combinations.stream()
                .map(this::mapCombinationToRow)
                .toList();
    }

    /**
     * Mapeia uma entidade TruckTrailerCombination para uma linha de dados.
     */
    private Map<String, Object> mapCombinationToRow(TruckTrailerCombinationEntity combination) {
        Map<String, Object> row = new LinkedHashMap<>();

        row.put("truckPlate", getTruckPlate(combination));
        row.put("trailerPlate", getTrailerPlate(combination));
        row.put("employerName", getEmployerName(combination));
        row.put("initialMileage", combination.getInitialMileage() != null ? combination.getInitialMileage() : 0);
        row.put("finalMileage", combination.getFinalMileage() != null ? combination.getFinalMileage() : 0);
        row.put("distance", calculateDistance(combination));
        row.put("finishedAt", formatFinishedDate(combination));
        row.put("createdAt", formatCreatedDate(combination));

        return row;
    }

    private String getTruckPlate(TruckTrailerCombinationEntity combination) {
        return combination.getTruck() != null ? combination.getTruck().getLicensePlate() : "N/A";
    }

    private String getTrailerPlate(TruckTrailerCombinationEntity combination) {
        return combination.getTrailer() != null ? combination.getTrailer().getLicensePlate() : "N/A";
    }

    private String getEmployerName(TruckTrailerCombinationEntity combination) {
        return combination.getEmployer() != null ? combination.getEmployer().getFullName() : "N/A";
    }

    private long calculateDistance(TruckTrailerCombinationEntity combination) {
        if (combination.getInitialMileage() != null && combination.getFinalMileage() != null) {
            return combination.getFinalMileage() - combination.getInitialMileage();
        }
        return 0;
    }

    private String formatFinishedDate(TruckTrailerCombinationEntity combination) {
        return combination.getFinishedAt() != null
                ? combination.getFinishedAt().format(DATE_TIME_FORMATTER)
                : "Ativo";
    }

    private String formatCreatedDate(TruckTrailerCombinationEntity combination) {
        return combination.getCreatedAt() != null
                ? combination.getCreatedAt().format(DATE_TIME_FORMATTER)
                : "N/A";
    }

    /**
     * Define a configuração das colunas do Excel.
     */
    private List<ExcelColumnConfig> defineColumnConfigs() {
        return List.of(
                ExcelColumnConfig.builder()
                        .headerName("Placa Caminhão")
                        .attributeName("truckPlate")
                        .width(18)
                        .dataType(STRING_TYPE)
                        .position(0)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Placa Reboque")
                        .attributeName("trailerPlate")
                        .width(18)
                        .dataType(STRING_TYPE)
                        .position(1)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Motorista")
                        .attributeName("employerName")
                        .width(25)
                        .dataType(STRING_TYPE)
                        .position(2)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("KM Inicial")
                        .attributeName("initialMileage")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(3)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("KM Final")
                        .attributeName("finalMileage")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(4)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Distância (KM)")
                        .attributeName("distance")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(5)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Data Finalização")
                        .attributeName("finishedAt")
                        .width(20)
                        .dataType("DATETIME")
                        .position(6)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Data Criação")
                        .attributeName("createdAt")
                        .width(20)
                        .dataType("DATETIME")
                        .position(7)
                        .build()
        );
    }
}
