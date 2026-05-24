
/**
 * Spring AI ChatClient 기반 AgentModel 구현체
 * 
 * @author ygpark
 * @version 0.0.1
 * @since 2026-05-25
 */
public class SpringAiAgentModel implements AgentModel {

    private final ChatClient chatClient;

    public SpringAiAgentModel(ChatClient.Builder chatClientBuilder) {
        if(chatClientBuilder == null) {
            throw new IllegalArgumentException("ChatClient.Builder must not be null");
        }
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generate(ComposedPrompt prompt) {
        if (prompt == null) {
            throw new IllegalArgumentException("ComposedPrompt must not be null");
        }

        return chatClient.prompt()
                .system(prompt.systemPrompt())
                .user(prompt.userPrompt())
                .call()
                .content();
    }
}
