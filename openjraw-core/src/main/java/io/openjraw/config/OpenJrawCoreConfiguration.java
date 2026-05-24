package io.openjraw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.openjraw.agent.AgentEngine;
import io.openjraw.agent.AgentModel;
import io.openjraw.agent.DefaultAgentEngine;
import io.openjraw.prompt.DefaultPromptComposer;
import io.openjraw.prompt.PromptComposer;
import io.openjraw.skill.InMemorySkillRegistry;
import io.openjraw.skill.KeywordSkillRouter;
import io.openjraw.skill.SkillRegistry;
import io.openjraw.skill.SkillRouter;

@Configuration
public class OpenJrawCoreConfiguration {

    @Bean
    public SkillRegistry skillRegistry() {
        return new InMemorySkillRegistry();
    }

    @Bean
    public SkillRouter skillRouter() {
        return new KeywordSkillRouter();
    }

    @Bean
    public PromptComposer promptComposer() {
        return new DefaultPromptComposer();
    }

    @Bean
    public AgentEngine agentEngine(
        SkillRegistry skillRegistry, 
        SkillRouter skillRouter, 
        PromptComposer promptComposer,
        AgentModel agentModel
    ) {
        return new DefaultAgentEngine(skillRegistry, skillRouter, promptComposer, agentModel);
    }
    
}
