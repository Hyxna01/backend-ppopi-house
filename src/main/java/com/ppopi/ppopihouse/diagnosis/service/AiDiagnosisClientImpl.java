package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;
import com.ppopi.ppopihouse.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
@Slf4j
public class AiDiagnosisClientImpl implements AiDiagnosisClient {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${external.ai-api.base-url}")
    private String aiApiBaseUrl;

    public AiDiagnosisResponse diagnose(AiDiagnosisRequest request) {
        try {
            log.info("[AI REQUEST] url={}, request={}", aiApiBaseUrl + "/diagnose", request);

            String rawResponse = webClientBuilder.build()
                    .post()
                    .uri(aiApiBaseUrl + "/diagnose")
                    .header("ngrok-skip-browser-warning", "true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("[AI RAW RESPONSE] {}", rawResponse);

            AiDiagnosisResponse response =
                    objectMapper.readValue(rawResponse, AiDiagnosisResponse.class);

            log.info("[AI PARSED RESPONSE] disease={}, familyLabel={}, triage={}, confidence={}",
                    response.getDisease(),
                    response.getFamilyLabel(),
                    response.getTriage(),
                    response.getTriageConfidence());

            return response;

        } catch (Exception e) {
            log.error("[AI ERROR] AI 진단 서버 호출 실패", e);
            throw new ExternalApiException("AI 진단 서버 호출 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }
}