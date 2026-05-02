package io.openjraw.skill;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LocalSkillLoader implements SkillLoader{

    private static final String SKILL_FILE_NAME = "Skill.md";

    private final SkillMdParser skillMdParser;

    public LocalSkillLoader(SkillMdParser skillMdParser) {
        if(skillMdParser == null){
            throw new IllegalArgumentException("SkillMdParser must not be null");
        }
        this.skillMdParser = skillMdParser;
    }

    @Override
    public List<SkillDefinition> load(Path rootPath) {
        
        validateRootPath(rootPath);
        
        try (var stream = Files.list(rootPath)) {
            return stream
                .filter(Files::isDirectory)
                .map(this::resolveSkillFile)
                .filter(Files::exists)
                .map(skillMdParser::parse)
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load skills from path: " + rootPath, e);
        }
    }

    private Path resolveSkillFile(Path skillDir) {
        return skillDir.resolve(SKILL_FILE_NAME);
    }

    private void validateRootPath(Path rootPath) {
        if(rootPath == null){
            throw new IllegalArgumentException("Skill root path must not be null");
        }

        if(!Files.exists(rootPath)){
            throw new IllegalArgumentException("Skill root path does not exist: " + rootPath);
        }

        if(!Files.isDirectory(rootPath)){
            throw new IllegalArgumentException("Skill root path must be a directory: " + rootPath);
        }
    }

}
