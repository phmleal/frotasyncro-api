package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTireRequestDTO {

    private LocalDate purchaseDate;

    @NotNull
    private int manufactureYear;

    @NotBlank
    private String manufacturer;

    @NotBlank
    private String fireCode;

    private String observation;

    @NotNull
    private TireCondition tireCondition;

    private BigDecimal price;

    private Long mileage;

}
