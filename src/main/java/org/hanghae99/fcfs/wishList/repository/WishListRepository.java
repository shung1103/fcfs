package org.hanghae99.fcfs.wishList.repository;

import org.hanghae99.fcfs.wishList.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    boolean existsByWishUserNameAndWishProductTitle(String username, String title);

    WishList findByWishUserNameAndWishProductTitle(String username, String title);

    List<WishList> findAllByWishUserName(String username);
}
