package com.example.boardservice.controller;

import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateBoardRequestDto;
import com.example.boardservice.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody CreateBoardRequestDto createBoardRequestDto,
            @RequestHeader("X-User-Id") Long userId // api gateway 에서 인증토큰 검증 완료시 userId를 헤더에 담아서 보내줌
    ) {
        boardService.create(createBoardRequestDto, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(
            @PathVariable Long boardId
    ) {
        // BoardResponseDto boardResponseDto = boardService.getBoard(boardId);

        // 데이터 조회 최적화
        BoardResponseDto boardResponseDto = boardService.getBoard2(boardId);
        return ResponseEntity.ok().body(boardResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getBoards() {
        // List<BoardResponseDto> boardResponseDtos = boardService.getBoards();

        // 데이터 조회 최적화
        List<BoardResponseDto> boardResponseDtos = boardService.getBoards2();
        return ResponseEntity.ok().body(boardResponseDtos);
    }

}
