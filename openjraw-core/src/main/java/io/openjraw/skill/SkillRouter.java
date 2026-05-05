package io.openjraw.skill;

import java.util.List;

/**
 * 사용자 요청에 적합한 Skill을 선택하는 Router입니다.
 * 
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-05
 */
public interface SkillRouter {
    
    /**
     * 사용자 메세지를 기준으로 사용할 Skill 목록을 선택합니다.
     * @param userMessage 사용자 입력
     * @param skills 후보 Skill 목록
     * @return 선택된 Skill 목록
     */
    List<SkillDefinition> route(String userMessage, List<SkillDefinition> skills);
    
}
