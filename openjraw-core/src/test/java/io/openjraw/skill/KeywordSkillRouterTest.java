package io.openjraw.skill;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class KeywordSkillRouterTest {
    @Test
    void routeSkillByTriggerKeyword() {
        SkillDefinition bankLogSkill = skill(
                "bank-log-analyzer",
                "Bank Log Analyzer",
                "Analyze banking logs",
                List.of("타행이체", "timeout", "대외계"),
                List.of("banking", "log")
        );

        SkillDefinition emailSkill = skill(
                "email-writer",
                "Email Writer",
                "Write business emails",
                List.of("email", "메일"),
                List.of("writing")
        );

        SkillRouter router = new KeywordSkillRouter();

        List<SkillDefinition> result = router.route(
                "타행이체 timeout 로그 분석해줘",
                List.of(bankLogSkill, emailSkill)
        );

        assertThat(result)
                .hasSize(1)
                .containsExactly(bankLogSkill);
    }

    @Test
    void routeMultipleSkillsByScoreOrder() {
        SkillDefinition logSkill = skill(
                "log-analyzer",
                "Log Analyzer",
                "Analyze logs",
                List.of("로그"),
                List.of("log")
        );

        SkillDefinition bankLogSkill = skill(
                "bank-log-analyzer",
                "Bank Log Analyzer",
                "Analyze bank transaction logs",
                List.of("타행이체", "로그"),
                List.of("banking", "log")
        );

        SkillRouter router = new KeywordSkillRouter();

        List<SkillDefinition> result = router.route(
                "타행이체 로그 확인해줘",
                List.of(logSkill, bankLogSkill)
        );

        assertThat(result)
                .containsExactly(bankLogSkill, logSkill);
    }

    @Test
    void returnEmptyWhenNoSkillMatches() {
        SkillDefinition emailSkill = skill(
                "email-writer",
                "Email Writer",
                "Write emails",
                List.of("email"),
                List.of("writing")
        );

        SkillRouter router = new KeywordSkillRouter();

        List<SkillDefinition> result = router.route(
                "SQL 실행계획 분석해줘",
                List.of(emailSkill)
        );

        assertThat(result).isEmpty();
    }

    @Test
    void respectLimit() {
        SkillDefinition skill1 = skill("skill-1", "Skill 1", "desc", List.of("로그"), List.of());
        SkillDefinition skill2 = skill("skill-2", "Skill 2", "desc", List.of("로그"), List.of());
        SkillDefinition skill3 = skill("skill-3", "Skill 3", "desc", List.of("로그"), List.of());

        SkillRouter router = new KeywordSkillRouter(2);

        List<SkillDefinition> result = router.route(
                "로그 분석해줘",
                List.of(skill1, skill2, skill3)
        );

        assertThat(result).hasSize(2);
    }

    @Test
    void throwExceptionWhenLimitIsInvalid() {
        assertThatThrownBy(() -> new KeywordSkillRouter(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit");
    }

    private SkillDefinition skill(
            String id,
            String name,
            String description,
            List<String> triggers,
            List<String> tags
    ) {
        return new SkillDefinition(
                id,
                name,
                description,
                "Instruction for " + name,
                triggers,
                tags,
                new SkillSource(SkillSourceType.LOCAL, null, "local", "0.1.0"),
                Map.of()
        );
    }
}
