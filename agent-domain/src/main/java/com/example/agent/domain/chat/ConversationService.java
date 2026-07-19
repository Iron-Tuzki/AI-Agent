package com.example.agent.domain.chat;

import java.util.List;

/**
 * 会话服务接口，用于保存用户消息和模型回复，支撑多轮对话与后续记忆能力。
 */
public interface ConversationService {

    /**
     * 确保聊天会话存在，为流式响应提前准备会话编号。
     *
     * @param request AI 对话请求，包含 Provider、可选会话编号和用户消息
     * @return 已存在或新创建的会话编号
     */
    String ensureConversation(AiChatRequest request);

    /**
     * 保存一轮用户输入和 AI 回复。
     *
     * @param request 用户对话请求
     * @param result AI 回复结果
     * @return 会话编号
     */
    String saveTurn(AiChatRequest request, AiChatResult result);

    /**
     * 保存流式对话中的用户消息。
     *
     * @param request AI 对话请求，包含会话编号、提供方和用户消息
     */
    void saveUserMessage(AiChatRequest request);

    /**
     * 保存流式对话中的助手消息。
     *
     * @param request AI 对话请求，包含会话编号和提供方
     * @param result AI 回复结果，包含已经生成的回复内容
     * @param status 助手消息状态，正常完成时为 SUCCESS，异常或中断时为 FAILED
     */
    void saveAssistantMessage(AiChatRequest request, AiChatResult result, MessageStatus status);

    /**
     * 查询指定会话下的消息列表。
     *
     * @param conversationId 会话编号
     * @return 按创建时间正序排列的消息列表
     */
    List<ConversationMessage> listMessages(String conversationId);

    /**
     * 查询最近活跃的会话列表。
     *
     * @return 按更新时间倒序排列的会话列表
     */
    List<Conversation> listConversations();
}
