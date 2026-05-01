package com.devsenior.jmorera.bibliokeep.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.devsenior.jmorera.bibliokeep.mapper.BookMapper;
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookDto;
import com.devsenior.jmorera.bibliokeep.model.dto.books.CreateBookRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.books.UpdateBookStatusRequest;
import com.devsenior.jmorera.bibliokeep.model.entity.Book;
import com.devsenior.jmorera.bibliokeep.model.entity.User;
import com.devsenior.jmorera.bibliokeep.model.enums.BookStatus;
import com.devsenior.jmorera.bibliokeep.repository.BookRepository;
import com.devsenior.jmorera.bibliokeep.repository.UserRepository;

public class BookServiceImplTest {

        private BookRepository bookRepositoryMock;
        private UserRepository userRepositoryMock;
        private BookMapper bookMapperMock;
        private BookServiceImpl bookService;

        private Authentication authenticationMock;
        private SecurityContext securityContextMock;

        @BeforeEach
        void init() {
                bookRepositoryMock = mock(BookRepository.class);
                userRepositoryMock = mock(UserRepository.class);
                bookMapperMock = mock(BookMapper.class);
                bookService = new BookServiceImpl(bookRepositoryMock, userRepositoryMock, bookMapperMock);

                authenticationMock = mock(Authentication.class);
                securityContextMock = mock(SecurityContext.class);
                SecurityContextHolder.setContext(securityContextMock);
        }

        private void setupMockSecurityContext(String email) {
                // "Define el comportamiento del contenedor de seguridad".
                // "Cuando el sistema pregunte: '¿Hay alguien identificado?', responde con este
                // objeto de autenticación"
                when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);

                // "Cuando el sistema pregunte: '¿Está identificado?', responde con verdadero"
                when(authenticationMock.isAuthenticated()).thenReturn(true);

                // "Cuando el sistema pregunte: '¿Quién es el usuario?', responde con este
                // email"
                when(authenticationMock.getPrincipal()).thenReturn(email);
        }

        // BDD Style : should<R>_When<c>()
        @Test
        void shouldPersistBook_whenCorrectData() {
                // Given
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockUser = new User();
                mockUser.setId(ownerId);
                // "Cuando el sistema intente encontrar el usuario por email, devuelve este
                // usuario"
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockUser));

                var mockBook = new Book();
                // "Cuando el sistema intente transformar el request en una entidad, devuelve
                // esta entidad"
                when(bookMapperMock.toEntity(any(CreateBookRequest.class))).thenReturn(mockBook);

                // "Cuando el sistema intente guardar la entidad, devuelve la misma entidad"
                when(bookRepositoryMock.save(any(Book.class))).thenReturn(mockBook);

                var mockBookDto = mockedBookDto();
                // "Cuando el sistema intente transformar la entidad en un DTO, devuelve este
                // DTO"
                when(bookMapperMock.toDto(any(Book.class)))
                                .thenReturn(mockBookDto);

                var request = mockedCreateBookRequest();

                // When
                var result = bookService.createBook(request);

                // Then
                assertNotNull(result);
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenOwnerIdNotExist() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);
                // "Cuando el sistema intente encontrar el usuario por email, devuelve un
                // usuario vacio"
                when(userRepositoryMock.findByEmail(email))
                                .thenReturn(Optional.empty());

                var request = mockedCreateBookRequest();

                // Act & Assert
                // "Cuando el sistema intente crear el libro, lanza una excepcion"
                assertThrows(IllegalArgumentException.class,
                                () -> bookService.createBook(request));
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenOwnerAlreadyHasTheBook() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockedUser = new User();
                mockedUser.setId(ownerId);
                // "Cuando el sistema intente encontrar el usuario por email, devuelve este
                // usuario"
                when(userRepositoryMock.findByEmail(email))
                                .thenReturn(Optional.of(mockedUser));

                var mockedBook = new Book();
                // "Cuando el sistema intente encontrar el libro por ownerId y isbn, devuelve
                // este libro"
                when(bookRepositoryMock.findByOwnerIdAndIsbn(eq(ownerId), anyString()))
                                .thenReturn(Optional.of(mockedBook));

                var request = mockedCreateBookRequest();

                // Act & Assert
                assertThrows(IllegalArgumentException.class,
                                () -> bookService.createBook(request));
        }

        @Test
        void shouldUpdateBookStatus_WhenCorrectData() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockedOwner = new User();
                mockedOwner.setId(ownerId);
                // "Cuando el sistema intente encontrar el usuario por email, devuelve este
                // usuario"
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));

                var mockedId = 1L;

                var mockedBook = new Book();
                mockedBook.setOwnerId(ownerId);
                // "Cuando el sistema intente encontrar el libro por id y ownerId, devuelve
                // este libro"
                when(bookRepositoryMock.findByIdAndOwnerId(mockedId, ownerId))
                                .thenReturn(Optional.of(mockedBook));
                // "Cuando el sistema intente guardar la entidad, devuelve la misma entidad"
                when(bookRepositoryMock.save(any(Book.class)))
                                .thenReturn(mockedBook);

                var response = new BookDto(1L, "123456789", "Test Book",
                                List.of("Test Author"), "Test Description",
                                "http://test.com/test.png", BookStatus.LEIDO,
                                1, false);
                // "Cuando el sistema intente transformar la entidad en un DTO, devuelve este
                // DTO"
                when(bookMapperMock.toDto(any(Book.class)))
                                .thenReturn(response);

                // Act
                var request = new UpdateBookStatusRequest(BookStatus.LEIDO);
                var result = bookService.updateBookStatus(mockedId, request);

                // Assert
                assertNotNull(result);
                assertEquals(BookStatus.LEIDO, result.status());
        }

        private CreateBookRequest mockedCreateBookRequest() {
                return new CreateBookRequest("123456789", "Test Book",
                                List.of("Test Author"), "Test Description",
                                "http://test.com/test.png", BookStatus.DESEADO, 1);
        }

        private BookDto mockedBookDto() {
                return new BookDto(1L, "123456789", "Test Book",
                                List.of("Test Author"), "Test Description",
                                "http://test.com/test.png", BookStatus.DESEADO,
                                1, false);
        }

        private UUID mockedOwnerId() {
                return UUID.fromString("11111111-2222-3333-4444-555555555555");
        }
}
