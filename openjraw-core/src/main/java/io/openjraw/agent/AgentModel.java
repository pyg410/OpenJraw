package io.openjraw.agent;

import io.openjraw.prompt.ComposedPrompt;

/**
 * OpenJraw Agent가 실제 AI 모델을 호출하기 위한 인터페이스
 * 
 * <p> core 모듈은 Spring AI, OpenAI, Ollama, vLLM 같은 구현을 알지 못합니다.
 * 실제 구현은 openjraw-ai 모듈에서 제공합니다.
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-25
 */
public interface AgentModel {
    
    /**
     * 조립된 Prompt 기반으로 AI 응답을 생성합니다.
     * @param prompt 조립된 Prompt 객체
     * @return AI 응답 문자열
     */
    String generate(ComposedPrompt prompt);
}
