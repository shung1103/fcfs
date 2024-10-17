package org.hanghae99.fcfs.product.dto;

import lombok.Getter;
import org.hanghae99.fcfs.product.entity.Product;

@Getter
public class ProductResponseDto {
    private Long id;
    private String category;
    private String title;
    private Long price;
    private String intro;
    private Long stock;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.category = product.getCategory();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.intro = product.getIntro();
        this.stock = product.getStock();
    }
}
