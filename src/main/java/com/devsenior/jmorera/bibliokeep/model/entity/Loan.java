package com.devsenior.jmorera.bibliokeep.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "loans",
		indexes = {
				@Index(name = "idx_loans_due_date_returned", columnList = "due_date,returned")
		}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "book_id", nullable = false)
	private Long bookId;

	@Column(name = "contact_name", nullable = false, length = 200)
	private String contactName;

	@Column(name = "loan_date", nullable = false)
	private LocalDate loanDate;

	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	@Column(nullable = false)
	@Builder.Default
	private Boolean returned = false;
}

