package io.openjraw.prompt;

import java.util.List;

/**
 * LLM에 전달할 최종 프롬프트를 담는 불변 데이터 객체
 * 
 * @param systemPrompt 시스템 레벨 지시사항(역할, 정책, 스킬 등)
 * @param userPrompt 실제 사용자 입력
 * @param usedSkillIds: 이번 요청에서 사용된 Skill ID 목록
 * 
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-06
 */
public record ComposedPrompt(
    String systemPrompt,
    String userPrompt,
    List<String> usedSkillIds
) {
    public ComposedPrompt {
        if(systemPrompt == null) {
            systemPrompt = "";
        }
        if(userPrompt == null || userPrompt.isBlank()) {
            throw new IllegalArgumentException("User prompt must not be blank");
        }
        usedSkillIds = usedSkillIds == null ? List.of() : List.copyOf(usedSkillIds);
    }
}
