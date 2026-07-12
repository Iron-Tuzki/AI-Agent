package com.example.agent.infrastructure.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话消息 Mapper，用于执行 agent_message 表的基础 CRUD 操作。
 */
@Mapper
public interface ConversationMessageMapper extends BaseMapper<ConversationMessageEntity> {
}
