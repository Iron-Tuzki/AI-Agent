package com.example.agent.domain.chat;

/**
 * 消息状态，用于标识消息生成和保存结果。
 */
public enum MessageStatus {

    /**
     * 消息已成功生成并保存。
     */
    SUCCESS,

    /**
     * 消息生成或保存失败。
     */
    FAILED,

    /**
     * 消息正在流式生成中。
     */
    STREAMING
}
