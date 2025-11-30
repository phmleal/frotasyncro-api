package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireEventType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTireHistoryRequestDTO {

    @NotNull
    private TireEventType type;

    private String observation;

    private String workshop;


    public CreateTireHistoryRequestDTO(TireEventType type) {
        this.type = type;
    }

}
