package com.example.boardservice.dto;

// 활동점수 적립 REST API 호출을 위한 DTO
public class AddActivityScoreRequestDto {
    private Long userId;
    private int score;

    public AddActivityScoreRequestDto(Long userId, int score) {
        this.userId = userId;
        this.score = score;
    }

    public Long getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }
}
