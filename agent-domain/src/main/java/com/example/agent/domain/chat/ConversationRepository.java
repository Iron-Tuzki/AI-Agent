package com.example.agent.domain.chat;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

/**
 * 会话仓储接口，用于屏蔽会话元信息的具体持久化实现。
 */
public interface ConversationRepository {

    /**
     * 根据会话编号查询会话。
     *
     * @param conversationId 会话编号
     * @return 会话信息；不存在时返回空
     */
    Optional<Conversation> findById(String conversationId);

    /**
     * 保存新会话。
     *
     * @param conversation 会话信息
     */
    void save(Conversation conversation);

    /**
     * 更新会话的最后活跃时间。
     *
     * @param conversationId 会话编号
     * @param updatedAt 更新时间
     */
    void updateTime(String conversationId, LocalDateTime updatedAt);

    /**
     * 按最近更新时间倒序查询会话列表。
     *
     * @return 会话列表
     */
    List<Conversation> findAllOrderByUpdatedAtDesc();
}
