package com.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.controller.BookController;
import com.library.entity.Book;
import com.library.entity.BookPatch;
import com.library.entity.Genre;
import com.library.exception.DuplicateIsbnException;
import com.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTests {

    private Book sampleBook;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        sampleBook = new Book();
        sampleBook.setIsbn("9781234567890");
        sampleBook.setTitle("The Great Gatsby");
        sampleBook.setAuthor("F. Scott Fitzgerald");
        sampleBook.setPublicationYear(1925);
        sampleBook.setGenre(Genre.FICTION);
        sampleBook.setCopiesAvailable(5);
    }

    @Test
    void createBook_ValidInput() throws Exception {
        Mockito.when(bookService.createBook(any(Book.class))).thenReturn(sampleBook);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("9781234567890"))
                .andExpect(jsonPath("$.title").value("The Great Gatsby"));
    }

    @Test
    void createBook_InvalidIsbn_ReturnsBadRequest() throws Exception {
        sampleBook.setIsbn("12345"); // Too short
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_DuplicateIsbn_ReturnsConflict() throws Exception {
        Mockito.when(bookService.createBook(any(Book.class)))
                .thenThrow(new DuplicateIsbnException("ISBN already exists"));
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBook)))
                .andExpect(status().isConflict())
                .andExpect(content().string("ISBN already exists"));
    }

    @Test
    void getAllBooks_ReturnsListOfBooks() throws Exception {
        Mockito.when(bookService.getBooks()).thenReturn(Collections.singletonList(sampleBook));
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value(sampleBook.getIsbn()));
    }

    @Test
    void getBookByISBN_Found_ReturnsBook() throws Exception {
        Mockito.when(bookService.getBookByISBN(sampleBook.getIsbn())).thenReturn(sampleBook);
        mockMvc.perform(get("/books/{isbn}", sampleBook.getIsbn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(sampleBook.getIsbn()));
    }

    @Test
    void getBookByISBN_NotFound_ReturnsNotFound() throws Exception {
        Mockito.when(bookService.getBookByISBN("notfound")).thenThrow(new com.library.exception.BookNotFoundException("Book not found"));
        mockMvc.perform(get("/books/{isbn}", "notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_ValidInput_ReturnsUpdatedBook() throws Exception {
        Mockito.when(bookService.updateBook(Mockito.eq(sampleBook.getIsbn()), any(Book.class))).thenReturn(sampleBook);
        mockMvc.perform(put("/books/{isbn}", sampleBook.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(sampleBook.getIsbn()));
    }

    @Test
    void updateBook_NotFound_ReturnsNotFound() throws Exception {
        Mockito.when(bookService.updateBook(Mockito.eq("notfound"), any(Book.class))).thenThrow(new com.library.exception.BookNotFoundException("Book not found"));
        mockMvc.perform(put("/books/{isbn}", "notfound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookPartial_ValidInput_ReturnsUpdatedBook() throws Exception {
        BookPatch patch = new BookPatch();
        patch.setTitle("Updated Title");
        patch.setAuthor("");
        patch.setPublicationYear(null);
        patch.setGenre(null);
        patch.setCopiesAvailable(null);
        Mockito.when(bookService.updateBookPartial(Mockito.eq(sampleBook.getIsbn()), any(BookPatch.class))).thenReturn(sampleBook);
        mockMvc.perform(patch("/books/{isbn}", sampleBook.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(sampleBook.getIsbn()));
    }

    @Test
    void deleteBook_ValidInput_ReturnsNoContent() throws Exception {
        Mockito.doNothing().when(bookService).deleteBook(sampleBook.getIsbn());
        mockMvc.perform(delete("/books/{isbn}", sampleBook.getIsbn()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_NotFound_ReturnsNotFound() throws Exception {
        Mockito.doThrow(new com.library.exception.BookNotFoundException("Book not found")).when(bookService).deleteBook("notfound");
        mockMvc.perform(delete("/books/{isbn}", "notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooksByIsbnRange_ReturnsSortedBooksAsc() throws Exception {
        Book book2 = new Book();
        book2.setIsbn("9781234567891");
        book2.setTitle("Another Book");
        book2.setAuthor("Author");
        book2.setPublicationYear(2000);
        book2.setGenre(Genre.FICTION);
        book2.setCopiesAvailable(2);
        List<Book> books = Arrays.asList(sampleBook, book2);
        Mockito.when(bookService.getBooksByIsbnRange("9781234567890", "9781234567891")).thenReturn(books);
        mockMvc.perform(get("/books/isbn-range")
                        .param("startIsbn", "9781234567890")
                        .param("endIsbn", "9781234567891"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("9781234567890"))
                .andExpect(jsonPath("$[1].isbn").value("9781234567891"));
    }

}
