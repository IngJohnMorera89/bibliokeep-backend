package com.devsenior.jmorera.bibliokeep.service.loan;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.jmorera.bibliokeep.exception.LoanNotFoundException;
import com.devsenior.jmorera.bibliokeep.mapper.LoanMapper;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.CreateLoanRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.LoanDto;
import com.devsenior.jmorera.bibliokeep.model.entity.Book;
import com.devsenior.jmorera.bibliokeep.repository.BookRepository;
import com.devsenior.jmorera.bibliokeep.repository.LoanRepository;
import com.devsenior.jmorera.bibliokeep.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanDto createLoan(CreateLoanRequest request) {
        var ownerId = getCurrentOwnerId();
        var book = bookRepository.findByIdAndOwnerId(request.bookId(), ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found or does not belong to the user."));

        if (book.getIsLent()) {
            throw new IllegalArgumentException("Book is already lent.");
        }

        if (request.dueDate().isBefore(request.loanDate())) {
            throw new IllegalArgumentException("Due date must be equal or after loan date.");
        }

        book.setIsLent(true);
        bookRepository.save(book);

        var loan = loanMapper.toEntity(request);
        loan.setReturned(false);
        var savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Override
    @Transactional
    public LoanDto returnLoan(Long loanId) {
        var ownerId = getCurrentOwnerId();
        var bookIds = getCurrentOwnerBookIds(ownerId);
        var loan = loanRepository.findByIdAndBookIdIn(loanId, bookIds)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found or does not belong to the user."));

        if (loan.getReturned()) {
            return loanMapper.toDto(loan);
        }

        loan.setReturned(true);
        var updatedLoan = loanRepository.save(loan);
        var book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Associated book not found."));
        book.setIsLent(false);
        bookRepository.save(book);
        return loanMapper.toDto(updatedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDto findById(Long loanId) {
        var ownerId = getCurrentOwnerId();
        var bookIds = getCurrentOwnerBookIds(ownerId);
        var loan = loanRepository.findByIdAndBookIdIn(loanId, bookIds)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found or does not belong to the user."));
        return loanMapper.toDto(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> findAll() {
        var ownerId = getCurrentOwnerId();
        var bookIds = getCurrentOwnerBookIds(ownerId);
        return loanRepository.findByBookIdIn(bookIds).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    private UUID getCurrentOwnerId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated.");
        }

        var principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String username) {
            email = username;
        } else {
            throw new AccessDeniedException("Unable to resolve authenticated user.");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."))
                .getId();
    }

    private List<Long> getCurrentOwnerBookIds(UUID ownerId) {
        return bookRepository.findAllByOwnerId(ownerId).stream()
                .map(Book::getId)
                .toList();
    }
}
