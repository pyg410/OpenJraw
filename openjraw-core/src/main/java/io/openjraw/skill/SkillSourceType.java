package io.openjraw.skill;

/**
 * Skill이 로딩된 출처 유형입니다.
 * @author ygPark
 * @version 0.0.1
 * @since 2026-05-02
 */
public enum SkillSourceType {
    /** OpenJraw 내부에 기본 포함된 Skill */
    BUILTIN,
    /** 사용자가 로컬 디렉토리에 직접 설치한 Skill */
    LOCAL,
    /** OpenClaw 호환 Skill */
    OPENCLAW,
    /** OpenJraw 전용 포맷 Skill */
    OPENJRAW,
    /** 외부 Plugin 패키지에 포함된 Skill */
    PLUGIN,
    /** 원격 저장소/API를 통해 동적으로 로딩된 Skill */
    REMOTE
}
