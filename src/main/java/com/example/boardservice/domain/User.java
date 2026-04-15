package com.example.boardservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long userId; // @GeneratedValue 없어야 함. ( userservice 마이크로서비스에서 회원가입 후 kafka 로 이벤트 발행한 메시지의 userId가 들어가야 함. )

    private String name;

    public User() {
    }

    public User(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
