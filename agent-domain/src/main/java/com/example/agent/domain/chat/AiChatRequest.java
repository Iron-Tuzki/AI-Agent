package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI 对话请求，封装用户消息、会话编号和目标 AI 框架提供方。
 *
 * @param provider AI 框架提供方，用于选择 Spring AI 或 LangChain4j 等具体实现
 * @param conversationId 会话编号，允许为空；为空时后续服务可以创建新会话
 * @param message 用户输入的对话内容
 */
public record AiChatRequest(
        @NotNull AiProvider provider,
        String conversationId,
        @NotBlank String message
) {
}
