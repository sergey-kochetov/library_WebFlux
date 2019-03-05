package ru.com.melt.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.com.melt.model.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class BookRepositoryTest {
    private static final String TITLE1 = "title1";
    private static final String TITLE2 = "title2";
    private static final String TITLE3 = "title3";
    private static final String AUTHOR1 = "author1";
    private static final String AUTHOR2 = "author2";
    private static final String AUTHOR3 = "author3";
    private static final String GENRE1 = "genre1";
    private static final String GENRE2 = "genre2";
    private static final String GENRE3 = "genre3";
    private static final String CUSTOMER1 = "customer1";
    private static final String TEXT1 = "text1";

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GenreRepository genreRepository;

    @Before
    public void init() {
        customerRepository.deleteAll().block();
        commentRepository.deleteAll().block();
        genreRepository.deleteAll().block();
        authorRepository.deleteAll().block();
        bookRepository.deleteAll().block();
    }

    @Test
    public void whenAddBook_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();

        // when
        Flux<Book> bookFlux = bookRepository.findAll();

        // then
        StepVerifier.create(bookFlux)
                .assertNext(b -> {
                    assertNotNull(b.getId());
                    assertEquals(TITLE1, b.getTitle());
                })
                .expectComplete()
                .verify();
        assertEquals("[Book(title=title1, genre=Genre(genreName=genre1), " +
                        "authors=[Author(name=author1)], comments=null)]",
                getActualList(bookFlux));
    }

    @Test
    public void whenFindAllBook_thenReturnEmpty() {
        // given

        // when
        Flux<Book> bookFlux = bookRepository.findAll();

        // then
        StepVerifier.create(bookFlux)
                .verifyComplete();
        assertEquals("[]", getActualList(bookFlux));
    }

    @Test
    public void whenFindAllBook_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Book book2 = addBook(TITLE2, AUTHOR2, GENRE2).block();

        // when
        Flux<Book> bookFlux = bookRepository.findAll();

        // then
        StepVerifier.create(bookFlux)
                .expectNext(book1, book2)
                .expectComplete()
                .verify();
        assertEquals("[Book(title=title1, genre=Genre(genreName=genre1), " +
                        "authors=[Author(name=author1)], comments=null), " +
                        "Book(title=title2, genre=Genre(genreName=genre2), " +
                        "authors=[Author(name=author2)], comments=null)]",
                getActualList(bookFlux));
    }

    @Test
    public void whenFindAllTitles_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Book book2 = addBook(TITLE2, AUTHOR2, GENRE2).block();

        // when
        Flux<String> titlesFlux = bookRepository.findAll().map(Book::getTitle);

        // then
        StepVerifier.create(titlesFlux)
                .expectNext(TITLE1, TITLE2)
                .expectComplete()
                .verify();
        assertEquals("[title1, title2]", titlesFlux.collectList().block().toString());
    }

    @Test
    public void whenFindBookByAuthor_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();

        Author author = book1.getAuthors().iterator().next();

        // when
        Flux<Book> bookFlux = bookRepository.findAllByAuthorsId(author.getId());

        // then
        StepVerifier.create(bookFlux)
                .assertNext(book -> {
                    assertEquals(book, book1);
                })
                .expectComplete()
                .verify();
        assertEquals("[Book(title=title1, genre=Genre(genreName=genre1), " +
                        "authors=[Author(name=author1)], comments=null)]",
                bookFlux.collectList().block().toString());
    }


    @Test
    public void whenFindBookById_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();

        String id = book1.getId();

        // when
        Mono<Book> bookMono = bookRepository.findBookById(id);

        // then
        StepVerifier.create(bookMono)
                .assertNext(book -> assertEquals(book, book1))
                .expectComplete()
                .verify();
    }

    @Test
    public void whenFindBookByTitle_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Book book2 = addBook(TITLE1, AUTHOR2, GENRE2).block();
        Book book3 = addBook(TITLE2, AUTHOR3, GENRE3).block();

        // when
        Flux<Book> bookMono = bookRepository.findBookByTitle(TITLE1);

        // then
        StepVerifier.create(bookMono)
                .expectNext(book1, book2)
                .expectComplete()
                .verify();
        assertEquals("[Book(title=title1, genre=Genre(genreName=genre1), " +
                "authors=[Author(name=author1)], comments=null), " +
                "Book(title=title1, genre=Genre(genreName=genre2), " +
                "authors=[Author(name=author2)], comments=null)]",
                bookMono.collectList().block().toString());
    }

    @Test
    public void whenDeleteBookById_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Book book2 = addBook(TITLE2, AUTHOR2, GENRE2).block();

        Customer customer = customerRepository.save(new Customer(CUSTOMER1)).block();
        BookInfo bookInfo1 = new BookInfo(book1);

        Comment comment = new Comment(customer, bookInfo1, TEXT1);
        commentRepository.save(comment).block();

        // when
        Long block1 = bookRepository.deleteBookById(book1.getId()).block();
        Long block2 = bookRepository.deleteBookById(book2.getId()).block();
        Flux<Book> booksAfter = bookRepository.findAll();

        // then
        assertEquals("1", block1.toString());
        assertEquals("1", block2.toString());
        assertEquals("[]", getActualList(booksAfter));
    }



    private String getActualList(Flux<Book> books) {
        return books.collectList().block().toString();
    }

    private Mono<Customer> addCustomer(String username) {
        return customerRepository.save(new Customer(username));
    }

    private Mono<Genre> addGenre(String testName) {
        Genre genre = new Genre();
        genre.setGenreName(testName);
        return genreRepository.save(genre);
    }

    private Mono<Book> addBook(String title, String authorName, String genreName) {
        Author author = new Author();
        author.setName(authorName);

        author = authorRepository.save(author).block();
        Set<Author> authors = new HashSet<>();
        authors.add(author);

        Genre genre = addGenre(genreName).block();

        Book book = new Book();
        book.setTitle(title);
        book.setAuthors(authors);
        book.setGenre(genre);

        return bookRepository.save(book);
    }

}