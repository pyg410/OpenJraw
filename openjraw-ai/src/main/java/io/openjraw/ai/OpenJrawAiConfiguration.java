

@Configuration
public class OpenJrawAiConfiguration {
    @Bean
    public AgentModel agentModel(ChatClient.Builder chatClientBuilder) {
        return new SpringAiAgentModel(chatClientBuilder);
    }    
}
