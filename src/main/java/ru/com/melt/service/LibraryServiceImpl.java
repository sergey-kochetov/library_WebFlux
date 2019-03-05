package ru.com.melt.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.com.melt.exception.NoBookWithSuchIdLidraryException;
import ru.com.melt.exception.NoCommentWithSuchIdLidraryException;
import ru.com.melt.model.*;
import ru.com.melt.repository.*;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final GenreRepository genreRepository;

    private final CustomerRepository customerRepository;

    @Autowired
    public LibraryServiceImpl(AuthorRepository authorRepository,
                              BookRepository bookRepository,
                              CommentRepository commentRepository,
                              GenreRepository genreRepository,
                              CustomerRepository customerRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
        this.genreRepository = genreRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Flux<String> getAllAuthorsNames() {
        return authorRepository.findAll()
                .map(Author::getName);
    }

    @Override
    public Flux<String> getAllGenres() {
        return genreRepository.findAll()
                .map(Genre::getGenreName);
    }

    @Override
    public Flux<Book> getBookByAuthorsName(@NonNull String name) {
        return bookRepository.findAll().filter(book -> {
            Set<Author> authors = book.getAuthors();
            if (authors != null) {
                return authors.stream()
                        .anyMatch(author -> name.equals(author.getName()));
            }
            return false;
        });
    }

    @Override
    public Flux<String> getAllCommentsTexts(@NonNull String bookId) {
        return commentRepository.findCommentsByBook_Id(bookId)
                .map(Comment::getCommentText);
    }

    @Override
    public Flux<Comment> getAllComments(@NonNull String bookId) {
        return commentRepository.findCommentsByBook_Id(bookId);
    }

    @Override
    public Mono<Book> getBookById(@NonNull String id) {
        return bookRepository.findBookById(id);
    }

    @Override
    public Flux<Book> getBookByTitle(@NonNull String title) {
        return bookRepository.findBookByTitle(title);
    }

    @Override
    public Mono<Genre> addNewGenre(@NonNull Genre genre) {
        return genreRepository
                .findGenreByGenreName(genre.getGenreName())
                .switchIfEmpty(genreRepository.save(genre));
    }

    @Override
    public Mono<Book> addNewBook(@NonNull Book book) {
        Mono<Genre> newGenre = addNewGenre(book.getGenre());

        Mono<Set<Author>> newAuthors = Flux.fromIterable(book.getAuthors())
                .flatMap(this::addNewAuthor)
                .collect(Collectors.toSet());

        return Mono.zip(newAuthors, newGenre, (authors, genre) ->
                new Book(book.getTitle(), genre, authors))
                .flatMap(bookRepository::save);
    }

    @Override
    public Mono<Author> addNewAuthor(@NonNull Author author) {
        return authorRepository.findAuthorByName(author.getName())
                .switchIfEmpty(authorRepository.save(author));
    }

    @Override
    public Mono<Comment> addComment(@NonNull String bookId, @NonNull String username, @NonNull String comment) {
        Mono<BookInfo> bookInfoMono = bookRepository
                .findBookById(bookId)
                .map(BookInfo::new)
                .switchIfEmpty(Mono.error(new NoBookWithSuchIdLidraryException(bookId)));

        Mono<Customer> customerMono = customerRepository
                .findCustomerByUsername(username)
                .switchIfEmpty(customerRepository.save(new Customer(username)));

        return Mono.zip(bookInfoMono, customerMono, (bookInfo, customer) ->
                new Comment(customer, bookInfo, comment))
                .flatMap(commentRepository::save);
    }

    @Override
    public Mono<Book> updateBookTitleById(@NonNull String id, @NonNull String newTitle) {
        return bookRepository.findBookById(id)
                .map(book -> {
                    book.setTitle(newTitle);
                    return book;
                })
                .flatMap(bookRepository::save)
                .switchIfEmpty(Mono.error(new NoBookWithSuchIdLidraryException(id)));
    }

    @Override
    public Mono<Book> updateBook(@NonNull Mono<Book> newBook) {
        return bookRepository.save(newBook);
    }

    @Override
    public Mono<Comment> updateComment(@NonNull Mono<Comment> comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Mono<Comment> updateComment(@NonNull String id, @NonNull String commentText) {
        return commentRepository.findById(id)
                .flatMap(comment -> {
                    comment.setCommentText(commentText);
                    return Mono.just(comment);
                })
                .flatMap(commentRepository::save)
                .switchIfEmpty(Mono.error(new NoCommentWithSuchIdLidraryException(id)));
    }

    @Override
    public Mono<Long> deleteBookById(@NonNull String id) {
        return bookRepository.deleteBookById(id);
    }

    @Override
    public Mono<Long> deleteAuthorById(@NonNull String id) {
        return authorRepository.deleteAuthorById(id);
    }

    @Override
    public Mono<Long> deleteGenreByGenreName(@NonNull String genreName) {
        return genreRepository.deleteGenreByGenreName(genreName);
    }

    @Override
    public Mono<Long> deleteCommeById(@NonNull String id) {
        return commentRepository.deleteCommentById(id);
    }
}
