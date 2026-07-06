# Java Agent Learning MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first Java Agent Learning Assistant MVP with Spring AI as the main implementation and LangChain4j as a comparison module.

**Architecture:** Use a Maven multi-module Spring Boot project. The API and domain modules define stable business contracts, while `agent-spring-ai` and `agent-langchain4j` implement the same use cases behind common interfaces. The first version supports normal chat, Spring AI streaming chat, structured learning-plan generation, and conversation persistence.

**Tech Stack:** Java 21, Spring Boot 3.5.x, Maven, Spring AI 2.0.x, LangChain4j 1.x, MySQL, MyBatis Plus, Redis-ready configuration, JUnit 5, Mockito, Reactor Test.

---

## 一、实施边界

第一阶段只做 MVP：

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
6. 复杂任务管理表。

---

## 二、概念学习顺序

每个编码任务前先理解对应概念：

1. **多模块工程边界**：为什么 API、Domain、Infrastructure、AI Provider 要拆开。
2. **LLM Chat 抽象**：ChatModel、Prompt、System Prompt、User Message、模型参数。
3. **流式输出**：SSE、Flux、响应聚合、异常中断。
4. **结构化输出**：JSON Schema、DTO 校验、模型输出修复。
5. **会话持久化**：Conversation、Message、Role、Provider、Metadata。
6. **框架对照**：Spring AI 的 ChatClient 风格 vs LangChain4j 的 AiServices 风格。

---

## 三、文件结构

```text
java-agent-learning
├── pom.xml
├── agent-bootstrap
│   ├── pom.xml
│   └── src/main/java/com/example/agent/AgentApplication.java
├── agent-api
│   ├── pom.xml
│   └── src/main/java/com/example/agent/api
│       ├── common/ApiResponse.java
│       ├── chat/AiChatController.java
│       └── learning/LearningPlanController.java
├── agent-domain
│   ├── pom.xml
│   └── src/main/java/com/example/agent/domain
│       ├── chat/AiChatService.java
│       ├── chat/AiChatRequest.java
│       ├── chat/AiChatResult.java
│       ├── chat/ConversationService.java
│       ├── learning/LearningPlanService.java
│       ├── learning/LearningPlanGenerateRequest.java
│       ├── learning/LearningPlan.java
│       ├── provider/AiProvider.java
│       └── exception/AgentBusinessException.java
├── agent-infrastructure
│   ├── pom.xml
│   └── src/main/java/com/example/agent/infrastructure
│       ├── conversation/ConversationEntity.java
│       ├── conversation/ConversationMessageEntity.java
│       ├── conversation/ConversationMapper.java
│       ├── conversation/ConversationMessageMapper.java
│       ├── conversation/DefaultConversationService.java
│       ├── learning/LearningPlanEntity.java
│       ├── learning/LearningPlanMapper.java
│       └── learning/DefaultLearningPlanRepository.java
├── agent-spring-ai
│   ├── pom.xml
│   └── src/main/java/com/example/agent/springai
│       ├── SpringAiChatService.java
│       ├── SpringAiLearningPlanService.java
│       └── SpringAiProperties.java
└── agent-langchain4j
    ├── pom.xml
    └── src/main/java/com/example/agent/langchain4j
        ├── LangChain4jChatService.java
        ├── LangChain4jLearningPlanService.java
        └── LangChain4jProperties.java
```

---

## Task 1: Create Maven Multi-Module Skeleton

**Files:**
- Create: `pom.xml`
- Create: `agent-bootstrap/pom.xml`
- Create: `agent-api/pom.xml`
- Create: `agent-domain/pom.xml`
- Create: `agent-infrastructure/pom.xml`
- Create: `agent-spring-ai/pom.xml`
- Create: `agent-langchain4j/pom.xml`

