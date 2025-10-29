package com.library;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.controller.BookControllerV1;
import com.library.entity.Book;
import com.library.service.BookService;

@WebMvcTest(BookControllerV1.class)
public class LibraryTestsController {

    @MockitoBean
    BookService bookService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testGetAllBooks() throws Exception{

        List<Book> books = List.of(new Book("9781234567890", "The Great Gatsby", "F. Scott Fitzgerald", 1925, null, 5));
        when(bookService.getBooks()).thenReturn(books);

    mockMvc.perform(get("/api/v1/books"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].isbn").value("9781234567890"))
        .andExpect(jsonPath("$[0].title").value("The Great Gatsby"));

        Assertions.assertThat(bookService.getBooks().size()).isEqualTo(1);

    }

    @Test
    public void testCreateBooks() throws Exception{
        
        Book book = new Book("9781234567890", "The Great Gatsby", "F. Scott Fitzgerald", 1925, null, 5);
        when(bookService.createBook(any(Book.class))).thenReturn(book);

    mockMvc.perform(post("/api/v1/books")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(book))
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.isbn").value("9781234567890"));

        Assertions.assertThat(bookService.createBook(book).getIsbn()).isEqualTo("9781234567890");
    }


}
