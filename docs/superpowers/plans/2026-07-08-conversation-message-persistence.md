# Conversation Message Persistence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build conversation and message persistence for ordinary AI chat.

**Architecture:** Keep model calls in provider services, add domain-level conversation persistence orchestration, and implement storage in `agent-infrastructure` with MyBatis-Plus. HTTP controllers orchestrate provider selection and persistence.

**Tech Stack:** Java 21, Spring Boot 3.5.0, Spring AI 1.0.3, MyBatis-Plus 3.5.12, JUnit 5, MockMvc.

## Global Constraints

1. Java classes and interfaces need Chinese JavaDoc.
2. Do not remove existing user comments unless the explained logic changed.
3. Do not commit code.
4. Modify the current branch directly.

---

### Task 1: API Chat Persistence Behavior

**Files:**
- Modify: `agent-api/src/test/java/com/example/agent/api/chat/AiChatControllerTest.java`
- Modify: `agent-api/src/main/java/com/example/agent/api/chat/AiChatController.java`

**Interfaces:**
- Consumes: `ConversationService.saveTurn(AiChatRequest request, AiChatResult result)`
- Produces: `/api/ai/chat` returns result with persisted conversation id.

- [ ] Write failing controller test verifying `ConversationService.saveTurn` is called and returned id appears in response.
- [ ] Run API test and confirm it fails because the controller does not use `ConversationService`.
- [ ] Inject `ConversationService` and wrap chat result with the saved conversation id.
- [ ] Run API test and confirm it passes.

### Task 2: Domain Conversation Service

**Files:**
- Create: domain conversation and message model files.
- Modify: `agent-domain/src/main/java/com/example/agent/domain/chat/ConversationService.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/chat/DefaultConversationService.java`
- Test: `agent-domain/src/test/java/com/example/agent/domain/chat/DefaultConversationServiceTest.java`

**Interfaces:**
- Produces: `ConversationRepository`, `ConversationMessageRepository`, `DefaultConversationService`.

- [ ] Write failing domain tests for new conversation, existing conversation, and missing conversation.
- [ ] Implement minimal domain model and repository interfaces.
- [ ] Implement `DefaultConversationService`.
- [ ] Run domain tests.

### Task 3: Message Query API

**Files:**
- Create: `agent-api/src/main/java/com/example/agent/api/chat/ConversationController.java`
- Create: `agent-api/src/test/java/com/example/agent/api/chat/ConversationControllerTest.java`

**Interfaces:**
- Consumes: `ConversationService.listMessages(String conversationId)`
- Produces: `GET /api/conversations/{conversationId}/messages`

- [ ] Write failing controller test for message list response.
- [ ] Add service method and controller.
- [ ] Run API tests.

### Task 4: Infrastructure Persistence

**Files:**
- Create MyBatis-Plus entity, mapper, and repository implementation classes under `agent-infrastructure/src/main/java/com/example/agent/infrastructure/chat`.
- Modify: `agent-bootstrap/src/main/resources/application.yml`
- Add SQL schema doc under `docs/sql`.

**Interfaces:**
- Consumes: domain repository interfaces.
- Produces: Spring beans for persistence repositories.

- [ ] Write repository-focused tests where practical.
- [ ] Implement entities, mappers, and repositories.
- [ ] Add mapper scanning.
- [ ] Run Maven verification.

