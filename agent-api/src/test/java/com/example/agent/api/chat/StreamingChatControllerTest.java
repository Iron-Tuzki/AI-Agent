package com.example.agent.api.chat;

import com.example.agent.api.common.GlobalExceptionHandler;
import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.chat.ConversationService;
import com.example.agent.domain.chat.MessageStatus;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 流式聊天接口测试，验证会话事件、回复内容和持久化触发行为。
 */
@WebMvcTest(AiChatController.class)
@ContextConfiguration(classes = {AiChatController.class, GlobalExceptionHandler.class})
class StreamingChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiChatService chatService;

    @MockitoBean
    private ConversationService conversationService;

    @Test
    void shouldStreamConversationEventAndPersistCompletedResponse() throws Exception {
        when(chatService.supports(AiProvider.SPRING_AI)).thenReturn(true);
        when(conversationService.ensureConversation(any())).thenReturn("c001");
        when(chatService.stream(any())).thenReturn(Flux.just("Hello", ", Java Agent"));

        mockMvc.perform(post("/api/ai/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content("""
                                {
                                  "provider": "SPRING_AI",
                                  "message": "What is Java Agent?"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("event:conversation")))
                .andExpect(content().string(containsString("c001")))
                .andExpect(content().string(containsString("Hello")))
                .andExpect(content().string(containsString("Java Agent")));

        verify(conversationService).saveUserMessage(any());
        verify(conversationService).saveAssistantMessage(any(), any(), eq(MessageStatus.SUCCESS));
    }

    @Test
    void shouldPersistFailedAssistantResponseWhenModelErrors() {
        when(chatService.supports(AiProvider.SPRING_AI)).thenReturn(true);
        when(conversationService.ensureConversation(any())).thenReturn("c001");
        when(chatService.stream(any())).thenReturn(
                Flux.just("partial").concatWith(Flux.error(new IllegalStateException("model failed")))
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> controller().stream(new AiChatRequest(AiProvider.SPRING_AI, null, "What is Java Agent?")).blockLast());

        org.junit.jupiter.api.Assertions.assertEquals("model failed", exception.getMessage());

        verify(conversationService).saveUserMessage(any());
        verify(conversationService).saveAssistantMessage(
                any(),
                argThat(result -> result instanceof AiChatResult && "partial".equals(((AiChatResult) result).content())),
                eq(MessageStatus.FAILED)
        );
    }

    @Test
    void shouldPersistFailedAssistantResponseWhenClientCancels() {
        when(chatService.supports(AiProvider.SPRING_AI)).thenReturn(true);
        when(conversationService.ensureConversation(any())).thenReturn("c001");
        when(chatService.stream(any())).thenReturn(Flux.just("partial").concatWith(Flux.never()));

        controller().stream(new AiChatRequest(AiProvider.SPRING_AI, null, "What is Java Agent?")).take(2).blockLast();

        verify(conversationService).saveUserMessage(any());
        verify(conversationService).saveAssistantMessage(
                any(),
                argThat(result -> result instanceof AiChatResult && "partial".equals(((AiChatResult) result).content())),
                eq(MessageStatus.FAILED)
        );
    }

    private AiChatController controller() {
        return new AiChatController(List.of(chatService), conversationService);
    }
}
