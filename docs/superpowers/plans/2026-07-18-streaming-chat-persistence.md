# 流式 Chat 三态持久化实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让流式 Chat 在正常完成、模型异常和客户端中断时都能正确保存助手消息状态。

**Architecture:** 普通 Chat 继续使用 `saveTurn` 原子保存一问一答。流式 Chat 先保存用户消息，再根据 Reactor 终止信号保存助手消息：`ON_COMPLETE` 为 `SUCCESS`，`ON_ERROR` 或 `CANCEL` 为 `FAILED`，并保留已生成的部分内容。

**Tech Stack:** Java 21, Spring Boot 3.5.0, Reactor, JUnit 5, Spring MockMvc, Mockito。

## Global Constraints

1. Java 类和接口提供中文 JavaDoc。
2. 不删除现有用户注释。
3. 不提交代码。
4. 直接修改当前分支。

---

### Task 1: 为流式三种终止状态补充失败测试

**Files:**
- Modify: `agent-api/src/test/java/com/example/agent/api/chat/StreamingChatControllerTest.java`

- [ ] 增加正常完成时助手消息为 `SUCCESS` 的断言。
- [ ] 增加模型异常时助手消息为 `FAILED` 的测试。
- [ ] 增加取消订阅时助手消息为 `FAILED` 的测试。
- [ ] 运行测试，确认新增行为测试先失败。

### Task 2: 扩展会话服务的分步消息保存能力

**Files:**
- Modify: `agent-domain/src/main/java/com/example/agent/domain/chat/ConversationService.java`
- Modify: `agent-domain/src/main/java/com/example/agent/domain/chat/DefaultConversationService.java`
- Modify: `agent-infrastructure/src/main/java/com/example/agent/infrastructure/chat/PersistentConversationService.java`

- [ ] 增加保存用户消息和保存助手消息的领域接口。
- [ ] 复用现有会话校验和消息实体创建逻辑。
- [ ] 保证普通 `saveTurn` 行为不变。
- [ ] 运行领域与 API 测试。

### Task 3: 根据 Reactor 终止信号持久化流式结果

**Files:**
- Modify: `agent-api/src/main/java/com/example/agent/api/chat/AiChatController.java`

- [ ] 在开始调用模型前保存用户消息。
- [ ] 使用 `doFinally` 区分 `ON_COMPLETE`、`ON_ERROR` 和 `CANCEL`。
- [ ] 正常完成保存 `SUCCESS`，异常和中断保存 `FAILED`。
- [ ] 保留已经接收的部分助手内容。
- [ ] 运行新增测试与全部 Maven 测试。

### Task 4: 回归验证并检查工作区

- [ ] 运行 `mvn -q test`。
- [ ] 检查 `git diff`，确认只包含本次功能代码、测试和计划文件。
- [ ] 确认没有执行提交操作。
