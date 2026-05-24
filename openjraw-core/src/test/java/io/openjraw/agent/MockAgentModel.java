package io.openjraw.agent;

import io.openjraw.prompt.ComposedPrompt;

public class MockAgentModel implements AgentModel {
    @Override
    public String generate(ComposedPrompt prompt) {
        // 단순히 사용된 Skill ID를 반환하는 Mock 응답 생성
        if(prompt.usedSkillIds().isEmpty()) {
            return "No skill selected. User message: " + prompt.userPrompt();
        }
        return "Selected skills: " + String.join(", ", prompt.usedSkillIds());
    }

}