- [ ] **Step 1: Create parent `pom.xml`**

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>java-agent-learning</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>agent-domain</module>
        <module>agent-infrastructure</module>
        <module>agent-spring-ai</module>
        <module>agent-langchain4j</module>
        <module>agent-api</module>
        <module>agent-bootstrap</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.5.0</spring-boot.version>
        <spring-ai.version>2.0.0</spring-ai.version>
        <langchain4j.version>1.1.0</langchain4j.version>
        <mybatis-plus.version>3.5.12</mybatis-plus.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.ai</groupId>
                <artifactId>spring-ai-bom</artifactId>
                <version>${spring-ai.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.13.0</version>
                    <configuration>
                        <release>${java.version}</release>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

- [ ] **Step 2: Create module POMs**

`agent-domain/pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>java-agent-learning</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <artifactId>agent-domain</artifactId>
    <dependencies>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
    </dependencies>
</project>
```

`agent-api/pom.xml` depends on `agent-domain`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>java-agent-learning</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <artifactId>agent-api</artifactId>
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>agent-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
    </dependencies>
</project>
```

`agent-bootstrap/pom.xml` aggregates runtime modules:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>java-agent-learning</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <artifactId>agent-bootstrap</artifactId>
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>agent-api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>agent-infrastructure</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>agent-spring-ai</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>agent-langchain4j</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Run Maven validation**

Run:

```bash
mvn -q validate
```

Expected: Maven resolves the reactor and exits with code 0.

- [ ] **Step 4: Check changes without committing**

Run:

```bash
git status --short
```

Expected: New module files are listed. Do not run `git add` or `git commit`.

---

## Task 2: Add Domain Contracts and JavaDoc

**Files:**
- Create: `agent-domain/src/main/java/com/example/agent/domain/provider/AiProvider.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/chat/AiChatService.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/chat/AiChatRequest.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/chat/AiChatResult.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/chat/ConversationService.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/learning/LearningPlanService.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/learning/LearningPlanGenerateRequest.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/learning/LearningPlan.java`
- Create: `agent-domain/src/main/java/com/example/agent/domain/exception/AgentBusinessException.java`

- [ ] **Step 1: Write domain unit tests for provider routing expectations**

Create `agent-domain/src/test/java/com/example/agent/domain/provider/AiProviderTest.java`:

```java
package com.example.agent.domain.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiProviderTest {

    @Test
    void shouldParseProviderFromName() {
        assertEquals(AiProvider.SPRING_AI, AiProvider.valueOf("SPRING_AI"));
        assertEquals(AiProvider.LANGCHAIN4J, AiProvider.valueOf("LANGCHAIN4J"));
    }
}
```

- [ ] **Step 2: Add domain test dependency**

Modify `agent-domain/pom.xml`:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 3: Implement domain enum and records**

`AiProvider.java`:

```java
package com.example.agent.domain.provider;

/**
 * AI 框架提供方枚举，用于在同一业务接口下切换不同的 Agent 实现。
 */
public enum AiProvider {
    SPRING_AI,
    LANGCHAIN4J
}
```

`AiChatRequest.java`:

```java
package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI 对话请求，封装用户消息、会话编号和目标 AI 框架提供方。
 */
public record AiChatRequest(
        @NotNull AiProvider provider,
        String conversationId,
        @NotBlank String message
) {
}
```

`AiChatResult.java`:

```java
package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;

/**
 * AI 对话结果，封装模型回复、会话编号、提供方和基础用量信息。
 */
public record AiChatResult(
        String conversationId,
        AiProvider provider,
        String content,
        Integer promptTokens,
        Integer completionTokens
) {
}
```

`LearningPlanGenerateRequest.java`:

```java
package com.example.agent.domain.learning;

import com.example.agent.domain.provider.AiProvider;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 学习计划生成请求，描述学习主题、学习周期、能力等级和 AI 框架提供方。
 */
public record LearningPlanGenerateRequest(
        @NotNull AiProvider provider,
        @NotBlank String topic,
        @Min(1) @Max(30) int days,
        @NotBlank String level
) {
}
```

`LearningPlan.java`:

```java
package com.example.agent.domain.learning;

import java.util.List;

/**
 * 结构化学习计划，描述某个主题的阶段安排、练习任务和面试题。
 */
public record LearningPlan(
        String topic,
        int days,
        String level,
        List<LearningStage> stages,
        List<PracticeTask> practiceTasks,
        List<InterviewQuestion> interviewQuestions
) {

    /**
     * 学习阶段，表示某一阶段的目标、天数范围和关键知识点。
     */
    public record LearningStage(String name, String dayRange, List<String> goals) {
    }

    /**
     * 练习任务，表示用于巩固知识点的项目化实践。
     */
    public record PracticeTask(String title, String description, String expectedOutcome) {
    }

    /**
     * 面试题，表示学习主题对应的高频问题和参考回答要点。
     */
    public record InterviewQuestion(String question, List<String> answerPoints) {
    }
}
```

- [ ] **Step 4: Implement domain interfaces**

`AiChatService.java`:

```java
package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;
import reactor.core.publisher.Flux;

/**
 * AI 对话服务接口，用于屏蔽 Spring AI、LangChain4j 等不同框架的调用差异。
 */
public interface AiChatService {

    /**
     * 判断当前实现是否支持指定 AI 框架提供方。
     *
     * @param provider AI 框架提供方
     * @return 如果当前实现支持该提供方则返回 true
     */
    boolean supports(AiProvider provider);

    /**
     * 执行普通 AI 对话。
     *
     * @param request AI 对话请求，包含提供方、会话编号和用户消息
     * @return AI 对话结果
     */
    AiChatResult chat(AiChatRequest request);

    /**
     * 执行流式 AI 对话。
     *
     * @param request AI 对话请求，包含提供方、会话编号和用户消息
     * @return AI 回复内容片段流
     */
    default Flux<String> stream(AiChatRequest request) {
        return Flux.error(new UnsupportedOperationException("当前 AI 对话实现暂不支持流式输出"));
    }
}
```

`ConversationService.java`:

```java
package com.example.agent.domain.chat;

/**
 * 会话服务接口，用于保存用户消息和模型回复，支撑多轮对话与后续记忆能力。
 */
public interface ConversationService {

    /**
     * 保存一轮用户输入和 AI 回复。
     *
     * @param request 用户对话请求
     * @param result AI 回复结果
     * @return 会话编号
     */
    String saveTurn(AiChatRequest request, AiChatResult result);
}
```

`LearningPlanService.java`:

```java
package com.example.agent.domain.learning;

import com.example.agent.domain.provider.AiProvider;

/**
 * 学习计划服务接口，用于根据学习目标生成结构化学习计划。
 */
public interface LearningPlanService {

    /**
     * 判断当前实现是否支持指定 AI 框架提供方。
     *
     * @param provider AI 框架提供方
     * @return 如果当前实现支持该提供方则返回 true
     */
    boolean supports(AiProvider provider);

    /**
     * 生成结构化学习计划。
     *
     * @param request 学习计划生成请求，包含主题、天数、等级和提供方
     * @return 结构化学习计划
     */
    LearningPlan generate(LearningPlanGenerateRequest request);
}
```

- [ ] **Step 5: Add business exception**

```java
package com.example.agent.domain.exception;

/**
 * Agent 业务异常，用于表达参数合法但业务状态不满足要求的场景。
 */
public class AgentBusinessException extends RuntimeException {

    public AgentBusinessException(String message) {
        super(message);
    }

    public AgentBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

- [ ] **Step 6: Run domain tests**

Run:

```bash
mvn -q -pl agent-domain test
```

Expected: `AiProviderTest` passes.

---

## Task 3: Add Spring Boot Bootstrap and API Layer

**Files:**
- Create: `agent-bootstrap/src/main/java/com/example/agent/AgentApplication.java`
- Create: `agent-bootstrap/src/main/resources/application.yml`
- Create: `agent-api/src/main/java/com/example/agent/api/common/ApiResponse.java`
- Create: `agent-api/src/main/java/com/example/agent/api/chat/AiChatController.java`
- Create: `agent-api/src/main/java/com/example/agent/api/learning/LearningPlanController.java`
- Create: `agent-api/src/main/java/com/example/agent/api/common/GlobalExceptionHandler.java`

- [ ] **Step 1: Add controller tests**

Create `agent-api/src/test/java/com/example/agent/api/chat/AiChatControllerTest.java`:

```java
package com.example.agent.api.chat;

import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiChatController.class)
class AiChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private List<AiChatService> chatServices;

    @Test
    void shouldReturnChatResult() throws Exception {
        AiChatService service = org.mockito.Mockito.mock(AiChatService.class);
        when(service.supports(AiProvider.SPRING_AI)).thenReturn(true);
        when(service.chat(any())).thenReturn(new AiChatResult("c001", AiProvider.SPRING_AI, "你好", 1, 1));
        when(chatServices.stream()).thenReturn(java.util.stream.Stream.of(service));

        mockMvc.perform(post("/api/ai/chat")
                        .contentType("application/json")
                        .content("""
                                {"provider":"SPRING_AI","conversationId":"c001","message":"你好"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("你好"));
    }
}
```

- [ ] **Step 2: Implement bootstrap application**

```java
package com.example.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Java Agent 学习助手启动类，负责装配 API、领域服务、基础设施和 AI 框架实现。
 */
@SpringBootApplication(scanBasePackages = "com.example.agent")
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }
}
```

- [ ] **Step 3: Implement API response and exception handler**

`ApiResponse.java`:

```java
package com.example.agent.api.common;

/**
 * 通用 API 响应体，用于统一返回成功数据或错误信息。
 */
public record ApiResponse<T>(boolean success, T data, String message) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, "success");
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
```

`GlobalExceptionHandler.java`:

```java
package com.example.agent.api.common;

import com.example.agent.domain.exception.AgentBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，用于将参数校验异常和业务异常转换为统一 API 响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AgentBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(AgentBusinessException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("请求参数不合法");
        return ApiResponse.fail(message);
    }
}
```

- [ ] **Step 4: Implement controllers**

`AiChatController.java`:

```java
package com.example.agent.api.chat;

import com.example.agent.api.common.ApiResponse;
import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.exception.AgentBusinessException;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 对话接口，提供普通对话和流式对话入口。
 */
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final List<AiChatService> chatServices;

    public AiChatController(List<AiChatService> chatServices) {
        this.chatServices = chatServices;
    }

    @PostMapping("/chat")
    public ApiResponse<AiChatResult> chat(@Valid @RequestBody AiChatRequest request) {
        return ApiResponse.ok(resolveService(request).chat(request));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@Valid @RequestBody AiChatRequest request) {
        return resolveService(request).stream(request);
    }

    private AiChatService resolveService(AiChatRequest request) {
        return chatServices.stream()
                .filter(service -> service.supports(request.provider()))
                .findFirst()
                .orElseThrow(() -> new AgentBusinessException("不支持的 AI 提供方：" + request.provider()));
    }
}
```

`LearningPlanController.java`:

```java
package com.example.agent.api.learning;

import com.example.agent.api.common.ApiResponse;
import com.example.agent.domain.exception.AgentBusinessException;
import com.example.agent.domain.learning.LearningPlan;
import com.example.agent.domain.learning.LearningPlanGenerateRequest;
import com.example.agent.domain.learning.LearningPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学习计划接口，提供基于 AI 的结构化学习计划生成能力。
 */
@RestController
@RequestMapping("/api/learning/plans")
public class LearningPlanController {

    private final List<LearningPlanService> learningPlanServices;

    public LearningPlanController(List<LearningPlanService> learningPlanServices) {
        this.learningPlanServices = learningPlanServices;
    }

    @PostMapping("/generate")
    public ApiResponse<LearningPlan> generate(@Valid @RequestBody LearningPlanGenerateRequest request) {
        LearningPlanService service = learningPlanServices.stream()
                .filter(item -> item.supports(request.provider()))
                .findFirst()
                .orElseThrow(() -> new AgentBusinessException("不支持的 AI 提供方：" + request.provider()));
        return ApiResponse.ok(service.generate(request));
    }
}
```

- [ ] **Step 5: Run API tests**

Run:

```bash
mvn -q -pl agent-api test
```

Expected: controller tests pass.

---

## Task 4: Add Conversation Persistence

**Files:**
- Create: `agent-bootstrap/src/main/resources/db/schema.sql`
- Create: `agent-infrastructure/src/main/java/com/example/agent/infrastructure/conversation/ConversationEntity.java`
- Create: `agent-infrastructure/src/main/java/com/example/agent/infrastructure/conversation/ConversationMessageEntity.java`
- Create: `agent-infrastructure/src/main/java/com/example/agent/infrastructure/conversation/DefaultConversationService.java`

- [ ] **Step 1: Create schema**

```sql
CREATE TABLE conversation (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    provider VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE conversation_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    provider VARCHAR(32) NOT NULL,
    metadata JSON NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_conversation_message_conversation_id (conversation_id)
);

CREATE TABLE learning_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    topic VARCHAR(255) NOT NULL,
    level VARCHAR(64) NOT NULL,
    provider VARCHAR(32) NOT NULL,
    plan_json JSON NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

- [ ] **Step 2: Implement persistence service**

`DefaultConversationService.java`:

```java
package com.example.agent.infrastructure.conversation;

import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.ConversationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 默认会话服务实现，负责保存用户消息和 AI 回复。
 */
@Service
public class DefaultConversationService implements ConversationService {

    private final ConversationMapper conversationMapper;
    private final ConversationMessageMapper messageMapper;

    public DefaultConversationService(ConversationMapper conversationMapper, ConversationMessageMapper messageMapper) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
    }

    @Override
    public String saveTurn(AiChatRequest request, AiChatResult result) {
        String conversationId = request.conversationId() == null || request.conversationId().isBlank()
                ? UUID.randomUUID().toString()
                : request.conversationId();
        LocalDateTime now = LocalDateTime.now();
        conversationMapper.upsert(conversationId, request.provider().name(), titleFrom(request.message()), now);
        messageMapper.insertMessage(conversationId, "USER", request.message(), request.provider().name(), now);
        messageMapper.insertMessage(conversationId, "ASSISTANT", result.content(), result.provider().name(), now);
        return conversationId;
    }

    private String titleFrom(String message) {
        return message.length() <= 40 ? message : message.substring(0, 40);
    }
}
```

- [ ] **Step 3: Add Mapper interfaces**

`ConversationMapper.java`:

```java
package com.example.agent.infrastructure.conversation;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 会话表 Mapper，负责创建或更新会话摘要。
 */
@Mapper
public interface ConversationMapper {

    @Insert("""
            INSERT INTO conversation(id, title, provider, created_at, updated_at)
            VALUES(#{id}, #{title}, #{provider}, #{now}, #{now})
            ON DUPLICATE KEY UPDATE updated_at = #{now}
            """)
    void upsert(@Param("id") String id,
                @Param("provider") String provider,
                @Param("title") String title,
                @Param("now") LocalDateTime now);
}
```

`ConversationMessageMapper.java`:

```java
package com.example.agent.infrastructure.conversation;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 会话消息表 Mapper，负责保存用户与 AI 的每轮消息。
 */
@Mapper
public interface ConversationMessageMapper {

    @Insert("""
            INSERT INTO conversation_message(conversation_id, role, content, provider, created_at)
            VALUES(#{conversationId}, #{role}, #{content}, #{provider}, #{createdAt})
            """)
    void insertMessage(@Param("conversationId") String conversationId,
                       @Param("role") String role,
                       @Param("content") String content,
                       @Param("provider") String provider,
                       @Param("createdAt") LocalDateTime createdAt);
}
```

- [ ] **Step 4: Run infrastructure compile**

Run:

```bash
mvn -q -pl agent-infrastructure -am test
```

Expected: module compiles and tests pass.

---

## Task 5: Implement Spring AI Mainline

**Files:**
- Create: `agent-spring-ai/src/main/java/com/example/agent/springai/SpringAiChatService.java`
- Create: `agent-spring-ai/src/main/java/com/example/agent/springai/SpringAiLearningPlanService.java`
- Create: `agent-spring-ai/src/main/resources/prompts/learning-plan-system.st`

- [ ] **Step 1: Add Spring AI dependencies**

Modify `agent-spring-ai/pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>agent-domain</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
</dependencies>
```

- [ ] **Step 2: Implement Spring AI chat service**

```java
package com.example.agent.springai;

import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.chat.ConversationService;
import com.example.agent.domain.provider.AiProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Spring AI 对话服务实现，作为项目主线 AI 调用方式。
 */
@Service
public class SpringAiChatService implements AiChatService {

    private final ChatClient chatClient;
    private final ConversationService conversationService;

    public SpringAiChatService(ChatClient.Builder builder, ConversationService conversationService) {
        this.chatClient = builder.defaultSystem("你是一个资深 Java Agent 工程师，回答要准确、结构化、贴近生产实践。").build();
        this.conversationService = conversationService;
    }

    @Override
    public boolean supports(AiProvider provider) {
        return provider == AiProvider.SPRING_AI;
    }

    @Override
    public AiChatResult chat(AiChatRequest request) {
        String content = chatClient.prompt()
                .user(request.message())
                .call()
                .content();
        AiChatResult result = new AiChatResult(request.conversationId(), AiProvider.SPRING_AI, content, null, null);
        String conversationId = conversationService.saveTurn(request, result);
        return new AiChatResult(conversationId, AiProvider.SPRING_AI, content, null, null);
    }

    @Override
    public Flux<String> stream(AiChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .stream()
                .content();
    }
}
```

- [ ] **Step 3: Implement Spring AI learning plan service**

```java
package com.example.agent.springai;

import com.example.agent.domain.learning.LearningPlan;
import com.example.agent.domain.learning.LearningPlanGenerateRequest;
import com.example.agent.domain.learning.LearningPlanService;
import com.example.agent.domain.provider.AiProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * Spring AI 学习计划服务实现，负责生成结构化 Java Agent 学习计划。
 */
@Service
public class SpringAiLearningPlanService implements LearningPlanService {

    private final ChatClient chatClient;

    public SpringAiLearningPlanService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public boolean supports(AiProvider provider) {
        return provider == AiProvider.SPRING_AI;
    }

    @Override
    public LearningPlan generate(LearningPlanGenerateRequest request) {
        BeanOutputConverter<LearningPlan> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<>() {
                }
        );
        String content = chatClient.prompt()
                .system("""
                        你是一个 Java Agent 工程师学习规划专家。
                        请严格按照输出格式返回 JSON，不要输出 Markdown。
                        输出格式：
                        {format}
                        """)
                .user("""
                        请为主题「%s」生成 %d 天学习计划。
                        学员水平：%s。
                        计划需要包含阶段目标、实践任务和面试题。
                        """.formatted(request.topic(), request.days(), request.level()))
                .call()
                .content();
        return converter.convert(content);
    }
}
```

- [ ] **Step 4: Configure model in `application.yml`**

```yaml
server:
  port: 8080

