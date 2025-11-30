package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireCondition;
import br.com.frotasyncro.domain.tire.enums.TireStatus;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryTireResponseDTO {

    private UUID id;

    private String fireCode;
    private String manufacturer;
    private int manufactureYear;
    private Long mileage;
    private TireCondition tireCondition;
    private TireStatus tireStatus;

    @Nullable
    private String licensePlate;

}
