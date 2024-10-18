package org.hanghae99.fcfs.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.product.entity.Product;
import org.hanghae99.fcfs.wishList.entity.WishListItem;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    private Boolean scoreComplete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem(WishListItem wishListItem, Order order) {
        this.quantity = wishListItem.getQuantity();
        this.scoreComplete = false;
        this.order = order;
        this.product = wishListItem.getProduct();
    }
}
