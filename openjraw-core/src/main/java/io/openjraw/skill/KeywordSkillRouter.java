package io.openjraw.skill;

import java.util.Comparator;
import java.util.List;

/**
 * 키워드 기반 SkillRouter 구현체 입니다
 * 
 * <p> 사용자 메세지와 Skill의 triggers, name, description, tags를 비교하여 점수를 계산하고 점수가 높은 Skill을 선택합니다.
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-05
 */
public class KeywordSkillRouter implements SkillRouter{

    private static final int DEFAULT_LIMIT = 3;

    private final int limit;

    public KeywordSkillRouter() {
        this.limit = DEFAULT_LIMIT;   
    }

    public KeywordSkillRouter(int limit) {
        if(limit <= 0) {
            throw new IllegalArgumentException("Router limit must be greater than 0");
        }
        this.limit = limit;
    }

    @Override
    public List<SkillDefinition> route(String userMessage, List<SkillDefinition> skills) {
        
        if(userMessage == null || userMessage.isBlank()) {
            return List.of();
        }

        if(skills == null || skills.isEmpty()) {
            return List.of();
        }

        String normalizedMessage = normalize(userMessage);

        return skills.stream()
            .map(skill -> new ScoredSkill(skill, calculateScore(normalizedMessage, skill)))
            .filter(scoredSkill -> scoredSkill.score() > 0)
            .sorted(Comparator.comparingInt(ScoredSkill::score)
                .reversed()
                .thenComparing(scoredSkill -> scoredSkill.skill().id()) 
            )
            .limit(limit)
            .map(ScoredSkill::skill)
            .toList();
    }

    private int calculateScore(String normalizedMessage, SkillDefinition skill) {
        int score = 0;

        score += scoreExactContains(normalizedMessage, skill.triggers(), 10);
        score += scoreExactContains(normalizedMessage, skill.tags(), 3);

        if(contains(normalizedMessage, skill.name())){
            score += 5;
        }

        if(contains(normalizedMessage, skill.description())){
            score += 2;
        }

        return score;
    }

    private int scoreExactContains(String normalizedMessage, List<String> keywords, int weight) {
        if(keywords == null || keywords.isEmpty()) {
            return 0;
        }

        int score = 0;

        for(String keyword : keywords) {
            if(contains(normalizedMessage, keyword)) {
                score += weight;
            }
        }
        return score;
    }

    private boolean contains(String normalizedMessage, String target) {
        if(target == null || target.isBlank()) {
            return false;
        }

        return normalizedMessage.contains(normalize(target));
    }

    private String normalize(String value){
        return value == null ? "" : value.trim().toLowerCase();
    }
    
}
