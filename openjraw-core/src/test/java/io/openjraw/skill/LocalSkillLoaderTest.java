package io.openjraw.skill;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LocalSkillLoaderTest {
    
    @TempDir
    Path tempDir;

    @Test
    void loadSkillsFromLocalDirectory() throws Exception {
        Path skillDir = tempDir.resolve("bank-log-analyzer");
        Files.createDirectories(skillDir);

        Path skillFile = skillDir.resolve("SKILL.md");
        Files.writeString(skillFile, "# Bank Log Analyzer");

        SkillDefinition skill = new SkillDefinition(
            "bank-log-analyzer",
            "Bank Log Analyzer",
            "Analyze bank logs",
            "Analyze transaction logs.",
            List.of("timeout"),
            List.of("banking"), 
            new SkillSource(SkillSourceType.LOCAL, skillFile, "local", "0.1.0"), 
            Map.of()
        );

        SkillMdParser parser = path -> skill;

        LocalSkillLoader loader = new LocalSkillLoader(parser);

        List<SkillDefinition> result = loader.load(tempDir);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("bank-log-analyzer");
    }

    @Test
    void ignoreDirectoryWithoutSkillMd() throws Exception {
        Files.createDirectories(tempDir.resolve("empty-skill"));

        SkillMdParser parser = path -> {
            throw new AssertionError("Parser should not be called");
        };

        LocalSkillLoader loader = new LocalSkillLoader(parser);

        List<SkillDefinition> result = loader.load(tempDir);

        assertThat(result).isEmpty();
    }
}
