package com.devsenior.jmorera.bibliokeep.service.book;

import com.devsenior.jmorera.bibliokeep.model.dto.books.BookDto;
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookSearchResponse;
import com.devsenior.jmorera.bibliokeep.model.dto.books.CreateBookRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.books.UpdateBookStatusRequest;
import java.util.List;

public interface BookService {

	BookDto createBook(CreateBookRequest request);

	BookDto updateBookStatus(Long bookId, UpdateBookStatusRequest request);

	BookDto findById(Long id);

	List<BookDto> findAll();

	BookSearchResponse searchBooks(String query);
}
