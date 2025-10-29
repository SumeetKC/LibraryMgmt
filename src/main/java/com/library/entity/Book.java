package com.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {

    @Id
    @NotBlank(message = "ISBN cannot be blank")
    @Pattern(regexp = "^(?:[A-Za-z0-9]{10}|[A-Za-z0-9]{13})$", message = "ISBN must be either 10 or 13 characters long, containing only letters and digits.")
    @Column(unique = true)
    private String isbn;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Author cannot be blank")
    @Size(max = 50, message = "Author cannot exceed 50 characters")
    private String author;

    @Min(value = 1800, message = "Publication year must be at least 1800")
    @Max(value = 2025, message = "Publication year must be at most 2025")
    @Column(name = "publication_year")
    private int publicationYear;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Min(value = 0, message = "Copies available cannot be negative")
    @Column(name = "copies")
    private int copiesAvailable;

}
