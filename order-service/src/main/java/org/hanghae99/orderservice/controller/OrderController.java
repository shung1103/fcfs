package org.hanghae99.orderservice.controller;

import com.jh.common.utils.ParseRequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hanghae99.orderservice.dto.ApiResponseDto;
import org.hanghae99.orderservice.dto.OrderRequestDto;
import org.hanghae99.orderservice.dto.OrderResponseDto;
import org.hanghae99.orderservice.service.OrderService;
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
    public ResponseEntity<ApiResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto, HttpServletRequest request) {
        long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.createOrder(orderRequestDto, userId);
    }

    @Operation(summary = "나의 주문 목록 조회")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(HttpServletRequest request) {
        long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.getMyOrders(userId);
    }

    @Operation(summary = "주문 단건 조회")
    @GetMapping("/order/{orderNo}")
    public ResponseEntity<OrderResponseDto> getOneOrder(@PathVariable Long orderNo, HttpServletRequest request) {
        long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.getOneOrder(orderNo, userId);
    }

    @Operation(summary = "주문 취소")
    @Transactional
    @DeleteMapping("/order/{orderNo}")
    public ResponseEntity<ApiResponseDto> cancelOrder(@PathVariable Long orderNo, HttpServletRequest request) {
        long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.cancelOrder(orderNo, userId);
    }

    // 완료된 주문 처리
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // 1시간마다 업데이트
    public void completeOrder() {
        orderService.completeOrder();
    }
}
