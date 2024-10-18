package org.hanghae99.fcfs.wishList.repository;

import org.hanghae99.fcfs.wishList.entity.WishList;
import org.hanghae99.fcfs.wishList.entity.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {
    List<WishListItem> findAllByWishList(WishList wishList);

    boolean existsByWishListIdAndProductId(Long id, Long id1);

    WishListItem findByWishListIdAndProductId(Long id, Long id1);

    Optional<WishListItem> findByIdAndWishListId(Long wishListItemNo, Long id);
}
