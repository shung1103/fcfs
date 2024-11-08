package org.hanghae99.productservice.client;

import org.hanghae99.productservice.dto.User;
import org.hanghae99.productservice.dto.WishList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Queue;

@FeignClient(name = "gateway-service", fallbackFactory = FeignOrderServiceFallbackFactory.class)
public interface FeignOrderService {
    @GetMapping("/api/wishList/adapt/wishListList/{productId}")
    List<WishList> adaptGetWishListList(@PathVariable("productId") Long productId);

    @GetMapping("/api/user/adapt/wishLists")
    Queue<User> adaptGetUserQueue(@RequestParam("wishLists") List<Long> wishUserIdList);
}
