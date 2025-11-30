package br.com.frotasyncro.domain.tire.model;

import br.com.frotasyncro.domain.report.Reportable;
import br.com.frotasyncro.domain.tire.enums.TireCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TireReport implements Reportable {

    private String fireCode;
    private String brand;
    private String model;
    private String size;
    private TireCondition tireCondition;
    private BigDecimal price;
    private LocalDate purchaseDate;
    private Long mileage;
    private Long totalMileage;
    private int retreadingCount;
    private LocalDateTime createdAt;

    @Override
    public String getSheetName() {
        return "Pneus";
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "Numero de Fogo",
                "Marca",
                "Modelo",
                "Tamanho",
                "Condição do Pneu",
                "Preço",
                "Data da Compra",
                "Quilometragem",
                "Quilometragem Total",
                "Número de Recapagens",
                "Criado Em"
        };
    }

    @Override
    public Object[] getRow() {
        return new Object[]{
                fireCode,
                brand,
                model,
                size,
                tireCondition,
                formatCurrency(price),
                formatLocalDate(purchaseDate),
                mileage,
                totalMileage,
                retreadingCount,
                formatLocalDateTime(createdAt)
        };
    }

}
