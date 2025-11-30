package br.com.frotasyncro.domain.contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentType {
    DOCUMENT("document"),
    RECEIPT("receipt"),
    INVOICE("invoice"),
    OTHERS("others");

    private final String code;
}

