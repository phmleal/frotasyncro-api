package br.com.frotasyncro.domain.expense.mapper;

import br.com.frotasyncro.controller.contract.model.CreateContractExpenseRequestDTO;
import br.com.frotasyncro.controller.contract.model.CreateContractExpenseResponseDTO;
import br.com.frotasyncro.infrastructure.persistence.expense.entities.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExpenseMapper {

    ExpenseMapper INSTANCE = Mappers.getMapper(ExpenseMapper.class);

    ExpenseEntity toEntity(CreateContractExpenseRequestDTO createContractExpenseRequestDTO);

    CreateContractExpenseResponseDTO toResponse(ExpenseEntity expenseEntity);

}
