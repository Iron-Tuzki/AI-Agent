package com.example.agent.domain.chat;

import com.example.agent.domain.exception.AgentBusinessException;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultConversationServiceTest {

    @Test
    void shouldCreateConversationAndSaveUserAndAssistantMessagesWhenConversationIdIsBlank() {
        FakeConversationRepository conversationRepository = new FakeConversationRepository();
        FakeConversationMessageRepository messageRepository = new FakeConversationMessageRepository();
        DefaultConversationService service = new DefaultConversationService(conversationRepository, messageRepository);

        String conversationId = service.saveTurn(
                new AiChatRequest(AiProvider.SPRING_AI, null, "什么是 Java Agent？"),
                new AiChatResult(null, AiProvider.SPRING_AI, "Java Agent 是工程化的 AI 应用。", 10, 20)
        );

        assertFalse(conversationId.isBlank());
        assertEquals(1, conversationRepository.conversations.size());
        assertEquals("什么是 Java Agent？", conversationRepository.conversations.get(conversationId).title());
        assertEquals(2, messageRepository.messages.size());
        assertEquals(ConversationRole.USER, messageRepository.messages.get(0).role());
        assertEquals(ConversationRole.ASSISTANT, messageRepository.messages.get(1).role());
        assertEquals(conversationId, messageRepository.messages.get(0).conversationId());
        assertEquals(conversationId, messageRepository.messages.get(1).conversationId());
    }

    @Test
    void shouldAppendMessagesWhenConversationExists() {
        FakeConversationRepository conversationRepository = new FakeConversationRepository();
        LocalDateTime now = LocalDateTime.now();
        conversationRepository.save(new Conversation(
                "c001",
                "已有会话",
                AiProvider.SPRING_AI,
                ConversationStatus.ACTIVE,
                now,
                now
        ));
        FakeConversationMessageRepository messageRepository = new FakeConversationMessageRepository();
        DefaultConversationService service = new DefaultConversationService(conversationRepository, messageRepository);

        String conversationId = service.saveTurn(
                new AiChatRequest(AiProvider.SPRING_AI, "c001", "继续解释"),
                new AiChatResult("c001", AiProvider.SPRING_AI, "好的。", null, null)
        );

        assertEquals("c001", conversationId);
        assertEquals(1, conversationRepository.conversations.size());
        assertEquals(2, messageRepository.messages.size());
    }

    @Test
    void shouldRejectMissingConversationWhenAppendingMessages() {
        DefaultConversationService service = new DefaultConversationService(
                new FakeConversationRepository(),
                new FakeConversationMessageRepository()
        );

        AgentBusinessException exception = assertThrows(AgentBusinessException.class, () -> service.saveTurn(
                new AiChatRequest(AiProvider.SPRING_AI, "missing", "继续解释"),
                new AiChatResult("missing", AiProvider.SPRING_AI, "好的。", null, null)
        ));

        assertEquals("会话不存在：missing", exception.getMessage());
    }

    @Test
    void shouldListMessagesWhenConversationExists() {
        FakeConversationRepository conversationRepository = new FakeConversationRepository();
        LocalDateTime now = LocalDateTime.now();
        conversationRepository.save(new Conversation(
                "c001",
                "已有会话",
                AiProvider.SPRING_AI,
                ConversationStatus.ACTIVE,
                now,
                now
        ));
        FakeConversationMessageRepository messageRepository = new FakeConversationMessageRepository();
        messageRepository.save(new ConversationMessage(
                "m001",
                "c001",
                ConversationRole.USER,
                "你好",
                MessageStatus.SUCCESS,
                AiProvider.SPRING_AI,
                null,
                null,
                now,
                now
        ));
        DefaultConversationService service = new DefaultConversationService(conversationRepository, messageRepository);

        List<ConversationMessage> messages = service.listMessages("c001");

        assertEquals(1, messages.size());
        assertEquals("你好", messages.get(0).content());
    }

    @Test
    void shouldEnsureConversationBeforeStreamingWhenConversationIdIsBlank() {
        FakeConversationRepository conversationRepository = new FakeConversationRepository();
        DefaultConversationService service = new DefaultConversationService(
                conversationRepository,
                new FakeConversationMessageRepository()
        );

        String conversationId = service.ensureConversation(
                new AiChatRequest(AiProvider.SPRING_AI, null, "什么是 Java Agent？")
        );

        assertFalse(conversationId.isBlank());
        assertEquals("什么是 Java Agent？", conversationRepository.conversations.get(conversationId).title());
    }

    @Test
    void shouldReuseExistingConversationWhenEnsuringConversation() {
        FakeConversationRepository conversationRepository = new FakeConversationRepository();
        LocalDateTime now = LocalDateTime.now();
        conversationRepository.save(new Conversation(
                "c001", "已有会话", AiProvider.SPRING_AI, ConversationStatus.ACTIVE, now, now
        ));
        DefaultConversationService service = new DefaultConversationService(
                conversationRepository,
                new FakeConversationMessageRepository()
        );

        assertEquals("c001", service.ensureConversation(
                new AiChatRequest(AiProvider.SPRING_AI, "c001", "继续解释")
        ));
    }

    private static class FakeConversationRepository implements ConversationRepository {

        private final Map<String, Conversation> conversations = new LinkedHashMap<>();

        @Override
        public Optional<Conversation> findById(String conversationId) {
            return Optional.ofNullable(conversations.get(conversationId));
        }

        @Override
        public void save(Conversation conversation) {
            conversations.put(conversation.id(), conversation);
        }

        @Override
        public void updateTime(String conversationId, LocalDateTime updatedAt) {
            Conversation conversation = conversations.get(conversationId);
            conversations.put(conversationId, new Conversation(
                    conversation.id(),
                    conversation.title(),
                    conversation.provider(),
                    conversation.status(),
                    conversation.createdAt(),
                    updatedAt
            ));
        }

        @Override
        public List<Conversation> findAllOrderByUpdatedAtDesc() {
            return conversations.values().stream().toList();
        }
    }

    private static class FakeConversationMessageRepository implements ConversationMessageRepository {

        private final List<ConversationMessage> messages = new ArrayList<>();

        @Override
        public void save(ConversationMessage message) {
            messages.add(message);
        }

        @Override
        public List<ConversationMessage> findByConversationId(String conversationId) {
            return messages.stream()
                    .filter(message -> conversationId.equals(message.conversationId()))
                    .toList();
        }
    }
}
