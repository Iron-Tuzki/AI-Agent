package com.example.agent.api.chat;

import com.example.agent.api.common.ApiResponse;
import com.example.agent.domain.chat.ConversationMessage;
import com.example.agent.domain.chat.Conversation;
import com.example.agent.domain.chat.ConversationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会话接口，负责提供会话消息查询等对话上下文相关能力。
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * 查询最近活跃的会话列表。
     *
     * @return 按最近更新时间倒序排列的会话列表
     */
    @GetMapping
    public ApiResponse<List<Conversation>> listConversations() {
        return ApiResponse.ok(conversationService.listConversations());
    }

    /**
     * 查询指定会话下的消息列表。
     *
     * @param conversationId 会话编号
     * @return 会话消息列表
     */
    @GetMapping("/{conversationId}/messages")
    public ApiResponse<List<ConversationMessage>> listMessages(@PathVariable("conversationId") String conversationId) {
        return ApiResponse.ok(conversationService.listMessages(conversationId));
    }
}
