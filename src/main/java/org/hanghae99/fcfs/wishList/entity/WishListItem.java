package org.hanghae99.fcfs.wishList.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.product.entity.Product;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "wish_list_items")
public class WishListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wish_list_id")
    private WishList wishList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public WishListItem(Integer quantity, WishList wishList, Product product) {
        this.quantity = quantity;
        this.wishList = wishList;
        this.product = product;
    }

    public void updateQuantity(Integer quantity) { this.quantity = quantity; }
}
