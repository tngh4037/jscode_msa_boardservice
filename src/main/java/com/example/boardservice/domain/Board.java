package com.example.boardservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;
    private String title;
    private String content;

    @Column(name = "user_id")
    private Long userId; // (마이크로서비스 특성상) FK 설정 안하고 그냥 컬럼으로 선언

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false) // 해당 user 변수는 조회용으로만 사용하겠다. (insertable = false, updatable = false )
    private User user;

    public Board() {
    }

    public Board(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }
}
