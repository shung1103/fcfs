package org.hanghae99.fcfs.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.product.dto.ProductRequestDto;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private Long productPrice;

    @Column(name = "product_intro", nullable = false)
    private String productIntro;

    @Column(name = "product_quantity", nullable = false)
    private Long productQuantity;

    public Product(ProductRequestDto productRequestDto) {
        this.productName = productRequestDto.getProductName();
        this.productPrice = productRequestDto.getProductPrice();
        this.productIntro = productRequestDto.getProductIntro();
        this.productQuantity = productRequestDto.getProductQuantity();
    }

    public void update(ProductRequestDto productRequestDto) {
        this.productName = productRequestDto.getProductName();
        this.productPrice = productRequestDto.getProductPrice();
        this.productIntro = productRequestDto.getProductIntro();
        this.productQuantity = productRequestDto.getProductQuantity();
    }
}