spring:
  application:
    name: java-agent-learning
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:}
      chat:
        options:
          model: ${OPENAI_CHAT_MODEL:gpt-4.1-mini}
```

- [ ] **Step 5: Run compile**

Run:

```bash
mvn -q -pl agent-spring-ai -am test
```

Expected: Spring AI module compiles.

---

## Task 6: Implement LangChain4j Comparison Module

**Files:**
- Create: `agent-langchain4j/src/main/java/com/example/agent/langchain4j/LangChain4jChatService.java`
- Create: `agent-langchain4j/src/main/java/com/example/agent/langchain4j/LangChain4jLearningPlanService.java`

- [ ] **Step 1: Add dependencies**

Modify `agent-langchain4j/pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>agent-domain</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j</artifactId>
        <version>${langchain4j.version}</version>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai</artifactId>
        <version>${langchain4j.version}</version>
    </dependency>
</dependencies>
```

- [ ] **Step 2: Implement LangChain4j chat service**

```java
package com.example.agent.langchain4j;

import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.chat.ConversationService;
import com.example.agent.domain.provider.AiProvider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

/**
 * LangChain4j 对话服务实现，用于和 Spring AI 主线实现进行框架对照学习。
 */
@Service
public class LangChain4jChatService implements AiChatService {

    private final ChatLanguageModel chatLanguageModel;
    private final ConversationService conversationService;

