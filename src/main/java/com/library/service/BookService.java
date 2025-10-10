package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookPatch;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateIsbnException;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(Book book) {
        if (bookRepository.existsById(book.getIsbn())) {
            throw new DuplicateIsbnException("ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    public List<Book> getBooks(){
        return  bookRepository.findAll();
    }

    public Book getBookByISBN(String isbn){
        return bookRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

    public Book updateBook(String isbn, Book updatedBook) {
        return bookRepository.findById(isbn).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setPublicationYear(updatedBook.getPublicationYear());
            book.setGenre(updatedBook.getGenre());
            book.setCopiesAvailable(updatedBook.getCopiesAvailable());
            return bookRepository.save(book);
        }).orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

    public Book updateBookPartial(String isbn, BookPatch updatedBook) {
        return bookRepository.findById(isbn).map(book -> {
            book.setTitle(updatedBook.getTitle().isEmpty() ? book.getTitle() : updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor().isEmpty() ? book.getAuthor() : updatedBook.getAuthor());
            book.setPublicationYear(updatedBook.getPublicationYear() == null ? book.getPublicationYear() : updatedBook.getPublicationYear());
            book.setGenre(updatedBook.getGenre() == null ? book.getGenre() : updatedBook.getGenre());
            book.setCopiesAvailable(updatedBook.getCopiesAvailable() == null ? book.getCopiesAvailable() : updatedBook.getCopiesAvailable());
            return bookRepository.save(book);
        }).orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

    public void deleteBook(String isbn) {
        if (!bookRepository.existsById(isbn)) {
            throw new BookNotFoundException("Book not found with ISBN: " + isbn);
        }
        bookRepository.deleteById(isbn);
    }

    public List<Book> getBooksByIsbnRange(String startIsbn, String endIsbn) {
        return bookRepository.findByIsbnBetweenOrderByIsbnAsc(startIsbn, endIsbn);
    }

    public List<Book> getAllBooksSortedByDesc(String field) {
        switch(field){
            case "year" -> {
                return bookRepository.findAllByOrderByPublicationYearDesc();
            }
            case "title" -> {
                return bookRepository.findAllByOrderByTitleDesc();
            }
            case "author" -> {
                return bookRepository.findAllByOrderByAuthorDesc();
            }
            case "genre" -> {
                return bookRepository.findAllByOrderByGenreDesc();
            }
            case "copies" -> {
                return bookRepository.findAllByOrderByCopiesAvailableDesc();
            }
            default -> throw new IllegalArgumentException("Invalid sort parameter: " + field);
        }
    }

    public List<Book> getTop3NewBooks() {
       return bookRepository.findTop3ByOrderByPublicationYearDesc();
    }

	public List<Book> getTop10ByTitle(String keyword) {
		return bookRepository.findTop10ByTitleContainingOrderByTitleAsc(keyword);
	}
}
