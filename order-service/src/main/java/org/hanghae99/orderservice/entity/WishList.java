package org.hanghae99.fcfs.wishList.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "wish_lists")
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_list_id")
    private Long id;

    @Column(name = "wish_user_name", nullable = false)
    private String wishUserName;

    @Column(name = "wish_product_title", nullable = false)
    private String wishProductTitle;

    @Column(name = "wish_quantity", nullable = false)
    private Integer wishQuantity;

    public WishList(String wishUserName, String wishProductTitle, Integer wishQuantity) {
        this.wishUserName = wishUserName;
        this.wishProductTitle = wishProductTitle;
        this.wishQuantity = wishQuantity;
    }

    public void updateQuantity(Integer quantity) {
        this.wishQuantity = quantity;
    }
}
