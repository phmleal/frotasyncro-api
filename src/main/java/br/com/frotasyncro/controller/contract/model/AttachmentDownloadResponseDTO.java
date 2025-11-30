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
public class AttachmentDownloadResponseDTO {
    private String presignedUrl;
    private UUID id;
    private AttachmentType type;
    private String description;
    private LocalDate date;
    private String fileName;
    private LocalDateTime createdAt;

    public AttachmentDownloadResponseDTO(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }

}
