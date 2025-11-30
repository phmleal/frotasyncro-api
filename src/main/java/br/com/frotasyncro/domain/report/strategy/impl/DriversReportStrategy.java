package br.com.frotasyncro.domain.report.strategy.impl;

import br.com.frotasyncro.core.specification.FilterCriteriaSpecification;
import br.com.frotasyncro.core.specification.enums.FilterCriteriaOperator;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.report.enums.ReportNameEnum;
import br.com.frotasyncro.domain.report.exception.ReportGenerationException;
import br.com.frotasyncro.domain.report.generator.ReportExcelGenerator;
import br.com.frotasyncro.domain.report.model.ExcelColumnConfig;
import br.com.frotasyncro.domain.report.strategy.ReportStrategy;
import br.com.frotasyncro.infrastructure.persistence.employer.EmployerRepository;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estratégia de geração de relatório de Motoristas (Drivers).
 * Gera um relatório em Excel com os dados completos dos motoristas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DriversReportStrategy implements ReportStrategy {

    private static final String SHEET_NAME = "Relatório de Motoristas";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String FULL_NAME_FILTER = "fullName";
    private static final String SOCIAL_NUMBER_FILTER = "socialNumber";
    private static final String DRIVER_LICENSE_FILTER = "driverLicense";
    private static final String STRING_TYPE = "STRING";
    private static final String NUMBER_TYPE = "NUMBER";
    private static final String DATE_TYPE = "DATE";
    private static final String DECIMAL_FORMAT = "#,##0.00";
    private final EmployerRepository employerRepository;
    private final ReportExcelGenerator excelGenerator;

    @Override
    public byte[] generateReport(Map<String, Object> filters) {
        log.info("Iniciando geração do relatório de Motoristas");

        try {
            // 1. Buscar dados com filtros dinâmicos
            List<EmployerEntity> drivers = fetchDataWithFilters(filters);

            // 2. Mapear dados para formato de Excel
            List<Map<String, Object>> excelData = mapToExcelData(drivers);

            // 3. Definir configuração das colunas
            List<ExcelColumnConfig> columnConfigs = defineColumnConfigs();

            // 4. Gerar Excel
            byte[] excelBytes = excelGenerator.generate(SHEET_NAME, columnConfigs, excelData);
            log.info("Relatório de Motoristas gerado com sucesso: {} bytes", excelBytes.length);

            return excelBytes;

        } catch (Exception e) {
            log.error("Erro ao gerar relatório de Motoristas: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar relatório de Motoristas: " + e.getMessage(), e);
        }
    }

    @Override
    public ReportNameEnum getReportName() {
        return ReportNameEnum.DRIVERS;
    }

    @Override
    public String getReportFileName() {
        return "drivers";
    }

    /**
     * Busca dados com filtros dinâmicos usando Specifications.
     * Filtros suportados: fullName, socialNumber, driverLicense
     */
    private List<EmployerEntity> fetchDataWithFilters(Map<String, Object> filters) {
        List<FilterCriteria> criteriaList = new ArrayList<>();

        // Construir predicates baseado nos filtros fornecidos
        if (filters.containsKey(FULL_NAME_FILTER) && filters.get(FULL_NAME_FILTER) != null) {
            criteriaList.add(new FilterCriteria(FULL_NAME_FILTER, FilterCriteriaOperator.LIKE, "%" + filters.get(FULL_NAME_FILTER) + "%"));
        }

        if (filters.containsKey(SOCIAL_NUMBER_FILTER) && filters.get(SOCIAL_NUMBER_FILTER) != null) {
            criteriaList.add(new FilterCriteria(SOCIAL_NUMBER_FILTER, FilterCriteriaOperator.LIKE, "%" + filters.get(SOCIAL_NUMBER_FILTER) + "%"));
        }

        if (filters.containsKey(DRIVER_LICENSE_FILTER) && filters.get(DRIVER_LICENSE_FILTER) != null) {
            criteriaList.add(new FilterCriteria(DRIVER_LICENSE_FILTER, FilterCriteriaOperator.LIKE, "%" + filters.get(DRIVER_LICENSE_FILTER) + "%"));
        }

        return criteriaList.isEmpty()
                ? employerRepository.findAll()
                : employerRepository.findAll(new FilterCriteriaSpecification<>(criteriaList));
    }

    /**
     * Mapeia entidades para formato de dados para o Excel.
     */
    private List<Map<String, Object>> mapToExcelData(List<EmployerEntity> drivers) {
        return drivers.stream()
                .map(this::mapDriverToRow)
                .toList();
    }

    /**
     * Mapeia uma entidade Employer (Driver) para uma linha de dados.
     */
    private Map<String, Object> mapDriverToRow(EmployerEntity driver) {
        Map<String, Object> row = new LinkedHashMap<>();

        // Informações pessoais
        row.put("fullName", driver.getFullName());
        row.put("socialNumber", driver.getSocialNumber());
        row.put("birthDate", formatDate(driver.getBirthDate()));
        row.put("age", calculateAge(driver.getBirthDate()));

        // Informações profissionais
        row.put("admissionDate", formatDate(driver.getAdmissionDate()));
        row.put("daysEmployed", calculateDaysEmployed(driver.getAdmissionDate()));
        row.put("commissionPercentage", driver.getCommissionPercentage() != null ? driver.getCommissionPercentage().doubleValue() : 0);

        // Informações da CNH (Carteira Nacional de Habilitação)
        row.put("driverLicense", driver.getDriverLicense() != null ? driver.getDriverLicense() : "N/A");
        row.put("driverLicenseExpiryDate", formatDate(driver.getDriverLicenseExpiryDate()));
        row.put("driverLicenseStatus", getDriverLicenseStatus(driver.getDriverLicenseExpiryDate()));

        // Informações de exames
        row.put("medicalExamValidityDate", formatDate(driver.getMedicalExamValidityDate()));
        row.put("medicalExamStatus", getExamStatus(driver.getMedicalExamValidityDate()));
        row.put("toxicologicalExamValidityDate", formatDate(driver.getToxicologicalExamValidityDate()));
        row.put("toxicologicalExamStatus", getExamStatus(driver.getToxicologicalExamValidityDate()));

        // Informações do usuário
        row.put("userStatus", driver.getUser() != null ? driver.getUser().getStatus().toString() : "N/A");
        row.put("userEmail", driver.getUser() != null && driver.getUser().getEmail() != null ? driver.getUser().getEmail() : "N/A");

        return row;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    private long calculateAge(LocalDate birthDate) {
        if (birthDate != null) {
            return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        }
        return 0;
    }

    private long calculateDaysEmployed(LocalDate admissionDate) {
        if (admissionDate != null) {
            return ChronoUnit.DAYS.between(admissionDate, LocalDate.now());
        }
        return 0;
    }

    private String getDriverLicenseStatus(LocalDate expiryDate) {
        if (expiryDate == null) {
            return "Não informado";
        }

        LocalDate today = LocalDate.now();
        if (today.isAfter(expiryDate)) {
            return "Vencida";
        } else if (ChronoUnit.DAYS.between(today, expiryDate) <= 30) {
            return "Vencendo em breve";
        }
        return "Válida";
    }

    private String getExamStatus(LocalDate validityDate) {
        if (validityDate == null) {
            return "Não realizado";
        }

        LocalDate today = LocalDate.now();
        if (today.isAfter(validityDate)) {
            return "Vencido";
        } else if (ChronoUnit.DAYS.between(today, validityDate) <= 30) {
            return "Vencendo em breve";
        }
        return "Válido";
    }

    /**
     * Define a configuração das colunas do Excel.
     */
    private List<ExcelColumnConfig> defineColumnConfigs() {
        return List.of(
                ExcelColumnConfig.builder()
                        .headerName("Nome Completo")
                        .attributeName("fullName")
                        .width(30)
                        .dataType(STRING_TYPE)
                        .position(0)
                        .wrapText(true)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("CPF")
                        .attributeName("socialNumber")
                        .width(18)
                        .dataType(STRING_TYPE)
                        .position(1)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Data de Nascimento")
                        .attributeName("birthDate")
                        .width(18)
                        .dataType(DATE_TYPE)
                        .position(2)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Idade")
                        .attributeName("age")
                        .width(10)
                        .dataType(NUMBER_TYPE)
                        .position(3)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Data de Admissão")
                        .attributeName("admissionDate")
                        .width(18)
                        .dataType(DATE_TYPE)
                        .position(4)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Dias Empregado")
                        .attributeName("daysEmployed")
                        .width(15)
                        .dataType(NUMBER_TYPE)
                        .position(5)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Percentual de Comissão (%)")
                        .attributeName("commissionPercentage")
                        .width(18)
                        .dataType(NUMBER_TYPE)
                        .format(DECIMAL_FORMAT)
                        .position(6)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Número da CNH")
                        .attributeName("driverLicense")
                        .width(20)
                        .dataType(STRING_TYPE)
                        .position(7)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Validade da CNH")
                        .attributeName("driverLicenseExpiryDate")
                        .width(18)
                        .dataType(DATE_TYPE)
                        .position(8)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Status CNH")
                        .attributeName("driverLicenseStatus")
                        .width(18)
                        .dataType(STRING_TYPE)
                        .position(9)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Validade do Exame Médico")
                        .attributeName("medicalExamValidityDate")
                        .width(20)
                        .dataType(DATE_TYPE)
                        .position(10)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Status Exame Médico")
                        .attributeName("medicalExamStatus")
                        .width(18)
                        .dataType(STRING_TYPE)
                        .position(11)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Validade do Exame Toxicológico")
                        .attributeName("toxicologicalExamValidityDate")
                        .width(20)
                        .dataType(DATE_TYPE)
                        .position(12)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Status Exame Toxicológico")
                        .attributeName("toxicologicalExamStatus")
                        .width(20)
                        .dataType(STRING_TYPE)
                        .position(13)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Status do Usuário")
                        .attributeName("userStatus")
                        .width(15)
                        .dataType(STRING_TYPE)
                        .position(14)
                        .build(),
                ExcelColumnConfig.builder()
                        .headerName("Email")
                        .attributeName("userEmail")
                        .width(30)
                        .dataType(STRING_TYPE)
                        .position(15)
                        .wrapText(true)
                        .build()
        );
    }
}

