package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireEventType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InactivateTirePositionRequestDTO {
    @NotNull
    private TireEventType eventType;
}
