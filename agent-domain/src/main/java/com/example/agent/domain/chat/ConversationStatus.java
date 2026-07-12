package com.example.agent.domain.chat;

/**
 * 会话状态，用于标识会话是否仍可继续使用。
 */
public enum ConversationStatus {

    /**
     * 正常会话。
     */
    ACTIVE,

    /**
     * 已归档会话。
     */
    ARCHIVED
}
