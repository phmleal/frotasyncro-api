package br.com.frotasyncro.domain.report.model;

import br.com.frotasyncro.domain.contract.delivery.enums.DeliveryStatus;
import br.com.frotasyncro.domain.contract.enums.ExpenseType;
import br.com.frotasyncro.infrastructure.persistence.contract.entities.DeliveryEntity;
import br.com.frotasyncro.infrastructure.persistence.expense.entities.ExpenseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record WorkOrderReportModel(
        String employerName,
        String referenceCode,
        String origin,
        String destiny,
        BigDecimal advanceValue,
        BigDecimal commission,
        BigDecimal fuelSupply,
        BigDecimal maintenance,
        BigDecimal helper,
        BigDecimal othersExpenses,
        BigDecimal remainingBalance,
        String status,
        BigDecimal finalAverage
) {

    public WorkOrderReportModel(DeliveryEntity delivery) {
        this(
                delivery.getEmployer().getFullName(),
                delivery.getReferenceCode(),
                delivery.getOrigin(),
                delivery.getDestiny(),
                delivery.getAdvanceValue(),
                delivery.getCommission(),
                delivery.getExpenses().stream()
                        .filter(expense -> expense.getExpenseType().equals(ExpenseType.FUEL_SUPPLY))
                        .map(ExpenseEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                delivery.getExpenses().stream()
                        .filter(expense -> expense.getExpenseType().equals(ExpenseType.MAINTENANCE))
                        .map(ExpenseEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                delivery.getExpenses().stream()
                        .filter(expense -> expense.getExpenseType().equals(ExpenseType.HELPER))
                        .map(ExpenseEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                delivery.getExpenses().stream()
                        .filter(expense -> expense.getExpenseType().equals(ExpenseType.OTHERS))
                        .map(ExpenseEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                delivery.getAdvanceValue().subtract(
                        delivery.getExpenses().stream().map(ExpenseEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                ),
                delivery.getDeliveryStatus().getPtDescription(),
                buildFinalAverage(delivery)
        );
    }

    private static BigDecimal buildFinalAverage(DeliveryEntity delivery) {
        if (delivery.getDeliveryStatus() != DeliveryStatus.CLOSED) {
            return BigDecimal.ZERO;
        } else {
            long totalMileage = delivery.getFinalMileage() - delivery.getInitialMileage();

            int totalUnit = delivery.getExpenses().stream()
                    .filter(expense -> expense.getExpenseType().equals(ExpenseType.FUEL_SUPPLY) && expense.getQuantity() != null)
                    .map(ExpenseEntity::getQuantity)
                    .reduce(Integer::sum).orElse(0);

            if (totalUnit == 0) {
                return BigDecimal.ZERO;
            }

            return BigDecimal
                    .valueOf(totalMileage).setScale(2, RoundingMode.HALF_EVEN)
                    .divide(BigDecimal.valueOf(totalUnit),
                            2, RoundingMode.HALF_EVEN);
        }
    }

}