    public LangChain4jChatService(ChatLanguageModel chatLanguageModel, ConversationService conversationService) {
        this.chatLanguageModel = chatLanguageModel;
        this.conversationService = conversationService;
    }

    @Override
    public boolean supports(AiProvider provider) {
        return provider == AiProvider.LANGCHAIN4J;
    }

    @Override
    public AiChatResult chat(AiChatRequest request) {
        String content = chatLanguageModel.generate(request.message());
        AiChatResult result = new AiChatResult(request.conversationId(), AiProvider.LANGCHAIN4J, content, null, null);
        String conversationId = conversationService.saveTurn(request, result);
        return new AiChatResult(conversationId, AiProvider.LANGCHAIN4J, content, null, null);
    }
}
```

- [ ] **Step 3: Implement LangChain4j learning plan service**

```java
package com.example.agent.langchain4j;

import com.example.agent.domain.learning.LearningPlan;
import com.example.agent.domain.learning.LearningPlanGenerateRequest;
import com.example.agent.domain.learning.LearningPlanService;
import com.example.agent.domain.provider.AiProvider;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

/**
 * LangChain4j 学习计划服务实现，用于对照结构化输出的开发体验。
 */
@Service
public class LangChain4jLearningPlanService implements LearningPlanService {

    private final LearningPlanAssistant assistant;

