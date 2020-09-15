package com.github.yshameer.reactive.mysql.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Customer {
    @Id
    private Long id;
    private String customer_name;
    private String customer_type;
    private String customer_status;

    public boolean hasId() {
        return id != null;
    }
}
