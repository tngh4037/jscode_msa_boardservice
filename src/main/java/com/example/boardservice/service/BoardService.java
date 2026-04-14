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

    @Transactional
    public void create(CreateBoardRequestDto createBoardRequestDto) {
        Board board = new Board(
                createBoardRequestDto.getTitle(),
                createBoardRequestDto.getContent(),
                createBoardRequestDto.getUserId()
        );

        // 포인트 차감
        this.pointClient.deductPoints(createBoardRequestDto.getUserId(), BOARD_POINT_DEDUCT_AMOUNT);

        // 게시글 작성
        this.boardRepository.save(board);

        // 활동 점수 적립
        this.userClient.addActivityScore(createBoardRequestDto.getUserId(), BOARD_ACTIVITY_ADD_SCORE);
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
