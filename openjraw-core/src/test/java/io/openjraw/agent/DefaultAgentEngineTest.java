package io.openjraw.agent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.openjraw.prompt.DefaultPromptComposer;
import io.openjraw.skill.InMemorySkillRegistry;
import io.openjraw.skill.KeywordSkillRouter;
import io.openjraw.skill.SkillDefinition;
import io.openjraw.skill.SkillRegistry;
import io.openjraw.skill.SkillSource;
import io.openjraw.skill.SkillSourceType;

public class DefaultAgentEngineTest {
    
    @Test
    void runAgentWithMatchedSkill() {
        SkillRegistry registry = new InMemorySkillRegistry();
        registry.register(skill());

        AgentEngine engine = new DefaultAgentEngine(
            registry, 
            new KeywordSkillRouter(), 
            new DefaultPromptComposer(),
            new MockAgentModel()
        );

        AgentResponse response = engine.run(new AgentRequest(
            "session-1",
            "타행이체 timeout 로그 분석해줘",
            Map.of()
        ));

        assertThat(response.usedSkillIds()).containsExactly("bank-log-analyzer");
        assertThat(response.prompt().systemPrompt()).contains("Bank Log Analyzer");
        assertThat(response.prompt().userPrompt()).isEqualTo("타행이체 timeout 로그 분석해줘");
        assertThat(response.content()).contains("bank-log-analyzer");
    }

    @Test
    void runAgentWithoutMatchedSkill() {
        SkillRegistry registry = new InMemorySkillRegistry();
        registry.register(skill());

        AgentEngine engine = new DefaultAgentEngine(
                registry,
                new KeywordSkillRouter(),
                new DefaultPromptComposer(),
                new MockAgentModel()
        );

        AgentResponse response = engine.run(new AgentRequest(
            "session-1",
            "오늘 날씨 알려줘",
            Map.of()
        ));

        assertThat(response.usedSkillIds()).isEmpty();
        assertThat(response.prompt().systemPrompt()).doesNotContain("Selected Skills");
        assertThat(response.content()).contains("No skill selected");
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
