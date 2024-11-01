package org.hanghae99.productservice.service;

import org.hanghae99.productservice.config.FeignOrderServiceFallbackFactory;
import org.hanghae99.productservice.dto.User;
import org.hanghae99.productservice.dto.WishList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Queue;

@FeignClient(name = "gateway-service", fallbackFactory = FeignOrderServiceFallbackFactory.class)
public interface FeignOrderService {
    @RequestMapping(path = "/api/wishList/adapt/wishListList/{productId}")
    List<WishList> adaptGetWishListList(@PathVariable("productId") Long productId);

    @RequestMapping(path = "/api/user/adapt/wishLists")
    Queue<User> adaptGetUserQueue(List<WishList> wishLists);
}