    public LangChain4jLearningPlanService(ChatLanguageModel chatLanguageModel) {
        this.assistant = AiServices.create(LearningPlanAssistant.class, chatLanguageModel);
    }

    @Override
    public boolean supports(AiProvider provider) {
        return provider == AiProvider.LANGCHAIN4J;
    }

    @Override
    public LearningPlan generate(LearningPlanGenerateRequest request) {
        return assistant.generate(request.topic(), request.days(), request.level());
    }

    interface LearningPlanAssistant {

        @SystemMessage("你是一个 Java Agent 工程师学习规划专家，请返回结构化学习计划。")
        @UserMessage("请为主题 {{topic}} 生成 {{days}} 天学习计划，学员水平是 {{level}}。")
        LearningPlan generate(@V("topic") String topic, @V("days") int days, @V("level") String level);
    }
}
```

- [ ] **Step 4: Run compile**

Run:

```bash
mvn -q -pl agent-langchain4j -am test
```

Expected: LangChain4j module compiles.

---

## Task 7: End-to-End Smoke Test

**Files:**
- Modify: `agent-bootstrap/src/main/resources/application.yml`

- [ ] **Step 1: Start application**

Run:

```bash
mvn -pl agent-bootstrap spring-boot:run
```

Expected:

```text
Started AgentApplication
```

- [ ] **Step 2: Test Spring AI normal chat**

Run:

```bash
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d "{\"provider\":\"SPRING_AI\",\"conversationId\":\"c001\",\"message\":\"请用三句话解释什么是 Java Agent 工程师\"}"
```

Expected: JSON response with `success=true` and non-empty `data.content`.

- [ ] **Step 3: Test Spring AI learning plan**

Run:

```bash
curl -X POST http://localhost:8080/api/learning/plans/generate \
  -H "Content-Type: application/json" \
  -d "{\"provider\":\"SPRING_AI\",\"topic\":\"JVM GC\",\"days\":7,\"level\":\"ADVANCED\"}"
```

Expected: JSON response with `topic=JVM GC`, `days=7`, and non-empty `stages`.

- [ ] **Step 4: Test LangChain4j comparison chat**

Run:

```bash
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d "{\"provider\":\"LANGCHAIN4J\",\"conversationId\":\"c002\",\"message\":\"请用三句话解释什么是 Tool Calling\"}"
```

Expected: JSON response with `provider=LANGCHAIN4J` and non-empty `content`.

- [ ] **Step 5: Check changes without committing**

Run:

```bash
git status --short
```

Expected: all created and modified files are visible. Do not commit.

---

## 四、自检结果

1. **需求覆盖**：计划覆盖多模块骨架、统一业务接口、Spring AI 主线、LangChain4j 对照、Chat、Stream、结构化学习计划、持久化。
2. **范围控制**：RAG、Tool Calling、多步骤 Agent、前端、权限系统已明确排除到后续阶段。
3. **类型一致性**：`AiProvider`、`AiChatRequest`、`AiChatResult`、`LearningPlanGenerateRequest`、`LearningPlan` 在 API、Spring AI、LangChain4j 模块中保持同名同签名。
4. **用户约束**：所有 Java 类和接口计划中都带中文 JavaDoc；计划不包含 commit 执行动作。
