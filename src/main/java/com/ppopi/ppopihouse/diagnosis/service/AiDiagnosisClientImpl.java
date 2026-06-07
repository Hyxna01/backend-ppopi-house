package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;
import com.ppopi.ppopihouse.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AiDiagnosisClientImpl implements AiDiagnosisClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.ai-api.base-url}")
    private String aiApiBaseUrl;

    public AiDiagnosisResponse diagnose(AiDiagnosisRequest request) {
        try {
            AiDiagnosisResponse response = webClientBuilder.build()
                    .post()
                    .uri(aiApiBaseUrl + "/diagnose")
                    // ngrok Free 플랜의 브라우저 경고창 레이어를 강제 패스하는 헤더 주입
                    .header("ngrok-skip-browser-warning", "true")
                    // 한글 깨짐으로 인한 DB 매핑 실패를 방지하기 위한 UTF-8 캐릭터셋 명시
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AiDiagnosisResponse.class)
                    .block();

            if (response == null) {
                throw new ExternalApiException("AI 진단 서버 응답이 비어 있습니다.");
            }

            return response;

        } catch (WebClientException e) {
            throw new ExternalApiException("AI 진단 서버 호출 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }
}
