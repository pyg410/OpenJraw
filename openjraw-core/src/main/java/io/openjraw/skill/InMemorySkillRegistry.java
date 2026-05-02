package io.openjraw.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 메모리 기반 SkillRegistry 구현체입니다.
 * 
 * <p> Skill id를 기준으로 SkillDefinition을 저장합니다.
 * ConcurrentHashMap을 사용하여 런타임 중 Skill 재로딩/조회 상황에서도 기본적인 thread-safe 동작을 보장합니다.
 * <p> 이 구현체는 영구 저장소가 아니므로, 애플리케이션이 종료되면 등록된 Skill 정보는 모두 사라집니다.
 * 
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-02
 * @see SkillRegistry
 */
public class InMemorySkillRegistry implements SkillRegistry {

    private final ConcurrentHashMap<String, SkillDefinition> skills = new ConcurrentHashMap<>();

    @Override
    public void register(SkillDefinition skill) {
        validateSkill(skill);
        skills.put(skill.id(), skill);
    }

    @Override
    public void registerAll(List<SkillDefinition> skills) {
        if(skills == null || skills.isEmpty()){
            return ;
        }

        for(SkillDefinition skill : skills){
            register(skill);
        }
    }

    @Override
    public Optional<SkillDefinition> findById(String id) {
        if(id == null || id.isBlank()){
            return Optional.empty();
        }

        return Optional.ofNullable(skills.get(id));
    }

    @Override
    public List<SkillDefinition> findAll() {
        return new ArrayList<>(skills.values());
    }

    @Override
    public boolean contains(String id) {
        if(id == null || id.isBlank()){
            return false;
        }
        return skills.containsKey(id);
    }

    @Override
    public boolean remove(String id) {
        if(id == null || id.isBlank()){
            return false;
        }

        return skills.remove(id) != null;
    }

    @Override
    public void clear() {
        skills.clear();
    }

    @Override
    public int size() {
        return skills.size();
    }

    private void validateSkill(SkillDefinition skill) {
        if(skill == null) {
            throw new IllegalArgumentException("SkillDefinition must not be null");
        }

        if(skill.id() == null || skill.id().isBlank()) {
            throw new IllegalArgumentException("SkillDefinition id must not be blank");
        }
    }
}
