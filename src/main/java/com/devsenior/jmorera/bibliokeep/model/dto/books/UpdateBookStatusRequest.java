package com.devsenior.jmorera.bibliokeep.model.dto.books;

import com.devsenior.jmorera.bibliokeep.model.enums.BookStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateBookStatusRequest(@NotNull BookStatus status) {
}

