package ru.com.melt.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.com.melt.model.Comment;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {

    Flux<Comment> findAllById(String id);

    Flux<Comment> findCommentsByBook_Id(String id);

    Mono<Long> deleteCommentById(String id);

    Mono<Long> deleteCommentByBook_Id(String id);

    Mono<Comment> save(Mono<Comment> comment);
}
