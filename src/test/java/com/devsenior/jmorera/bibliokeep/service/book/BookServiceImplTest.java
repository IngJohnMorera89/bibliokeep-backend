package com.devsenior.jmorera.bibliokeep.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.security.access.AccessDeniedException;
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
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookPreviewDto;
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookSearchResponse;
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

        @Test
        void shouldThrowIllegalArgumentException_WhenBookNotFound() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockedOwner = new User();
                mockedOwner.setId(ownerId);
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));

                var mockedId = 1L;
                when(bookRepositoryMock.findByIdAndOwnerId(mockedId, ownerId))
                                .thenReturn(Optional.empty());

                var request = new UpdateBookStatusRequest(BookStatus.LEIDO);

                // Act & Assert
                assertThrows(IllegalArgumentException.class,
                                () -> bookService.updateBookStatus(mockedId, request));
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenOwnerNotMatch() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId(); // El ID del usuario logueado
                var mockedOwner = new User();
                mockedOwner.setId(ownerId);
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));

                var mockedId = 1L;

                // SIMULACIÓN CORRECTA:
                // Al buscar por el ID del libro Y el ID del dueño logueado,
                // el repo devuelve vacío porque el libro le pertenece a OTRO.
                when(bookRepositoryMock.findByIdAndOwnerId(mockedId, ownerId))
                                .thenReturn(Optional.empty());

                var request = new UpdateBookStatusRequest(BookStatus.LEIDO);

                // Act & Assert
                // El servicio al ver que el Optional está vacío, debería lanzar la excepción
                assertThrows(IllegalArgumentException.class,
                                () -> bookService.updateBookStatus(mockedId, request));
        }

        @Test
        void shouldReturnBookById_WhenQueryIsEmpty() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockedId = 1L;
                var mockedOwner = new User();
                mockedOwner.setId(ownerId);
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));

                var mockedBook = new Book();
                mockedBook.setOwnerId(ownerId);
                when(bookRepositoryMock.findByIdAndOwnerId(mockedId, ownerId))
                                .thenReturn(Optional.of(mockedBook));

                var response = new BookDto(1L, "123456789", "Test Book",
                                List.of("Test Author"), "Test Description",
                                "http://test.com/test.png", BookStatus.DESEADO,
                                1, false);
                when(bookMapperMock.toDto(any(Book.class)))
                                .thenReturn(response);

                // Act
                var result = bookService.findById(mockedId);

                // Assert
                assertNotNull(result);
                assertEquals(BookStatus.DESEADO, result.status());

        }

        @Test
        void shuldReturnBookFinAll_WhenQueryIsEmpty() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockedOwner = new User();
                mockedOwner.setId(ownerId);
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));

                List<Book> mockedBooks = List.of(new Book());
                when(bookRepositoryMock.findAllByOwnerId(ownerId))
                                .thenReturn(mockedBooks);

                List<BookDto> response = List.of(mockedBookDto());
                when(bookMapperMock.toDto(any(Book.class)))
                                .thenReturn(response.get(0));

                // Act
                var result = bookService.findAll();

                // Assert
                assertNotNull(result);
                assertEquals(response, result);

        }

        @Test
        void shouldSearchBooks_WhenQueryIsNotEmpty() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockedOwner = new User();
                mockedOwner.setId(ownerId);
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));

                // Usamos una query con espacios para probar el .trim() del servicio
                String query = "  java  ";
                String expectedTrimmedQuery = "java";

                Book mockedBook = new Book();
                List<Book> mockedBooks = List.of(mockedBook);

                // El mock debe esperar la query YA limpia (trimmed)
                when(bookRepositoryMock.searchLocalByOwnerAndTitleOrIsbn(ownerId, expectedTrimmedQuery))
                                .thenReturn(mockedBooks);

                var previewDto = createMockedPreviewDto();
                when(bookMapperMock.toPreviewDto(any(Book.class))).thenReturn(previewDto);

                // Act
                BookSearchResponse result = bookService.searchBooks(query);

                // Assert
                assertNotNull(result);
                assertNotNull(result.results()); // Accedemos a la lista dentro del record/clase
                assertEquals(1, result.results().size());

                // Verificamos que se usó el método correcto de búsqueda
                verify(bookRepositoryMock).searchLocalByOwnerAndTitleOrIsbn(ownerId, expectedTrimmedQuery);
                verify(bookRepositoryMock, never()).findAllByOwnerId(any());
        }

        @Test
        void shouldThrowIllegalArgumentException_WhenUpdateFailsOnSave() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                var ownerId = mockedOwnerId();
                var mockedOwner = new User();
                mockedOwner.setId(ownerId);
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));

                var mockedId = 1L;
                var mockedBook = new Book();
                mockedBook.setOwnerId(ownerId);
                when(bookRepositoryMock.findByIdAndOwnerId(mockedId, ownerId))
                                .thenReturn(Optional.of(mockedBook));

                // SIMULACIÓN DE FALLO:
                // El repositorio lanza una excepción al intentar guardar el cambio
                when(bookRepositoryMock.save(any(Book.class)))
                                .thenThrow(new RuntimeException("Error de base de datos"));

                var request = new UpdateBookStatusRequest(BookStatus.LEIDO);

                // Act & Assert
                // La excepción del repositorio debe propagarse a través del servicio
                assertThrows(RuntimeException.class,
                                () -> bookService.updateBookStatus(mockedId, request));

                // Verificamos que save fue llamado, lo que valida que el flujo llegó hasta
                // ahí
                verify(bookRepositoryMock).save(any(Book.class));
        }

        @Test
        void shouldThrowRuntimeException_WhenMapperFails() {
                // --- ARRANGE ---
                String email = "test@test.com";
                setupMockSecurityContext(email);

                UUID ownerId = mockedOwnerId();
                User mockedOwner = new User();
                mockedOwner.setId(ownerId);

                // 1. Mock de usuario
                when(userRepositoryMock.findByEmail(email))
                                .thenReturn(Optional.of(mockedOwner));

                Long bookId = 1L;
                Book mockedBook = new Book();
                mockedBook.setOwnerId(ownerId);

                // 2. Mock del repositorio: el libro DEBE existir para llegar al mapper
                when(bookRepositoryMock.findByIdAndOwnerId(bookId, ownerId))
                                .thenReturn(Optional.of(mockedBook));

                // 3. Mock del save: fundamental para que el flujo no devuelva null antes de
                // tiempo
                when(bookRepositoryMock.save(any(Book.class)))
                                .thenReturn(mockedBook);

                // 4. Simulamos el fallo en el Mapper (asegúrate de usar toDto o toPreviewDto
                // según tu código)
                when(bookMapperMock.toDto(any(Book.class)))
                                .thenThrow(new RuntimeException("Error de mapeo"));

                UpdateBookStatusRequest request = new UpdateBookStatusRequest(BookStatus.LEIDO);

                // --- ACT & ASSERT ---
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                        bookService.updateBookStatus(bookId, request);
                });

                // Verificaciones finales
                assertTrue(exception.getMessage().contains("Error de mapeo"));
                verify(bookRepositoryMock).save(any(Book.class)); // Confirmamos que intentó guardar
                verify(bookMapperMock).toDto(any(Book.class)); // Confirmamos que llegó al mapper
        }

        @Test
        void shouldThrowAccessDeniedException_WhenAuthenticationIsInvalid() {
                // Arrange: Configuramos el SecurityContext para que devuelva una auth no
                // autenticada
                when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
                when(authenticationMock.isAuthenticated()).thenReturn(false);

                // Act & Assert
                assertThrows(AccessDeniedException.class, () -> bookService.searchBooks("test"));
        }

        @Test
        void shouldThrowAccessDeniedException_WhenNoUserFound() {
                // Arrange
                String email = "test@test.com";
                setupMockSecurityContext(email);

                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> bookService.searchBooks("test"));
        }

        @Test
        void shouldResolveEmail_WhenPrincipalIsUserDetails() {
                // Arrange
                String email = "userDetails@test.com";
                setupMockSecurityContext(""); // Limpiamos el setup estándar

                // Creamos un mock de UserDetails (clase de Spring Security)
                var userDetailsMock = mock(org.springframework.security.core.userdetails.UserDetails.class);
                when(userDetailsMock.getUsername()).thenReturn(email);

                when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
                when(authenticationMock.isAuthenticated()).thenReturn(true);
                // Hacemos que el principal sea el objeto UserDetails
                when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);

                UUID ownerId = mockedOwnerId();
                User mockedOwner = new User();
                mockedOwner.setId(ownerId);
                when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(mockedOwner));
                when(bookRepositoryMock.findAllByOwnerId(ownerId)).thenReturn(List.of());

                // Act
                var result = bookService.searchBooks(null);

                // Assert
                assertNotNull(result);
                verify(userDetailsMock).getUsername(); // Verifica que se entró a esa rama del IF
        }

        @Test
        void shouldThrowAccessDeniedException_WhenPrincipalIsUnknownType() {
                // Arrange
                setupMockSecurityContext("");
                when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
                when(authenticationMock.isAuthenticated()).thenReturn(true);

                // El principal es un objeto que el código no espera (un Integer por ejemplo)
                when(authenticationMock.getPrincipal()).thenReturn(12345);

                // Act & Assert
                AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                                () -> bookService.searchBooks("test"));

                assertEquals("Unable to resolve authenticated user.", ex.getMessage());
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

        private BookPreviewDto createMockedPreviewDto() {
                return new BookPreviewDto(
                                1L, "123", "Test", List.of("A"), "Desc", "url", null, 2024, false);
        }

        private UUID mockedOwnerId() {
                return UUID.fromString("11111111-2222-3333-4444-555555555555");
        }
}
