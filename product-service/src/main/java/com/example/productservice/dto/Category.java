package com.example.productservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Long p_category_id;
    private String name;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

}
