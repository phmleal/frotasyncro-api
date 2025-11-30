package br.com.frotasyncro.controller.contract.delivery.model;

import br.com.frotasyncro.controller.contract.model.CreateContractRequestDTO;
import jakarta.validation.constraints.NotBlank;
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
public class CreateDeliveryRequestDTO extends CreateContractRequestDTO {

    @NotBlank
    private String origin;

    @NotBlank
    private String destiny;

    @NotNull
    private UUID truckTrailerCombinationId;

}
