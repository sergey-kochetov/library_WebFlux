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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CommentRepositoryTest {
    private static final String USER = "user1";
    private static final String TEXT1 = "text1";
    private static final String TEXT2 = "text2";
    private static final String TITLE1 = "title1";
    private static final String AUTHOR1 = "Author1";
    private static final String GENRE1 = "Genre1";

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Before
    public void init() {
        customerRepository.deleteAll().block();
        commentRepository.deleteAll().block();
        genreRepository.deleteAll().block();
        authorRepository.deleteAll().block();
        bookRepository.deleteAll().block();
    }

    @Test
    public void whenAddComment_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Customer customer = addCustomer(USER).block();
        BookInfo bookInfo = new BookInfo(book1);

        Comment comment = new Comment( customer, bookInfo, TEXT1);

        // when
        Comment saveComment = commentRepository.save(comment).block();

        // then
        Mono<Comment> commentMono = commentRepository.findById(saveComment.getId());
        StepVerifier.create(commentMono)
                .assertNext(c -> assertEquals(c, comment))
                .expectComplete()
                .verify();

    }

    @Test
    public void whenFindCommentsByBookId_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Customer customer = addCustomer(USER).block();
        BookInfo bookInfo = new BookInfo(book1);

        Comment comment1 = new Comment(customer, bookInfo, TEXT1);
        Comment comment2 = new Comment(customer, bookInfo, TEXT2);
        commentRepository.save(comment1).block();
        commentRepository.save(comment2).block();

        // when
        Flux<String> comments = commentRepository
                .findCommentsByBook_Id(book1.getId())
                .map(Comment::getCommentText);
        // then
        StepVerifier.create(comments)
                .expectNext(TEXT1, TEXT2)
                .expectComplete()
                .verify();
        assertEquals("[text1, text2]", getActualList(comments));
    }

    @Test
    public void whenDeleteCommentById_thenOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Customer customer = addCustomer(USER).block();
        BookInfo bookInfo = new BookInfo(book1);

        Comment comment1 = new Comment(customer, bookInfo, TEXT1);
        commentRepository.save(comment1).block();

        // when
        Mono<Long> longMono = commentRepository.deleteCommentByBook_Id(bookInfo.getId());
        // then
        assertEquals("1", longMono.block().toString());
    }

    @Test
    public void whenDeleteCommentById_thenNoOk() {
        // given
        Book book1 = addBook(TITLE1, AUTHOR1, GENRE1).block();
        Customer customer = addCustomer(USER).block();
        BookInfo bookInfo = new BookInfo(book1);

        Comment comment1 = new Comment(customer, bookInfo, TEXT1);
        commentRepository.save(comment1).block();

        // when
        Mono<Long> longMono = commentRepository.deleteCommentByBook_Id("no_id");
        // then
        assertEquals("0", longMono.block().toString());
    }

    private String getActualList(Flux<String> comments) {
        return comments.collectList().block().toString();
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