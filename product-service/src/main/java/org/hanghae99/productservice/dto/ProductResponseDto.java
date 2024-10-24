package org.hanghae99.productservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.productservice.entity.Product;

@Getter
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String category;
    private String title;
    private Long price;
    private String intro;
    private Integer stock;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.category = product.getCategory();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.intro = product.getIntro();
        this.stock = product.getStock();
    }
}
