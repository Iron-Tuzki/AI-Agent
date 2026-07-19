package com.example.agent.domain.chat;

import com.example.agent.domain.exception.AgentBusinessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 默认会话服务实现，负责编排会话创建、消息保存和消息查询的领域规则。
 */
public class DefaultConversationService implements ConversationService {

    private static final int TITLE_MAX_LENGTH = 30;

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;

    public DefaultConversationService(ConversationRepository conversationRepository,
                                      ConversationMessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public String ensureConversation(AiChatRequest request) {
        return resolveConversationId(request, LocalDateTime.now());
    }

    @Override
    public String saveTurn(AiChatRequest request, AiChatResult result) {
        LocalDateTime now = LocalDateTime.now();
        String conversationId = resolveConversationId(request, now);
        messageRepository.save(new ConversationMessage(
                nextId(),
                conversationId,
                ConversationRole.USER,
                request.message(),
                MessageStatus.SUCCESS,
                request.provider(),
                null,
                null,
                now,
                now
        ));
        messageRepository.save(new ConversationMessage(
                nextId(),
                conversationId,
                ConversationRole.ASSISTANT,
                result.content(),
                MessageStatus.SUCCESS,
                result.provider(),
                result.promptTokens(),
                result.completionTokens(),
                now,
                now
        ));
        conversationRepository.updateTime(conversationId, now);
        return conversationId;
    }

    @Override
    public void saveUserMessage(AiChatRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String conversationId = resolveConversationId(request, now);
        messageRepository.save(new ConversationMessage(
                nextId(),
                conversationId,
                ConversationRole.USER,
                request.message(),
                MessageStatus.SUCCESS,
                request.provider(),
                null,
                null,
                now,
                now
        ));
        conversationRepository.updateTime(conversationId, now);
    }

    @Override
    public void saveAssistantMessage(AiChatRequest request, AiChatResult result, MessageStatus status) {
        LocalDateTime now = LocalDateTime.now();
        String conversationId = resolveConversationId(request, now);
        messageRepository.save(new ConversationMessage(
                nextId(),
                conversationId,
                ConversationRole.ASSISTANT,
                result.content(),
                status,
                result.provider(),
                result.promptTokens(),
                result.completionTokens(),
                now,
                now
        ));
        conversationRepository.updateTime(conversationId, now);
    }

    @Override
    public List<ConversationMessage> listMessages(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            throw new AgentBusinessException("会话编号不能为空");
        }
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AgentBusinessException("会话不存在：" + conversationId));
        return messageRepository.findByConversationId(conversationId);
    }

    @Override
    public List<Conversation> listConversations() {
        return conversationRepository.findAllOrderByUpdatedAtDesc();
    }

    private String resolveConversationId(AiChatRequest request, LocalDateTime now) {
        if (request.conversationId() == null || request.conversationId().isBlank()) {
            String conversationId = nextId();
            conversationRepository.save(new Conversation(
                    conversationId,
                    buildTitle(request.message()),
                    request.provider(),
                    ConversationStatus.ACTIVE,
                    now,
                    now
            ));
            return conversationId;
        }
        conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new AgentBusinessException("会话不存在：" + request.conversationId()));
        return request.conversationId();
    }

    private String buildTitle(String message) {
        String title = message.strip();
        if (title.length() <= TITLE_MAX_LENGTH) {
            return title;
        }
        return title.substring(0, TITLE_MAX_LENGTH);
    }

    private String nextId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
