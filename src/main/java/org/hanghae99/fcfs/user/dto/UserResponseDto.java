package org.hanghae99.fcfs.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.order.dto.OrderResponseDto;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.wishList.dto.WishListResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String username;
    private String email;
    private String realName;
    private String address;
    private String phone;
    private UserRoleEnum role;
    private String social;
    private List<WishListResponseDto> wishList;
    private List<OrderResponseDto> orderList;

    public UserResponseDto(User user, String email, String realName, String address, String phone, List<WishListResponseDto> wishList, List<OrderResponseDto> orderList) {
        this.username = user.getUsername();
        this.email = email;
        this.realName = realName;
        this.address = address;
        this.phone = phone;
        this.role = user.getRole();
        this.social = user.getSocial();
        this.wishList = wishList;
        this.orderList = orderList;
    }

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.realName = user.getRealName();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.social = user.getSocial();
    }
}
