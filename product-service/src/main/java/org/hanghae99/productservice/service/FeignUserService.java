package org.hanghae99.productservice.service;

import org.hanghae99.productservice.config.FeignUserServiceFallbackFactory;
import org.hanghae99.productservice.entity.User;
import org.hanghae99.productservice.entity.WishList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Queue;

@FeignClient(name = "user-service", fallbackFactory = FeignUserServiceFallbackFactory.class)
public interface FeignUserService {
    @RequestMapping(path = "/api/user/adapt/wishLists")
    Queue<User> adaptGetUserQueue(List<WishList> wishLists);
}
