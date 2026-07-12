# Conversation Message Persistence Design

## 一、目标

为普通 AI 对话增加会话和消息持久化能力。第一版只覆盖非流式 `POST /api/ai/chat`，确保一次用户提问和一次模型回复可以绑定到同一个会话并落库。

## 二、范围

1. 如果请求没有传入 `conversationId`，系统创建新会话。
2. 保存用户消息，角色为 `USER`，状态为 `SUCCESS`。
3. 调用模型服务获取回复。
4. 保存模型回复，角色为 `ASSISTANT`，状态为 `SUCCESS`。
5. 返回结果中的 `conversationId` 必须是实际持久化会话编号。
6. 新增查询会话消息列表接口。

流式 chat 的持久化暂不进入本轮实现。

## 三、分层设计

1. `agent-domain` 定义会话、消息、角色、状态、仓储接口和会话应用服务。
2. `agent-api` 负责 HTTP 编排：选择 AI 服务、调用模型、调用会话服务保存一轮对话。
3. `agent-infrastructure` 负责 MyBatis-Plus 实体、Mapper 和仓储实现。
4. `agent-spring-ai` 继续只负责模型调用，不直接依赖持久化。

## 四、数据模型

`agent_conversation` 保存会话元信息：`id`、`title`、`provider`、`status`、`created_at`、`updated_at`。

`agent_message` 保存消息明细：`id`、`conversation_id`、`role`、`content`、`status`、`provider`、`prompt_tokens`、`completion_tokens`、`created_at`、`updated_at`。

## 五、错误处理

1. 不存在的 `conversationId` 继续对话时抛出业务异常。
2. 模型调用失败时本轮暂不保存 assistant 成功消息。
3. 消息查询遇到不存在会话时抛出业务异常。

## 六、测试策略

1. `agent-api` 用 `MockMvc` 验证 chat 会保存一问一答，并返回持久化会话编号。
2. `agent-domain` 用单元测试验证新会话创建、已有会话追加、缺失会话报错。
3. `agent-api` 用 `MockMvc` 验证消息列表查询接口响应结构。

