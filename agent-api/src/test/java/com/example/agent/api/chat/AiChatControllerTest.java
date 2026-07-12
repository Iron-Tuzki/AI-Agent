package com.example.agent.api.chat;

import com.example.agent.api.common.GlobalExceptionHandler;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.chat.ConversationService;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiChatController.class)
@ContextConfiguration(classes = {AiChatController.class, GlobalExceptionHandler.class})
class AiChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiChatService chatService;

    @MockitoBean
    private ConversationService conversationService;

    @Test
    void shouldReturnChatResultWhenProviderIsSupported() throws Exception {
        when(chatService.supports(AiProvider.SPRING_AI)).thenReturn(true);
        when(chatService.chat(any())).thenReturn(
                new AiChatResult("c001", AiProvider.SPRING_AI, "Java Agent 是工程化的 AI 应用。", 10, 20)
        );
        when(conversationService.saveTurn(any(), any())).thenReturn("c001");

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "SPRING_AI",
                                  "conversationId": "c001",
                                  "message": "什么是 Java Agent？"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.conversationId").value("c001"))
                .andExpect(jsonPath("$.data.provider").value("SPRING_AI"))
                .andExpect(jsonPath("$.data.content").value("Java Agent 是工程化的 AI 应用。"));

        verify(conversationService).saveTurn(any(), any());
    }

    @Test
    void shouldReturnBadRequestWhenMessageIsBlank() throws Exception {
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "SPRING_AI",
                                  "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequestWhenProviderIsUnsupported() throws Exception {
        when(chatService.supports(AiProvider.LANGCHAIN4J)).thenReturn(false);

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "LANGCHAIN4J",
                                  "message": "解释 Tool Calling"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message", containsString("不支持的 AI 提供方")));
    }
}
