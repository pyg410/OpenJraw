package io.openjraw.skill;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultSkillMdParserTest {
    
    @TempDir
    Path tempDir;

    @Test
    void parseSkillMdWithFrontmatterAndInstruction() throws IOException{
        
        Path skillMd = tempDir.resolve("Skill.md");

        Files.writeString(skillMd, """
            ---
            id: bank-log-analyzer
            name: Bank Log Analyzer
            description: Analyze bank logs.
            version: 0.1.0
            triggers:
                - 타행이체
                - timeout
            tags:
                - banking
                - log
            ---

            Use this skill when analyzing banking logs.

            Steps:
            1. Extract transaction id.
            2. Detect timeout.
            """);

        SkillMdParser parser = new DefaultSkillMdParser();

        SkillDefinition result = parser.parse(skillMd);

        assertThat(result.id()).isEqualTo("bank-log-analyzer");
        assertThat(result.name()).isEqualTo("Bank Log Analyzer");
        assertThat(result.description()).isEqualTo("Analyze bank logs.");
        assertThat(result.source().version()).isEqualTo("0.1.0");
        assertThat(result.triggers()).containsExactly("타행이체", "timeout");
        assertThat(result.tags()).containsExactly("banking", "log");
        assertThat(result.instruction()).contains("Use this skill");
        assertThat(result.source().type()).isEqualTo(SkillSourceType.LOCAL);
        assertThat(result.source().path()).isEqualTo(skillMd);

    }

    @Test
    void throwExceptionWhenFrontmatterIsMissing() throws IOException {
        Path skillMd = tempDir.resolve("SKILL.md");

        Files.writeString(skillMd, """
                ---
                name: Bank Log Analyzer
                ---

                Use this skill when analyzing logs.
                """);
        
        SkillMdParser parser = new DefaultSkillMdParser();

        assertThatThrownBy(
                () -> parser.parse(skillMd)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("id");
    }

    @Test
    void throwExceptionWhenInstructionIsBlank() throws IOException {
        Path skillMd = tempDir.resolve("SKILL.md");

        Files.writeString(skillMd, """
                ---
                id: bank-log-analyzer
                name: Bank Log Analyzer
                ---   
                """);

        SkillMdParser parser = new DefaultSkillMdParser();    

        assertThatThrownBy(
            () -> parser.parse(skillMd)
        ).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("instruction");
    }
}
