package br.com.frotasyncro.domain.report;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    @SneakyThrows
    public <T extends Reportable> void generateExcel(OutputStream outputStream,
                                                     PageFetcher<T> pageFetcher,
                                                     int pageSize) {

        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            workbook.setCompressTempFiles(true);

            int page = 0;
            List<T> batch;
            Sheet sheet = null;
            int currentRow = 0;

            int[] maxColumnWidths = null;

            do {
                batch = pageFetcher.fetch(page, pageSize);

                for (T dto : batch) {
                    if (sheet == null) {
                        sheet = createSheetWithHeader(workbook, dto);
                        maxColumnWidths = initColumnWidths(dto.getHeaders());
                        currentRow = 1;
                    }

                    writeRow(sheet, currentRow++, dto, maxColumnWidths);
                }

                page++;
            } while (!batch.isEmpty());

            adjustColumnWidths(sheet, maxColumnWidths);

            workbook.write(outputStream);
        } catch (Exception e) {
            throw new IOException("Erro ao gerar Excel", e);
        }
    }

    private Sheet createSheetWithHeader(SXSSFWorkbook workbook, Reportable dto) {
        Sheet sheet = workbook.createSheet(dto.getSheetName());
        Row headerRow = sheet.createRow(0);
        String[] headers = dto.getHeaders();

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        return sheet;
    }

    private int[] initColumnWidths(String[] headers) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        return widths;
    }

    private <T extends Reportable> void writeRow(Sheet sheet, int rowIndex, T dto, int[] maxWidths) {
        Row row = sheet.createRow(rowIndex);
        Object[] values = dto.getRow();

        for (int col = 0; col < values.length; col++) {
            Object value = values[col];
            Cell cell = row.createCell(col);

            if (value != null) {
                if (value instanceof Number num) {
                    cell.setCellValue(num.doubleValue());
                } else {
                    cell.setCellValue(value.toString());
                }

                maxWidths[col] = Math.max(maxWidths[col], value.toString().length());
            }
        }
    }

    private void adjustColumnWidths(Sheet sheet, int[] maxWidths) {
        if (sheet == null || maxWidths == null) return;

        for (int i = 0; i < maxWidths.length; i++) {
            int width = Math.min(maxWidths[i] * 256 + 200, 255 * 256); // padding extra
            sheet.setColumnWidth(i, width);
        }
    }


    @FunctionalInterface
    public interface PageFetcher<T> {
        List<T> fetch(int page, int size);
    }

}
