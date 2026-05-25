package io.openjraw.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.openjraw.agent.AgentModel;

@Configuration
public class OpenJrawAiConfiguration {
    @Bean
    public AgentModel agentModel(ChatClient.Builder chatClientBuilder) {
        return new SpringAiAgentModel(chatClientBuilder);
    }    
}
