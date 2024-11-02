package org.hanghae99.orderservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Product {
    private Long id;
    private String category;
    private String title;
    private Long price;
    private String intro;
    private Integer stock;
}
