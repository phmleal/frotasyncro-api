package br.com.frotasyncro.controller.contract.model;

import br.com.frotasyncro.domain.contract.enums.AttachmentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateContractAttachmentRequestDTO {
    @NotNull
    private AttachmentType type;
    @NotNull
    private String description;
    @NotNull
    private LocalDate date;
}

