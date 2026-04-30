package com.devsenior.jmorera.bibliokeep.service.book;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.jmorera.bibliokeep.mapper.BookMapper;
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookDto;
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookSearchResponse;
import com.devsenior.jmorera.bibliokeep.model.dto.books.CreateBookRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.books.UpdateBookStatusRequest;
import com.devsenior.jmorera.bibliokeep.repository.BookRepository;
import com.devsenior.jmorera.bibliokeep.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookDto createBook(CreateBookRequest request) {
        var ownerId = getCurrentOwnerId();
        if (bookRepository.findByOwnerIdAndIsbn(ownerId, request.isbn()).isPresent()) {
            throw new IllegalArgumentException("Book with this ISBN already exists for the user.");
        }

        var book = bookMapper.toEntity(request);
        book.setOwnerId(ownerId);
        book.setIsLent(false);
        var saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookDto updateBookStatus(Long bookId, UpdateBookStatusRequest request) {
        var ownerId = getCurrentOwnerId();
        var book = bookRepository.findByIdAndOwnerId(bookId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found or does not belong to the user."));
        book.setStatus(request.status());
        var updated = bookRepository.save(book);
        return bookMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        var ownerId = getCurrentOwnerId();
        var book = bookRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found or does not belong to the user."));
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<BookDto> findAll() {
        var ownerId = getCurrentOwnerId();
        return bookRepository.findAllByOwnerId(ownerId).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookSearchResponse searchBooks(String query) {
        var ownerId = getCurrentOwnerId();
        var trimmed = query == null ? "" : query.trim();
        var books = trimmed.isEmpty()
                ? bookRepository.findAllByOwnerId(ownerId)
                : bookRepository.searchLocalByOwnerAndTitleOrIsbn(ownerId, trimmed);
        var results = books.stream()
                .map(bookMapper::toPreviewDto)
                .toList();
        return new BookSearchResponse(results);
    }

    private UUID getCurrentOwnerId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated.");
        }

        var principal = authentication.getPrincipal();
        String email;
        if (!(principal instanceof UserDetails userDetails))
            if (principal instanceof String username) {
                email = username;
            } else {
                throw new AccessDeniedException("Unable to resolve authenticated user.");
            }
        else {
            email = userDetails.getUsername();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."))
                .getId();
    }
}
