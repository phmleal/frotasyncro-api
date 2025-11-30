package br.com.frotasyncro.domain.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface Reportable {

    String getSheetName();

    String[] getHeaders();

    Object[] getRow();

    default String formatLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return localDate.format(formatter);
    }

    default String formatLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return localDateTime.format(formatter);
    }

    default String formatCurrency(BigDecimal number) {
        if (number == null) {
            return null;
        }

        return String.format("R$ %.2f", number);
    }


}
