package com.devsenior.jmorera.bibliokeep.service.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.devsenior.jmorera.bibliokeep.exception.LoanNotFoundException;
import com.devsenior.jmorera.bibliokeep.mapper.LoanMapper;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.CreateLoanRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.LoanDto;
import com.devsenior.jmorera.bibliokeep.model.entity.Book;
import com.devsenior.jmorera.bibliokeep.model.entity.Loan;
import com.devsenior.jmorera.bibliokeep.model.entity.User;
import com.devsenior.jmorera.bibliokeep.repository.BookRepository;
import com.devsenior.jmorera.bibliokeep.repository.LoanRepository;
import com.devsenior.jmorera.bibliokeep.repository.UserRepository;

public class LoanServiceImplTest {

    private LoanRepository loanRepositoryMock;
    private BookRepository bookRepositoryMock;
    private UserRepository userRepositoryMock;
    private LoanMapper loanMapperMock;
    private LoanServiceImpl loanService;

    private Authentication authenticationMock;
    private SecurityContext securityContextMock;

    @BeforeEach
    void init() {
        loanRepositoryMock = mock(LoanRepository.class);
        bookRepositoryMock = mock(BookRepository.class);
        userRepositoryMock = mock(UserRepository.class);
        loanMapperMock = mock(LoanMapper.class);
        loanService = new LoanServiceImpl(loanRepositoryMock, bookRepositoryMock, userRepositoryMock, loanMapperMock);

        authenticationMock = mock(Authentication.class);
        securityContextMock = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContextMock);
    }

    private void setupMockSecurityContext(String email) {
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);
        when(authenticationMock.getPrincipal()).thenReturn(email);
    }

    private UUID mockedOwnerId() {
        return UUID.fromString("11111111-2222-3333-4444-555555555555");
    }

    private User mockedUser() {
        User user = new User();
        user.setId(mockedOwnerId());
        return user;
    }

    @Test
    void shouldCreateLoan_WhenValidData() {
        // Arrange
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Long bookId = 1L;
        Book mockBook = new Book();
        mockBook.setId(bookId);
        mockBook.setIsLent(false);
        when(bookRepositoryMock.findByIdAndOwnerId(bookId, ownerId)).thenReturn(Optional.of(mockBook));

        CreateLoanRequest request = new CreateLoanRequest(bookId, "Contact Name", LocalDate.now(),
                LocalDate.now().plusDays(7));

        Loan mockLoan = new Loan();
        when(loanMapperMock.toEntity(any(CreateLoanRequest.class))).thenReturn(mockLoan);
        when(loanRepositoryMock.save(any(Loan.class))).thenReturn(mockLoan);

        LoanDto mockDto = new LoanDto(1L, bookId, "Contact Name", LocalDate.now(), LocalDate.now().plusDays(7), false);
        when(loanMapperMock.toDto(any(Loan.class))).thenReturn(mockDto);

        // Act
        LoanDto result = loanService.createLoan(request);

        // Assert
        assertNotNull(result);
        assertTrue(mockBook.getIsLent());
        verify(bookRepositoryMock).save(mockBook);
        verify(loanRepositoryMock).save(mockLoan);
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenBookNotFoundInCreateLoan() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Long bookId = 1L;
        when(bookRepositoryMock.findByIdAndOwnerId(bookId, ownerId)).thenReturn(Optional.empty());

        CreateLoanRequest request = new CreateLoanRequest(bookId, "Contact Name", LocalDate.now(),
                LocalDate.now().plusDays(7));

        assertThrows(IllegalArgumentException.class, () -> loanService.createLoan(request));
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenBookIsAlreadyLentInCreateLoan() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Long bookId = 1L;
        Book mockBook = new Book();
        mockBook.setId(bookId);
        mockBook.setIsLent(true); // Already lent
        when(bookRepositoryMock.findByIdAndOwnerId(bookId, ownerId)).thenReturn(Optional.of(mockBook));

        CreateLoanRequest request = new CreateLoanRequest(bookId, "Contact Name", LocalDate.now(),
                LocalDate.now().plusDays(7));

        assertThrows(IllegalArgumentException.class, () -> loanService.createLoan(request));
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenDueDateIsBeforeLoanDateInCreateLoan() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Long bookId = 1L;
        Book mockBook = new Book();
        mockBook.setId(bookId);
        mockBook.setIsLent(false);
        when(bookRepositoryMock.findByIdAndOwnerId(bookId, ownerId)).thenReturn(Optional.of(mockBook));

        CreateLoanRequest request = new CreateLoanRequest(bookId, "Contact Name", LocalDate.now().plusDays(7),
                LocalDate.now()); // Invalid dates

        assertThrows(IllegalArgumentException.class, () -> loanService.createLoan(request));
    }

    @Test
    void shouldReturnLoan_WhenValidData() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Book mockBook = new Book();
        mockBook.setId(10L);
        mockBook.setIsLent(true);
        when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of(mockBook));

        Long loanId = 1L;
        Loan mockLoan = new Loan();
        mockLoan.setId(loanId);
        mockLoan.setBookId(10L);
        mockLoan.setReturned(false);
        when(loanRepositoryMock.findByIdAndBookIdIn(eq(loanId), any(List.class))).thenReturn(Optional.of(mockLoan));
        when(loanRepositoryMock.save(any(Loan.class))).thenReturn(mockLoan);
        when(bookRepositoryMock.findById(10L)).thenReturn(Optional.of(mockBook));

        LoanDto mockDto = new LoanDto(loanId, 10L, "Contact Name", LocalDate.now(), LocalDate.now().plusDays(7), true);
        when(loanMapperMock.toDto(any(Loan.class))).thenReturn(mockDto);

        LoanDto result = loanService.returnLoan(loanId);

        assertNotNull(result);
        assertTrue(result.returned());
        assertFalse(mockBook.getIsLent());
        verify(bookRepositoryMock).save(mockBook);
        verify(loanRepositoryMock).save(mockLoan);
    }

    @Test
    void shouldReturnExistingDto_WhenLoanAlreadyReturned() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Book mockBook = new Book();
        mockBook.setId(10L);
        when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of(mockBook));

        Long loanId = 1L;
        Loan mockLoan = new Loan();
        mockLoan.setId(loanId);
        mockLoan.setBookId(10L);
        mockLoan.setReturned(true); // Already returned
        when(loanRepositoryMock.findByIdAndBookIdIn(eq(loanId), any(List.class))).thenReturn(Optional.of(mockLoan));

        LoanDto mockDto = new LoanDto(loanId, 10L, "Contact Name", LocalDate.now(), LocalDate.now().plusDays(7), true);
        when(loanMapperMock.toDto(any(Loan.class))).thenReturn(mockDto);

        LoanDto result = loanService.returnLoan(loanId);

        assertNotNull(result);
        verify(loanRepositoryMock, never()).save(any());
        verify(bookRepositoryMock, never()).save(any());
    }

    @Test
    void shouldThrowLoanNotFoundException_WhenLoanNotFoundInReturnLoan() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));
        when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of());

        Long loanId = 1L;
        when(loanRepositoryMock.findByIdAndBookIdIn(eq(loanId), any(List.class))).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.returnLoan(loanId));
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenAssociatedBookNotFoundInReturnLoan() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Book mockBook = new Book();
        mockBook.setId(10L);
        when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of(mockBook));

        Long loanId = 1L;
        Loan mockLoan = new Loan();
        mockLoan.setId(loanId);
        mockLoan.setBookId(10L);
        mockLoan.setReturned(false);
        when(loanRepositoryMock.findByIdAndBookIdIn(eq(loanId), any(List.class))).thenReturn(Optional.of(mockLoan));
        when(loanRepositoryMock.save(any(Loan.class))).thenReturn(mockLoan);

        // Associated book not found
        when(bookRepositoryMock.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> loanService.returnLoan(loanId));
    }

    @Test
    void shouldFindById_WhenValidData() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Book mockBook = new Book();
        mockBook.setId(10L);
        when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of(mockBook));

        Long loanId = 1L;
        Loan mockLoan = new Loan();
        mockLoan.setId(loanId);
        when(loanRepositoryMock.findByIdAndBookIdIn(eq(loanId), any(List.class))).thenReturn(Optional.of(mockLoan));

        LoanDto mockDto = new LoanDto(loanId, 10L, "Contact Name", LocalDate.now(), LocalDate.now().plusDays(7), false);
        when(loanMapperMock.toDto(any(Loan.class))).thenReturn(mockDto);

        LoanDto result = loanService.findById(loanId);

        assertNotNull(result);
    }

    @Test
    void shouldThrowLoanNotFoundException_WhenLoanNotFoundInFindById() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));
        when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of());

        Long loanId = 1L;
        when(loanRepositoryMock.findByIdAndBookIdIn(eq(loanId), any(List.class))).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.findById(loanId));
    }

    @Test
    void shouldFindAll_WhenValidData() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        UUID ownerId = mockedOwnerId();
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        Book mockBook = new Book();
        mockBook.setId(10L);
        when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of(mockBook));

        Loan mockLoan = new Loan();
        when(loanRepositoryMock.findByBookIdIn(any(List.class))).thenReturn(List.of(mockLoan));

        LoanDto mockDto = new LoanDto(1L, 10L, "Contact Name", LocalDate.now(), LocalDate.now().plusDays(7), false);
        when(loanMapperMock.toDto(any(Loan.class))).thenReturn(mockDto);

        List<LoanDto> result = loanService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowAccessDeniedException_WhenAuthenticationIsNull() {
        when(securityContextMock.getAuthentication()).thenReturn(null);
        assertThrows(AccessDeniedException.class, () -> loanService.findAll());
    }

    @Test
    void shouldThrowAccessDeniedException_WhenUserNotAuthenticated() {
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> loanService.findAll());
    }

    @Test
    void shouldResolveEmail_WhenPrincipalIsUserDetails() {
        String email = "userdetails@test.com";
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);

        UserDetails userDetailsMock = mock(UserDetails.class);
        when(userDetailsMock.getUsername()).thenReturn(email);
        when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedUser()));

        loanService.findAll();
        verify(userRepositoryMock).findByEmail(email);
    }

    @Test
    void shouldThrowAccessDeniedException_WhenPrincipalIsUnknownType() {
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);
        when(authenticationMock.getPrincipal()).thenReturn(12345); // Invalid principal type

        assertThrows(AccessDeniedException.class, () -> loanService.findAll());
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenUserNotFoundInRepository() {
        String email = "test@test.com";
        setupMockSecurityContext(email);
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> loanService.findAll());
    }
}
