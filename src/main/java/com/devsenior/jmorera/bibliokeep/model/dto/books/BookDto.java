package com.devsenior.jmorera.bibliokeep.model.dto.books;

import java.util.List;

import com.devsenior.jmorera.bibliokeep.model.enums.BookStatus;

public record BookDto(
		Long id,
		String isbn,
		String title,
		List<String> authors,
		String description,
		String thumbnail,
		BookStatus status,
		Integer rating,
		Boolean isLent
) {
}

