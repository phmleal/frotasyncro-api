package br.com.frotasyncro.domain.contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpenseType {
    FUEL_SUPPLY("fuel_supply"), MAINTENANCE("maintenance"), HELPER("helper"),
    OTHERS(
            "others");

    private final String code;
}
