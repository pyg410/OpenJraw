package io.openjraw.skill;

import java.util.List;
import java.util.Optional;

/**
 * OpenJraw Runtime에서 사용 가능한 SkillDefinition들을 관리하는 Registry 입니다.
 * 
 * <p> SkillRegistry는 Skill을 직접 파일에서 읽지 않습니다.
 * 파일 로딩은 LocalSkillLoader가 담당하고, Registry는 이미 생성된 SkillDefinition을 등록/조회/삭제하는 역할만 담당합니다.
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-02
 */
public interface SkillRegistry {
    
    /**
     * SkillDefinition을 Registry에 등록합니다.
     * 
     * @param skill 등록할 SkillDefinition
     * @throws IllegalArgumentException skill이 null이거나 id가 유효하지 않은 경우
     */
    void register(SkillDefinition skill);

    /**
     * 여러 SkillDefinition을 한 번에 등록합니다.
     * 
     * @param skills 등록할 SkillDefinition 목록
     * @throws IllegalArgumentException skills가 null이거나 유효하지 않은 경우
     */
    void registerAll(List<SkillDefinition> skills);

    /**
     * SkillDefinition을 고유 식별자(id)로 조회합니다.
     * 
     * @param id 조회할 SkillDefinition의 고유 식별자(id)
     * @return 조회된 SkillDefinition이 존재하면 Optional에 담아 반환, 없으면 Optional.empty()
     */
    Optional<SkillDefinition> findById(String id);
    
    /** 
     * 현재 등록된 모든 SkillDefinition 목록을 반환합니다.
     * 
     * @return 등록된 모든 SkillDefinition 목록 (빈 목록일 수 있음)
     */
    List<SkillDefinition> findAll();

    /**
     * Skill이 등록되어 있는지 확인합니다.
     * 
     * @param id 확인할 SkillDefinition의 고유 식별자(id)
     * @return 등록되어 있으면 true, 없으면 false
     */
    boolean contains(String id);

    /**
     * SkillDefinition을 고유 식별자(id)로 삭제합니다.
     * 
     * @param id 삭제할 SkillDefinition의 고유 식별자(id)
     * @return 삭제 성공 시 true, 해당 id가 존재하지 않으면 false
     */
    boolean remove(String id);

    /**
     * 모든 SkillDefinition을 삭제합니다.
     */
    void clear();

    /**
     * 현재 등록된 SkillDefinition의 총 개수를 반환합니다.
     * 
     * @return 등록된 SkillDefinition의 총 개수
     */
    int size();
}
