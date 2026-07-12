package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;

import java.time.LocalDateTime;

/**
 * 对话会话领域对象，用于描述一次连续 AI 对话的元信息。
 *
 * @param id 会话编号
 * @param title 会话标题
 * @param provider AI 框架提供方
 * @param status 会话状态
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record Conversation(
        String id,
        String title,
        AiProvider provider,
        ConversationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
