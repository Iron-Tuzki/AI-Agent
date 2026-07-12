package com.example.agent.infrastructure.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.agent.domain.chat.ConversationMessage;
import com.example.agent.domain.chat.ConversationMessageRepository;
import com.example.agent.domain.chat.ConversationRole;
import com.example.agent.domain.chat.MessageStatus;
import com.example.agent.domain.provider.AiProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MyBatis-Plus 会话消息仓储实现，用于将消息明细保存到数据库。
 */
@Repository
public class MyBatisConversationMessageRepository implements ConversationMessageRepository {

    private final ConversationMessageMapper messageMapper;

    public MyBatisConversationMessageRepository(ConversationMessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public void save(ConversationMessage message) {
        messageMapper.insert(toEntity(message));
    }

    @Override
    public List<ConversationMessage> findByConversationId(String conversationId) {
        LambdaQueryWrapper<ConversationMessageEntity> wrapper = new LambdaQueryWrapper<ConversationMessageEntity>()
                .eq(ConversationMessageEntity::getConversationId, conversationId)
                .orderByAsc(ConversationMessageEntity::getCreatedAt);
        return messageMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    private ConversationMessage toDomain(ConversationMessageEntity entity) {
        return new ConversationMessage(
                entity.getId(),
                entity.getConversationId(),
                ConversationRole.valueOf(entity.getRole()),
                entity.getContent(),
                MessageStatus.valueOf(entity.getStatus()),
                AiProvider.valueOf(entity.getProvider()),
                entity.getPromptTokens(),
                entity.getCompletionTokens(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ConversationMessageEntity toEntity(ConversationMessage message) {
        ConversationMessageEntity entity = new ConversationMessageEntity();
        entity.setId(message.id());
        entity.setConversationId(message.conversationId());
        entity.setRole(message.role().name());
        entity.setContent(message.content());
        entity.setStatus(message.status().name());
        entity.setProvider(message.provider().name());
        entity.setPromptTokens(message.promptTokens());
        entity.setCompletionTokens(message.completionTokens());
        entity.setCreatedAt(message.createdAt());
        entity.setUpdatedAt(message.updatedAt());
        return entity;
    }
}
