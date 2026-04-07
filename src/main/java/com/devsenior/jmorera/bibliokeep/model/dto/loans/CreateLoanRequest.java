package com.devsenior.jmorera.bibliokeep.model.dto.loans;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateLoanRequest(
		@NotNull
		Long bookId,
		@NotBlank
		String contactName,
		@FutureOrPresent
		LocalDate loanDate,
		@FutureOrPresent
		LocalDate dueDate
) {
}

