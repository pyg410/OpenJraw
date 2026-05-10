package io.openjraw.agent;

/**
 * OpenJraw Agent 실행 엔진 인터페이스
 * 
 * <p> 사용자 요청을 받아 Skill 선택, Prompt 조립, 응답 생성을 담당한다.
 * 
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-11
 */
public interface AgentEngine {
    
    /**
     * 사용자 요청을 실행한다
     * 
     * @param request Agent 요청
     * @return Agent 실행 결과
     */
    AgentResponse run(AgentRequest request);
}
