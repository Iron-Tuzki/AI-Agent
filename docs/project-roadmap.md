# Java Agent 学习项目总纲

## 一、我想干什么

我想成为一名 **Java Agent 工程师**。

这个目标不是停留在“会调用大模型 API”层面，而是希望能够用 Java 后端工程能力，把 Agent 做成真正可运行、可扩展、可观测、可上线的系统。

更具体地说，我希望通过这个项目完成三件事：

1. **系统学习 Agent 相关概念**：先理解 Chat、Prompt、Tool Calling、RAG、Memory、Workflow、Evaluation 等核心能力。
2. **通过写项目来学习**：每学一个概念，就在项目里做一个可运行的功能，而不是只看文档和示例。
3. **形成 Java 后端工程化能力**：把 AI 能力和 Spring Boot、数据库、缓存、日志、测试、异常处理、接口设计等后端能力结合起来。

## 二、我想学什么

### 1. Java Agent 工程师需要掌握的核心能力

1. **Java 后端基础工程能力**
   - Spring Boot 3.x
   - Maven 多模块工程
   - REST API 设计
   - MySQL 持久化
   - Redis 缓存
   - 参数校验、异常处理、日志与测试

2. **LLM 应用基础**
   - Chat Model 调用
   - System Prompt / User Prompt
   - Prompt 模板
   - 流式响应
   - 结构化输出
   - Token、模型参数、上下文窗口
   - 超时、重试、降级和错误处理

3. **Agent 核心机制**
   - Tool Calling
   - Agent 工具参数设计
   - 工具权限控制
   - 工具调用结果回传
   - 幂等性和敏感操作确认
   - Planner / Executor 模式
   - ReAct 思路

4. **RAG 知识库能力**
   - 文档上传与解析
   - 文本切片
   - Embedding
   - 向量数据库
   - TopK 检索
   - Metadata 过滤
   - 引用来源
   - 防幻觉设计

5. **生产级 Agent 工程化**
   - Prompt 版本管理
   - Agent 评测集
   - 调用日志和 Trace
   - 成本统计
   - 限流和熔断
   - 权限边界
   - 内容安全
   - 可观测性和问题排查

### 2. 技术路线选择

项目采用：

**Spring AI 主线 + LangChain4j 对照模块**

选择这个路线的原因：

1. **Spring AI** 更贴近 Spring Boot 后端工程化，适合作为主线。
2. **LangChain4j** 的 Agent、RAG、AI Service 抽象适合作为对照学习。
3. 通过同一业务场景的双实现，可以理解不同框架背后的共同本质，而不是只记住某个框架 API。

## 三、这个项目是干嘛的

### 1. 项目名称

**Java Agent Learning Assistant**

这是一个面向 Java 后端学习场景的 Agent 项目。

它不是一个简单聊天 Demo，而是一个逐步演进的 Java Agent 工程实践项目。项目会从最小可用的 AI 对话能力开始，逐步加入结构化输出、Tool Calling、RAG、多步骤任务规划和工程化能力。

### 2. 第一阶段项目目标

第一阶段先做 MVP，目标是把项目骨架和基础 AI 能力跑起来。

第一阶段包含：

1. Maven 多模块项目骨架。
2. 统一领域接口和 DTO。
3. 普通 Chat 接口。
4. Spring AI 流式 Chat 接口。
5. 结构化学习计划生成接口。
6. 会话和消息持久化。
7. LangChain4j 普通 Chat 与学习计划生成对照实现。

第一阶段暂不做：

1. RAG 文档知识库。
2. Tool Calling。
3. 多步骤 Agent 工作流。
4. 权限系统。
5. 前端页面。
6. 复杂任务管理系统。

### 3. 当前项目模块规划

```text
java-agent-learning
├── agent-bootstrap          # 启动模块，负责装配整个 Spring Boot 应用
├── agent-api                # Controller、请求响应 DTO、统一响应、异常处理
├── agent-domain             # 领域模型、统一业务接口、枚举、业务异常
├── agent-infrastructure     # MySQL、Redis、Mapper、Repository、基础设施能力
├── agent-spring-ai          # Spring AI 主线实现
└── agent-langchain4j        # LangChain4j 对照实现
```

