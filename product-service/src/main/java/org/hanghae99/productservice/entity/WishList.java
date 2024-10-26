package org.hanghae99.productservice.entity;

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

    @Column(name = "wish_user_id", nullable = false)
    private Long wishUserId;

    @Column(name = "wish_product_id", nullable = false)
    private Long wishProductId;

    @Column(name = "wish_quantity", nullable = false)
    private Integer wishQuantity;
}
