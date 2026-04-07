package com.devsenior.jmorera.bibliokeep.model.dto.books;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

import com.devsenior.jmorera.bibliokeep.model.enums.BookStatus;

public record CreateBookRequest(
		@NotBlank
		String isbn,
		@NotBlank
		@Size(max = 400)
		String title,
		@NotNull
		List<@NotBlank @Size(max = 200) String> authors,
		@Size(max = 5000)
		String description,
		@Size(max = 2000)
		String thumbnail,
		BookStatus status,
		@Min(1)
		@Max(5)
		Integer rating
) {
}

