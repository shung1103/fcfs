package org.hanghae99.orderservice.dto;

import lombok.Getter;

@Getter
public class Product {
    private Long id;
    private String category;
    private String title;
    private Long price;
    private String intro;
    private Integer stock;
}
