package com.example.boardservice.client;

import com.example.boardservice.dto.DeductPointsRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PointClient {

    private final RestClient restClient;

    public PointClient(
            @Value("${client.point-service.url}") String pointServiceUrl)
    {
        this.restClient = RestClient.builder().baseUrl(pointServiceUrl).build();
    }

    public void deductPoints(Long userId, int amount) {
        DeductPointsRequestDto deductPointsRequestDto = new DeductPointsRequestDto(userId, amount);

        this.restClient.post()
                .uri("/points/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .body(deductPointsRequestDto)
                .retrieve()
                .toBodilessEntity();
    }
}
