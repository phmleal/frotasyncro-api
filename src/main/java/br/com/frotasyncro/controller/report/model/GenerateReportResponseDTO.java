package br.com.frotasyncro.controller.report.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GenerateReportResponseDTO {

    /**
     * URL assinada (presigned) para download do arquivo
     */
    private String presignedUrl;

    /**
     * Nome do arquivo gerado
     */
    private String fileName;

}
