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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "상품 결제 및 주문 생성")
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto, HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.createOrder(orderRequestDto, userId);
    }

    @Operation(summary = "나의 주문 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.getMyOrders(userId);
    }

    @Operation(summary = "주문 단건 조회")
    @GetMapping("/{orderNo}")
    public ResponseEntity<OrderResponseDto> getOneOrder(@PathVariable Long orderNo, HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return orderService.getOneOrder(orderNo, userId);
    }

    @Operation(summary = "주문 취소")
    @PutMapping("/{orderNo}")
    public ResponseEntity<ApiResponseDto> cancelOrder(@PathVariable Long orderNo) {
        return orderService.cancelOrder(orderNo);
    }

    // 완료된 주문 처리
    @Scheduled(cron = "0 0 * * * *") // 1시간마다 업데이트
    public void completeOrder() {
        orderService.completeOrder();
    }

    @Operation(summary = "Eureka 유저 주문 목록 조회")
    @GetMapping("/adapt/{userId}/orders")
    public List<OrderResponseDto> adaptGetOrders(@PathVariable("userId") Long userId) {
        return orderService.adaptGetOrders(userId);
    }
}
