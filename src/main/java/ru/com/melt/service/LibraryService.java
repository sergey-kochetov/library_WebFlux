package ru.com.melt.service;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.com.melt.model.Author;
import ru.com.melt.model.Book;
import ru.com.melt.model.Comment;
import ru.com.melt.model.Genre;

public interface LibraryService {

    Flux<Book> getAllBooks();

    Flux<String> getAllAuthorsNames();

    Flux<String> getAllGenres();

    Flux<Book> getBookByAuthorsName(@NonNull String name);

    Flux<String> getAllCommentsTexts(@NonNull String bookId);

    Flux<Comment> getAllComments(@NonNull String bookId);

    Mono<Book> getBookById(@NonNull String id);

    Mono<Book> getBookByTitle(@NonNull String title);

    Mono<Genre> addNewGenre(@NonNull Genre genre);

    Mono<Book> addNewBook(@NonNull Book book);

    Mono<Author> addNewAuthor(@NonNull Author author);

    Mono<Comment> addComment(@NonNull String bookId, @NonNull String username, @NonNull String comment);

    Mono<Book> updateBookTitleById(@NonNull String id, @NonNull String newTitle);

    Mono<Book> updateBook(@NonNull Mono<Book> newBook);

    Mono<Comment> updateComment(@NonNull Mono<Comment> comment);

    Mono<Comment> updateComment(@NonNull String id, @NonNull String commentText);

    Mono<Long> deleteBookById(@NonNull String id);
    Mono<Long> deleteAuthorById(@NonNull String id);
    Mono<Long> deleteGenreByGenreName(@NonNull String genreName);
    Mono<Long> deleteCommeById(@NonNull String id);
}
