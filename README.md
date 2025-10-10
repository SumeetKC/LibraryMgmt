# LibraryBookCatalog

Small Spring Boot application demonstrating a simple library book catalog using Spring Data JPA and an H2 in-memory database.

## What this project contains
- Spring Boot application in `com.library.LibraryBookCatalogApplication`
- REST controllers:
  - `BookController` — CRUD and query operations for `Book` resources (see endpoints below)
  - `HelloController` — simple `/` health/hello endpoint
- JPA entity: `Book` (ISBN as primary key)
- Repository: `BookRepository` (several convenience query methods)
- Service: `BookService` — application logic, validation and exception throwing
- Exception handling: `GlobalExceptionHandler` handles validation, not-found and duplicate errors
- SQL seed data: `src/main/resources/data.sql` (seed rows inserted at startup)

## Data model (Book)
- isbn (String) — primary key, 10 or 13 alphanumeric characters
- title (String)
- author (String)
- publicationYear (int)
- genre (Enum: FICTION, NON_FICTION, MYSTERY, SCI_FI, BIOGRAPHY)
- copiesAvailable (int)

Validation rules are present via Jakarta Validation annotations on the entity and patch DTO.

## REST API Endpoints
Base path: `/books`

- POST `/books`
  - Create a new book (JSON body with full Book fields). Returns 201 on success.
- GET `/books`
  - Returns all books.
- GET `/books/{isbn}`
  - Get a single book by ISBN.
- PUT `/books/{isbn}`
  - Full update (replace fields) for the book with the given ISBN.
- PATCH `/books/{isbn}`
  - Partial update using `BookPatch` (fields optional). Missing/empty fields are left unchanged.
- DELETE `/books/{isbn}`
  - Delete a book by ISBN. Returns 204 on success.
- GET `/books/isbn-range?startIsbn={start}&endIsbn={end}`
  - Returns books with ISBN between `start` and `end` (inclusive), sorted ascending by ISBN.
- GET `/books/sorted/{field}/desc`
  - Sorts results by one of `year`, `title`, `author`, `genre`, or `copies` in descending order.
  - Example: `/books/sorted/year/desc`
- GET `/books/top3/newest`
  - Returns the top 3 newest books by publicationYear (descending).
- GET `/books/top10/search?title={keyword}`
  - Returns up to 10 books whose title contains `keyword`, ordered by title ascending.

There is also a root endpoint `/` that returns `Hello, Library!`.

## Error handling
- Validation errors (bad request) return 400 with a JSON map of field->message.
- Duplicate ISBN insertion returns 409 Conflict.
- Not found returns 404 with a simple message.

## How to run
Requirements: Java 17, Maven

From the project root (Windows PowerShell):

```powershell
mvn -f "c:\SUMEET\Personal\Workspaces\IntelliJ_Workspace\LibraryBookCatalog\pom.xml" spring-boot:run
```

Or run tests:

```powershell
mvn -f "c:\SUMEET\Personal\Workspaces\IntelliJ_Workspace\LibraryBookCatalog\pom.xml" test
```

The app uses an in-memory H2 database by default (configured in `application.properties`) and will seed initial data from `src/main/resources/data.sql` on startup.

## Notes about data initialization
- Spring Boot will look for `schema.sql` and `data.sql` in `src/main/resources` to initialize the database. In this project `data.sql` contains INSERTs for several sample books.
- Because Hibernate (JPA) also generates the schema, to ensure `data.sql` runs after Hibernate creates tables we set:
  - `spring.jpa.defer-datasource-initialization=true`
  - `spring.sql.init.mode=always`

These settings ensure seed data is applied when running with the embedded H2 database and during tests.

If you intended to rely on Hibernate's `import.sql` mechanism instead, be aware ordering differences can cause the script to be ignored; using `data.sql` with the properties above gives consistent behavior across Spring Boot versions.

## Next steps / improvements
- Add integration tests that assert the seeded rows are present.
- Add pagination for list endpoints.
- Add DTOs and mapping if you want to decouple persistence model from API schema.

---
Generated README based on project sources. If you'd like I can:
- Run tests and paste the output here
- Add a small integration test that asserts seeded rows exist
- Add API usage examples (curl / Postman collection)
