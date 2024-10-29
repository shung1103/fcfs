package org.hanghae99.productservice.dto;

import lombok.Getter;
import org.hanghae99.productservice.entity.TimeStamped;

@Getter
public class Order extends TimeStamped {
    private Long id;
    private Long orderUserId;
    private Long orderProductId;
    private Integer orderQuantity;
    private String orderStatus;
    private Long payment;
}
