package io.openjraw.prompt;

import java.util.List;

import io.openjraw.skill.SkillDefinition;

/**
 * Prompt를 구성하는 인터페이스
 * 
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-06
 */
public interface PromptComposer {

    /**
     * 사용자 메시지와 선택된 Skill 목록을 기반으로 최종 Prompt를 만든다.
     *
     * @param userMessage 사용자가 입력한 메시지
     * @param selectedSkills 라우터가 선택한 Skill 목록
     * @return LLM에 전달할 ComposedPrompt
     */
    ComposedPrompt compose(String userMessage, List<SkillDefinition> selectedSkills);
    
}
