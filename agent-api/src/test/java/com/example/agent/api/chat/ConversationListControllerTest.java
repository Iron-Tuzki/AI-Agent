package com.example.agent.api.chat;

import com.example.agent.api.common.GlobalExceptionHandler;
import com.example.agent.domain.chat.Conversation;
import com.example.agent.domain.chat.ConversationService;
import com.example.agent.domain.chat.ConversationStatus;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 会话列表接口测试，验证工作台历史会话查询能力。
 */
@WebMvcTest(ConversationController.class)
@ContextConfiguration(classes = {ConversationController.class, GlobalExceptionHandler.class})
class ConversationListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationService conversationService;

    @Test
    void shouldReturnConversationsOrderedByUpdatedAt() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        when(conversationService.listConversations()).thenReturn(List.of(
                new Conversation("c002", "最新会话", AiProvider.SPRING_AI, ConversationStatus.ACTIVE, now, now),
                new Conversation("c001", "较早会话", AiProvider.LANGCHAIN4J, ConversationStatus.ACTIVE,
                        now.minusHours(1), now.minusHours(1))
        ));

        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("c002"))
                .andExpect(jsonPath("$.data[1].id").value("c001"));
    }
}
