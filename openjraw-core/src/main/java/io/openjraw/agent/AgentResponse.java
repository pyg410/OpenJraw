package io.openjraw.agent;

import java.util.List;

import io.openjraw.prompt.ComposedPrompt;

/**
 * AgentEngine 실행 결과
 * 
 * @param content Agent 응답 내용
 * @param prompt 조립된 Prompt
 * @param usedSkillIds 사용된 Skill ID 목록
 */
public record AgentResponse(
    String content,
    ComposedPrompt prompt,
    List<String> usedSkillIds
) {

    public AgentResponse {
        content = content == null ? "" : content;
        usedSkillIds = usedSkillIds == null ? List.of() : List.copyOf(usedSkillIds);
    }
    
}
