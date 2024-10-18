package org.hanghae99.fcfs.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class OrderRequestDto {
    private Long payment;

    @JsonIgnore
    private String orderComplete = "주문 완료";
}
