package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireSide;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTirePositionRequestDTO {

    @NotNull
    private UUID machineId;

    @NotNull
    private UUID tireId;

    @NotNull
    private int axle; // 0 - 8

    @NotNull
    private TireSide side;

}
