package org.hanghae99.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private String title;
    private Long totalPrice;
    private String orderStatus;
}
