package br.com.frotasyncro.domain.report.enums;

import lombok.Getter;

@Getter
public enum ReportNameEnum {
    COUPLING_HISTORY("coupling_history", "Histórico de Acoplamento"),
    WORK_ORDERS("work_orders", "Ordens de Trabalho"),
    TIRES("tires", "Relatório de Pneus"),
    DRIVERS("drivers", "Relatório de Motoristas");

    private final String code;
    private final String description;

    ReportNameEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ReportNameEnum fromCode(String code) {
        for (ReportNameEnum report : ReportNameEnum.values()) {
            if (report.code.equalsIgnoreCase(code)) {
                return report;
            }
        }
        throw new IllegalArgumentException("Tipo de relatório não suportado: " + code);
    }
}
