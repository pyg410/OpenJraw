package io.openjraw.skill;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class InMemorySkillRegistTest {
    
    @Test
    void registerAndFindById() {
        SkillDefinition skill = new SkillDefinition(
             "bank-log-analyzer"
            , "Bank Log Analyzer"
            , "Analyze bank logs"
            , "Analyze transaction logs."
            , List.of("타행이체", "timeout")
            , List.of("banking", "log")
            , new SkillSource(SkillSourceType.LOCAL, null, "local", "0.1.0"),
        Map.of());

        SkillRegistry registry = new InMemorySkillRegistry();

        registry.register(skill);

        assertThat(registry.findById("bank-log-analyzer"))
            .isPresent()
            .contains(skill);
    }

    @Test
    void removeSkill() {
        SkillDefinition skill = new SkillDefinition(
            "test-skill"
            , "test-skill"
            , ""
            , "Test instruction"
            , List.of()
            , List.of()
            , new SkillSource(SkillSourceType.LOCAL, null, "local", "0.1.0"),
            Map.of()
        );

        SkillRegistry registry = new InMemorySkillRegistry();

        registry.register(skill);
        
        boolean removed = registry.remove("test-skill");

        assertThat(registry.contains("test-skill")).isFalse();
        assertThat(removed).isTrue();
    }
}
