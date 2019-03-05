package ru.com.melt.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.com.melt.model.Customer;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, Long> {

    Mono<Customer> findUserByUsername(String username);
}
