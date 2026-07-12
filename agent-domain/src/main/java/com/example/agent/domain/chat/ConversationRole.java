package com.example.agent.domain.chat;

/**
 * 会话消息角色，用于区分用户、AI 助手和系统消息。
 */
public enum ConversationRole {

    /**
     * 用户消息。
     */
    USER,

    /**
     * AI 助手消息。
     */
    ASSISTANT,

    /**
     * 系统消息。
     */
    SYSTEM
}
