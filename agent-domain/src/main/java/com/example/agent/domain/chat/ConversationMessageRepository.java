package com.example.agent.domain.chat;

import java.util.List;

/**
 * 会话消息仓储接口，用于屏蔽消息明细的具体持久化实现。
 */
public interface ConversationMessageRepository {

    /**
     * 保存会话消息。
     *
     * @param message 会话消息
     */
    void save(ConversationMessage message);

    /**
     * 查询指定会话下的消息列表。
     *
     * @param conversationId 会话编号
     * @return 按创建时间正序排列的消息列表
     */
    List<ConversationMessage> findByConversationId(String conversationId);
}
