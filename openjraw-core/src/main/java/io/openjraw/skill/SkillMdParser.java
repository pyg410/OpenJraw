package io.openjraw.skill;

import java.nio.file.Path;

/**
 * SKILL.md 파일을 SkillDefinition으로 변환하는 Parser입니다.
 * 
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-02
 * @see SkillDefinition
 */
public interface SkillMdParser {
    
    /**
     * SKILL.md 파일 파싱하여 SkillDefinition을 생성합니다.
     * 
     * @param skillMdPath SKILL.md 파일의 경로
     * @return 파싱된 SkillDefinition
     */
    SkillDefinition parse(Path skillMdPath);
}
