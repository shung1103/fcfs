package org.hanghae99.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.hanghae99.orderservice.dto.ApiResponseDto;
import org.hanghae99.orderservice.dto.OrderRequestDto;
import org.hanghae99.orderservice.dto.OrderResponseDto;
import org.hanghae99.orderservice.service.OrderService;
import org.hanghae99.userservice.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "상품 결제 및 주문 생성")
    @Transactional
    @PostMapping("/order")
    public ResponseEntity<ApiResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.createOrder(orderRequestDto, userDetails.getUser().getId());
    }

    @Operation(summary = "나의 주문 목록 조회")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.getMyOrders(userDetails.getUser().getId());
    }

    @Operation(summary = "주문 단건 조회")
    @GetMapping("/order/{orderNo}")
    public ResponseEntity<OrderResponseDto> getOneOrder(@PathVariable Long orderNo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.getOneOrder(orderNo, userDetails.getUser().getId());
    }

    @Operation(summary = "주문 취소")
    @Transactional
    @DeleteMapping("/order/{orderNo}")
    public ResponseEntity<ApiResponseDto> cancelOrder(@PathVariable Long orderNo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.cancelOrder(orderNo, userDetails.getUser().getId());
    }

    // 완료된 주문 처리
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // 1시간마다 업데이트
    public void completeOrder() {
        orderService.completeOrder();
    }
}
