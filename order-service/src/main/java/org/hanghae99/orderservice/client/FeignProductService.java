package org.hanghae99.orderservice.client;

import org.hanghae99.orderservice.exception.FeignProductServiceFallbackFactory;
import org.hanghae99.orderservice.dto.OrderResponseDto;
import org.hanghae99.orderservice.entity.Order;
import org.hanghae99.orderservice.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "gateway-service", fallbackFactory = FeignProductServiceFallbackFactory.class)
public interface FeignProductService {
    @RequestMapping(path = "/api/product/adapt/{productId}")
    Product getProduct(@PathVariable("productId") Long productId);

    @RequestMapping(path = "/api/product/adapt/{productId}/re-stock/{quantity}")
    void reStockProduct(@PathVariable("productId") Long productId, @PathVariable("quantity") Integer quantity);

    @RequestMapping(path = "/api/product/adapt/{userId}/dtoList")
    List<OrderResponseDto> adaptGetDtoList(@PathVariable("userId") Long userId, List<Order> orderList);
}