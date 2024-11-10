package org.hanghae99.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.hanghae99.orderservice.client.FeignProductService;
import org.hanghae99.orderservice.dto.ApiResponseDto;
import org.hanghae99.orderservice.dto.OrderRequestDto;
import org.hanghae99.orderservice.dto.OrderResponseDto;
import org.hanghae99.orderservice.dto.Product;
import org.hanghae99.orderservice.entity.Order;
import org.hanghae99.orderservice.entity.WishList;
import org.hanghae99.orderservice.repository.OrderRepository;
import org.hanghae99.orderservice.repository.WishListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final WishListRepository wishListRepository;
    private final OrderRepository orderRepository;
    private final FeignProductService feignProductService;

    public ResponseEntity<ApiResponseDto> createOrder(OrderRequestDto orderRequestDto, Long userId) {
        Product product = feignProductService.getProduct(orderRequestDto.getProductId());

        if (product.getStock() < orderRequestDto.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponseDto(product.getTitle() + "의 재고가 부족합니다.", HttpStatus.BAD_REQUEST.value()));
        }

        long totalPrice = product.getPrice() * orderRequestDto.getQuantity();
        if (orderRequestDto.getPayment().equals(totalPrice)) {
            Order order = new Order(userId, product.getId(), orderRequestDto);
            feignProductService.reStockProduct(product.getId(), orderRequestDto.getQuantity());
            orderRepository.save(order);
            if (wishListRepository.existsByWishUserIdAndWishProductId(userId, product.getId())) {
                WishList wishList = wishListRepository.findByWishUserIdAndWishProductId(userId, product.getId());
                wishListRepository.delete(wishList);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("주문이 완료 되었습니다.", HttpStatus.CREATED.value()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto("금액을 올바르게 입력해 주세요.", HttpStatus.BAD_REQUEST.value()));
        }
    }

    public ResponseEntity<List<OrderResponseDto>> getMyOrders(Long userId) {
        List<Order> orderList = orderRepository.findAllByOrderUserIdOrderByCreatedAtDesc(userId);
        if (orderList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        } else {
            List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
            for (Order order : orderList) {
                Product product = feignProductService.getProduct(order.getOrderProductId());
                OrderResponseDto orderResponseDto = new OrderResponseDto(userId, product.getTitle(), order);
                orderResponseDtoList.add(orderResponseDto);
            }
            return ResponseEntity.status(HttpStatus.OK).body(orderResponseDtoList);
        }
    }

    public ResponseEntity<OrderResponseDto> getOneOrder(Long orderNo, Long userId) {
        Order order = orderRepository.findById(orderNo).orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));
        Product product = feignProductService.getProduct(order.getOrderProductId());
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto(userId, product.getTitle(), order));
    }

    public ResponseEntity<ApiResponseDto> cancelOrder(Long orderNo) {
        Order order = orderRepository.findById(orderNo).orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));

        switch (order.getOrderStatus()) {
            case "배송 중":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto("배송이 진행 중입니다.", HttpStatus.BAD_REQUEST.value()));
            case "배송 완료":
                if (Duration.between(order.getModifiedAt(), LocalDateTime.now()).toDays() < 2) {
                    order.updateOrderStatus("반품 진행");
                    orderRepository.saveAndFlush(order);
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto("반품을 진행합니다.", HttpStatus.OK.value()));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto("반품 기한이 지났습니다.", HttpStatus.BAD_REQUEST.value()));
                }
            case "주문 완료":
                orderRepository.delete(order);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto("주문이 취소 되었습니다.", HttpStatus.OK.value()));
            default:
                throw new RuntimeException();
        }
    }

    public void completeOrder() {
        List<Order> orderList = orderRepository.findAll();
        for (Order order : orderList) {
            switch (order.getOrderStatus()) {
                case "주문 완료":
                    if (Duration.between(order.getCreatedAt(), LocalDateTime.now()).toDays() == 1) order.updateOrderStatus("배송 중");
                case "배송 중":
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now()).toDays() == 1) order.updateOrderStatus("배송 완료");
                case "반품 진행":
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now()).toDays() == 1) {
                        order.updateOrderStatus("반품 완료");
                        // 반품된 수량만큼 재고 회복
                        Product product = feignProductService.getProduct(order.getOrderProductId());
                        int newStock = product.getStock() + order.getOrderQuantity();
                        feignProductService.reStockProduct(product.getId(), newStock);
                    }
                case "반품 완료":
                    continue;
                default:
                    throw new RuntimeException();
            }
        }
    }

    public List<OrderResponseDto> adaptGetOrders(Long userId) {
        List<Order> orderList = orderRepository.findAllByOrderUserIdOrderByCreatedAtDesc(userId);
        if (orderList.isEmpty()) return new ArrayList<>();

        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order : orderList) {
            Product product = feignProductService.getProduct(order.getOrderProductId());
            OrderResponseDto orderResponseDto = new OrderResponseDto(userId, product.getTitle(), order);
            orderResponseDtoList.add(orderResponseDto);
        }

        return orderResponseDtoList;
    }
}
