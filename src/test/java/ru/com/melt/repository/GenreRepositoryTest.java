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
import ru.com.melt.model.Genre;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class GenreRepositoryTest {

    private static final String NAME1 = "name1";
    private static final String NAME2 = "name2";

    @Autowired
    private GenreRepository repository;

    @Before
    public void init() {
        repository.deleteAll().block();
    }

    @Test
    public void whenAddGenre_thenOk() {
        // given
        Genre block1 = addGenre(NAME1).block();

        // when
        Flux<Genre> genreFlux = repository.findAll();

        // then
        StepVerifier.create(genreFlux)
                .assertNext(genre -> {
                    assertNotNull(genre);
                    assertEquals(genre.getGenreName(), NAME1);
                })
                .expectComplete()
                .verify();
        assertEquals("[Genre(genreName=name1)]", getActualList(genreFlux));
    }

    @Test
    public void whenAllGenre_thenOk() {
        // given
        Genre genre1 = addGenre(NAME1).block();
        Genre genre2 = addGenre(NAME2).block();

        // when
        Flux<Genre> genreFlux = repository.findAll();

        // then
        StepVerifier.create(genreFlux)
                .expectNext(genre1, genre2)
                .verifyComplete();
        assertEquals("[Genre(genreName=name1), Genre(genreName=name2)]", getActualList(genreFlux));
    }

    @Test
    public void whenFindGenreByGenreName_thenOk() {
        // given
        Genre genre1 = addGenre(NAME1).block();

        // when
        Genre actual = repository.findGenreByGenreName(NAME1).block();

        // then
        assertEquals(genre1, actual);
    }

    @Test
    public void whenDeleteGenre_thenOk() {
        // given
        Genre genre1 = addGenre(NAME1).block();
        Genre genre2 = addGenre(NAME2).block();

        // when
        Flux<Genre> genreFlux = repository.findAll();
        StepVerifier.create(genreFlux)
                .expectNext(genre1, genre2)
                .verifyComplete();
        Mono<Long> longMono = repository.deleteGenreByGenreName(NAME1);
        Long actual = longMono.block();
        Flux<Genre> genres = repository.findAll();

        // then
        assertTrue(actual > 0);
        assertEquals("[Genre(genreName=name2)]", getActualList(genres));
    }

    @Test
    public void whenDeleteAll_thenOk() {
        // given
        Genre genre1 = addGenre(NAME1).block();
        Genre genre2 = addGenre(NAME2).block();

        // when
        repository.deleteAll().block();
        Flux<Genre> genres = repository.findAll();

        // then
        assertEquals("[]", getActualList(genres));
    }

    private String getActualList(Flux<Genre> genreFlux) {
        return genreFlux.collectList().block().toString();
    }

    private Mono<Genre> addGenre(String testName) {
        Genre genre = new Genre();
        genre.setGenreName(testName);
        return repository.save(genre);
    }

}