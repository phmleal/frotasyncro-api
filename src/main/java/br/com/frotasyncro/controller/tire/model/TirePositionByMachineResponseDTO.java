package br.com.frotasyncro.controller.tire.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TirePositionByMachineResponseDTO {

    private List<TirePositionResponseDTO> tiresPositions;

}
