package com.example.boardservice.controller;

import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateBoardRequestDto;
import com.example.boardservice.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody CreateBoardRequestDto createBoardRequestDto
    ) {
        boardService.create(createBoardRequestDto);
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
