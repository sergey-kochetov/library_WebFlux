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
import ru.com.melt.model.Customer;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CustomerRepositoryTest {
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";

    @Autowired CustomerRepository customerRepository;

    @Before
    public void init() {
        customerRepository.deleteAll().block();
    }

    @Test
    public void whenAddCustomer_thenOk() {
        // given
        Customer customer1 = addCustomer(USER1).block();

        // when
        Flux<Customer> customerFlux = customerRepository.findAll();

        // given
        StepVerifier
                .create(customerFlux)
                .assertNext(customer -> {
                    assertNotNull(customer.getId());
                    assertEquals(USER1, customer.getUsername());
                })
                .expectComplete()
                .verify();
        assertEquals("[Customer(username=user1, comments=[])]", getActualList(customerFlux));
    }

    @Test
    public void whenFindCustomerByUsername_thenOk() {
        // given
        Customer customer1 = addCustomer(USER1).block();

        // when
        Customer actual = customerRepository.findCustomerByUsername(USER1).block();

        // given
        assertEquals(customer1, actual);
        assertEquals("Customer(username=user1, comments=[])", actual.toString());
    }


    private String getActualList(Flux<Customer> customerFlux) {
        return customerFlux.collectList().block().toString();
    }

    private Mono<Customer> addCustomer(String name) {
        Customer user = new Customer(name);
        return customerRepository.save(user);
    }

}