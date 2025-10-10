package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByIsbnBetweenOrderByIsbnAsc(String startIsbn, String endIsbn);

    List<Book> findAllByOrderByPublicationYearDesc();

    List<Book> findAllByOrderByTitleDesc();

    List<Book> findAllByOrderByAuthorDesc();

    List<Book> findAllByOrderByGenreDesc();

    List<Book> findAllByOrderByCopiesAvailableDesc();

	List<Book> findTop3ByOrderByPublicationYearDesc();

    List<Book> findTop10ByTitleContainingOrderByTitleAsc(String keyword);
}
