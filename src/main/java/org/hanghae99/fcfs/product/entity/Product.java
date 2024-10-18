package org.hanghae99.fcfs.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.likemark.entity.LikeMark;
import org.hanghae99.fcfs.product.dto.ProductRequestDto;

import java.util.List;

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
    private Long stock;

    @OneToMany(mappedBy = "product")
    private List<LikeMark> likemarkList;

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

    public void reStock(Long reStockQuantity) {
        this.stock += reStockQuantity;
    }
}
