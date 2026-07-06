package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;

/**
 * AI 对话结果，封装模型回复、会话编号、提供方和基础用量信息。
 *
 * @param conversationId 会话编号，用于关联本轮 AI 回复所属的对话上下文
 * @param provider 产生本次回复的 AI 框架提供方
 * @param content AI 模型生成的回复内容
 * @param promptTokens 输入提示词消耗的 Token 数量，暂未统计时允许为空
 * @param completionTokens 输出回复消耗的 Token 数量，暂未统计时允许为空
 */
public record AiChatResult(
        String conversationId,
        AiProvider provider,
        String content,
        Integer promptTokens,
        Integer completionTokens
) {
}
