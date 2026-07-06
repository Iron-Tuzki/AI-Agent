package com.example.agent.domain.provider;

/**
 * AI 框架提供方枚举，用于在同一业务接口下切换不同的 Agent 实现。
 */
public enum AiProvider {
    SPRING_AI,
    LANGCHAIN4J
}
