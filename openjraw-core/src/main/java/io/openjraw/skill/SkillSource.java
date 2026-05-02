package io.openjraw.skill;

import java.nio.file.Path;

/**
 * Skill이 어디서 로딩되었는지에 대한 출처 정보를 표현합니다.
 * 
 * <p>예시:
 * <ul>
 *  <li> 로컬 skills 폴더에서 읽은 Skill</li>
 *  <li> OpenClaw 호환 Skill </li>
 *  <li> 내장(Built-in) Skill </li>
 *  <li> Plugin 패키지에 포함된 Skill </li>
 * </ul>
 * 
 * <p> 출처 정보는 설치 경로, 업데이트 체크, 삭제 처리, 신뢰도 표시 등에 활용됩니다.
 * 
 * @param type Skill 출처타입
 * @param path path 로컬 파일 시스템 경로 (없을 수 있음)
 * @param origin 원본 저장소 / URL / 공급자 이름
 * @param version Skill 버전 정보
 * 
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-02
 */
public record SkillSource(
    SkillSourceType type,
    Path path,
    String origin,
    String version
) {
    public SkillSource {
        if(type == null){
            throw new IllegalArgumentException("Skill source type must not be null");
        }
    }
} 
