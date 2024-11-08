package org.hanghae99.orderservice.client;

import org.hanghae99.orderservice.dto.OrderResponseDto;
import org.hanghae99.orderservice.entity.Order;
import org.hanghae99.orderservice.dto.Product;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeignProductServiceFallbackFactory implements FallbackFactory<FeignProductService> {
    @Override
    public FeignProductService create(Throwable cause) {
        return new FeignProductService() {
            @Override
            public Product getProduct(Long productId) {
                return new Product();
            }

            @Override
            public void reStockProduct(Long productId, Integer quantity) {}

            @Override
            public List<OrderResponseDto> adaptGetDtoList(Long userId, List<Order> orderList) {
                return List.of();
            }
        };
    }
}
