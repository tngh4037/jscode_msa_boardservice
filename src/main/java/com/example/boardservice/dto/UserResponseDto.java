package com.example.boardservice.dto;

// 회원서비스 REST API 호출 후, 응답으로 받을 객체
public class UserResponseDto {
    private Long userId;
    private String email;
    private String name;

    public UserResponseDto(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
