package com.example.agent.infrastructure.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话 Mapper，用于执行 agent_conversation 表的基础 CRUD 操作。
 */
@Mapper
public interface ConversationMapper extends BaseMapper<ConversationEntity> {
}
