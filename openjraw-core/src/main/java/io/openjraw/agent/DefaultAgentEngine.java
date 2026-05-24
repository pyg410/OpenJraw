package io.openjraw.agent;

import java.util.List;

import io.openjraw.prompt.ComposedPrompt;
import io.openjraw.prompt.PromptComposer;
import io.openjraw.skill.SkillDefinition;
import io.openjraw.skill.SkillRegistry;
import io.openjraw.skill.SkillRouter;

/**
 * 기본 AgentEngine 구현체
 * 
 * <p> 현재 MVP 단계에서는 실제 LLM 호출 없이 다음 작업만 수행한다.
 * 
 * <ol>
 *  <li>SkillRegistry에서 전체 Skill 조회</li>
 *  <li>SkillRouter로 사용자 요청에 맞는 Skill 선택</li>
 *  <li>PromptComposer로 system/user prompt 생성</li>
 *  <li>선택된 Skill과 Prompt 정보를 AgentResponse로 반환</li>
 * </ol>
 * 
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-11
 */
public class DefaultAgentEngine implements AgentEngine {

    private final SkillRegistry skillRegistry;
    private final SkillRouter skillRouter;
    private final PromptComposer promptComposer;
    private final AgentModel agentModel;

    public DefaultAgentEngine(
        SkillRegistry skillRegistry, 
        SkillRouter skillRouter, 
        PromptComposer promptComposer,
        AgentModel agentModel
    ) {

        if(skillRegistry == null) {
            throw new IllegalArgumentException("SkillRegistry must not be null");
        }
        if(skillRouter == null) {
            throw new IllegalArgumentException("SkillRouter must not be null");
        }
        if(promptComposer == null) {
            throw new IllegalArgumentException("PromptComposer must not be null");
        }
        if(agentModel == null) {
            throw new IllegalArgumentException("AgentModel must not be null");
        }

        this.skillRegistry = skillRegistry;
        this.skillRouter = skillRouter;
        this.promptComposer = promptComposer;
        this.agentModel = agentModel;
    }

    @Override
    public AgentResponse run(AgentRequest request) {
        if(request == null) {
            throw new IllegalArgumentException("AgentRequest must not be null");
        }

        List<SkillDefinition> allSkills = skillRegistry.findAll();

        List<SkillDefinition> selectedSkills = skillRouter.route(
            request.userMessage(),
            allSkills
        );

        ComposedPrompt composedPrompt = promptComposer.compose(
            request.userMessage(),
            selectedSkills
        );

        String aiResponse = agentModel.generate(composedPrompt);

        return new AgentResponse(
            aiResponse, 
            composedPrompt, 
            composedPrompt.usedSkillIds()
        );
    }
    
}
