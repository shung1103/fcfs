package org.hanghae99.orderservice.repository;

import org.hanghae99.orderservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    boolean existsByWishUserIdAndWishProuctId(Long userId, Long productId);

    WishList findByWishUserIdAndWishProductId(Long userId, Long productId);

    List<WishList> findAllByWishUserId(Long userId);
}
