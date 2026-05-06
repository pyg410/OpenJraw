package io.openjraw.prompt;

import java.util.List;
import java.util.stream.Collectors;

import io.openjraw.skill.SkillDefinition;

/**
 * 기본 PromptComposer 구현체.
 * 
 * 사용자 메세지와 선택된 Skill 목록을 받아서 LLM에게 전달할 System Prompt를 구성하고 실제 사용자 메세지, 사용된 Skill ID 목록과 함께 ComposedPrompt로 반환한다.
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-06
 */
public class DefaultPromptComposer implements PromptComposer{

    private static final String BASE_SYSTEM_PROMPT = """
            You are OpenJraw, a local-first AI agent runtime.

            Follow the selected skills carefully.
            If no skill is selected, answer normally.
            Do not claim that you used a skill unless it is included below.
            """;

    @Override
    public ComposedPrompt compose(String userMessage, List<SkillDefinition> selectedSkills) {

        validateUserMessage(userMessage);

        List<SkillDefinition> safeSkills = selectedSkills == null ? List.of() : selectedSkills;

        String systemPrompt = buildSystemPrompt(safeSkills);
        List<String> usedSkillIds = safeSkills.stream()
            .map(SkillDefinition::id)
            .toList();

        return new ComposedPrompt(
            systemPrompt,
            userMessage.trim(),
            usedSkillIds
        );
    }

    private String buildSystemPrompt(List<SkillDefinition> selectedSkills) {

        if(selectedSkills.isEmpty()) {
            return BASE_SYSTEM_PROMPT.trim();
        }

        String skillInstructions = selectedSkills.stream()
        .map(this::formatSkillInstruction)
        .collect(Collectors.joining("\n\n"));

        return """
                %s

                # Selected Skills

                %s
                """.formatted(BASE_SYSTEM_PROMPT.trim(), skillInstructions).trim();
    }

    private String formatSkillInstruction(SkillDefinition skill) {
        return """
                ## Skill: %s
                ID: %s
                Description: %s
                
                Instructions:
                %s
                """.formatted(
                    skill.name(),
                    skill.id(),
                    skill.description(),
                    skill.instruction()
                ).trim();
    }

    private void validateUserMessage(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("User message must not be blank");
        }
    }
    
}
