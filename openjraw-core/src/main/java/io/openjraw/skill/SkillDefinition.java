package io.openjraw.skill;

import java.util.List;
import java.util.Map;

/**
 * OpenJraw Skill의 메타 정보와 실행 지침을 표현하는 불변 객체입니다.
 * 
 * <p> Skill은 OpenJraw Agent가 특정 작업을 더 잘 수행하도록 돕는 행동지침(Instruction) 단위입니다.
 * 
 * <p> 예를들어,
 * <ul>
 *  <li> Bank Log Analyzer</li>
 *  <li> Email Writer</li>
 *  <li> SQL Performance Checker</li>
 * </ul>
 * 
 * <p> SkillDefinition은 실제 실행 코드가 아니라,
 * Skill이 무엇인지, 언제 사용하는지, 어떤 지침을 주입할지에 대한 메타데이터를 담습니다.
 * @param id Skill 고유식별자(중복불가)
 * @param name 사용자에게 표시되는 Skill 이름
 * @param description Skill에 대한 설명
 * @param instruction LLM Prmpt에 주입되는 실제 행동 지침
 * @param triggers 사용자 입력과 매칭할 키워드 목록
 * @param tags 분류/검색용 태그 목록
 * @param source Skill 출처 정보
 * @param metadata 확장용 추가 메타 데이터
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-02
 */
public record SkillDefinition(
    String id,
    String name,
    String description,
    String instruction,
    List<String> triggers,
    List<String> tags,
    SkillSource source,
    Map<String, Object> metadata
) {
    public SkillDefinition {
        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("Skill id must not be blank");
        }
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Skill name must not be blank");
        }
        if(instruction == null || instruction.isBlank()){
            throw new IllegalArgumentException("Skill instruction must not be blank");
        }

        description = description == null ? "" : description;
        triggers = triggers == null ? List.of() : List.copyOf(triggers);
        tags = tags == null ? List.of() : List.copyOf(tags);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean hasTrigger(String text){
        if(text == null || text.isBlank()){
            return false;
        }

        String normalizedText = text.toLowerCase();

        return triggers.stream()
            .map(String::toLowerCase)
            .anyMatch(normalizedText::contains);
    }
    
}
