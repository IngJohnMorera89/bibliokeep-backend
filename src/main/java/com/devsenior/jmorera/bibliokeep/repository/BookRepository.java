package com.devsenior.jmorera.bibliokeep.repository;

import com.devsenior.jmorera.bibliokeep.model.entity.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

	Optional<Book> findByOwnerIdAndIsbn(UUID ownerId, String isbn);

	Optional<Book> findByIdAndOwnerId(Long id, UUID ownerId);

	List<Book> findAllByOwnerId(UUID ownerId);

	@Query("""
		select b
		from Book b
		where b.ownerId = :ownerId
		and (
			lower(b.title) like lower(concat('%', :q, '%'))
			or lower(b.isbn) like lower(concat('%', :q, '%'))
		)
	""")
	List<Book> searchLocalByOwnerAndTitleOrIsbn(@Param("ownerId") UUID ownerId, @Param("q") String q);
}

