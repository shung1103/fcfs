package org.hanghae99.fcfs.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.user.dto.SignupRequestDto;
import org.hanghae99.fcfs.user.dto.UserResponseDto;
import org.hanghae99.fcfs.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequestDto));
    }
}
