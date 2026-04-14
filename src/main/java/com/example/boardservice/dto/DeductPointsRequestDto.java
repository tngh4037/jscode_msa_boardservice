package com.example.boardservice.dto;

// 포인트 차감 REST API 호출을 위한 DTO
public class DeductPointsRequestDto {

    private Long userId;
    private int amount;

    public DeductPointsRequestDto(Long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }
}
