package com.github.yshameer.reactive.mysql.controller;

import com.github.yshameer.reactive.mysql.entity.Customer;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ConnectionFactory cf;

    @BeforeEach
    public void setUp() {
        Flux.from(cf.create())
                .flatMap(c ->
                        c.createBatch()
                                .add("DROP TABLE IF EXISTS customer;")
                                .add("CREATE TABLE customer ( id SERIAL PRIMARY KEY, customer_name VARCHAR(100) NOT NULL, customer_type VARCHAR(100) NOT NULL, customer_status VARCHAR(100) NOT NULL);")
                                .add("insert into customer(customer_name,customer_type,customer_status) values ( 'Ryan', 'Subscriber', 'Active' ) ")
                                .add("insert into customer(customer_name,customer_type,customer_status) values ( 'Bob', 'Publisher', 'InActive') ")
                                .execute()
                )
                .log()
                .blockLast();
    }

    @Test
    public void createCustomer() {
        Customer dave = Customer.builder()
                .id(null)
                .customer_name("Dave")
                .customer_type("Subscriber")
                .customer_status("Active")
                .build();

        webTestClient
                .post()
                .uri("/")
                .body(BodyInserters.fromValue(dave))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Customer.class)
                .value((cust) ->
                        assertNotNull(cust.getId())
                );
    }

    @Test
    public void getCustomers() {
        webTestClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(List.class)
                .value((customers) -> {
                    assertEquals(2, customers.size());
                });
    }

    @Test
    public void getCustomer() {
        webTestClient
                .get()
                .uri("/customer?id=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .value((cust) -> {
                    assertEquals(1, cust.getId());
                });
    }

    @Test
    void updateCustomer() {
        Customer bob = Customer.builder()
                .id(2L)
                .customer_name("Bob")
                .customer_type("Subscriber")
                .customer_status("Active")
                .build();

        webTestClient
                .put()
                .uri("/")
                .body(BodyInserters.fromValue(bob))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Customer.class)
                .value((cust) ->
                        assertEquals("Active", cust.getCustomer_status())
                );
    }

    @Test
    void delete() {
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/").queryParam("id",3).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }
}