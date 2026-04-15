package com.example.boardservice.service;

import com.example.boardservice.client.PointClient;
import com.example.boardservice.client.UserClient;
import com.example.boardservice.domain.Board;
import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateBoardRequestDto;
import com.example.boardservice.domain.BoardRepository;
import com.example.boardservice.dto.UserDto;
import com.example.boardservice.dto.UserResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private static final int BOARD_POINT_DEDUCT_AMOUNT = 100;
    private static final int BOARD_ACTIVITY_ADD_SCORE = 10;

    private final BoardRepository boardRepository;
    private final UserClient userClient;
    private final PointClient pointClient;

    public BoardService(BoardRepository boardRepository,
                        UserClient userClient,
                        PointClient pointClient) {
        this.boardRepository = boardRepository;
        this.userClient = userClient;
        this.pointClient = pointClient;
    }

    // @Transactional
    public void create(CreateBoardRequestDto createBoardRequestDto) {
        // 게시글 저장을 성공했는지 판단하는 플래그
        boolean isBoardCreated = false;
        Long savedBoardId = null;

        // 포인트 차감을 성공했는지 판단하는 플래스
        boolean isPointDeducted = false;

        try {

            // 포인트 차감
            this.pointClient.deductPoints(createBoardRequestDto.getUserId(), BOARD_POINT_DEDUCT_AMOUNT);
            isPointDeducted = true;
            System.out.println("포인트 차감 성공");

            // 게시글 작성
            Board board = new Board(
                    createBoardRequestDto.getTitle(),
                    createBoardRequestDto.getContent(),
                    createBoardRequestDto.getUserId()
            );
            Board savedBoard = this.boardRepository.save(board);
            savedBoardId = savedBoard.getBoardId();
            isBoardCreated = true; // 게시글 저장 성공 플래그
            System.out.println("게시글 저장 성공");

            // 활동 점수 적립
            this.userClient.addActivityScore(createBoardRequestDto.getUserId(), BOARD_ACTIVITY_ADD_SCORE);
            System.out.println("포인트 적립 성공");
        } catch (Exception e) {
            // === [ saga : 이전에 수행했던 작업에 대해서 다시 그 전 상태로 돌린다. ] ===

            // 게시글 작성 보상 트랜잭션 실행 => 게시글 삭제
            if (isBoardCreated) {
                // 게시글 삭제
                this.boardRepository.deleteById(savedBoardId);
                System.out.println("[보상트랜잭션] 게시글 삭제");
            }

            // 포인트 차감 보상 트랜잭션 실행 => 포인트 적립
            if (isPointDeducted) {
                this.pointClient.addPoints(createBoardRequestDto.getUserId(), BOARD_POINT_DEDUCT_AMOUNT);
                System.out.println("[보상트랜잭션] 포인트 적립");
            }

            // (클라이언트도 실패 인지를 할수있도록) 실패 응답으로 처리하기 위해서 예외를 던진다.
            throw e;
        }
    }

    public BoardResponseDto getBoard(Long boardId) {
        // 게시글 불러오기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // userservice 로 부터 사용자 불러오기
        Optional<UserResponseDto> optionalUserResponseDto = userClient.fetchUser(board.getUserId());

        // 조합해서 반환
        UserDto userDto = null;
        if (optionalUserResponseDto.isPresent()) {
            UserResponseDto userResponseDto = optionalUserResponseDto.get();
            userDto = new UserDto(userResponseDto.getUserId(), userResponseDto.getName());
        }
        BoardResponseDto boardResponseDto = new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), userDto);

        return boardResponseDto;
    }

    public List<BoardResponseDto> getBoards() {
        List<Board> boards = boardRepository.findAll();

        // userId 목록 추출해서 userservice 에서 해당 전체 회원 정보 조회
        List<Long> userIds = boards.stream().map(Board::getUserId).distinct().toList();
        List<UserResponseDto> userResponseDtos = userClient.fetchUsersByIds(userIds);

        // userId를 key로 하는 Map 생성
        Map<Long, UserDto> userMap = userResponseDtos.stream().collect(
                Collectors.toMap(
                        urd -> urd.getUserId(),
                        urd -> new UserDto(urd.getUserId(), urd.getName())
                )
        );

        // 게시글 정보와 사용자 정보를 조합해서 BoardResponseDto 생성
        return boards.stream()
                .map(b -> new BoardResponseDto(b.getBoardId(), b.getTitle(), b.getContent(), userMap.get(b.getUserId())))
                .collect(Collectors.toList());
    }

}
