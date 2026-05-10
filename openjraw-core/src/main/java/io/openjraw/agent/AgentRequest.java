package io.openjraw.agent;

import java.util.Map;

/**
 * AgentEngine에 전달되는 사용자 요청
 * 
 * @param sessionId 대화 세션 ID
 * @param userMessage 사용자 입력 메시지
 * @param variables 추가 실행 변수
 */
public record AgentRequest(
    String sessionId,
    String userMessage,
    Map<String, Object> variables
) {
    public AgentRequest{
        if(sessionId == null || sessionId.isBlank()) {
            sessionId = "default";
        }

        if(userMessage == null) {
            throw new IllegalArgumentException("User message must not be null");
        }

        variables = variables == null ? Map.of() : Map.copyOf(variables);
    }
    
}
