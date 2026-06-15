package com.email.smart.email.assistant.service;

import com.email.smart.email.assistant.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor

public class EmailGeneraterService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private  String apiKey;


    public String generateEmail(EmailRequest emailRequest) {
        // Build prompt
        String prompt = buildPrompt(emailRequest);

        //prepare row JSON body

        String requestBody = String.format(
                """
                {
                    "contents": [
                      {
                        "parts": [
                          {
                            "text": "%s"
                          }
                        ]
                      }
                    ]
                  }
                """, prompt
        );

        // send request to Gemini API
        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-3.5-flash:generateContent")
                        .build())
                .header("x-goog-api-key",apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Extract response
        return extractResponse(response);

    }

    private String extractResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing response";
        }

    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a professional reply for the following email ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            promptBuilder.append("The tone of the email should be: ").append(emailRequest.getTone()).append(". ");
        }
        promptBuilder.append("The email content is: ").append(emailRequest.getEmailContent()).append(". ");
        promptBuilder.append("Make sure the email is well-structured and professional.");
        return promptBuilder.toString();

    }
}
