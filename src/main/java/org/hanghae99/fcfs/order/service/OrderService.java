package org.hanghae99.fcfs.order.service;

import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.order.dto.OrderRequestDto;
import org.hanghae99.fcfs.order.dto.OrderResponseDto;
import org.hanghae99.fcfs.order.entity.Order;
import org.hanghae99.fcfs.order.entity.OrderItem;
import org.hanghae99.fcfs.order.repository.OrderItemRepository;
import org.hanghae99.fcfs.order.repository.OrderRepository;
import org.hanghae99.fcfs.product.entity.Product;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.wishList.entity.WishList;
import org.hanghae99.fcfs.wishList.entity.WishListItem;
import org.hanghae99.fcfs.wishList.repository.WishListItemRepository;
import org.hanghae99.fcfs.wishList.service.WishListService;
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
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WishListService wishListService;
    private final WishListItemRepository wishListItemRepository;

    public ResponseEntity<ApiResponseDto> createOrder(OrderRequestDto orderRequestDto, User user) {
        WishList wishList = wishListService.createWishList(user);
        if (wishList.getWishListItemList().isEmpty()) throw new NullPointerException("위시 리스트에 상품이 없습니다.");

        long totalPrice = 0L;
        for (WishListItem wishListItem : wishList.getWishListItemList()) totalPrice += wishListItem.getProduct().getPrice() * wishListItem.getWishListItemQuantity();

        if (orderRequestDto.getPayment().equals(totalPrice)) {
            Order order = new Order(user, totalPrice, orderRequestDto);
            orderRepository.save(order);

            for (WishListItem wishListItem : wishList.getWishListItemList()) {
                if (wishListItem.getProduct().getStock() < wishListItem.getWishListItemQuantity()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new ApiResponseDto(wishListItem.getProduct().getTitle() + "의 재고가 부족합니다.", HttpStatus.BAD_REQUEST.value()));
                }
                OrderItem orderItem = new OrderItem(wishListItem, order);
                Product product = wishListItem.getProduct();
                product.reStock(wishListItem.getWishListItemQuantity() * (-1L));

                orderItemRepository.save(orderItem);
                wishListItemRepository.delete(wishListItem);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("결제가 완료 되었습니다.", HttpStatus.CREATED.value()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto("금액을 올바르게 입력해 주세요.", HttpStatus.BAD_REQUEST.value()));
        }
    }

    public ResponseEntity<List<OrderResponseDto>> getMyOrders(User user) {
        List<Order> orderList = orderRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        if (orderList.isEmpty()) {
            throw new NullPointerException("주문이 없습니다.");
        } else {
            List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
            for (Order order : orderList) {
                OrderResponseDto orderResponseDto = new OrderResponseDto(order);
                orderResponseDtoList.add(orderResponseDto);
            }
            return ResponseEntity.status(HttpStatus.OK).body(orderResponseDtoList);
        }
    }

    public ResponseEntity<OrderResponseDto> getOneOrder(Long orderNo, User user) {
        Order order = orderRepository.findById(orderNo).orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));
        if (user.getId().equals(order.getUser().getId()) || user.getRole().equals(UserRoleEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto(order));
        } else {
            throw new IllegalArgumentException("해당 번호의 주문에 대한 권한이 없습니다.");
        }
    }

    public ResponseEntity<ApiResponseDto> cancelOrder(Long orderNo, User user) {
        Order order = orderRepository.findById(orderNo).orElseThrow(() -> new NullPointerException("존재하지 않는 주문 번호입니다."));
        if (user.getId().equals(order.getUser().getId()) || user.getRole().equals(UserRoleEnum.ADMIN)) {
            switch (order.getOrderStatus()) {
                case "배송 중":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto("배송이 진행 중입니다.", HttpStatus.BAD_REQUEST.value()));
                case "배송 완료":
                    if (Duration.between(order.getModifiedAt(), LocalDateTime.now()).toDays() < 2) {
                        order.updateOrderStatus("반품 진행");
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
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto("해당 주문에 대한 권한이 없습니다.", HttpStatus.BAD_REQUEST.value()));
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
                        List<OrderItem> orderItemList = order.getOrderItemList();
                        for (OrderItem orderItem : orderItemList) orderItem.getProduct().reStock((long) orderItem.getOrderItemQuantity());
                    }
                default:
                    throw new RuntimeException();
            }
        }
    }
}
