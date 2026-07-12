package com.example.agent.api.chat;

import com.example.agent.api.common.GlobalExceptionHandler;
import com.example.agent.domain.chat.ConversationMessage;
import com.example.agent.domain.chat.ConversationRole;
import com.example.agent.domain.chat.ConversationService;
import com.example.agent.domain.chat.MessageStatus;
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

@WebMvcTest(ConversationController.class)
@ContextConfiguration(classes = {ConversationController.class, GlobalExceptionHandler.class})
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationService conversationService;

    @Test
    void shouldReturnConversationMessages() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        when(conversationService.listMessages("c001")).thenReturn(List.of(
                new ConversationMessage(
                        "m001",
                        "c001",
                        ConversationRole.USER,
                        "你好",
                        MessageStatus.SUCCESS,
                        AiProvider.SPRING_AI,
                        null,
                        null,
                        now,
                        now
                )
        ));

        mockMvc.perform(get("/api/conversations/c001/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("m001"))
                .andExpect(jsonPath("$.data[0].conversationId").value("c001"))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[0].content").value("你好"));
    }
}
