package com.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookPatch {

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 50, message = "Author cannot exceed 50 characters")
    private String author;

    @Min(value = 1800, message = "Publication year must be at least 1800")
    @Max(value = 2025, message = "Publication year must be at most 2025")
    private Integer publicationYear;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Min(value = 0, message = "Copies available cannot be negative")
    private Integer copiesAvailable;

}
