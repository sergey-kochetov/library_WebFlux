package ru.com.melt.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.com.melt.model.Book;

@Repository
public interface BookRepository extends ReactiveMongoRepository<Book, String> {

   Flux<Book> findAll();

   Flux<Book> findAllByAuthorsId(String authorId);

   Flux<Book> findAllByAuthorsName(String authorName);

   Flux<Book> findBookByTitle(String title);

   Mono<Book> findBookById(String id);

   Mono<Book> save(Mono<Book> book);

   Mono<Long> deleteBookById(String id);
}
