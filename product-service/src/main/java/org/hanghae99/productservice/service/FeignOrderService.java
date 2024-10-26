package org.hanghae99.productservice.service;

import org.hanghae99.productservice.entity.WishList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "order", url = "http://localhost:8082/")
public interface FeignOrderService {
    @RequestMapping(path = "/api/adapt/wishListList/{productId}")
    List<WishList> adaptGetWishListList(@PathVariable("productId") Long productId);
}
