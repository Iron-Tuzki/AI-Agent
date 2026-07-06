package com.example.agent.domain.chat;

/**
 * 会话服务接口，用于保存用户消息和模型回复，支撑多轮对话与后续记忆能力。
 */
public interface ConversationService {

    /**
     * 保存一轮用户输入和 AI 回复。
     *
     * @param request 用户对话请求
     * @param result AI 回复结果
     * @return 会话编号
     */
    String saveTurn(AiChatRequest request, AiChatResult result);
}
