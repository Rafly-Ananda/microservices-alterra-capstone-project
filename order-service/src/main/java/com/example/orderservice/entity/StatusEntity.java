package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long status_id;

    private String order_status;

    private Integer is_paid;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "status")
    private OrderEntity order;

}
