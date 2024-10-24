package org.hanghae99.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.productservice.dto.ProductRequestDto;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "intro", nullable = false)
    private String intro;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    public Product(ProductRequestDto productRequestDto) {
        this.category = productRequestDto.getCategory();
        this.title = productRequestDto.getTitle();
        this.price = productRequestDto.getPrice();
        this.intro = productRequestDto.getIntro();
        this.stock = productRequestDto.getStock();
    }

    public void update(ProductRequestDto productRequestDto) {
        this.category = productRequestDto.getCategory();
        this.title = productRequestDto.getTitle();
        this.price = productRequestDto.getPrice();
        this.intro = productRequestDto.getIntro();
        this.stock = productRequestDto.getStock();
    }

    public void reStock(Integer reStockQuantity) {
        this.stock = reStockQuantity;
    }
}
