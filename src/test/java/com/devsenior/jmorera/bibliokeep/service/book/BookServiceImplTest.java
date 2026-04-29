package com.devsenior.jmorera.bibliokeep.service.book;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devsenior.jmorera.bibliokeep.mapper.BookMapper;
import com.devsenior.jmorera.bibliokeep.repository.BookRepository;
import com.devsenior.jmorera.bibliokeep.repository.UserRepository;

public class BookServiceImplTest {

    // BDD Style : should<R>_When<c>()
    @Test
    void shouldPersistBook_whenCorrectData() {
        // Given
        var bookRepositoryMock = Mockito.mock(BookRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var bookMapper = Mockito.mock(BookMapper.class);
        var bookService = new BookServiceImpl(bookRepositoryMock, userRepository, bookMapper);
        // When
        var reqest = k(new CreateBookRequest("Title", "1234567890", 2022, "Author", Genre.FICTION));
        // Then
        assertNotNull(result);
    }

    @Test
    void testFindAll() {

    }

    @Test
    void testFindById() {

    }

    @Test
    void testSearchBooks() {

    }

    @Test
    void testUpdateBookStatus() {

    }
}
