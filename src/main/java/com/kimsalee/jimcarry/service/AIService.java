package com.kimsalee.jimcarry.service;

import com.kimsalee.jimcarry.dto.ChecklistItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {

    @Value("${gemini.api.key:test-key}")
    private String apiKey;

    // 최신 Gemini 모델로 변경
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public List<ChecklistItemDto> generateChecklist(List<ChecklistItemDto> selectedItems) {

        System.out.println("=== Gemini API 테스트 시작 ===");
        System.out.println("API Key 설정됨: " + (apiKey != null && !apiKey.equals("test-key")));

        if (apiKey == null || apiKey.equals("test-key")) {
            System.out.println("Gemini API 키가 설정되지 않음. 테스트 데이터 반환");
            return getTestData(selectedItems);
        }

        System.out.println("Gemini API 키 확인됨: " + apiKey.substring(0, 10) + "...");

        try {
            String prompt = createPrompt(selectedItems);
            System.out.println("프롬프트: " + prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("""
                {
                    "contents": [{
                        "parts": [{
                            "text": "%s"
                        }]
                    }]
                }
                """, prompt.replace("\"", "\\\""));

            String urlWithKey = GEMINI_API_URL + "?key=" + apiKey;
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            System.out.println("Gemini API 호출 중...");
            ResponseEntity<String> response = restTemplate.exchange(urlWithKey, HttpMethod.POST, entity, String.class);

            System.out.println("성공! Gemini 응답 상태: " + response.getStatusCode());
            System.out.println("Gemini 응답 내용: " + response.getBody());

            // 응답 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content").path("parts");
                if (content.isArray() && content.size() > 0) {
                    String text = content.get(0).path("text").asText();
                    System.out.println("Gemini 생성 텍스트: " + text);
                    return parseGeminiResponse(text);
                }
            }

        } catch (Exception e) {
            System.out.println("Gemini API 호출 중 에러 발생:");
            e.printStackTrace();
        }

        return getTestData(selectedItems);
    }

    private String createPrompt(List<ChecklistItemDto> selectedItems) {
        StringBuilder sb = new StringBuilder("사용자가 이사할 때 챙겨야 할 항목들을 선택했습니다:\n");

        for (ChecklistItemDto item : selectedItems) {
            if (item.isSelected()) {
                sb.append("- ").append(item.getItemName()).append("\n");
            }
        }

        sb.append("""
            
            위 항목들을 바탕으로 실용적인 이삿짐 체크리스트를 만들어주세요.
            각 줄마다 하나의 할 일을 적어주세요. 10개 정도로 만들어주세요.
            간단하고 실행 가능한 항목들로 구성해주세요.
            """);

        return sb.toString();
    }

    private List<ChecklistItemDto> parseGeminiResponse(String text) {
        List<ChecklistItemDto> result = new ArrayList<>();
        String[] lines = text.split("\n");
        int idCounter = 1;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.length() < 3) continue;

            // 번호나 기호 제거
            line = line.replaceFirst("^[0-9]+[.)\\s]*", "")
                    .replaceFirst("^[-*+]\\s*", "");

            if (!line.isEmpty()) {
                result.add(new ChecklistItemDto(
                        "gemini_item_" + idCounter++,
                        line,
                        false,
                        "ai-generated"
                ));
            }
        }

        return result;
    }

    private List<ChecklistItemDto> getTestData(List<ChecklistItemDto> selectedItems) {
        List<ChecklistItemDto> testData = new ArrayList<>();
        testData.add(new ChecklistItemDto("gemini_test_1", "Gemini 테스트 항목 1", false, "test"));
        testData.add(new ChecklistItemDto("gemini_test_2", "Gemini 테스트 항목 2", false, "test"));
        return testData;
    }
}