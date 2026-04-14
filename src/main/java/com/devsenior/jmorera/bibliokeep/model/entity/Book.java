package com.devsenior.jmorera.bibliokeep.model.entity;

import java.util.List;
import java.util.UUID;

import com.devsenior.jmorera.bibliokeep.model.enums.BookStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "books",
		indexes = {
				@Index(name = "idx_books_owner_isbn", columnList = "owner_id,isbn", unique = true)
		},
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_books_owner_isbn", columnNames = {"owner_id", "isbn"})
		}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "owner_id", nullable = false)
	private UUID ownerId;

	@Column(nullable = false, length = 13)
	private String isbn;

	@Column(nullable = false, length = 400)
	private String title;

	@ElementCollection
	@CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
	@OrderColumn(name = "author_order")
	@Column(name = "author", nullable = false, length = 200)
	@Builder.Default
	private List<String> authors = List.of();

	@Column(length = 5000)
	private String description;

	@Column(length = 2000)
	private String thumbnail;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private BookStatus status;

	@Column
	private Integer rating;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isLent = false;
}

