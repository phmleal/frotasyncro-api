package br.com.frotasyncro.domain.report.generator.impl;

import br.com.frotasyncro.domain.report.exception.ReportGenerationException;
import br.com.frotasyncro.domain.report.generator.ReportExcelGenerator;
import br.com.frotasyncro.domain.report.model.ExcelColumnConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Implementação de geração de relatórios Excel usando Apache POI.
 */
@Slf4j
@Component
public class PoiReportExcelGenerator implements ReportExcelGenerator {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String CURRENCY_FORMAT = "#,##0.00";

    @Override
    public byte[] generate(String sheetName, List<ExcelColumnConfig> columnConfigs, List<Map<String, Object>> data) {
        log.debug("Iniciando geração de Excel com {} linhas", data.size());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Criar cabeçalho
            createHeader(sheet, columnConfigs, workbook);

            // Adicionar dados
            addDataRows(sheet, columnConfigs, data, workbook);

            // Auto-ajustar colunas se não tiver largura customizada
            autoAdjustColumns(sheet, columnConfigs);

            // Converter para bytes
            return workbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Erro ao gerar Excel: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar arquivo Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateMultiSheet(Map<String, SheetData> sheetsData) {
        log.debug("Iniciando geração de Excel com {} abas", sheetsData.size());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            for (Map.Entry<String, SheetData> entry : sheetsData.entrySet()) {
                String sheetName = entry.getKey();
                SheetData sheetData = entry.getValue();

                Sheet sheet = workbook.createSheet(sheetName);
                createHeader(sheet, sheetData.columnConfigs(), workbook);
                addDataRows(sheet, sheetData.columnConfigs(), sheetData.data(), workbook);
                autoAdjustColumns(sheet, sheetData.columnConfigs());
            }

            return workbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Erro ao gerar Excel multi-aba: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar arquivo Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Cria a linha de cabeçalho com formatação
     */
    private void createHeader(Sheet sheet, List<ExcelColumnConfig> columnConfigs, XSSFWorkbook workbook) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook);

        // Ordenar colunas por posição
        List<ExcelColumnConfig> sortedConfigs = columnConfigs.stream()
                .sorted(Comparator.comparingInt(c -> c.getPosition() != null ? c.getPosition() : Integer.MAX_VALUE))
                .toList();

        for (int i = 0; i < sortedConfigs.size(); i++) {
            ExcelColumnConfig config = sortedConfigs.get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(config.getHeaderName());
            cell.setCellStyle(headerStyle);
        }

        // Congelar primeira linha
        sheet.createFreezePane(0, 1);
    }

    /**
     * Adiciona linhas de dados com formatação apropriada
     */
    private void addDataRows(Sheet sheet, List<ExcelColumnConfig> columnConfigs,
                             List<Map<String, Object>> data, XSSFWorkbook workbook) {
        // Ordenar colunas por posição
        List<ExcelColumnConfig> sortedConfigs = columnConfigs.stream()
                .sorted(Comparator.comparingInt(c -> c.getPosition() != null ? c.getPosition() : Integer.MAX_VALUE))
                .toList();

        int rowNum = 1;
        for (Map<String, Object> rowData : data) {
            Row row = sheet.createRow(rowNum++);

            for (int colNum = 0; colNum < sortedConfigs.size(); colNum++) {
                ExcelColumnConfig config = sortedConfigs.get(colNum);
                Object value = rowData.get(config.getAttributeName());

                Cell cell = row.createCell(colNum);
                setCellValue(cell, value, config, workbook);
            }
        }
    }

    /**
     * Define o valor da célula com formatação apropriada
     */
    private void setCellValue(Cell cell, Object value, ExcelColumnConfig config, XSSFWorkbook workbook) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        CellStyle cellStyle = createDataStyle(workbook, config);
        cell.setCellStyle(cellStyle);

        switch (config.getDataType()) {
            case "DATE" -> setCellDateValue(cell, value);
            case "DATETIME" -> setCellDateTimeValue(cell, value);
            case "NUMBER" -> setCellNumberValue(cell, value);
            case "CURRENCY" -> setCellNumberValue(cell, value);
            default -> cell.setCellValue(value.toString());
        }

        if (Boolean.TRUE.equals(config.getWrapText())) {
            cellStyle.setWrapText(true);
        }
    }

    private void setCellDateValue(Cell cell, Object value) {
        if (value instanceof LocalDate localDate) {
            cell.setCellValue(localDate);
        } else if (value instanceof LocalDateTime localDateTime) {
            cell.setCellValue(localDateTime.toLocalDate());
        } else if (value instanceof java.util.Date date) {
            cell.setCellValue(date);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private void setCellDateTimeValue(Cell cell, Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            cell.setCellValue(localDateTime.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
        } else if (value instanceof java.util.Date) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private void setCellNumberValue(Cell cell, Object value) {
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Auto-ajusta a largura das colunas
     */
    private void autoAdjustColumns(Sheet sheet, List<ExcelColumnConfig> columnConfigs) {
        List<ExcelColumnConfig> sortedConfigs = columnConfigs.stream()
                .sorted(Comparator.comparingInt(c -> c.getPosition() != null ? c.getPosition() : Integer.MAX_VALUE))
                .toList();

        for (int i = 0; i < sortedConfigs.size(); i++) {
            ExcelColumnConfig config = sortedConfigs.get(i);
            if (config.getWidth() != null) {
                sheet.setColumnWidth(i, config.getWidth() * 256);
            } else {
                sheet.autoSizeColumn(i);
            }
        }
    }

    /**
     * Cria estilo para cabeçalho (negrito, fundo cinza)
     */
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * Cria estilo para células de dados com formato apropriado
     */
    private CellStyle createDataStyle(XSSFWorkbook workbook, ExcelColumnConfig config) {
        CellStyle style = workbook.createCellStyle();

        // Aplicar formato se definido
        if (config.getFormat() != null) {
            DataFormat dataFormat = workbook.createDataFormat();
            style.setDataFormat(dataFormat.getFormat(config.getFormat()));
        } else if ("DATE".equals(config.getDataType())) {
            DataFormat dataFormat = workbook.createDataFormat();
            style.setDataFormat(dataFormat.getFormat(DATE_FORMAT));
        } else if ("DATETIME".equals(config.getDataType())) {
            DataFormat dataFormat = workbook.createDataFormat();
            style.setDataFormat(dataFormat.getFormat(DATETIME_FORMAT));
        } else if ("CURRENCY".equals(config.getDataType())) {
            DataFormat dataFormat = workbook.createDataFormat();
            style.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));
        }

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    /**
     * Converte workbook em byte array
     */
    private byte[] workbookToBytes(XSSFWorkbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            log.debug("Excel gerado com sucesso: {} bytes", outputStream.size());
            return outputStream.toByteArray();
        }
    }
}

