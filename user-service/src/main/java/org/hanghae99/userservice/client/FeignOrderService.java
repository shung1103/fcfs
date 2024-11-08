package org.hanghae99.userservice.client;

import org.hanghae99.userservice.dto.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "gateway-service")
public interface FeignOrderService {
    @GetMapping(path = "/api/order/adapt/{userId}/orders")
    List<OrderResponseDto> adaptGetOrders(@PathVariable("userId") Long userId);
}
