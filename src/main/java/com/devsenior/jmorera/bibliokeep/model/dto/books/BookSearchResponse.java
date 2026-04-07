package com.devsenior.jmorera.bibliokeep.model.dto.books;

import java.util.List;

public record BookSearchResponse(List<BookPreviewDto> results) {
}

