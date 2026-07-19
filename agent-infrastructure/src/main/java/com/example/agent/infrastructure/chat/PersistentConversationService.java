package com.example.agent.infrastructure.chat;

import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.ConversationMessage;
import com.example.agent.domain.chat.Conversation;
import com.example.agent.domain.chat.ConversationMessageRepository;
import com.example.agent.domain.chat.ConversationRepository;
import com.example.agent.domain.chat.ConversationService;
import com.example.agent.domain.chat.DefaultConversationService;
import com.example.agent.domain.chat.MessageStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 持久化会话服务，负责为领域会话服务增加 Spring Bean 注册和事务边界。
 */
@Service
public class PersistentConversationService implements ConversationService {

    private final DefaultConversationService delegate;

    public PersistentConversationService(ConversationRepository conversationRepository,
                                         ConversationMessageRepository messageRepository) {
        this.delegate = new DefaultConversationService(conversationRepository, messageRepository);
    }

    @Override
    @Transactional
    public String ensureConversation(AiChatRequest request) {
        return delegate.ensureConversation(request);
    }

    @Override
    @Transactional
    public String saveTurn(AiChatRequest request, AiChatResult result) {
        return delegate.saveTurn(request, result);
    }

    @Override
    @Transactional
    public void saveUserMessage(AiChatRequest request) {
        delegate.saveUserMessage(request);
    }

    @Override
    @Transactional
    public void saveAssistantMessage(AiChatRequest request, AiChatResult result, MessageStatus status) {
        delegate.saveAssistantMessage(request, result, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationMessage> listMessages(String conversationId) {
        return delegate.listMessages(conversationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conversation> listConversations() {
        return delegate.listConversations();
    }
}
