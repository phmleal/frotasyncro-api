package br.com.frotasyncro.controller.home.model;

import br.com.frotasyncro.controller.contract.delivery.model.DeliveryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeResponseDTO {

    private String fullName;
    private long activeEmployers;
    private long activeContracts;
    private long totalCouplings;
    private String truckPlate;
    private String truckModel;
    private BigDecimal monthlyAmountToReceive;
    private DeliveryResponseDTO order;

    public HomeResponseDTO(String fullName) {
        this.fullName = fullName;
    }

}

