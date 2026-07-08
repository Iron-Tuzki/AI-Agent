package com.example.agent.springai;

import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringAiChatServiceTest {

    @Test
    void shouldOnlySupportSpringAiProvider() {
        ChatClient chatClient = mock(ChatClient.class);
        SpringAiChatService service = new SpringAiChatService(chatClient);

        assertTrue(service.supports(AiProvider.SPRING_AI));
        assertFalse(service.supports(AiProvider.LANGCHAIN4J));
    }

    @Test
    void shouldReturnChatResultFromChatClient() {
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);
        SpringAiChatService service = new SpringAiChatService(chatClient);
        AiChatRequest request = new AiChatRequest(AiProvider.SPRING_AI, "c001", "什么是 Java Agent？");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("什么是 Java Agent？")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("Java Agent 是具备工具调用和任务规划能力的 AI 应用。");

        AiChatResult result = service.chat(request);

        assertEquals("c001", result.conversationId());
        assertEquals(AiProvider.SPRING_AI, result.provider());
        assertEquals("Java Agent 是具备工具调用和任务规划能力的 AI 应用。", result.content());
    }
}
