# ConversationService 分层说明

## 一、文档目的

本文说明对话与消息持久化中 `ConversationService` 的分层设计、两个实现类的职责边界，以及后续新增功能应当放置的位置。

当前设计保留两个实现类，但它们不是两套并行的业务实现，而是“领域实现 + 基础设施适配”的组合：

```text
AiChatController
        |
        v
PersistentConversationService
        |
        v
DefaultConversationService
        |
        +--> ConversationRepository
        +--> ConversationMessageRepository
```

## 二、各层职责

### 1. `ConversationService`

位置：`agent-domain/src/main/java/com/example/agent/domain/chat/ConversationService.java`

这是对话服务的领域接口，定义业务需要的能力，不关心具体使用 MyBatis、JPA 还是其他存储方式。

当前职责包括：

1. 保存一轮用户输入和 AI 回复。
2. 查询指定会话下的消息列表。

控制器依赖这个接口，而不是依赖某个具体实现类。这样可以避免 API 层和具体持久化技术直接耦合。

### 2. `DefaultConversationService`

位置：`agent-domain/src/main/java/com/example/agent/domain/chat/DefaultConversationService.java`

这是纯领域服务实现，负责真正的业务规则：

1. 请求没有会话编号时创建新会话。
2. 使用首条用户消息生成会话标题，并限制标题长度。
3. 一轮请求保存一条用户消息和一条助手消息。
4. 更新会话的最后活跃时间。
5. 查询消息前校验会话编号和会话是否存在。
6. 生成会话和消息编号。

这个类不使用 `@Service`、`@Transactional` 等 Spring 注解，因此可以通过内存仓储直接进行单元测试，也可以在未来复用到其他入口，例如定时任务、命令行工具或消息消费端。

### 3. `PersistentConversationService`

位置：`agent-infrastructure/src/main/java/com/example/agent/infrastructure/chat/PersistentConversationService.java`

这是面向 Spring 和持久化运行环境的适配层，负责：

1. 使用 `@Service` 注册为 Spring Bean。
2. 使用 `@Transactional` 包住保存一轮消息的完整操作。
3. 使用 `@Transactional(readOnly = true)` 包住消息查询。
4. 将调用转交给 `DefaultConversationService`。

它本身不重复编写创建会话、保存消息等业务规则，而是通过 `delegate` 复用领域服务：

```java
private final DefaultConversationService delegate;
```

## 三、为什么要这样设计

### 1. 事务边界需要位于 Spring Bean 上

保存一轮对话至少包含以下步骤：

1. 必要时创建会话。
2. 保存用户消息。
3. 保存助手消息。
4. 更新会话时间。

这些操作必须作为一个整体提交。任何一步失败，都不应留下半轮对话。因此事务边界放在 `PersistentConversationService.saveTurn()` 上，由 Spring 代理统一管理。

如果只在普通领域对象上增加事务注解，领域模块就会被 Spring 技术绑定；如果由控制器自行管理事务，则 API 层会承担不应承担的持久化细节。

### 2. 领域规则不依赖具体框架

`DefaultConversationService` 只依赖领域仓储接口：

```text
ConversationService
    -> ConversationRepository
    -> ConversationMessageRepository
```

MyBatis 的实现位于基础设施模块：

```text
MyBatisConversationRepository
MyBatisConversationMessageRepository
```

因此，业务规则与数据库表结构、ORM 框架和 Spring 生命周期保持相对独立。

### 3. 测试成本更低

领域服务可以使用内存仓储测试，不需要启动 Spring 容器，也不需要连接 MySQL。这样可以快速验证会话创建、消息顺序、标题截断和异常校验等核心规则。

事务行为则由 Spring 集成测试或基础设施测试验证，两类测试关注点清晰分离。

## 四、一轮对话的调用流程

```text
1. AiChatController.chat(request)
2. 调用 AI Provider 获取 AiChatResult
3. 调用 ConversationService.saveTurn(request, result)
4. Spring 进入 PersistentConversationService 的事务
5. PersistentConversationService 委托 DefaultConversationService
6. DefaultConversationService 创建或校验 conversationId
7. 保存 USER 消息
8. 保存 ASSISTANT 消息
9. 更新会话时间
10. 事务提交并返回 conversationId
```

消息保存失败时，事务会回滚本轮已经写入的内容，避免出现只有用户消息、没有助手消息的异常状态。

## 五、后续代码应该放在哪里

| 需求 | 放置位置 |
| --- | --- |
| 新增会话业务规则 | `DefaultConversationService` |
| 新增消息业务校验 | `DefaultConversationService` 或领域模型 |
| 新增服务能力定义 | `ConversationService` |
| 新增事务边界 | `PersistentConversationService` |
| MyBatis、SQL、表字段映射 | `agent-infrastructure` |
| HTTP 参数和返回值 | `agent-api` |
| 数据库建表或迁移脚本 | `docs/sql` 或项目约定的迁移目录 |

原则是：业务判断进入领域实现，框架能力留在持久化适配层，控制器只负责协议转换和流程编排。

## 六、什么时候可以合并两个实现类

如果项目规模很小，且确定不会复用领域服务，可以把领域逻辑直接放进 `PersistentConversationService`，减少类数量。

当前项目已经使用模块化结构，并且对话持久化属于核心业务能力，因此保留两个实现类更合适：

1. `DefaultConversationService` 保证业务规则集中、可独立测试。
2. `PersistentConversationService` 保证 Spring 事务和持久化运行时职责集中。
3. 两个类之间通过委托连接，没有重复业务逻辑。

后续如果出现第三种入口，例如批量导入历史消息，可以直接复用 `DefaultConversationService` 的规则，而不必复制控制器或 Spring 事务适配代码。

## 七、当前设计约束

1. 只有 `PersistentConversationService` 作为 Spring 服务注入到 API 层。
2. API 层不得直接注入 `DefaultConversationService`。
3. `DefaultConversationService` 不得引入 Spring 注解或 MyBatis 类型。
4. `saveTurn` 必须保持一轮消息的原子性。
5. 流式聊天接入持久化时，应在流正常结束后统一保存完整助手消息；流中断时应按约定保存失败或部分响应状态。
