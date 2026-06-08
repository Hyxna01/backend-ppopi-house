package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;
import com.ppopi.ppopihouse.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiDiagnosisClientImpl implements AiDiagnosisClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.ai-api.base-url}")
    private String aiApiBaseUrl;

    @Override
    public AiDiagnosisResponse diagnose(AiDiagnosisRequest request) {

        try {

            log.info("[AI REQUEST URL] {}", aiApiBaseUrl + "/diagnose");
            log.info("[AI REQUEST BODY] {}", request);

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

            log.info("==================================================");
            log.info("[AI RAW RESPONSE]");
            log.info("{}", rawResponse);
            log.info("==================================================");

            throw new RuntimeException(
                    "AI RAW RESPONSE 확인용 로그. Render 로그에서 [AI RAW RESPONSE] 확인하세요."
            );

        } catch (Exception e) {

            log.error("[AI ERROR]", e);

            throw new ExternalApiException(
                    "AI 진단 서버 호출 중 오류가 발생했습니다. 원인: "
                            + e.getMessage()
            );
        }
    }
}