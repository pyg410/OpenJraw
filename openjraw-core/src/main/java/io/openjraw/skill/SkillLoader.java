package io.openjraw.skill;

import java.nio.file.Path;
import java.util.List;

/**
 * 특정 위치나 저장소에서 SkillDefinition을 로딩하는 컴포넌트입니다.
 * 
 * <p> 구현체는 로컬 파일 시스템, 내장 리소스, OpenClaw 호환 패키지, 원격 저장소 등 다양한 출처에서 Skill을 읽어올 수 있습니다.
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-02
 * @see SkillDefinition
 */
public interface SkillLoader {
    
    /**
     * 주어진 경로에서 Skill 목록을 로딩합니다.
     * 
     * @param rootPath Skill이 위치한 루트 경로입니다.
     * @return 로딩된 Skill 목록입니다.
     */
    List<SkillDefinition> load(Path rootPath);
}
