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
    @Column(name = "order_item_id")
    private Long id;

    @Column(name = "order_item_quantity", nullable = false)
    private Integer orderItemQuantity;

    @Column(name = "mark_complete")
    private Boolean markComplete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem(WishListItem wishListItem, Order order) {
        this.orderItemQuantity = wishListItem.getWishListItemQuantity();
        this.markComplete = false;
        this.order = order;
        this.product = wishListItem.getProduct();
    }
}
