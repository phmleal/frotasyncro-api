package br.com.frotasyncro.domain.report.strategy.impl;

import br.com.frotasyncro.core.specification.FilterCriteriaSpecification;
import br.com.frotasyncro.core.specification.enums.FilterCriteriaOperator;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import br.com.frotasyncro.domain.report.exception.ReportGenerationException;
import br.com.frotasyncro.domain.report.generator.ReportExcelGenerator;
import br.com.frotasyncro.domain.report.model.ExcelColumnConfig;
import br.com.frotasyncro.domain.report.strategy.ReportStrategy;
import br.com.frotasyncro.infrastructure.persistence.tire.TirePositionRepository;
import br.com.frotasyncro.infrastructure.persistence.tire.TireRepository;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TirePositionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Estratégia de geração de relatório de Pneus.
 * Gera um relatório em Excel com os dados completos dos pneus, incluindo posição atual se alocado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TiresReportStrategy implements ReportStrategy {

    private static final String SHEET_NAME = "Relatório de Pneus";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String FIRE_CODE_FILTER = "fireCode";
    private static final String TIRE_STATUS_FILTER = "tireStatus";
    private static final String TIRE_CONDITION_FILTER = "tireCondition";
    private static final String STRING_TYPE = "STRING";
    private static final String NUMBER_TYPE = "NUMBER";
    private static final String DATE_TYPE = "DATE";
    private static final String NUMBER_FORMAT = "#,##0";
    private final TireRepository tireRepository;
    private final TirePositionRepository tirePositionRepository;
    private final ReportExcelGenerator excelGenerator;

    @Override
    public byte[] generateReport(Map<String, Object> filters) {
        log.info("Iniciando geração do relatório de Pneus");

        try {
            // 1. Buscar dados com filtros dinâmicos
            List<TireEntity> tires = fetchDataWithFilters(filters);

            // 2. Mapear dados para formato de Excel
            List<Map<String, Object>> excelData = mapToExcelData(tires);

            // 3. Definir configuração das colunas
            List<ExcelColumnConfig> columnConfigs = defineColumnConfigs();

            // 4. Gerar Excel
            byte[] excelBytes = excelGenerator.generate(SHEET_NAME, columnConfigs, excelData);
            log.info("Relatório de Pneus gerado com sucesso: {} bytes", excelBytes.length);

            return excelBytes;

        } catch (Exception e) {
            log.error("Erro ao gerar relatório de Pneus: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar relatório de Pneus: " + e.getMessage(), e);
        }
    }

    @Override
    public ReportNameEnum getReportName() {
        return ReportNameEnum.TIRES;
    }

    @Override
    public String getReportFileName() {
        return "tires";
    }

    /**
     * Busca dados com filtros dinâmicos usando Specifications.
     * Filtros suportados: fireCode, tireStatus, tireCondition
     */
    private List<TireEntity> fetchDataWithFilters(Map<String, Object> filters) {
        List<FilterCriteria> criteriaList = new ArrayList<>();

        // Construir predicates baseado nos filtros fornecidos
        if (filters.containsKey(FIRE_CODE_FILTER) && filters.get(FIRE_CODE_FILTER) != null) {
            criteriaList.add(new FilterCriteria(FIRE_CODE_FILTER, FilterCriteriaOperator.LIKE, "%" + filters.get(FIRE_CODE_FILTER) + "%"));
        }

        if (filters.containsKey(TIRE_STATUS_FILTER) && filters.get(TIRE_STATUS_FILTER) != null) {
            criteriaList.add(new FilterCriteria(TIRE_STATUS_FILTER, FilterCriteriaOperator.EQUAL, filters.get(TIRE_STATUS_FILTER)));
        }

        if (filters.containsKey(TIRE_CONDITION_FILTER) && filters.get(TIRE_CONDITION_FILTER) != null) {
            criteriaList.add(new FilterCriteria(TIRE_CONDITION_FILTER, FilterCriteriaOperator.EQUAL, filters.get(TIRE_CONDITION_FILTER)));
        }

        return criteriaList.isEmpty()
                ? tireRepository.findAll()
                : tireRepository.findAll(new FilterCriteriaSpecification<>(criteriaList));
    }

    /**
     * Mapeia entidades para formato de dados para o Excel.
     */
    private List<Map<String, Object>> mapToExcelData(List<TireEntity> tires) {
        return tires.stream()
                .map(this::mapTireToRow)
                .toList();
    }

    /**
     * Mapeia uma entidade Tire para uma linha de dados.
     */
    private Map<String, Object> mapTireToRow(TireEntity tire) {
        Map<String, Object> row = new LinkedHashMap<>();

        // Informações básicas do pneu
        row.put("fireCode", tire.getFireCode());
        row.put("manufacturer", tire.getManufacturer());
        row.put("manufactureYear", tire.getManufactureYear());
        row.put("purchaseDate", formatDate(tire.getPurchaseDate()));
        row.put("price", tire.getPrice() != null ? tire.getPrice().doubleValue() : 0);
        row.put("mileage", tire.getMileage() != null ? tire.getMileage() : 0);
        row.put("tireCondition", formatEnum(tire.getTireCondition()));
        row.put("tireStatus", formatEnum(tire.getTireStatus()));
        row.put("observation", tire.getObservation() != null ? tire.getObservation() : "");

        // Informações de alocação atual (se estiver alocado)
        TirePositionInfo positionInfo = getCurrentPosition(tire);
        row.put("vehiclePlate", positionInfo.vehiclePlate);
        row.put("currentAxle", positionInfo.axle);
        row.put("currentSide", positionInfo.side);
        row.put("isAllocated", positionInfo.isAllocated ? "Sim" : "Não");

        return row;
    }

    /**
     * Obtém a posição atual do pneu se estiver alocado.
     */
    private TirePositionInfo getCurrentPosition(TireEntity tire) {
        Optional<TirePositionEntity> activePosition = tirePositionRepository.findByTireAndInUseTrue(tire);

        if (activePosition.isPresent()) {
            TirePositionEntity position = activePosition.get();
            String vehiclePlate = position.getMachine() != null ? position.getMachine().getLicensePlate() : "N/A";
            String axle = String.valueOf(position.getAxle());
            String side = position.getSide() != null ? position.getSide().toString() : "N/A";
            return new TirePositionInfo(vehiclePlate, axle, side, true);
        }

        return new TirePositionInfo("N/A", "N/A", "N/A", false);
    }

    private String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    private String formatEnum(Enum<?> enumValue) {
        return enumValue != null ? enumValue.toString() : "N/A";
    }

    /**
     * Define a configuração das colunas do Excel.
     */
    private List<ExcelColumnConfig> defineColumnConfigs() {
        return List.of(
                ExcelColumnConfig.builder()
                        .headerName("Código do Pneu")
                        .attributeName("fireCode")
                        .width(20)
                        .dataType(STRING_TYPE)
                        .position(0)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Fabricante")
                        .attributeName("manufacturer")
                        .width(20)
                        .dataType(STRING_TYPE)
                        .position(1)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Ano de Fabricação")
                        .attributeName("manufactureYear")
                        .width(18)
                        .dataType(NUMBER_TYPE)
                        .position(2)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Data de Compra")
                        .attributeName("purchaseDate")
                        .width(18)
                        .dataType(DATE_TYPE)
                        .position(3)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Preço")
                        .attributeName("price")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .format("#,##0.00")
                        .position(4)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Quilometragem")
                        .attributeName("mileage")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .format(NUMBER_FORMAT)
                        .position(5)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Condição")
                        .attributeName("tireCondition")
                        .width(15)
                        .dataType(STRING_TYPE)
                        .position(6)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Status")
                        .attributeName("tireStatus")
                        .width(15)
                        .dataType(STRING_TYPE)
                        .position(7)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Observações")
                        .attributeName("observation")
                        .width(30)
                        .dataType(STRING_TYPE)
                        .position(8)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Placa do Veículo (Alocação Atual)")
                        .attributeName("vehiclePlate")
                        .width(25)
                        .dataType(STRING_TYPE)
                        .position(9)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Eixo Atual")
                        .attributeName("currentAxle")
                        .width(12)
                        .dataType(STRING_TYPE)
                        .position(10)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Lado Atual")
                        .attributeName("currentSide")
                        .width(12)
                        .dataType(STRING_TYPE)
                        .position(11)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Alocado")
                        .attributeName("isAllocated")
                        .width(12)
                        .dataType(STRING_TYPE)
                        .position(12)
                        .build()
        );
    }

    /**
     * Classe auxiliar para armazenar informações de posição do pneu.
     */
    private static class TirePositionInfo {
        String vehiclePlate;
        String axle;
        String side;
        boolean isAllocated;

        TirePositionInfo(String vehiclePlate, String axle, String side, boolean isAllocated) {
            this.vehiclePlate = vehiclePlate;
            this.axle = axle;
            this.side = side;
            this.isAllocated = isAllocated;
        }
    }
}
