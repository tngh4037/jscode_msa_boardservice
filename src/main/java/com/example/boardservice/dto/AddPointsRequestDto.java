package com.example.boardservice.dto;

// 포인트 차감에 대한 보상트랜잭션 처리를 위한 DTO
public class AddPointsRequestDto {
    private Long userId;
    private int amount;

    public AddPointsRequestDto(Long userId, int amount) {
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
