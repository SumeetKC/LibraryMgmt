package com.library.controller;

import com.library.entity.Book;
import com.library.entity.BookPatch;
import com.library.service.BookService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v2/books")
public class BookControllerV2 {

    private final BookService bookService;

    @Autowired
    private Validator validator;

    public BookControllerV2(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Create a new book with manual validation. It demonstrates how to handle validation
     * errors without using @Valid annotation.
     * @param book
     * @return
     */
    /* 
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        System.out.println("Validating book: " + book);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        if( !violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Book> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new IllegalArgumentException("Validation failed: " + sb.toString());
        }
        return new ResponseEntity<>(bookService.createBook(book), HttpStatus.CREATED);
    }

    */

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(bookService.createBook(book), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return new ResponseEntity<>(bookService.getBooks(), HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByISBN(@PathVariable String isbn) {
        return new ResponseEntity<>(bookService.getBookByISBN(isbn), HttpStatus.OK);
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @Valid @RequestBody Book book) {
        return new ResponseEntity<>(bookService.updateBook(isbn, book), HttpStatus.OK);
    }

    @PatchMapping("/{isbn}")
    public ResponseEntity<Book> updateBookPartial(@PathVariable String isbn, @Valid @RequestBody BookPatch bookPatch) {
        return new ResponseEntity<>(bookService.updateBookPartial(isbn, bookPatch), HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
            bookService.deleteBook(isbn);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/isbn-range")
    public ResponseEntity<List<Book>> getBooksByIsbnRange(@RequestParam String startIsbn, @RequestParam String endIsbn) {
        List<Book> books = bookService.getBooksByIsbnRange(startIsbn, endIsbn);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/{year}/desc")
    public ResponseEntity<List<Book>> getAllBooksSortedByDesc(@PathVariable String year) {
        return new ResponseEntity<>(bookService.getAllBooksSortedByDesc(year), HttpStatus.OK);
    }

    @GetMapping("/top3/newest")
    public ResponseEntity<List<Book>> getTop3NewBooks() {
        return new ResponseEntity<>(bookService.getTop3NewBooks(), HttpStatus.OK);
    }

    @GetMapping("top10/search")
    public ResponseEntity<List<Book>> getTop10ByTitle(@RequestParam String title) {
        return new ResponseEntity<>(bookService.getTop10ByTitle(title), HttpStatus.OK);
    }
}
