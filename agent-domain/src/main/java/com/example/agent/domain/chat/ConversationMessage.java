package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;

import java.time.LocalDateTime;

/**
 * 对话消息领域对象，用于保存用户输入、系统消息和 AI 回复内容。
 *
 * @param id 消息编号
 * @param conversationId 会话编号
 * @param role 消息角色
 * @param content 消息内容
 * @param status 消息状态
 * @param provider AI 框架提供方
 * @param promptTokens 输入提示词消耗的 Token 数量，暂未统计时允许为空
 * @param completionTokens 输出回复消耗的 Token 数量，暂未统计时允许为空
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record ConversationMessage(
        String id,
        String conversationId,
        ConversationRole role,
        String content,
        MessageStatus status,
        AiProvider provider,
        Integer promptTokens,
        Integer completionTokens,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
