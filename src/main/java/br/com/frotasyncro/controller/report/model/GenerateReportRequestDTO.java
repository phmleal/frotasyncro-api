package br.com.frotasyncro.controller.report.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GenerateReportRequestDTO {

    @NotBlank(message = "reportName é obrigatório")
    private String reportName;

    private Map<String, Object> filters = new HashMap<>();

}
