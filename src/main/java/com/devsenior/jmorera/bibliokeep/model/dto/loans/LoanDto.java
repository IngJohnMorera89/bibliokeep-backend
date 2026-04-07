package com.devsenior.jmorera.bibliokeep.model.dto.loans;

import java.time.LocalDate;

public record LoanDto(
		Long id,
		Long bookId,
		String contactName,
		LocalDate loanDate,
		LocalDate dueDate,
		Boolean returned
) {
}

