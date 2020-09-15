package com.github.yshameer.reactive.mysql.repository;

import com.github.yshameer.reactive.mysql.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
}
