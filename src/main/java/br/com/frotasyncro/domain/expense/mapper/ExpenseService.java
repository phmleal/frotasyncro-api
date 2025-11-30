package br.com.frotasyncro.domain.expense.mapper;

import br.com.frotasyncro.infrastructure.persistence.expense.ExpenseRepository;
import br.com.frotasyncro.infrastructure.persistence.expense.entities.ExpenseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseEntity saveExpense(ExpenseEntity expenseEntity) {
        return expenseRepository.save(expenseEntity);
    }

}
