package br.com.frotasyncro.domain.contract.delivery.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    OPEN("Aberta"), IN_PROGRESS("Em Rota"), CLOSING("Em Fechamento"), CLOSED(
            "Fechada"), CANCELED(
            "Cancelada");

    private final String ptDescription;

}
