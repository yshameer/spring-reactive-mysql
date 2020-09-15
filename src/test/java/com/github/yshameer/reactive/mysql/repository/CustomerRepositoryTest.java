package com.github.yshameer.reactive.mysql.repository;

import com.github.yshameer.reactive.mysql.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customers;
    @Autowired
    DatabaseClient database;

    @BeforeEach
    public void setUp() {

        Hooks.onOperatorDebug();

        List<String> statements = Arrays.asList(
                "DROP TABLE IF EXISTS customer;",
                "CREATE TABLE customer ( id SERIAL PRIMARY KEY, customer_name VARCHAR(100) NOT NULL, customer_type VARCHAR(100) NOT NULL, customer_status VARCHAR(100) NOT NULL);");

        statements.forEach(it -> database.execute(it)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete());
    }

    @Test
    public void executesFindAll() {

        Customer dave = Customer.builder()
                .id(null)
                .customer_name("Dave")
                .customer_type("Subscriber")
                .customer_status("Active")
                .build();
        Customer carter = Customer.builder()
                .id(null)
                .customer_name("Carter")
                .customer_type("Publisher")
                .customer_status("InActive")
                .build();

        insertCustomers(dave, carter);

        customers.findAll()
                .as(StepVerifier::create)
                .assertNext(dave::equals)
                .assertNext(carter::equals)
                .verifyComplete();
    }

    @Test
    public void executesSave() {
        Customer billy = Customer.builder()
                .id(null)
                .customer_name("Billy")
                .customer_type("Subscriber")
                .customer_status("Active")
                .build();
        customers.save(billy)
                .as(StepVerifier::create)
                .expectNextMatches(Customer::hasId)
                .verifyComplete();
    }

    @Test
    public void executesDelete() {
        Customer jimmy = Customer.builder()
                .id(null)
                .customer_name("Jimmy")
                .customer_type("Producer")
                .customer_status("Active")
                .build();

        Mono<Customer> deleted = customers
                .save(jimmy)
                .flatMap(saved -> customers.delete(saved).thenReturn(saved));

        StepVerifier
                .create(deleted)
                .expectNextMatches(customer -> customer.getCustomer_name().equalsIgnoreCase("Jimmy"))
                .verifyComplete();
    }

    @Test
    public void executesUpdate() {
        Customer tony = Customer.builder()
                .id(null)
                .customer_name("Tony")
                .customer_type("Organizer")
                .customer_status("InActive")
                .build();

        Mono<Customer> saved = customers
                .save(tony)
                .flatMap(c -> {
                    c.setCustomer_status("Active");
                    return customers.save(c);
                });

        StepVerifier
                .create(saved)
                .expectNextMatches(c -> c.getCustomer_status().equalsIgnoreCase("Active"))
                .verifyComplete();
    }

    private void insertCustomers(Customer... customers) {

        this.customers.saveAll(Arrays.asList(customers))
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

}