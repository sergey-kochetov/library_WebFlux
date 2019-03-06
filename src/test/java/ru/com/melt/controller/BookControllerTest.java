package ru.com.melt.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.com.melt.model.Book;
import ru.com.melt.service.LibraryService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private LibraryService service;

    @Test
    public void whenGetAllBooks_thenOk() {
        // given
        when(service.getAllBooks()).thenReturn(Flux.just(new Book()));

        client.get()
                .uri("/all/")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .exchange()
                .expectStatus()
                .isOk();
    }


}