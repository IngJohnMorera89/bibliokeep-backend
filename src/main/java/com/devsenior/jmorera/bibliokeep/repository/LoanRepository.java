package com.devsenior.jmorera.bibliokeep.repository;

import com.devsenior.jmorera.bibliokeep.model.entity.Loan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

	List<Loan> findByDueDateBeforeAndReturnedFalse(LocalDate dueDate);

	List<Loan> findByBookIdIn(List<Long> bookIds);

	Optional<Loan> findByIdAndBookIdIn(Long id, List<Long> bookIds);
}

