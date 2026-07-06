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