### 4. 已经完成的内容

截至目前，项目已经完成：

1. 将项目迁移到：

```text
C:\Users\75974\IdeaProjects\AI-Agent
```

2. 创建 Maven 多模块工程骨架。
3. 创建 Spring Boot 启动模块。
4. 创建基础配置文件 `application.yml`。
5. 创建 `.gitignore`。
6. 创建领域层基础契约：
   - `AiProvider`
   - `AiChatRequest`
   - `AiChatResult`
   - `AiChatService`
   - `ConversationService`
   - `LearningPlan`
   - `LearningPlanGenerateRequest`
   - `LearningPlanService`
   - `AgentBusinessException`
7. 使用测试验证 `AiProvider` 枚举基础行为。

## 四、未来计划是怎样的

### 1. 第一阶段：基础 Chat 与学习计划生成

目标：先让项目具备最基础的 AI 调用闭环。

计划：

1. 完成 `agent-api`：
   - 统一响应体 `ApiResponse`
   - 全局异常处理 `GlobalExceptionHandler`
   - Chat Controller
   - LearningPlan Controller

2. 完成 `agent-spring-ai`：
   - 普通 Chat
   - 流式 Chat
   - 结构化学习计划生成
   - Prompt 模板管理

3. 完成 `agent-langchain4j`：
   - 普通 Chat 对照实现
   - 学习计划生成对照实现

4. 完成基础持久化：
   - 会话表
   - 消息表
   - 学习计划表

### 2. 第二阶段：Tool Calling

目标：让 Agent 能调用 Java 方法完成具体任务。

计划实现的工具：

1. 查询学习计划工具。
2. 创建学习任务工具。
3. 标记任务完成工具。
4. 生成复习题工具。

示例目标：

> 用户说：“帮我创建一个 7 天 JVM 学习计划，每天安排 2 个任务。”

Agent 能够理解需求，并调用 Java 工具创建结构化任务。

### 3. 第三阶段：RAG 知识库

目标：让 Agent 能基于自己的学习资料回答问题。

计划：

1. 支持上传 Markdown / PDF 文档。
2. 解析文档内容。
3. 文本切片。
4. 生成 Embedding。
5. 写入向量数据库。
6. 提问时先检索相关片段。
7. 基于检索结果生成回答。
8. 回答附带引用来源。

### 4. 第四阶段：多步骤 Agent

目标：让 Agent 能完成长期任务，而不是只做一次问答。

计划：

1. 引入任务状态。
2. 实现 Planner / Executor 模式。
3. 支持每日学习任务生成。
4. 根据答题结果调整后续计划。
5. 支持阶段性复盘。

示例目标：

> 用户说：“我想两周内掌握 Redis 高可用，请你安排计划、每天出题、根据我的答题调整后续任务。”

### 5. 第五阶段：生产级工程化

目标：把 Demo 变成更接近真实项目的系统。

计划：

1. Prompt 版本管理。
2. Agent 评测集。
3. 调用链路日志。
4. 模型调用耗时统计。
5. Token 和成本统计。
6. 限流、重试、熔断。
7. 敏感工具调用二次确认。
8. 权限隔离。
9. 可观测性面板。

## 五、学习方式约定

这个项目采用 **概念先行，项目落地** 的学习方式。

每个主题按以下顺序推进：

1. 先学习概念：理解它解决什么问题、核心机制是什么、生产中有什么坑。
2. 再写最小 Demo：只验证核心能力。
3. 再合入项目：把能力放进真实业务模块。
4. 最后复盘：总结面试表达、工程经验和后续改进点。

## 六、当前下一步

下一步优先推进：

1. 完成 `agent-api` 模块。
2. 实现统一 API 响应和全局异常处理。
3. 实现 Chat Controller。
4. 实现 LearningPlan Controller。
5. 为 Controller 编写基础测试。

完成后，项目就会具备清晰的 HTTP 入口，可以开始接入 Spring AI 主线实现。
