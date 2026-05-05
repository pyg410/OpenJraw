package io.openjraw.skill;

public record ScoredSkill(
    SkillDefinition skill,
    int score
) {
    
}
