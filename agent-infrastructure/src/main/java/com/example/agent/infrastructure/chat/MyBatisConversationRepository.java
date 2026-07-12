package com.example.agent.infrastructure.chat;

import com.example.agent.domain.chat.Conversation;
import com.example.agent.domain.chat.ConversationRepository;
import com.example.agent.domain.chat.ConversationStatus;
import com.example.agent.domain.provider.AiProvider;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * MyBatis-Plus 会话仓储实现，用于将会话领域对象保存到数据库。
 */
@Repository
public class MyBatisConversationRepository implements ConversationRepository {

    private final ConversationMapper conversationMapper;

    public MyBatisConversationRepository(ConversationMapper conversationMapper) {
        this.conversationMapper = conversationMapper;
    }

    @Override
    public Optional<Conversation> findById(String conversationId) {
        return Optional.ofNullable(conversationMapper.selectById(conversationId))
                .map(this::toDomain);
    }

    @Override
    public void save(Conversation conversation) {
        conversationMapper.insert(toEntity(conversation));
    }

    @Override
    public void updateTime(String conversationId, LocalDateTime updatedAt) {
        ConversationEntity entity = new ConversationEntity();
        entity.setId(conversationId);
        entity.setUpdatedAt(updatedAt);
        conversationMapper.updateById(entity);
    }

    @Override
    public List<Conversation> findAllOrderByUpdatedAtDesc() {
        return conversationMapper.selectList(new QueryWrapper<ConversationEntity>()
                        .orderByDesc("updated_at"))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Conversation toDomain(ConversationEntity entity) {
        return new Conversation(
                entity.getId(),
                entity.getTitle(),
                AiProvider.valueOf(entity.getProvider()),
                ConversationStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ConversationEntity toEntity(Conversation conversation) {
        ConversationEntity entity = new ConversationEntity();
        entity.setId(conversation.id());
        entity.setTitle(conversation.title());
        entity.setProvider(conversation.provider().name());
        entity.setStatus(conversation.status().name());
        entity.setCreatedAt(conversation.createdAt());
        entity.setUpdatedAt(conversation.updatedAt());
        return entity;
    }
}
