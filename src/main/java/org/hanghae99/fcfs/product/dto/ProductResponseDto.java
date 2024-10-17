package org.hanghae99.fcfs.product.dto;

import lombok.Getter;
import org.hanghae99.fcfs.product.entity.Product;

@Getter
public class ProductResponseDto {
    private Long id;
    private String productName;
    private Long productPrice;
    private String productIntro;
    private Long productQuantity;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.productPrice = product.getProductPrice();
        this.productIntro = product.getProductIntro();
        this.productQuantity = product.getProductQuantity();
    }
}
