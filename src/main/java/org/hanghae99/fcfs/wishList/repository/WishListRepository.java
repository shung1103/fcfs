package org.hanghae99.fcfs.wishList.repository;

import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.wishList.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    WishList findByUser(User user);
}