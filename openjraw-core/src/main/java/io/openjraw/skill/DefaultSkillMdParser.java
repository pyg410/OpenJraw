package io.openjraw.skill;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * 기본 SKILL.md 파서 구현체입니다.
 * 
 * <p> 지원하는 형식:
 * 
 * <pre>
 * ---
 * id: bank-log-analyzer
 * name: Bank Log Analyzer
 * description: Analyze bank logs
 * version: 0.1.0
 * triggers:
 *   - timeout
 * tags:
 *   - banking
 *   - log
 * ---
 * 
 * Use this skill when...
 * </pre>
 * 
 * <p> YAML frontmatter는 Skill 메타데이터로 사용하고, frontmatter 이후 본문은 instruction으로 사용합니다.
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-03
 * 
 */
public class DefaultSkillMdParser implements SkillMdParser{

    private static final String FRONTMATTER_DELIMITER = "---";

    private final Yaml yaml = new Yaml();

    @Override
    public SkillDefinition parse(Path skillMdPath) {
        validatePath(skillMdPath);
        
        try{
            String content = Files.readString(skillMdPath);
            ParsedSkillMd parsed = parseContent(content);

            Map<String, Object> frontmatter = parseFrontMatter(parsed.frontmatter());
            
            String id = getRequiredString(frontmatter, "id");
            String name = getRequiredString(frontmatter, "name");
            String description = getOptionalString(frontmatter, "description", "");
            String version = getOptionalString(frontmatter, "version", "0.1.0");
            List<String> triggers = getStringList(frontmatter, "triggers");
            List<String> tags = getStringList(frontmatter, "tags");

            String instruction = parsed.instruction().trim();
            if(instruction.isBlank()){
                throw new IllegalArgumentException("Skill instruction must not be blank. " + skillMdPath);
            }
            
            return new SkillDefinition(
                id, 
                name, 
                description, 
                instruction, 
                triggers, 
                tags, 
                new SkillSource(
                    SkillSourceType.LOCAL, 
                    skillMdPath, 
                    "local", 
                    version), 
                frontmatter);
            
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read SKILL.md: " + skillMdPath, e);
        }
    }

    private void validatePath(Path skillMdPath) {
        if(skillMdPath == null){
            throw new IllegalArgumentException("SKILL.md path must not be null");
        }

        if(!Files.exists(skillMdPath)){
            throw new IllegalArgumentException("SKILL.md does not exist: " + skillMdPath);
        }

        if(!Files.isRegularFile(skillMdPath)){
            throw new IllegalArgumentException("SKILL.md path must be a regular file: " + skillMdPath);
        }
    }

    private ParsedSkillMd parseContent(String content) {
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException("SKILL.md content must not be blank");
        }

        String normalized = content.replace("\r\n", "\n");

        if(!normalized.startsWith(FRONTMATTER_DELIMITER + "\n")){
            throw new IllegalArgumentException("SKILL.md must start with YAML frontmatter delimiter: ---");
        }

        int endIndex = normalized.indexOf("\n" + FRONTMATTER_DELIMITER + "\n", 4);
        if(endIndex < 0){
            throw new IllegalArgumentException("SKILL.md frontmatter closing delimiter not found");
        }

        String frontmatter = normalized.substring(4, endIndex).trim();
        String instruction = normalized.substring(endIndex + 5).trim();

        return new ParsedSkillMd(frontmatter, instruction);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseFrontMatter(String frontmatter) {
        Object loaded = yaml.load(frontmatter);

        if(loaded == null){
            return Map.of();
        }
        if (!(loaded instanceof Map)) {
            throw new IllegalArgumentException("SKILL.md frontmatter must be a YAML object");
        }
        return (Map<String, Object>) loaded;
    }

    private String getRequiredString(Map<String, Object> source, String key) {
        Object value = source.get(key);

        if(!(value instanceof String stringValue) || stringValue.isBlank()){
            throw new IllegalArgumentException("Required frontmatter field is missing or blank: " + key); 
        }

        return stringValue;
    }
    
    private String getOptionalString(Map<String, Object> source, String key, String defaultValue) {
        Object value = source.get(key);
        if(value == null){
            return defaultValue;
        }
        
        if(!(value instanceof String stringValue)){
            throw new IllegalArgumentException("Frontmatter field must be string: " + key);
        }

        return stringValue;
    }


    private List<String> getStringList(Map<String, Object> source, String key) {
        Object value = source.get(key);

        if(value == null) {
            return List.of();
        }

        if(!(value instanceof List<?> list)){
            throw new IllegalArgumentException("Frontmatter field must be list: " + key);
        }

        return list.stream()
            .map(item -> {
                if(!(item instanceof String stringItem)){
                    throw new IllegalArgumentException("Frontmatter list item must be string: " + key);
                }
                return stringItem;
            })
            .filter(item -> !item.isBlank())
            .toList();
    }
    
}
