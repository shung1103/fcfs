package org.hanghae99.fcfs.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.common.security.UserDetailsImpl;
import org.hanghae99.fcfs.user.dto.*;
import org.hanghae99.fcfs.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원 가입")
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, BindingResult bindingResult) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            throw new IllegalArgumentException("형식에 맞게 입력해주세요");
        }
        return userService.signup(signupRequestDto);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response, BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            throw new IllegalArgumentException("잘못된 ID 또는 비밀번호입니다.");
        }
        return userService.login(loginRequestDto, request, response);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        userService.logout(request, userDetails.getUser()) ;
        return ResponseEntity.ok().body(new ApiResponseDto("로그 아웃 완료", HttpStatus.OK.value()));
    }

    @Operation(summary = "유저 정보 조회")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userDetails.getUser()));
    }

    @Operation(summary = "프로필 수정")
    @PutMapping("/update-profile")
    public ResponseEntity<UserResponseDto> updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestDto userRequestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDetails.getUser(), userRequestDto));
    }

    @Operation(summary = "비밀번호 수정", description = "등록된 모든 기기에서 로그아웃")
    @PutMapping("/update-password")
    public ResponseEntity<ApiResponseDto> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PasswordRequestDto passwordRequestDto, HttpServletRequest request) {
        return userService.updatePassword(userDetails.getUser(), passwordRequestDto, request);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponseDto> handleMethodArgumentNotValidException(IllegalArgumentException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(),HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ApiResponseDto> handleMethodArgumentNotValidException(NullPointerException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(),HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }
}
