package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireCondition;
import br.com.frotasyncro.domain.tire.enums.TireStatus;
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
public class UpdateTireRequestDTO {

    private LocalDate purchaseDate;

    private int manufactureYear;

    private String manufacturer;

    private String fireCode;

    private String observation;

    private TireCondition tireCondition;

    private TireStatus tireStatus;

    private BigDecimal price;

    private Long mileage;

}
