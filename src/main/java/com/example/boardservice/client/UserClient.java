package com.example.boardservice.client;

import com.example.boardservice.dto.AddActivityScoreRequestDto;
import com.example.boardservice.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(
            @Value("${client.user-service.url}") String userServiceUrl
    ) {
        this.restClient = RestClient.builder().baseUrl(userServiceUrl).build();
    }

    // 외부 API 통신 (using RestClient)
    public Optional<UserResponseDto> fetchUser(Long userId) {
        try {
            UserResponseDto userResponseDto = this.restClient.get()
                    .uri("/internal/users/{userId}", userId)
                    .retrieve()
                    .body(UserResponseDto.class); // 응답으로 온 body를 UserResponseDto 형태로 받겠다.

            return Optional.of(userResponseDto);
        } catch (RestClientException e) { // api 과정에서 장애 발생 시
            // 로깅: 예외 발생 시 로그를 남겨 문제를 파악할 수 있게 해야 함.
            // ㄴ log.error("사용자 정보 조회 실패");

            return Optional.empty(); // 장애가 난 서비스(userservice)로부터 받아와야 하는 정보를 필수로 보내지 않아도 된다고 가정.
        }
    }

    public List<UserResponseDto> fetchUsersByIds(List<Long> ids) {
        try {
            return this.restClient.get()
                    .uri(uriBuilder -> uriBuilder // 참고) 쿼리파라미터를 사용하려면 uriBuilder 를 사용해야 한다.
                            .path("/internal/users")
                            .queryParam("ids", ids)
                            .build()
                    )
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserResponseDto>>() {});
        } catch (RestClientException e) {
            // 로깅: 예외 발생 시 로그를 남겨 문제를 파악할 수 있게 해야 함.
            // ㄴ log.error("사용자 정보 조회 실패");

            return Collections.emptyList();
        }
    }

    // 활동 점수 적립 API
    public void addActivityScore(Long userId, int score) {
        AddActivityScoreRequestDto addActivityScoreRequestDto = new AddActivityScoreRequestDto(userId, score);

        this.restClient.post()
                .uri("/internal/users/activity-score/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(addActivityScoreRequestDto)
                .retrieve()
                .toBodilessEntity();
    }

}
