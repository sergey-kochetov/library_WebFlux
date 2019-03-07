package ru.com.melt.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.com.melt.model.Author;
import ru.com.melt.model.Book;
import ru.com.melt.model.Comment;
import ru.com.melt.model.Genre;
import ru.com.melt.repository.AuthorRepository;
import ru.com.melt.repository.BookRepository;
import ru.com.melt.repository.GenreRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LibraryServiceTest {

    private static final String GENRE1 = "genre1";
    private static final String GENRE2 = "genre2";
    private static final String TITLE1 = "title1";
    private static final String TITLE2 = "title2";
    private static final String AUTHOR1 = "author1";

    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private GenreRepository genreRepository;

    @Autowired
    private LibraryService libraryService;

    @Before
    public void init() {
        reset(bookRepository);
        reset(authorRepository);
        reset(genreRepository);
    }

    @Test
    public void whenGetAllBooks_thenOk() {
        // given
        // when
        libraryService.getAllBooks();
        // then
        verify(bookRepository).findAll();
    }

    @Test
    public void whenGetAllGenres_thenOk() {
        // given
        Genre genre1 = new Genre(GENRE1);
        genre1.setId("1");

        Genre genre2 = new Genre(GENRE2);
        genre2.setId("2");

        // when
        Flux<Genre> genreFlux = Flux.just(genre1, genre2);
        when(genreRepository.findAll()).thenReturn(genreFlux);

        Flux<String> allGenres = libraryService.getAllGenres();

        // then
        verify(genreRepository).findAll();
        StepVerifier.create(allGenres)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    /**
     * bookRepository.findAll().filter(book -> {
     * Set<Author> authors = book.getAuthors();
     * if (authors != null) {
     * return authors.stream()
     * .anyMatch(author -> name.equals(author.getName()));
     * }
     * return false;
     * });
     **/
    @Test
    public void whenGetBookByAuthorsName_thenOk() {
        // given
        Book book = new Book(TITLE1, new Genre(GENRE1), null);
        book.addAuthor(new Author(AUTHOR1));

        // when
        Flux<Book> genreFlux = Flux.just(book);
        when(bookRepository.findAll()).thenReturn(genreFlux);

        Flux<Book> allGenres = libraryService.getBookByAuthorsName(AUTHOR1);
        assertEquals("", allGenres.collectList().block().toString());

        // then
    }


}