package org.hanghae99.productservice.repository;

import org.hanghae99.orderservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findAllByWishProductTitle(String title);
}
