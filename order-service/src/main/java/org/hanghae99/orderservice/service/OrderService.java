package org.hanghae99.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.hanghae99.orderservice.dto.ApiResponseDto;
import org.hanghae99.orderservice.dto.OrderRequestDto;
import org.hanghae99.orderservice.dto.OrderResponseDto;
import org.hanghae99.orderservice.entity.Order;
import org.hanghae99.orderservice.repository.OrderRepository;
import org.hanghae99.orderservice.repository.ProductRepository;
import org.hanghae99.orderservice.repository.UserRepository;
import org.hanghae99.productservice.entity.Product;
import org.hanghae99.userservice.entity.User;
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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ResponseEntity<ApiResponseDto> createOrder(OrderRequestDto orderRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
        Product product = productRepository.findById(orderRequestDto.getProductId()).orElseThrow(NullPointerException::new);
        if (product.getStock() < orderRequestDto.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponseDto(product.getTitle() + "의 재고가 부족합니다.", HttpStatus.BAD_REQUEST.value()));
        }

        long totalPrice = product.getPrice() * orderRequestDto.getQuantity();
        if (orderRequestDto.getPayment().equals(totalPrice)) {
            Order order = new Order(user.getId(), product.getId(), orderRequestDto);
            product.reStock(product.getStock() - orderRequestDto.getQuantity());
            orderRepository.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("결제가 완료 되었습니다.", HttpStatus.CREATED.value()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto("금액을 올바르게 입력해 주세요.", HttpStatus.BAD_REQUEST.value()));
        }
    }

    public ResponseEntity<List<OrderResponseDto>> getMyOrders(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
        List<Order> orderList = orderRepository.findAllByOrderUserIdOrderByCreatedAtDesc(user.getId());
        if (orderList.isEmpty()) {
            throw new NullPointerException("주문이 없습니다.");
        } else {
            List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
            for (Order order : orderList) {
                Product product = productRepository.findById(order.getOrderProductId()).orElseThrow(NullPointerException::new);
                OrderResponseDto orderResponseDto = new OrderResponseDto(user.getUsername(), product.getTitle(), order);
                orderResponseDtoList.add(orderResponseDto);
            }
            return ResponseEntity.status(HttpStatus.OK).body(orderResponseDtoList);
        }
    }

    public ResponseEntity<OrderResponseDto> getOneOrder(Long orderNo, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
        Order order = orderRepository.findById(orderNo).orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));
        Product product = productRepository.findById(order.getOrderProductId()).orElseThrow(NullPointerException::new);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto(user.getUsername(), product.getTitle(), order));
    }

    public ResponseEntity<ApiResponseDto> cancelOrder(Long orderNo, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
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
                        Product product = productRepository.findById(order.getOrderProductId()).orElseThrow(NullPointerException::new);
                        int newStock = product.getStock() + order.getOrderQuantity();
                        product.reStock(newStock);
                        productRepository.saveAndFlush(product);
                    }
                default:
                    throw new RuntimeException();
            }
        }
    }
}
