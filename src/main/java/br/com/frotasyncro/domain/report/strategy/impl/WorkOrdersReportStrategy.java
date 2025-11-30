package br.com.frotasyncro.domain.report.strategy.impl;

import br.com.frotasyncro.core.specification.FilterCriteriaSpecification;
import br.com.frotasyncro.core.specification.enums.FilterCriteriaOperator;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import br.com.frotasyncro.domain.report.exception.ReportGenerationException;
import br.com.frotasyncro.domain.report.generator.ReportExcelGenerator;
import br.com.frotasyncro.domain.report.model.ExcelColumnConfig;
import br.com.frotasyncro.domain.report.model.WorkOrderReportModel;
import br.com.frotasyncro.domain.report.strategy.ReportStrategy;
import br.com.frotasyncro.infrastructure.persistence.contract.DeliveryRepository;
import br.com.frotasyncro.infrastructure.persistence.contract.entities.DeliveryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estratégia de geração de relatório de Ordens de Trabalho (Deliveries).
 * Gera um relatório em Excel com os dados de entregas/ordens de trabalho.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkOrdersReportStrategy implements ReportStrategy {

    private static final String SHEET_NAME = "Ordens de Trabalho";
    private static final String TRUCK_ID_FILTER = "truckId";
    private static final String TRAILER_ID_FILTER = "trailerId";
    private static final String EMPLOYER_ID_FILTER = "driverId";
    private static final String STATUS_FILTER = "status";
    private static final String START_DATE_FILTER = "startDate";
    private static final String FINAL_DATE_FILTER = "endDate";
    private static final String STRING_TYPE = "STRING";
    private static final String NUMBER_TYPE = "NUMBER";
    private static final String NUMBER_FORMAT = "#,##0";

    private final DeliveryRepository deliveryRepository;
    private final ReportExcelGenerator excelGenerator;

    @Override
    public byte[] generateReport(Map<String, Object> filters) {
        log.info("Iniciando geração do relatório de Ordens de Trabalho");

        try {
            // 1. Buscar dados com filtros dinâmicos
            List<WorkOrderReportModel> deliveries =
                    fetchDataWithFilters(filters)
                            .stream().map(WorkOrderReportModel::new)
                            .toList();

            // 2. Mapear dados para formato de Excel
            List<Map<String, Object>> excelData = mapToExcelData(deliveries);

            // 3. Definir configuração das colunas
            List<ExcelColumnConfig> columnConfigs = defineColumnConfigs();

            // 4. Gerar Excel
            byte[] excelBytes = excelGenerator.generate(SHEET_NAME, columnConfigs, excelData);
            log.info("Relatório de Ordens de Trabalho gerado com sucesso: {} bytes", excelBytes.length);

            return excelBytes;

        } catch (Exception e) {
            log.error("Erro ao gerar relatório de Ordens de Trabalho: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar relatório de Ordens de Trabalho: " + e.getMessage(), e);
        }
    }

    @Override
    public ReportNameEnum getReportName() {
        return ReportNameEnum.WORK_ORDERS;
    }

    @Override
    public String getReportFileName() {
        return "workOrders";
    }

    /**
     * Busca dados com filtros dinâmicos usando Specifications.
     * Filtros suportados: truckId, trailerId, employerId, deliveryStatus
     */
    private List<DeliveryEntity> fetchDataWithFilters(Map<String, Object> filters) {
        List<FilterCriteria> criteriaList = new ArrayList<>();

        if (filters.containsKey(START_DATE_FILTER) && filters.get(START_DATE_FILTER) != null) {
            criteriaList.add(new FilterCriteria(START_DATE_FILTER,
                    FilterCriteriaOperator.GREATER_THAN_OR_EQUAL_TO, filters.get(START_DATE_FILTER)));
        }

        if (filters.containsKey(FINAL_DATE_FILTER) && filters.get(FINAL_DATE_FILTER) != null) {
            criteriaList.add(new FilterCriteria("finalDate",
                    FilterCriteriaOperator.LESS_THAN_OR_EQUAL_TO,
                    filters.get(FINAL_DATE_FILTER)));
        }

        if (filters.containsKey(TRUCK_ID_FILTER) && filters.get(TRUCK_ID_FILTER) != null) {
            criteriaList.add(new FilterCriteria("truckTrailerCombination.truck.id", FilterCriteriaOperator.EQUAL, filters.get(TRUCK_ID_FILTER)));
        }

        if (filters.containsKey(TRAILER_ID_FILTER) && filters.get(TRAILER_ID_FILTER) != null) {
            criteriaList.add(new FilterCriteria("truckTrailerCombination.trailer.id", FilterCriteriaOperator.EQUAL, filters.get(TRAILER_ID_FILTER)));
        }

        if (filters.containsKey(EMPLOYER_ID_FILTER) && filters.get(EMPLOYER_ID_FILTER) != null) {
            criteriaList.add(new FilterCriteria("employer.id", FilterCriteriaOperator.EQUAL, filters.get(EMPLOYER_ID_FILTER)));
        }

        if (filters.containsKey(STATUS_FILTER) && filters.get(STATUS_FILTER) != null) {
            criteriaList.add(new FilterCriteria("deliveryStatus", FilterCriteriaOperator.EQUAL, filters.get(STATUS_FILTER)));
        }

        return criteriaList.isEmpty()
                ? deliveryRepository.findAll()
                : deliveryRepository.findAll(new FilterCriteriaSpecification<>(criteriaList));
    }

    /**
     * Mapeia entidades para formato de dados para o Excel.
     */
    private List<Map<String, Object>> mapToExcelData(List<WorkOrderReportModel> deliveries) {
        return deliveries.stream()
                .map(this::mapDeliveryToRow)
                .toList();
    }

    /**
     * Mapeia uma entidade Delivery para uma linha de dados.
     */
    private Map<String, Object> mapDeliveryToRow(WorkOrderReportModel delivery) {
        Map<String, Object> row = new LinkedHashMap<>();

        for (var component : delivery.getClass().getRecordComponents()) {
            try {
                var accessor = component.getAccessor();
                row.put(component.getName(), accessor.invoke(delivery));
            } catch (Exception e) {
                throw new ReportGenerationException(e);
            }
        }

        return row;
    }

    /**
     * Define a configuração das colunas do Excel.
     */
    private List<ExcelColumnConfig> defineColumnConfigs() {
        return List.of(
                ExcelColumnConfig.builder()
                        .headerName("Motorista")
                        .attributeName("employerName")
                        .width(15)
                        .dataType(STRING_TYPE)
                        .position(0)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Nº Referência")
                        .attributeName("referenceCode")
                        .width(15)
                        .dataType(STRING_TYPE)
                        .position(1)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Origem")
                        .attributeName("origin")
                        .width(25)
                        .dataType(STRING_TYPE)
                        .position(2)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Destino")
                        .attributeName("destiny")
                        .width(25)
                        .dataType(STRING_TYPE)
                        .position(3)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Adiantamento")
                        .attributeName("advanceValue")
                        .width(25)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(4)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Comissão")
                        .attributeName("commission")
                        .width(25)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(5)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Abastecimento")
                        .attributeName("fuelSupply")
                        .width(25)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(4)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Manutenção")
                        .attributeName("maintenance")
                        .width(25)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(5)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Chapa")
                        .attributeName("helper")
                        .width(25)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(4)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Outras despesas")
                        .attributeName("othersExpenses")
                        .width(25)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(5)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Saldo Final")
                        .attributeName("remainingBalance")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(6)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Média Final")
                        .attributeName("finalAverage")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(6)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Status")
                        .attributeName("status")
                        .width(15)
                        .dataType(STRING_TYPE)
                        .position(11)
                        .build()
        );
    }
}

