package io.openjraw.prompt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.openjraw.skill.SkillDefinition;
import io.openjraw.skill.SkillSource;
import io.openjraw.skill.SkillSourceType;

public class DefaultPromptComposerTest {
    
    @Test
    void composePromptWithSelectedSkill() {

        SkillDefinition skill = skill();

        PromptComposer composer = new DefaultPromptComposer();

        ComposedPrompt result = composer.compose(
            "타행이체 timeout 로그 분석해줘", 
            List.of(skill)
        );

        assertThat(result.userPrompt()).isEqualTo("타행이체 timeout 로그 분석해줘");
        assertThat(result.usedSkillIds()).containsExactly("bank-log-analyzer");
        assertThat(result.systemPrompt()).contains("OpenJraw");
        assertThat(result.systemPrompt()).contains("Selected Skills");
        assertThat(result.systemPrompt()).contains("Bank Log Analyzer");
        assertThat(result.systemPrompt()).contains("Analyze bank transaction logs.");
    }

    @Test
    void composePromptWithoutSelectedSkill() {
        PromptComposer composer = new DefaultPromptComposer();

        ComposedPrompt result = composer.compose(
            "안녕", 
            List.of()
        );

        assertThat(result.userPrompt()).isEqualTo("안녕");
        assertThat(result.usedSkillIds()).isEmpty();
        assertThat(result.systemPrompt()).contains("OpenJraw");
        assertThat(result.systemPrompt()).doesNotContain("Selected Skills");
    }

    @Test
    void throwExceptionWhenUserMessageIsBlank() {
        PromptComposer composer = new DefaultPromptComposer();

        assertThatThrownBy(() -> composer.compose(" ", List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User message");
    }

    private SkillDefinition skill() {
        return new SkillDefinition(
                "bank-log-analyzer",
                "Bank Log Analyzer",
                "Analyze bank logs",
                "Analyze bank transaction logs.",
                List.of("타행이체", "timeout"),
                List.of("banking", "log"),
                new SkillSource(SkillSourceType.LOCAL, null, "local", "0.1.0"),
                Map.of()
        );
    }
}
