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
import ru.com.melt.model.Author;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class AuthorRepositoryTest {

    private static final String NAME1 = "name1";
    private static final String NAME2 = "name2";

    @Autowired
    private AuthorRepository repository;

    @Before
    public void init() {
        repository.deleteAll().block();
    }

    @Test
    public void addNewAuthorTest() {
        // given
        Author author = new Author();
        author.setName(NAME1);

        // when
        Mono<Author> authorMono = repository.save(author);
        Flux<Author> authorFlux = repository.findAll();

        // then
        assertThat(authorMono.block()).isNotNull();
        StepVerifier.create(authorFlux)
                .assertNext(a -> {
                    assertThat(a.getId()).isNotNull();
                    assertThat(a.getName()).isEqualTo(NAME1);
                })
                .thenAwait(Duration.ofSeconds(2))
                .expectComplete()
                .verify();
        assertEquals(authorFlux.collectList().block().toString(), "[Author(name=name1)]");
    }

    @Test
    public void whenGetAllAuthors_thenReturnList() {
        // given
        Author author1 = addAuthor(NAME1).block();
        Author author2 = addAuthor(NAME2).block();

        // when
        Flux<Author> authorFlux = repository.findAll();

        // then
        StepVerifier.create(authorFlux)
                .expectNext(author1, author2)
                .thenAwait(Duration.ofSeconds(2))
                .verifyComplete();
        assertEquals(authorFlux.collectList().block().toString(),
                "[Author(name=name1), Author(name=name2)]");
    }

    @Test
    public void whenGetAuthorsByNameTest() {
        // given
        Mono<Author> authorMono = addAuthor(NAME1);

        // when
        Mono<Author> author = repository.findAuthorByName(NAME1);

        // then
        assertNotNull(authorMono.block());
        assertNotNull(author.block());
        assertEquals(NAME1, author.block().getName());
    }

    @Test
    public void whenDeleteAuthor_thenSuccess() {
        // given
        Author author1 = addAuthor(NAME1).block();
        Author author2 = addAuthor(NAME2).block();

        // when
        Mono<Long> longMono = repository.deleteAuthorById(author1.getId());
        Flux<Author> authorFlux = repository.findAll();
        // then
        assertTrue(longMono.block() > 0);
        StepVerifier.create(authorFlux)
                .thenAwait(Duration.ofSeconds(2))
                .assertNext(author -> {
                    assertEquals(NAME2, author.getName());
                })
                .expectComplete()
                .verify();
        assertEquals(authorFlux.collectList().block().toString(),
                "[Author(name=name2)]");
    }

    @Test
    public void whenDeleteAll_thenSuccess() {
        // given
        Author author1 = addAuthor(NAME1).block();
        Author author2 = addAuthor(NAME2).block();

        // when
        Flux<Author> authorFlux = repository.findAll();
        StepVerifier.create(authorFlux)
                .expectNext(author1, author2)
                .verifyComplete();

        repository.deleteAll().block();
        List<Author> authors = repository.findAll().collectList().block();

        // then
        assertEquals("[]", authors.toString());
        assertThat(authors).isEmpty();

    }

    private Mono<Author> addAuthor(String name) {
        Author author = new Author();
        author.setName(name);
        return repository.save(author);
    }
}