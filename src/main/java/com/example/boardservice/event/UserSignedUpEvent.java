package com.example.boardservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserSignedUpEvent {
    private Long userId;
    private String name;

    public UserSignedUpEvent() {
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public static UserSignedUpEvent fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, UserSignedUpEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 에러");
        }
    }
}
