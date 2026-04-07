package com.devsenior.jmorera.bibliokeep.controller;

import com.devsenior.jmorera.bibliokeep.model.dto.books.BookDto;
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookSearchResponse;
import com.devsenior.jmorera.bibliokeep.model.dto.books.CreateBookRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.books.UpdateBookStatusRequest;
import com.devsenior.jmorera.bibliokeep.service.book.BookService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody CreateBookRequest request) {
        var created = bookService.createBook(request);
        return ResponseEntity.created(URI.create("/api/books/" + created.id())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> findAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<BookSearchResponse> searchBooks(@RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookDto> updateBookStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookStatusRequest request) {
        return ResponseEntity.ok(bookService.updateBookStatus(id, request));
    }
}
