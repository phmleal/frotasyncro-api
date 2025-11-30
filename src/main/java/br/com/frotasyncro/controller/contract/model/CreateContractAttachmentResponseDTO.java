package br.com.frotasyncro.controller.contract.model;

import br.com.frotasyncro.domain.contract.enums.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateContractAttachmentResponseDTO {
    private UUID id;
    private AttachmentType type;
    private String description;
    private LocalDate date;
    private String s3Key;
    private String fileName;
    private LocalDateTime createdAt;
}
