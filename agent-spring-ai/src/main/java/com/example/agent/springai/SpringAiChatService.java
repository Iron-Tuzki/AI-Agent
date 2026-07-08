package com.example.agent.springai;

import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.provider.AiProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Spring AI 对话服务实现，使用 OpenAI 兼容接口接入千问模型。
 */
@Service
public class SpringAiChatService implements AiChatService {

    private final ChatClient chatClient;

    @Autowired
    public SpringAiChatService(ChatClient.Builder builder) {
        this(builder.defaultSystem("你是一个资深 Java Agent 工程师，回答要准确、结构化、贴近生产实践。").build());
    }

    SpringAiChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public boolean supports(AiProvider provider) {
        return provider == AiProvider.SPRING_AI;
    }

    @Override
    public AiChatResult chat(AiChatRequest request) {
        String content = chatClient.prompt()
                .user(request.message())
                .call()
                .content();
        return new AiChatResult(request.conversationId(), AiProvider.SPRING_AI, content, null, null);
    }

    @Override
    public Flux<String> stream(AiChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .stream()
                .content();
    }
}
